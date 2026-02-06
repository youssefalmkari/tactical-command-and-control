package com.example.tacticalcommandandcontrol.core.network.mavlink

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MavlinkSignerTest {

    private lateinit var signer: MavlinkSigner

    @Before
    fun setup() {
        signer = MavlinkSigner()
    }

    @Test
    fun `not configured by default`() {
        assertFalse(signer.isConfigured)
        assertNull(signer.getSigningParams())
    }

    @Test
    fun `configure with valid key`() {
        val key = ByteArray(32) { it.toByte() }
        signer.configure(key)
        assertTrue(signer.isConfigured)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `configure with wrong key size throws`() {
        signer.configure(ByteArray(16))
    }

    @Test
    fun `getSigningParams returns valid params after configure`() {
        val key = ByteArray(32) { 0xAB.toByte() }
        signer.configure(key, linkId = 5)

        val params = signer.getSigningParams()
        assertNotNull(params)
        assertEquals(5, params!!.linkId)
        assertTrue(params.timestamp > 0)
        assertEquals(32, params.secretKey.size)
    }

    @Test
    fun `timestamps are monotonically increasing`() {
        val key = ByteArray(32) { 0x01 }
        signer.configure(key)

        val ts1 = signer.getSigningParams()!!.timestamp
        val ts2 = signer.getSigningParams()!!.timestamp
        val ts3 = signer.getSigningParams()!!.timestamp

        assertTrue("ts2 should be > ts1", ts2 > ts1)
        assertTrue("ts3 should be > ts2", ts3 > ts2)
    }

    @Test
    fun `deriveKey produces 32 byte key`() {
        val key = signer.deriveKey("test-passphrase")
        assertEquals(32, key.size)
    }

    @Test
    fun `deriveKey is deterministic`() {
        val key1 = signer.deriveKey("hello")
        val key2 = signer.deriveKey("hello")
        assertArrayEquals(key1, key2)
    }

    @Test
    fun `deriveKey different inputs produce different keys`() {
        val key1 = signer.deriveKey("alpha")
        val key2 = signer.deriveKey("bravo")
        assertFalse(key1.contentEquals(key2))
    }

    @Test
    fun `verifySignature returns false when not configured`() {
        val packet = ByteArray(30)
        assertFalse(signer.verifySignature(packet))
    }

    @Test
    fun `verifySignature returns false for too short packet`() {
        val key = ByteArray(32) { 0x01 }
        signer.configure(key)
        assertFalse(signer.verifySignature(ByteArray(10)))
    }

    @Test
    fun `verifySignature returns false for non-MAVLink2 packet`() {
        val key = ByteArray(32) { 0x01 }
        signer.configure(key)

        val packet = ByteArray(30)
        packet[0] = 0xFE.toByte() // MAVLink v1 magic
        assertFalse(signer.verifySignature(packet))
    }

    @Test
    fun `verifySignature returns false for unsigned packet`() {
        val key = ByteArray(32) { 0x01 }
        signer.configure(key)

        val packet = ByteArray(30)
        packet[0] = 0xFD.toByte() // MAVLink v2 magic
        packet[2] = 0x00          // no signing flag
        assertFalse(signer.verifySignature(packet))
    }
}
