package com.example.tacticalcommandandcontrol.core.network.mavlink

import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.concurrent.atomic.AtomicLong
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MAVLink v2 message signing using HMAC-SHA256.
 *
 * Signing spec: https://mavlink.io/en/guide/message_signing.html
 *
 * - Outgoing: uses [send2] 6-arg overload via [getSigningParams]
 * - Incoming: manual verification via [verifySignature] on raw packet bytes
 *
 * Timestamp is 48-bit, units of 10 microseconds since 2015-01-01T00:00:00 UTC.
 */
@Singleton
class MavlinkSigner @Inject constructor() {

    companion object {
        private const val HMAC_ALGORITHM = "HmacSHA256"
        private const val SECRET_KEY_LENGTH = 32
        private const val SIGNATURE_LENGTH = 6
        private const val TIMESTAMP_BYTES = 6
        private const val LINK_ID_BYTE = 1
        private const val MAVLINK2_HEADER_SIZE = 10

        /** Epoch offset: microseconds between Unix epoch and 2015-01-01T00:00:00 UTC. */
        private const val MAVLINK_EPOCH_OFFSET_MILLIS = 1420070400000L

        /** 10-microsecond unit divisor from milliseconds. */
        private const val TIMESTAMP_UNIT_DIVISOR_US = 10
    }

    private val timestampCounter = AtomicLong(0)

    private var secretKey: ByteArray? = null
    private var linkId: Int = 0

    /**
     * Configure signing with the given 32-byte secret key and link ID.
     */
    fun configure(key: ByteArray, linkId: Int = 0) {
        require(key.size == SECRET_KEY_LENGTH) {
            "MAVLink signing key must be $SECRET_KEY_LENGTH bytes, got ${key.size}"
        }
        this.secretKey = key.copyOf()
        this.linkId = linkId
        Timber.d("MAVLink signing configured for linkId=$linkId")
    }

    /**
     * Derive a 32-byte signing key from a passphrase via SHA-256.
     */
    fun deriveKey(passphrase: String): ByteArray {
        return MessageDigest.getInstance("SHA-256")
            .digest(passphrase.toByteArray(Charsets.UTF_8))
    }

    val isConfigured: Boolean get() = secretKey != null

    /**
     * Returns the signing parameters (linkId, timestamp, secretKey) for use with
     * [MavlinkConnection.send2(sysId, compId, payload, linkId, timestamp, secretKey)].
     *
     * Returns null if signing is not configured.
     */
    fun getSigningParams(): SigningParams? {
        val key = secretKey ?: return null
        return SigningParams(
            linkId = linkId,
            timestamp = nextTimestamp(),
            secretKey = key,
        )
    }

    /**
     * Verify the signature of a raw MAVLink v2 packet.
     *
     * Packet structure:
     * [0]      0xFD (magic)
     * [1]      payload length
     * [2]      incompat_flags (bit 0x01 = signed)
     * [3..9]   remaining header
     * [10..n]  payload
     * [n+1,n+2] CRC
     * [n+3]    linkId
     * [n+4..n+9] timestamp (6 bytes LE)
     * [n+10..n+15] signature (6 bytes)
     */
    fun verifySignature(rawPacket: ByteArray): Boolean {
        val key = secretKey ?: return false

        // Minimum: header(10) + CRC(2) + signature(13) = 25 bytes with 0 payload
        if (rawPacket.size < 25) return false

        // Check magic byte
        if (rawPacket[0] != 0xFD.toByte()) return false

        // Check signing flag
        val incompatFlags = rawPacket[2].toInt() and 0xFF
        if (incompatFlags and 0x01 == 0) return false

        val payloadLength = rawPacket[1].toInt() and 0xFF
        val crcEnd = MAVLINK2_HEADER_SIZE + payloadLength + 2
        val signatureStart = crcEnd

        if (rawPacket.size < signatureStart + LINK_ID_BYTE + TIMESTAMP_BYTES + SIGNATURE_LENGTH) {
            return false
        }

        // Extract the received signature components
        val msgLinkId = rawPacket[signatureStart].toInt() and 0xFF
        val timestampBytes = rawPacket.copyOfRange(signatureStart + 1, signatureStart + 1 + TIMESTAMP_BYTES)
        val receivedSignature = rawPacket.copyOfRange(
            signatureStart + 1 + TIMESTAMP_BYTES,
            signatureStart + 1 + TIMESTAMP_BYTES + SIGNATURE_LENGTH,
        )

        // Compute expected signature: HMAC-SHA256(key, header + payload + CRC + linkId + timestamp)
        val dataToSign = ByteArray(crcEnd + LINK_ID_BYTE + TIMESTAMP_BYTES)
        System.arraycopy(rawPacket, 0, dataToSign, 0, crcEnd)
        dataToSign[crcEnd] = msgLinkId.toByte()
        System.arraycopy(timestampBytes, 0, dataToSign, crcEnd + 1, TIMESTAMP_BYTES)

        val mac = Mac.getInstance(HMAC_ALGORITHM)
        mac.init(SecretKeySpec(key, HMAC_ALGORITHM))
        val fullHash = mac.doFinal(dataToSign)

        // Compare first 6 bytes
        val expectedSignature = fullHash.copyOfRange(0, SIGNATURE_LENGTH)
        return MessageDigest.isEqual(expectedSignature, receivedSignature)
    }

    /**
     * Generate a monotonically increasing MAVLink timestamp.
     * Units: 10 microseconds since 2015-01-01T00:00:00 UTC.
     */
    private fun nextTimestamp(): Long {
        val currentTimestamp = (System.currentTimeMillis() - MAVLINK_EPOCH_OFFSET_MILLIS) *
            (1000 / TIMESTAMP_UNIT_DIVISOR_US)

        return timestampCounter.updateAndGet { prev ->
            maxOf(prev + 1, currentTimestamp)
        }
    }

    data class SigningParams(
        val linkId: Int,
        val timestamp: Long,
        val secretKey: ByteArray,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SigningParams) return false
            return linkId == other.linkId && timestamp == other.timestamp
        }

        override fun hashCode(): Int = 31 * linkId + timestamp.hashCode()
    }
}
