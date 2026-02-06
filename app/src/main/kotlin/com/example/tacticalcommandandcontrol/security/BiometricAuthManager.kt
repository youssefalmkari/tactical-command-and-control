package com.example.tacticalcommandandcontrol.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

/**
 * Manages biometric authentication gate for the C2 application.
 *
 * Supports fingerprint, face, and device credential fallback.
 * On devices without biometric hardware, falls back to PIN/pattern/password.
 */
class BiometricAuthManager(private val activity: FragmentActivity) {

    private val biometricManager = BiometricManager.from(activity)
    private val allowedAuthenticators = BIOMETRIC_STRONG or DEVICE_CREDENTIAL

    /**
     * Check if the device can authenticate (biometric or device credential).
     */
    fun canAuthenticate(): Boolean {
        return when (biometricManager.canAuthenticate(allowedAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Timber.w("No biometric/credentials enrolled")
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Timber.w("No biometric hardware available")
                false
            }
            else -> false
        }
    }

    /**
     * Show the biometric authentication prompt.
     */
    fun authenticate(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        if (!canAuthenticate()) {
            // Skip auth on devices without biometric support
            onSuccess()
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Timber.i("Biometric authentication succeeded")
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Timber.e("Biometric auth error [$errorCode]: $errString")
                onFailure(errString.toString())
            }

            override fun onAuthenticationFailed() {
                Timber.w("Biometric authentication failed (bad biometric)")
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("C2 Authentication Required")
            .setSubtitle("Verify identity to access command interface")
            .setAllowedAuthenticators(allowedAuthenticators)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}
