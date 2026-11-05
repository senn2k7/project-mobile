package com.example.snapchatsphlasscreen

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class UserSession(
    val userId: Int,
    val nama: String,
    val email: String,
    val token: String
)

object SessionManager {

    private const val PREF_NAME = "temanstudi_secure_session"
    private const val KEY_ALIAS = "temanstudi_session_key"
    private const val DATA_KEY = "encrypted_session"

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    fun saveSession(
        context: Context,
        userId: Int,
        nama: String,
        email: String,
        token: String
    ) {
        val sessionJson = JSONObject().apply {
            put("user_id", userId)
            put("nama", nama)
            put("email", email)
            put("token", token)
        }.toString()

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

        val encryptedBytes = cipher.doFinal(
            sessionJson.toByteArray(Charsets.UTF_8)
        )

        val iv = Base64.encodeToString(
            cipher.iv,
            Base64.NO_WRAP
        )

        val encryptedData = Base64.encodeToString(
            encryptedBytes,
            Base64.NO_WRAP
        )

        val storedValue = "$iv:$encryptedData"

        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(DATA_KEY, storedValue)
            .apply()
    }

    fun getSession(context: Context): UserSession? {
        return try {
            val preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
            )

            val storedValue = preferences.getString(DATA_KEY, null)
                ?: return null

            val parts = storedValue.split(":", limit = 2)

            if (parts.size != 2) {
                clearSession(context)
                return null
            }

            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val encryptedData = Base64.decode(parts[1], Base64.NO_WRAP)

            val cipher = Cipher.getInstance(TRANSFORMATION)

            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateSecretKey(),
                GCMParameterSpec(128, iv)
            )

            val decryptedBytes = cipher.doFinal(encryptedData)
            val sessionJson = JSONObject(
                String(decryptedBytes, Charsets.UTF_8)
            )

            UserSession(
                userId = sessionJson.getInt("user_id"),
                nama = sessionJson.getString("nama"),
                email = sessionJson.getString("email"),
                token = sessionJson.getString("token")
            )
        } catch (e: Exception) {
            clearSession(context)
            null
        }
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .remove(DATA_KEY)
            .apply()
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)

        val existingKey = keyStore.getKey(KEY_ALIAS, null)

        if (existingKey is SecretKey) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }
}
