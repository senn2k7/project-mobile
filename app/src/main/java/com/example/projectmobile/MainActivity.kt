package com.example.snapchatsphlasscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({

            val session = SessionManager.getSession(this)

            val intent = if (session != null) {
                Intent(this, ModePembelajaranActivity::class.java).apply {
                    putExtra("USER_ID", session.userId)
                    putExtra("USER_NAME", session.nama)
                    putExtra("USER_EMAIL", session.email)
                }
            } else {
                Intent(this, OnboardingActivity::class.java)
            }

            startActivity(intent)
            finish()

        }, 3000L)
    }
}
