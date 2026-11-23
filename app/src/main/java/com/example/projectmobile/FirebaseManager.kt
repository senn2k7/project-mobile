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

    fun savePengajarMandiriProfile(
        uid: String,
        nama: String,
        email: String,
        nomorTelepon: String,
        bidangKeahlian: String,
        namaInstansi: String,
        pengalamanMengajar: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (uid.isBlank() || nama.isBlank() || email.isBlank() || bidangKeahlian.isBlank()) {
            onFailure("Nama, email, dan bidang keahlian wajib diisi.")
            return
        }

        if (FirebaseAuthManager.currentUser?.uid != uid) {
            onFailure("UID pengguna tidak sesuai dengan sesi Firebase.")
            return
        }

        val userRef = db.collection("users").document(uid)
        val profileRef = db.collection("pengajar_mandiri").document(uid)

        val profileData = hashMapOf<String, Any>(
            "uid" to uid,
            "nama" to nama.trim(),
            "email" to email.trim(),
            "nomor_telepon" to nomorTelepon.trim(),
            "bidang_keahlian" to bidangKeahlian.trim(),
            "nama_instansi" to namaInstansi.trim(),
            "pengalaman_mengajar" to pengalamanMengajar.trim(),
            "role" to "pengajar",
            "mode" to "mandiri",
            "profil_lengkap" to true,
            "updated_at" to FieldValue.serverTimestamp()
        )

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val profileSnapshot = transaction.get(profileRef)

            if (!userSnapshot.exists()) {
                transaction.set(
                    userRef,
                    hashMapOf(
                        "uid" to uid,
                        "email" to email.trim(),
                        "role" to "pengajar",
                        "mode" to "mandiri",
                        "created_at" to FieldValue.serverTimestamp()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                )
            } else {
                transaction.set(
                    userRef,
                    hashMapOf(
                        "email" to email.trim(),
                        "role" to "pengajar",
                        "mode" to "mandiri",
                        "updated_at" to FieldValue.serverTimestamp()
                    ),
                    com.google.firebase.firestore.SetOptions.merge()
                )
            }

            if (!profileSnapshot.exists()) {
                profileData["created_at"] = FieldValue.serverTimestamp()
            }

            transaction.set(
                profileRef,
                profileData,
                com.google.firebase.firestore.SetOptions.merge()
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onFailure(error.message ?: "Gagal menyimpan profil pengajar.")
        }
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
