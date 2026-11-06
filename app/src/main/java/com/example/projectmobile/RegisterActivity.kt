package com.example.snapchatsphlasscreen

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

        // Kembali ke halaman login
        btnBack.setOnClickListener { finish() }
        tvLoginLink.setOnClickListener { finish() }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val identifier = etEmail.text.toString().trim()
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

            if (identifier.isEmpty()) {
                tilEmail.error = "Email atau nomor telepon harus diisi"
                valid = false
            } else if (identifier.contains("@")) {
                if (!Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                    tilEmail.error = "Format email tidak valid"
                    valid = false
                }
            } else if (identifier.filter { it.isDigit() }.length < 9) {
                tilEmail.error = "Nomor telepon tidak valid"
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

            // TODO: hubungkan ke ApiConfig.REGISTER_URL untuk menyimpan akun ke server
            Toast.makeText(
                this,
                "Registrasi berhasil! Silakan masuk.",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }
}
