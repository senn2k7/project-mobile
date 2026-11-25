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
        fotoProfilUrl: String? = null,
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

        if (!fotoProfilUrl.isNullOrBlank()) {
            profileData["foto_profil_url"] = fotoProfilUrl
        }

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val profileSnapshot = transaction.get(profileRef)

            val userData = hashMapOf<String, Any>(
                "uid" to uid,
                "nama" to nama.trim(),
                "email" to email.trim(),
                "role" to "pengajar",
                "mode" to "mandiri",
                "updated_at" to FieldValue.serverTimestamp()
            )

            if (!userSnapshot.exists()) {
                userData["created_at"] = FieldValue.serverTimestamp()
            }

            if (!profileSnapshot.exists()) {
                profileData["created_at"] = FieldValue.serverTimestamp()
            }

            transaction.set(
                userRef,
                userData,
                com.google.firebase.firestore.SetOptions.merge()
            )

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



    fun saveRoleModeProfile(
        uid: String,
        collection: String,
        role: String,
        mode: String,
        data: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val expectedCollection = when {
            role == "pengajar" && mode == "institusi" -> "pengajar_institusi"
            role == "pelajar" && mode == "mandiri" -> "pelajar_mandiri"
            role == "pelajar" && mode == "institusi" -> "pelajar_institusi"
            else -> null
        }

        if (expectedCollection == null || collection != expectedCollection) {
            onFailure("Koleksi profil tidak sesuai dengan peran dan mode pengguna.")
            return
        }

        if (uid.isBlank() || FirebaseAuthManager.currentUser?.uid != uid) {
            onFailure("UID pengguna tidak sesuai dengan sesi Firebase.")
            return
        }

        val nama = data["nama"] as? String ?: ""
        val email = data["email"] as? String ?: ""

        if (nama.isBlank() || email.isBlank()) {
            onFailure("Nama dan email wajib tersedia untuk menyimpan profil.")
            return
        }

        val userRef = db.collection("users").document(uid)
        val profileRef = db.collection(collection).document(uid)

        val profileData = HashMap<String, Any>(data).apply {
            put("uid", uid)
            put("role", role)
            put("mode", mode)
            put("profil_lengkap", true)
            put("updated_at", FieldValue.serverTimestamp())
        }

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val profileSnapshot = transaction.get(profileRef)

            val userData = hashMapOf<String, Any>(
                "uid" to uid,
                "nama" to nama.trim(),
                "email" to email.trim(),
                "role" to role,
                "mode" to mode,
                "updated_at" to FieldValue.serverTimestamp()
            )

            if (!userSnapshot.exists()) {
                userData["created_at"] = FieldValue.serverTimestamp()
            }

            if (!profileSnapshot.exists()) {
                profileData["created_at"] = FieldValue.serverTimestamp()
            }

            transaction.set(
                userRef,
                userData,
                com.google.firebase.firestore.SetOptions.merge()
            )

            transaction.set(
                profileRef,
                profileData,
                com.google.firebase.firestore.SetOptions.merge()
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onFailure(error.message ?: "Gagal menyimpan profil.")
        }
    }

    fun saveProfileEdits(
        uid: String,
        collection: String,
        role: String,
        mode: String,
        data: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val expectedCollection = when {
            role == "pengajar" && mode == "mandiri" -> "pengajar_mandiri"
            role == "pengajar" && mode == "institusi" -> "pengajar_institusi"
            role == "pelajar" && mode == "mandiri" -> "pelajar_mandiri"
            role == "pelajar" && mode == "institusi" -> "pelajar_institusi"
            else -> null
        }

        if (expectedCollection == null || collection != expectedCollection) {
            onFailure("Koleksi profil tidak sesuai dengan peran dan mode pengguna.")
            return
        }

        if (uid.isBlank() || FirebaseAuthManager.currentUser?.uid != uid) {
            onFailure("UID pengguna tidak sesuai dengan sesi Firebase.")
            return
        }

        val nama = data["nama"] as? String ?: ""
        val email = data["email"] as? String ?: ""

        if (nama.isBlank() || email.isBlank()) {
            onFailure("Nama dan email wajib diisi.")
            return
        }

        val userRef = db.collection("users").document(uid)
        val profileRef = db.collection(collection).document(uid)

        val profileData = HashMap<String, Any>(data).apply {
            put("uid", uid)
            put("role", role)
            put("mode", mode)
            put("updated_at", FieldValue.serverTimestamp())
        }

        db.runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            transaction.get(profileRef)

            val userData = hashMapOf<String, Any>(
                "uid" to uid,
                "nama" to nama.trim(),
                "email" to email.trim(),
                "role" to role,
                "mode" to mode,
                "updated_at" to FieldValue.serverTimestamp()
            )

            if (!userSnapshot.exists()) {
                userData["created_at"] = FieldValue.serverTimestamp()
            }

            transaction.set(
                userRef,
                userData,
                com.google.firebase.firestore.SetOptions.merge()
            )

            transaction.set(
                profileRef,
                profileData,
                com.google.firebase.firestore.SetOptions.merge()
            )
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onFailure(error.message ?: "Gagal memperbarui profil.")
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


    /**
     * Menyimpan kelas mandiri pengajar ke Firestore.
     * Document ID menggunakan kode kelas.
     */
    fun saveTeacherClass(
        teacherClass: TeacherClass,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = FirebaseAuthManager.currentUser?.uid

        if (uid.isNullOrBlank()) {
            onFailure("Silakan login kembali untuk membuat kelas.")
            return
        }

        if (teacherClass.ownerUid != uid) {
            onFailure("UID pemilik kelas tidak sesuai dengan sesi Firebase.")
            return
        }

        if (
            teacherClass.code.isBlank() ||
            teacherClass.name.isBlank() ||
            teacherClass.topic.isBlank() ||
            teacherClass.category.isBlank() ||
            teacherClass.level.isBlank() ||
            teacherClass.capacity !in 5..200 ||
            teacherClass.capacity % 5 != 0
        ) {
            onFailure("Data kelas tidak valid. Periksa kembali informasi kelas.")
            return
        }

        val classRef = db.collection("kelas_mandiri")
            .document(teacherClass.code)

        val classData = hashMapOf<String, Any>(
            "code" to teacherClass.code,
            "name" to teacherClass.name.trim(),
            "description" to teacherClass.description.trim(),
            "topic" to teacherClass.topic,
            "category" to teacherClass.category,
            "level" to teacherClass.level,
            "capacity" to teacherClass.capacity,
            "ownerUid" to uid,
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.runTransaction { transaction ->
            val existing = transaction.get(classRef)

            if (existing.exists()) {
                throw IllegalStateException(
                    "Kode kelas sudah digunakan. Silakan coba lagi."
                )
            }

            transaction.set(classRef, classData)
            null
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { error ->
            onFailure(
                error.message ?: "Gagal menyimpan kelas ke Firestore."
            )
        }
    }

}
