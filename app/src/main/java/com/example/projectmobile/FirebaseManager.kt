package com.example.projectmobile

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FirebaseManager {

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun saveUserProfile(
        uid: String,
        nama: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (uid.isBlank() || nama.isBlank() || email.isBlank()) {
            onFailure("UID, nama, dan email harus diisi.")
            return
        }

        val currentUid = FirebaseAuthManager.currentUser?.uid

        if (currentUid != uid) {
            onFailure("UID pengguna tidak sesuai dengan sesi Firebase.")
            return
        }

        val profile = hashMapOf(
            "uid" to uid,
            "nama" to nama.trim(),
            "email" to email.trim(),
            "created_at" to FieldValue.serverTimestamp(),
            "updated_at" to FieldValue.serverTimestamp(),
            "profil_lengkap" to false
        )

        db.collection("users")
            .document(uid)
            .set(profile)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(
                    error.message ?: "Gagal menyimpan profil pengguna."
                )
            }
    }

    fun ensureUserProfile(
        scope: CoroutineScope,
        uid: String,
        nama: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (uid.isBlank() || email.isBlank()) {
            onFailure("UID dan email harus diisi.")
            return
        }

        val currentUid = FirebaseAuthManager.currentUser?.uid

        if (currentUid != uid) {
            onFailure("UID pengguna tidak sesuai dengan sesi Firebase.")
            return
        }

        val userRef = db.collection("users").document(uid)

        scope.launch {
            try {
                GoogleLoginRecovery.execute(
                    operation = {
                        suspendCancellableCoroutine { continuation ->
                            db.runTransaction { transaction ->
                                val snapshot = transaction.get(userRef)

                                if (!snapshot.exists()) {
                                    val profile = hashMapOf(
                                        "uid" to uid,
                                        "nama" to nama.trim(),
                                        "email" to email.trim(),
                                        "created_at" to FieldValue.serverTimestamp(),
                                        "updated_at" to FieldValue.serverTimestamp(),
                                        "profil_lengkap" to false
                                    )

                                    transaction.set(userRef, profile)
                                }

                                null
                            }.addOnSuccessListener {
                                continuation.resume(Unit)
                            }.addOnFailureListener { error ->
                                continuation.resumeWithException(error)
                            }
                        }
                    },
                    shouldRetry = { error ->
                        GoogleLoginRecovery.isRetryableNetworkError(error) ||
                        (
                            error is com.google.firebase.firestore.FirebaseFirestoreException &&
                            (
                                error.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE ||
                                error.code == com.google.firebase.firestore.FirebaseFirestoreException.Code.DEADLINE_EXCEEDED
                            )
                        )
                    }
                )

                onSuccess()
            } catch (error: Exception) {
                onFailure(error.message ?: "Gagal memeriksa profil pengguna.")
            }
        }
    }
}
