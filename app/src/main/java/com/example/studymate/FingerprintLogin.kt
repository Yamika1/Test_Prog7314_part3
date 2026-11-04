package com.example.studymate


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class FingerprintLogin : AppCompatActivity() {
    private lateinit var loginFingerprintButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fingerprint_login)

        loginFingerprintButton = findViewById(R.id.loginFingerprintButton)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { view, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        loginFingerprintButton.setOnClickListener {
            authenticateUser()
        }
    }

    private fun authenticateUser() {

        val biometricManager = BiometricManager.from(this)

        val result = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )

        if (result != BiometricManager.BIOMETRIC_SUCCESS) {
            val message = when (result) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    "This device does not support biometric authentication."

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    "Biometric hardware is currently unavailable."

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    "Please enrol a fingerprint in device settings."

                else ->
                    "Biometric authentication is unavailable."
            }

            showMessage(message)
            return
        }

        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    showMessage("Fingerprint matched. Login successful!")

                    startActivity(
                        Intent(
                            this@FingerprintLogin,
                            HomeActivity::class.java
                        )
                    )

                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    showMessage("Fingerprint not matched. Try again.")
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    showMessage(errString.toString())
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Use your fingerprint to access the app")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}