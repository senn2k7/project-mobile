package com.example.projectmobile

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmobile.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnBack = findViewById<ImageView>(R.id.btnBackRegister)
        val tilName = findViewById<TextInputLayout>(R.id.tilName)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmailRegister)
        val tilPassword = findViewById<TextInputLayout>(R.id.tilPasswordRegister)
        val tilConfirm = findViewById<TextInputLayout>(R.id.tilConfirmRegister)

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailRegister)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordRegister)
        val etConfirm = findViewById<TextInputEditText>(R.id.etConfirmPasswordRegister)

        val btnRegister = findViewById<Button>(R.id.btnRegisterAction)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Menyimpan akun yang sudah berhasil dibuat,
        // agar penyimpanan profil dapat dicoba ulang.
        var createdUser: FirebaseUser? = null

        btnBack.setOnClickListener {
            finish()
        }

        tvLoginLink.setOnClickListener {
            finish()
        }

        fun saveProfile(user: FirebaseUser, nama: String, email: String) {
            btnRegister.isEnabled = false
            btnRegister.text = "Menyimpan profil..."

            FirebaseManager.saveUserProfile(
                uid = user.uid,
                nama = nama,
                email = email,
                onSuccess = {
                    FirebaseAuthManager.signOut()

                    Toast.makeText(
                        this,
                        "Registrasi berhasil! Silakan masuk.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                },
                onFailure = { error ->
                    btnRegister.isEnabled = true
                    btnRegister.text = "Daftar"

                    Toast.makeText(
                        this,
                        "Akun berhasil dibuat, tetapi profil gagal disimpan: $error. Tekan Daftar lagi untuk mencoba menyimpan profil.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()

            tilName.error = null
            tilEmail.error = null
            tilPassword.error = null
            tilConfirm.error = null

            var valid = true

            if (name.isEmpty()) {
                tilName.error = "Nama lengkap harus diisi"
                valid = false
            }

            if (email.isEmpty()) {
                tilEmail.error = "Email harus diisi"
                valid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Format email tidak valid"
                valid = false
            }

            if (password.length < 6) {
                tilPassword.error = "Password minimal 6 karakter"
                valid = false
            }

            if (confirm != password) {
                tilConfirm.error = "Konfirmasi password tidak sama"
                valid = false
            }

            if (!valid) return@setOnClickListener

            // Jika akun sudah dibuat sebelumnya, ulangi penyimpanan profil.
            val existingUser = createdUser

            if (existingUser != null) {
                saveProfile(existingUser, name, email)
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Membuat akun..."

            FirebaseAuthManager.createAccount(
                email = email,
                password = password,
                onSuccess = { user ->
                    createdUser = user
                    saveProfile(user, name, email)
                },
                onFailure = { error ->
                    btnRegister.isEnabled = true
                    btnRegister.text = "Daftar"

                    Toast.makeText(
                        this,
                        "Registrasi gagal: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}
