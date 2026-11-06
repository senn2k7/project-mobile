package com.example.snapchatsphlasscreen

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object FirebaseManager {

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun testConnection(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val data = mapOf(
            "nama" to "TemanStudi",
            "status" to "Koneksi Firebase berhasil",
            "pengujian" to true,
            "updated_at" to System.currentTimeMillis()
        )

        db.collection("system")
            .document("connection_test")
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(error.message ?: "Koneksi Firebase gagal")
            }
    }
}
