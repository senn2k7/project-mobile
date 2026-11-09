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

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val btnBack = findViewById<ImageView>(R.id.btnBackForgot)
        val tilEmail = findViewById<TextInputLayout>(R.id.tilEmailForgot)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailForgot)
        val btnReset = findViewById<Button>(R.id.btnResetPassword)
        val tvBackToLogin = findViewById<TextView>(R.id.tvBackToLogin)

        btnBack.setOnClickListener {
            finish()
        }

        tvBackToLogin.setOnClickListener {
            finish()
        }

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            tilEmail.error = null

            when {
                email.isEmpty() -> {
                    tilEmail.error = "Masukkan email terlebih dahulu"
                    return@setOnClickListener
                }

                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    tilEmail.error = "Format email tidak valid"
                    return@setOnClickListener
                }
            }

            btnReset.isEnabled = false

            FirebaseAuthManager.sendPasswordReset(
                email = email,
                onSuccess = {
                    Toast.makeText(
                        this,
                        "Instruksi pemulihan password berhasil dikirim ke email kamu.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                },
                onFailure = { error ->
                    btnReset.isEnabled = true

                    Toast.makeText(
                        this,
                        "Gagal mengirim email pemulihan: $error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }
}
