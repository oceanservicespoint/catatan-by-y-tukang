package com.indonesiaemas.note.ui


import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.indonesiaemas.note.MainActivity

/**
 * Status ketersediaan biometrik pada perangkat.
 */
sealed class BiometricAvailability {
    object Available : BiometricAvailability()
    object NoHardware : BiometricAvailability()
    object NotEnrolled : BiometricAvailability()  // Hardware ada tapi belum ada sidik jari terdaftar
    object Unavailable : BiometricAvailability()
}

/**
 * Hasil autentikasi biometrik.
 */
sealed class BiometricResult {
    object Success : BiometricResult()
    data class Error(val errorCode: Int, val message: String) : BiometricResult()
    object Failed : BiometricResult()   // Sidik jari tidak cocok
    object Cancelled : BiometricResult()
}

object BiometricHelper {

    /**
     * Cek apakah perangkat mendukung dan sudah setup biometrik.
     */
    fun checkAvailability(context: Context): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.Available
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.NoHardware
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NotEnrolled
            else -> BiometricAvailability.Unavailable
        }
    }

    /**
     * Tampilkan dialog autentikasi biometrik.
     *
     * @param activity      FragmentActivity (MainActivity)
     * @param title         Judul dialog
     * @param subtitle      Subjudul dialog
     * @param description   Deskripsi tambahan (opsional)
     * @param negativeText  Teks tombol batal
     * @param onResult      Callback hasil autentikasi
     */
    fun authenticate(
        activity: MainActivity,
        title: String = "Verifikasi Identitas",
        subtitle: String = "Gunakan sidik jari untuk membuka aplikasi",
        description: String = "",
        negativeText: String = "Batal",
        onResult: (BiometricResult) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onResult(BiometricResult.Success)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_CANCELED -> onResult(BiometricResult.Cancelled)
                    else -> onResult(BiometricResult.Error(errorCode, errString.toString()))
                }
            }

            override fun onAuthenticationFailed() {
                // Dipanggil tiap sidik jari tidak cocok, tapi dialog tetap terbuka
                // Tidak perlu dismiss — sistem akan otomatis handle retry
                onResult(BiometricResult.Failed)
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .apply { if (description.isNotBlank()) setDescription(description) }
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}