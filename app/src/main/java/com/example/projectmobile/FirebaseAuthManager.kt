package com.example.projectmobile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

object FirebaseAuthManager {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun createAccount(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onFailure("Email dan password harus diisi.")
            return
        }

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure("Akun berhasil diproses, tetapi data pengguna tidak ditemukan.")
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Registrasi gagal.")
            }
    }

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            onFailure("Email dan password harus diisi.")
            return
        }

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure("Data pengguna tidak ditemukan.")
                }
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Login gagal.")
            }
    }

    fun signInWithGoogle(
        idToken: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (idToken.isBlank()) {
            onFailure("Google ID Token kosong.")
            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val maxRetries = 2

        fun attemptSignIn(retryCount: Int) {
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure("Data pengguna Google tidak ditemukan.")
                    }
                }
                .addOnFailureListener { error ->
                    val retryable =
                        GoogleLoginRecovery.isRetryableNetworkError(error)

                    if (retryable && retryCount < maxRetries) {
                        val delayMs = if (retryCount == 0) 1_000L else 2_000L

                        handler.postDelayed({
                            attemptSignIn(retryCount + 1)
                        }, delayMs)
                    } else {
                        onFailure(error.message ?: "Login Google gagal.")
                    }
                }
        }

        attemptSignIn(0)
    }

    fun sendPasswordReset(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (email.isBlank()) {
            onFailure("Email harus diisi.")
            return
        }

        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Gagal mengirim email pemulihan.")
            }
    }

    fun signOut() {
        auth.signOut()
    }
}
