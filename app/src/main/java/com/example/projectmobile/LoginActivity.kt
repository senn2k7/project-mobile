package com.example.projectmobile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.projectmobile.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

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

        btnBack.setOnClickListener {
            finish()
        }

        // LOGIN EMAIL DAN PASSWORD
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Email dan password harus diisi.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this,
                    "Format email tidak valid.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            btnLogin.text = "Memproses..."

            FirebaseAuthManager.signInWithEmail(
                email = email,
                password = password,
                onSuccess = { user ->
                    handleAuthenticatedUser(user, btnLogin)
                },
                onFailure = { message ->
                    btnLogin.isEnabled = true
                    btnLogin.text = "Masuk"

                    Toast.makeText(
                        this,
                        "Login gagal: $message",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }

        // LOGIN GOOGLE
        btnGoogleLogin.setOnClickListener {
            startGoogleLogin(btnGoogleLogin)
        }

        txtForgotPassword.setOnClickListener {
            startActivity(
                Intent(this, ForgotPasswordActivity::class.java)
            )
        }

        txtRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }
    }

    private fun startGoogleLogin(button: MaterialButton) {
        button.isEnabled = false
        button.text = "Menghubungkan Google..."

        lifecycleScope.launch {
            try {
                val googleOption = GetSignInWithGoogleOption.Builder(
                    getString(R.string.default_web_client_id)
                ).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                val credentialManager =
                    CredentialManager.create(this@LoginActivity)

                val result = credentialManager.getCredential(
                    this@LoginActivity,
                    request
                )

                val credential = result.credential

                if (
                    credential is CustomCredential &&
                    credential.type ==
                    GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    button.text = "Memverifikasi akun..."

                    FirebaseAuthManager.signInWithGoogle(
                        idToken = googleCredential.idToken,
                        onSuccess = { user ->
                            handleAuthenticatedUser(user, button)
                        },
                        onFailure = { message ->
                            button.isEnabled = true
                            button.text = "Masuk dengan Google"

                            Toast.makeText(
                                this@LoginActivity,
                                "Login Google gagal: $message",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                } else {
                    throw IllegalStateException(
                        "Kredensial Google tidak dikenali."
                    )
                }

            } catch (e: GetCredentialException) {
                button.isEnabled = true
                button.text = "Masuk dengan Google"

                Toast.makeText(
                    this@LoginActivity,
                    "Gagal memilih akun Google: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                button.isEnabled = true
                button.text = "Masuk dengan Google"

                Toast.makeText(
                    this@LoginActivity,
                    "Login Google gagal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun handleAuthenticatedUser(
        user: FirebaseUser,
        button: Button
    ) {
        val uid = user.uid
        val email = user.email.orEmpty()

        val namaDefault = user.displayName
            ?: email.substringBefore("@")

        if (email.isBlank()) {
            button.isEnabled = true
            button.text = "Masuk"

            Toast.makeText(
                this,
                "Email akun tidak ditemukan.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        button.text = "Memeriksa profil..."

        FirebaseManager.ensureUserProfile(
            uid = uid,
            nama = namaDefault,
            email = email,
            onSuccess = {
                loadUserProfile(uid, namaDefault, email, button)
            },
            onFailure = { message ->
                button.isEnabled = true
                button.text = "Masuk"

                Toast.makeText(
                    this,
                    "Gagal memeriksa profil: $message",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun loadUserProfile(
        uid: String,
        namaDefault: String,
        email: String,
        button: Button
    ) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val nama = document.getString("nama")
                    ?: namaDefault

                Toast.makeText(
                    this,
                    "Selamat datang, $nama!",
                    Toast.LENGTH_SHORT
                ).show()

                openLearningMode(uid, nama, email)
            }
            .addOnFailureListener { error ->
                button.isEnabled = true
                button.text = "Masuk"

                Toast.makeText(
                    this,
                    "Gagal mengambil profil: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun openLearningMode(
        uid: String,
        nama: String,
        email: String
    ) {
        val intent = Intent(
            this,
            ModePembelajaranActivity::class.java
        ).apply {
            putExtra("USER_ID", uid)
            putExtra("USER_NAME", nama)
            putExtra("USER_EMAIL", email)
        }

        startActivity(intent)
        finish()
    }
}
