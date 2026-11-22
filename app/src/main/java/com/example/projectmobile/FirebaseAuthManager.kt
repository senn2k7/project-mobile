package com.example.projectmobile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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

        kotlinx.coroutines.MainScope().launch {
            try {
                val user = GoogleLoginRecovery.execute(
                    operation = {
                        suspendCancellableCoroutine { continuation ->
                            auth.signInWithCredential(credential)
                                .addOnSuccessListener { result ->
                                    val firebaseUser = result.user

                                    if (firebaseUser != null) {
                                        continuation.resume(firebaseUser)
                                    } else {
                                        continuation.resumeWithException(
                                            IllegalStateException(
                                                "Data pengguna Google tidak ditemukan."
                                            )
                                        )
                                    }
                                }
                                .addOnFailureListener { error ->
                                    continuation.resumeWithException(error)
                                }
                        }
                    },
                    shouldRetry = GoogleLoginRecovery::isRetryableNetworkError
                )

                onSuccess(user)
            } catch (error: Exception) {
                onFailure(error.message ?: "Login Google gagal.")
            }
        }
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
