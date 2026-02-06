package com.example.tacticalcommandandcontrol

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.tacticalcommandandcontrol.core.ui.theme.TacticalTheme
import com.example.tacticalcommandandcontrol.security.BiometricAuthManager
import com.example.tacticalcommandandcontrol.ui.C2App
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var biometricAuth: BiometricAuthManager
    private var isAuthenticated by mutableStateOf(false)
    private var authError by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        biometricAuth = BiometricAuthManager(this)

        setContent {
            if (isAuthenticated) {
                C2App()
            } else {
                TacticalTheme {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    ) {
                        Text(
                            text = "C2 Command Interface",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Authentication required",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        authError?.let { error ->
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                        Button(onClick = { requestAuth() }) {
                            Text("Authenticate")
                        }
                    }
                }
            }
        }

        if (savedInstanceState == null) {
            requestAuth()
        }
    }

    private fun requestAuth() {
        authError = null
        biometricAuth.authenticate(
            onSuccess = { isAuthenticated = true },
            onFailure = { error -> authError = error },
        )
    }
}
