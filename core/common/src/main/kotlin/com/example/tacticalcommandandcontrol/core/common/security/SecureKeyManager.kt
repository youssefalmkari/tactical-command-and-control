package com.example.tacticalcommandandcontrol.core.common.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import timber.log.Timber
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized Android Keystore key management for the C2 platform.
 *
 * Manages AES-256-GCM keys for:
 * - Database encryption passphrase
 * - MAVLink signing key protection
 * - Secure local storage encryption
 *
 * Keys are hardware-backed on supported devices (StrongBox / TEE).
 */
@Singleton
class SecureKeyManager @Inject constructor() {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val GCM_TAG_LENGTH = 128
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    }

    /**
     * Get or create an AES-256-GCM key with the given alias.
     */
    fun getOrCreateAesKey(alias: String): SecretKey {
        val existing = keyStore.getKey(alias, null) as? SecretKey
        if (existing != null) return existing

        Timber.d("Generating new AES key: $alias")
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE,
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setUserAuthenticationRequired(false)
                .build(),
        )
        return keyGenerator.generateKey()
    }

    /**
     * Get or create an HMAC-SHA256 key with the given alias.
     */
    fun getOrCreateHmacKey(alias: String): SecretKey {
        val existing = keyStore.getKey(alias, null) as? SecretKey
        if (existing != null) return existing

        Timber.d("Generating new HMAC key: $alias")
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_HMAC_SHA256,
            ANDROID_KEYSTORE,
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN)
                .build(),
        )
        return keyGenerator.generateKey()
    }

    /**
     * Encrypt data using the AES key identified by [alias].
     * Returns IV prepended to ciphertext: [12-byte IV][ciphertext + GCM tag].
     */
    fun encrypt(alias: String, plaintext: ByteArray): ByteArray {
        val key = getOrCreateAesKey(alias)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext)

        return ByteArray(iv.size + ciphertext.size).also {
            System.arraycopy(iv, 0, it, 0, iv.size)
            System.arraycopy(ciphertext, 0, it, iv.size, ciphertext.size)
        }
    }

    /**
     * Decrypt data using the AES key identified by [alias].
     * Expects IV prepended to ciphertext: [12-byte IV][ciphertext + GCM tag].
     */
    fun decrypt(alias: String, data: ByteArray): ByteArray {
        val key = getOrCreateAesKey(alias)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val ivSize = 12
        val iv = data.copyOfRange(0, ivSize)
        val ciphertext = data.copyOfRange(ivSize, data.size)

        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return cipher.doFinal(ciphertext)
    }

    /**
     * Check if a key with the given alias exists in the Keystore.
     */
    fun hasKey(alias: String): Boolean = keyStore.containsAlias(alias)

    /**
     * Delete a key from the Keystore.
     */
    fun deleteKey(alias: String) {
        if (keyStore.containsAlias(alias)) {
            keyStore.deleteEntry(alias)
            Timber.d("Deleted key: $alias")
        }
    }
}
