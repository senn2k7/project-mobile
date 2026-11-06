package com.example.snapchatsphlasscreen

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthManager {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun signOut() {
        auth.signOut()
    }

    fun signInWithCustomToken(
        customToken: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (customToken.isBlank()) {
            onFailure("Firebase Custom Token kosong.")
            return
        }

        auth.signInWithCustomToken(customToken)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(
                    error.message ?: "Autentikasi Firebase gagal."
                )
            }
    }
}