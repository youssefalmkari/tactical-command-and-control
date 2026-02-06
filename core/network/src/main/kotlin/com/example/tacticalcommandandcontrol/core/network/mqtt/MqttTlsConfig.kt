package com.example.tacticalcommandandcontrol.core.network.mqtt

import timber.log.Timber
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.TrustManagerFactory

/**
 * TLS configuration for MQTT connections.
 *
 * Supports:
 * - TLS 1.3 enforcement
 * - Server certificate pinning via custom CA
 * - Mutual TLS (mTLS) with client certificate
 */
data class MqttTlsConfig(
    val protocols: List<String> = listOf("TLSv1.3"),
    val caCertificate: InputStream? = null,
    val clientKeyStore: KeyStore? = null,
    val clientKeyStorePassword: CharArray? = null,
) {

    fun buildTrustManagerFactory(): TrustManagerFactory {
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())

        val caCert = caCertificate
        if (caCert != null) {
            val cf = CertificateFactory.getInstance("X.509")
            val cert = cf.generateCertificate(caCert) as X509Certificate
            Timber.d("TLS: Pinning to CA: ${cert.subjectX500Principal.name}")

            val ks = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
                load(null, null)
                setCertificateEntry("ca", cert)
            }
            tmf.init(ks)
        } else {
            tmf.init(null as KeyStore?)
        }

        return tmf
    }

    fun buildKeyManagerFactory(): KeyManagerFactory? {
        val ks = clientKeyStore ?: return null
        val password = clientKeyStorePassword ?: charArrayOf()

        return KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
            init(ks, password)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MqttTlsConfig) return false
        return protocols == other.protocols
    }

    override fun hashCode(): Int = protocols.hashCode()
}
