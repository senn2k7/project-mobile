package com.example.snapchatsphlasscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleLogin = findViewById<MaterialButton>(R.id.btnGoogleLogin)
        val txtForgotPassword = findViewById<TextView>(R.id.txtForgotPassword)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val cbRememberMe = findViewById<CheckBox>(R.id.cbRememberMe)

        btnBack.setOnClickListener {
            finish()
        }

        // LOGIN MANUAL
        btnLogin.setOnClickListener {
            val identifier = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val rememberMe = cbRememberMe.isChecked

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Email/nomor telepon dan password harus diisi.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Memproses..."

            val request = JSONObject().apply {
                put("identifier", identifier)
                put("password", password)
                put("remember_me", rememberMe)
            }

            ApiClient.postJson(
                ApiConfig.LOGIN_URL,
                request,
                object : ApiClient.ApiCallback {

                    override fun onSuccess(response: JSONObject) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Masuk"

                        if (response.optBoolean("success")) {
                            val data = response.optJSONObject("data")

                            if (data == null || data.optString("token").isEmpty() || data.optString("firebase_custom_token").isEmpty()) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Respons server tidak lengkap.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }

                            val userId = data.optInt("id")
                            val nama = data.optString("nama")
                            val email = data.optString("email")
                            val token = data.optString("token")
                            val firebaseCustomToken = data.optString("firebase_custom_token")

                            try {
                                SessionManager.saveSession(
                                    this@LoginActivity,
                                    userId,
                                    nama,
                                    email,
                                    token
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Sesi gagal disimpan: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                return
                            }

                            Toast.makeText(
                                this@LoginActivity,
                                "Selamat datang, $nama!",
                                Toast.LENGTH_SHORT
                            ).show()

                            completeLoginWithFirebase(userId, nama, email, firebaseCustomToken)

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                response.optString("message", "Login gagal."),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onError(message: String) {
                        btnLogin.isEnabled = true
                        btnLogin.text = "Masuk"

                        Toast.makeText(
                            this@LoginActivity,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }

        // LOGIN DENGAN GOOGLE
        btnGoogleLogin.setOnClickListener {
            startGoogleLogin(btnGoogleLogin, cbRememberMe.isChecked)
        }

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun startGoogleLogin(
        button: MaterialButton,
        rememberMe: Boolean
    ) {
        button.isEnabled = false
        button.text = "Menghubungkan Google..."

        lifecycleScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(ApiConfig.GOOGLE_WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(this@LoginActivity)

                val result = credentialManager.getCredential(
                    this@LoginActivity,
                    request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    sendGoogleTokenToServer(
                        googleCredential.idToken,
                        rememberMe,
                        button
                    )
                } else {
                    button.isEnabled = true
                    button.text = "Masuk dengan Google"

                    Toast.makeText(
                        this@LoginActivity,
                        "Jenis kredensial Google tidak dikenali.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: GetCredentialException) {
                button.isEnabled = true
                button.text = "Masuk dengan Google"

                Toast.makeText(
                    this@LoginActivity,
                    "Login Google dibatalkan atau gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                button.isEnabled = true
                button.text = "Masuk dengan Google"

                Toast.makeText(
                    this@LoginActivity,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun sendGoogleTokenToServer(
        idToken: String,
        rememberMe: Boolean,
        button: MaterialButton
    ) {
        button.text = "Memverifikasi akun..."

        val request = JSONObject().apply {
            put("id_token", idToken)
            put("remember_me", rememberMe)
        }

        ApiClient.postJson(
            ApiConfig.GOOGLE_LOGIN_URL,
            request,
            object : ApiClient.ApiCallback {

                override fun onSuccess(response: JSONObject) {
                    button.isEnabled = true
                    button.text = "Masuk dengan Google"

                    if (!response.optBoolean("success")) {
                        Toast.makeText(
                            this@LoginActivity,
                            response.optString(
                                "message",
                                "Login Google gagal."
                            ),
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    val data = response.optJSONObject("data")

                    if (data == null || data.optString("token").isEmpty() || data.optString("firebase_custom_token").isEmpty()) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Respons server tidak lengkap.",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    val userId = data.optInt("id")
                    val nama = data.optString("nama")
                    val email = data.optString("email")
                    val token = data.optString("token")
                    val firebaseCustomToken = data.optString("firebase_custom_token")

                    try {
                        SessionManager.saveSession(
                            this@LoginActivity,
                            userId,
                            nama,
                            email,
                            token
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Sesi gagal disimpan: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    Toast.makeText(
                        this@LoginActivity,
                        "Login Google berhasil. Selamat datang, $nama!",
                        Toast.LENGTH_SHORT
                    ).show()

                    completeLoginWithFirebase(userId, nama, email, firebaseCustomToken)
                }

                override fun onError(message: String) {
                    button.isEnabled = true
                    button.text = "Masuk dengan Google"

                    Toast.makeText(
                        this@LoginActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    private fun completeLoginWithFirebase(
        userId: Int,
        nama: String,
        email: String,
        firebaseCustomToken: String
    ) {
        FirebaseAuthManager.signInWithCustomToken(
            customToken = firebaseCustomToken,
            onSuccess = {
                Toast.makeText(
                    this@LoginActivity,
                    "Firebase berhasil terhubung.",
                    Toast.LENGTH_SHORT
                ).show()

                openLearningMode(userId, nama, email)
            },
            onFailure = { message ->
                FirebaseAuthManager.signOut()
                SessionManager.clearSession(this@LoginActivity)

                Toast.makeText(
                    this@LoginActivity,
                    "Autentikasi Firebase gagal: $message",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
    private fun openLearningMode(
        userId: Int,
        nama: String,
        email: String
    ) {
        val intent = Intent(
            this,
            ModePembelajaranActivity::class.java
        ).apply {
            putExtra("USER_ID", userId)
            putExtra("USER_NAME", nama)
            putExtra("USER_EMAIL", email)
        }

        startActivity(intent)
        finish()
    }
}
