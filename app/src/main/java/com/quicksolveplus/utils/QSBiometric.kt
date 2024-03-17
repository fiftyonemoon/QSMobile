package com.quicksolveplus.utils

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.quicksolveplus.qspmobile.R
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * 21/03/23.
 *
 * @author hardkgosai.
 */
class QSBiometric(private val activity: FragmentActivity) {

    private var biometricManager: BiometricManager = BiometricManager.from(activity)
    private val keystoreAlias = UUID.randomUUID().toString()

    private fun getBiometricCapability(): Int {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK)
    }

    private fun hasBiometricCapability(): Boolean {
        return when (getBiometricCapability()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                true
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED, BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                showBiometricNotSupported()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                showNoneEnrollDialog()
                false
            }
            else -> {
                toast(activity, "It seems like device's biometric doesn't work")
                false
            }
        }
    }

    private fun showBiometricNotSupported() {
        showAlertDialog(
            activity,
            message = activity.getString(R.string.str_fingerprint_not_supported_device),
            positiveButtonText = activity.getString(R.string.str_ok)
        ) {
            //do nothing
        }
    }

    private fun showNoneEnrollDialog() {
        showAlertDialog(
            activity,
            title = activity.getString(R.string.str_warning),
            message = activity.getString(R.string.str_fingerprint_not_configured_device),
            positiveButtonText = activity.getString(R.string.str_setting),
            negativeButtonText = activity.getString(R.string.str_cancel)
        ) { isPositive ->
            if (isPositive) {
                navigateToSettings()
            }
        }
    }

    private fun setBiometricPromptInfo(
        title: String, subtitle: String, description: String
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().setTitle(title).setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText(activity.getString(R.string.str_cancel)).build()
    }

    fun show(
        title: String = "",
        subtitle: String = "",
        description: String = "",
        authenticationCallback: BiometricPrompt.AuthenticationCallback
    ) {
        if (hasBiometricCapability()) {
            val executor = ContextCompat.getMainExecutor(activity)
            val biometricPromptInfo = setBiometricPromptInfo(title, subtitle, description)
            val biometricPrompt = BiometricPrompt(activity, executor, authenticationCallback)

            //generate secret key first
            generateSecretKey()

            try {
                //get that generated secret key
                val cipher = getCipher()
                val secretKey = getSecretKey()
                cipher?.run {
                    init(Cipher.ENCRYPT_MODE, secretKey)
                    biometricPrompt.authenticate(
                        biometricPromptInfo, BiometricPrompt.CryptoObject(cipher)
                    )
                }
            } catch (e: Exception) {
                toast(activity, "Something goes wrong with device's Biometric")
            }
        }
    }

    private fun getKeyGenParameterSpec(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            keystoreAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true)
            .setDigests(KeyProperties.DIGEST_SHA256).build()
    }

    private fun generateSecretKey() {
        try {
            val keyGenParameterSpec = getKeyGenParameterSpec()
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }

    private fun getSecretKey(): SecretKey? {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            return keyStore.getKey(keystoreAlias, null) as SecretKey
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getCipher(): Cipher? {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchPaddingException) {
            e.printStackTrace()
        }
        return null
    }

    private fun navigateToSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
            enrollIntent.putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
            activity.startActivity(enrollIntent)
        } else {
            val intent = Intent(Settings.ACTION_SETTINGS)
            activity.startActivity(intent)
        }
    }
}