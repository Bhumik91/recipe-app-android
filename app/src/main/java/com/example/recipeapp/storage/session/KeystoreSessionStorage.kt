package com.example.recipeapp.storage.session

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.example.recipeapp.data.auth.LoginResponse
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreSessionStorage(context: Context) : SessionStorage {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

    private val secretKey: SecretKey
        get() = (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
            ?: generateSecretKey()

    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    private fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, secretKey) }
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = cipher.iv + cipherBytes
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(encoded: String): String? = runCatching {
        val combined = Base64.decode(encoded, Base64.NO_WRAP)
        val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
        val cipherBytes = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))
        }
        String(cipher.doFinal(cipherBytes), Charsets.UTF_8)
    }.getOrNull()

    private fun putEncrypted(key: String, value: String) {
        prefs.edit { putString(key, encrypt(value)) }
    }

    private fun getDecrypted(key: String): String? = prefs.getString(key, null)?.let { decrypt(it) }

    override var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit { putBoolean(KEY_IS_LOGGED_IN, value) }

    override fun saveAuthSession(response: LoginResponse) {
        prefs.edit { putInt(KEY_ID, response.id) }
        putEncrypted(KEY_NAME, response.name)
        putEncrypted(KEY_ACCESS_TOKEN, response.accessToken)
        putEncrypted(KEY_REFRESH_TOKEN, response.refreshToken)
        isLoggedIn = true
    }

    override fun getUserId(): Int = prefs.getInt(KEY_ID, 0)
    override fun getUserName(): String = getDecrypted(KEY_NAME) ?: ""

    override fun getAccessToken(): String? = getDecrypted(KEY_ACCESS_TOKEN)
    override fun getRefreshToken(): String? = getDecrypted(KEY_REFRESH_TOKEN)

    override fun updateTokens(accessToken: String, refreshToken: String) {
        putEncrypted(KEY_ACCESS_TOKEN, accessToken)
        putEncrypted(KEY_REFRESH_TOKEN, refreshToken)
    }

    override fun clearSession() {
        prefs.edit { clear() }
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "RecipeAppSessionKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH_BITS = 128

        private const val PREF_NAME = "RecipeAppSessionSecure"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_ID = "userId"
        private const val KEY_NAME = "userName"
        private const val KEY_ACCESS_TOKEN = "accessToken"
        private const val KEY_REFRESH_TOKEN = "refreshToken"
    }
}
