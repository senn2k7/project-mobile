package com.example.snapchatsphlasscreen

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

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

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Berhasil masuk!", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoogleLogin.setOnClickListener {
            Toast.makeText(this, "Masuk dengan Google...", Toast.LENGTH_SHORT).show()
        }

        txtForgotPassword.setOnClickListener {
            Toast.makeText(this, "Fitur lupa password", Toast.LENGTH_SHORT).show()
        }

        txtRegister.setOnClickListener {
            Toast.makeText(this, "Fitur pendaftaran akun", Toast.LENGTH_SHORT).show()
        }
    }
}
