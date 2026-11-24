package com.example.projectmobile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeStore.applySaved(this)
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            checkFirebaseSession()
        }, 3000L)
    }

    private fun checkFirebaseSession() {
        val user = FirebaseAuthManager.currentUser

        if (user == null) {
            openOnboarding()
            return
        }

        val uid = user.uid
        val email = user.email.orEmpty()
        val namaDefault = user.displayName
            ?: email.substringBefore("@")

        if (email.isBlank()) {
            openOnboarding()
            return
        }

        FirebaseManager.ensureUserProfile(
            scope = lifecycleScope,
            uid = uid,
            nama = namaDefault,
            email = email,
            onSuccess = {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val nama = document.getString("nama")
                            ?: namaDefault

                        bukaModePembelajaran(uid, nama, email)
                    }
                    .addOnFailureListener { error ->
                        android.util.Log.e(
                            "MainActivity",
                            "Gagal mengambil data dasar pengguna",
                            error
                        )
                        Toast.makeText(
                            this,
                            "Gagal memuat data akun. Periksa koneksi internet.",
                            Toast.LENGTH_LONG
                        ).show()
                        openOnboarding()
                    }
            },
            onFailure = { message ->
                Toast.makeText(
                    this,
                    "Gagal menyiapkan akun: $message",
                    Toast.LENGTH_LONG
                ).show()
                openOnboarding()
            }
        )
    }

    private fun bukaModePembelajaran(
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

    private fun openOnboarding() {
        val intent = Intent(
            this,
            OnboardingActivity::class.java
        )

        startActivity(intent)
        finish()
    }
}