package com.example.studymate

import android.content.Context
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class BiometricSetting : AppCompatActivity() {

    private lateinit var switch2: Switch

    private val switchListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFingerprint()
            } else {
                disableFingerprint()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_biometric_setting)

        switch2 = findViewById(R.id.switch2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        switch2.isChecked = isFingerprintEnabled(this)
        switch2.setOnCheckedChangeListener(switchListener)
    }

    private fun enableFingerprint() {
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
                    "Please enrol a fingerprint in device settings first."

                else ->
                    "Biometric authentication is unavailable."
            }

            showMessage(message)
            setSwitchSilently(false)
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
                    saveFingerprintEnabled(true)
                    showMessage("Fingerprint login enabled.")
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
                    setSwitchSilently(false)
                    showMessage(errString.toString())
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enable Fingerprint Login")
            .setSubtitle("Confirm your fingerprint to turn this on")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun disableFingerprint() {
        saveFingerprintEnabled(false)
        showMessage("Fingerprint login disabled.")
    }

    private fun setSwitchSilently(checked: Boolean) {
        switch2.setOnCheckedChangeListener(null)
        switch2.isChecked = checked
        switch2.setOnCheckedChangeListener(switchListener)
    }

    private fun saveFingerprintEnabled(enabled: Boolean) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_FINGERPRINT_ENABLED, enabled)
            .apply()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val PREFS_NAME = "Biometric"
        private const val KEY_FINGERPRINT_ENABLED = "fingerprint_enabled"

        fun isFingerprintEnabled(context: Context): Boolean {
            return context
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_FINGERPRINT_ENABLED, false)
        }
    }
}