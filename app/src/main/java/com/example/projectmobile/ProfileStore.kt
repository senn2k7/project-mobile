package com.example.projectmobile

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Cache profil pengguna berdasarkan UID, peran, dan mode.
 * Sumber data utama berasal dari Firestore.
 */
object ProfileStore {

    const val NAMA = "nama"
    const val EMAIL = "email"
    const val PHONE = "phone"
    const val NIM = "nim"
    const val PRODI = "prodi"
    const val INSTITUSI = "institusi"
    const val JABATAN = "jabatan"
    const val FAKULTAS = "fakultas"

    private const val PREFS = "profile_prefs"

    private val allFields = listOf(
        NAMA, EMAIL, PHONE, NIM, PRODI, INSTITUSI, JABATAN, FAKULTAS
    )

    private fun sp(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun currentUid(): String? =
        FirebaseAuthManager.currentUser?.uid?.takeIf { it.isNotBlank() }

    private fun key(uid: String, role: String, mode: String, field: String) =
        "${uid}_${role}_${mode}_$field"

    private fun legacyKey(role: String, field: String) = "${role}_$field"

    fun isTeacher(role: String) =
        role == PilihPeranActivity.ROLE_PENGAJAR

    /**
     * Membaca cache berdasarkan pengguna, peran, dan mode aktif.
     * Nilai kosong berarti data belum tersedia.
     */
    fun get(ctx: Context, role: String, mode: String, field: String): String {
        val uid = currentUid() ?: return ""
        return sp(ctx).getString(key(uid, role, mode, field), "") ?: ""
    }

    /**
     * Kompatibilitas untuk pemanggil lama yang belum mengirim mode.
     * Tidak lagi mengembalikan data contoh.
     */
    fun get(ctx: Context, role: String, field: String): String {
        val uid = currentUid() ?: return ""
        val preferences = sp(ctx)

        val mandiri = preferences.getString(
            key(uid, role, ModePembelajaranActivity.MODE_MANDIRI, field), null
        )
        val institusi = preferences.getString(
            key(uid, role, ModePembelajaranActivity.MODE_INSTITUSI, field), null
        )

        return mandiri?.takeIf { it.isNotBlank() }
            ?: institusi?.takeIf { it.isNotBlank() }
            ?: preferences.getString(legacyKey(role, field), "")
            ?: ""
    }

    fun put(ctx: Context, role: String, field: String, value: String) {
        sp(ctx).edit().putString(legacyKey(role, field), value).apply()
    }

    fun put(
        ctx: Context,
        role: String,
        mode: String,
        field: String,
        value: String
    ) {
        val uid = currentUid() ?: return
        sp(ctx).edit().putString(key(uid, role, mode, field), value).apply()
    }

    /**
     * Mengambil data users/{uid} dan koleksi profil sesuai peran/mode.
     * Callback bernilai true jika pembacaan Firestore berhasil.
     */
    fun refresh(
        ctx: Context,
        role: String,
        mode: String,
        onComplete: (Boolean) -> Unit
    ) {
        val uid = currentUid()
        if (uid == null) {
            onComplete(false)
            return
        }

        val collection = when {
            role == PilihPeranActivity.ROLE_PENGAJAR &&
                mode == ModePembelajaranActivity.MODE_MANDIRI -> "pengajar_mandiri"

            role == PilihPeranActivity.ROLE_PENGAJAR &&
                mode == ModePembelajaranActivity.MODE_INSTITUSI -> "pengajar_institusi"

            role == PilihPeranActivity.ROLE_PELAJAR &&
                mode == ModePembelajaranActivity.MODE_MANDIRI -> "pelajar_mandiri"

            role == PilihPeranActivity.ROLE_PELAJAR &&
                mode == ModePembelajaranActivity.MODE_INSTITUSI -> "pelajar_institusi"

            else -> {
                onComplete(false)
                return
            }
        }

        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)
        val profileRef = db.collection(collection).document(uid)

        userRef.get()
            .addOnSuccessListener { userSnapshot ->
                profileRef.get()
                    .addOnSuccessListener { profileSnapshot ->
                        val values = mutableMapOf<String, String>()

                        allFields.forEach { values[it] = "" }

                        fun readString(
                            map: Map<String, Any>?,
                            vararg names: String
                        ): String {
                            for (name in names) {
                                val value = map?.get(name) as? String
                                if (!value.isNullOrBlank()) return value.trim()
                            }
                            return ""
                        }

                        val userData = userSnapshot.data
                        val profileData = profileSnapshot.data

                        values[NAMA] = readString(
                            profileData, "nama"
                        ).ifBlank {
                            readString(userData, "nama")
                        }

                        values[EMAIL] = readString(
                            profileData, "email"
                        ).ifBlank {
                            readString(userData, "email")
                        }.ifBlank {
                            FirebaseAuthManager.currentUser?.email.orEmpty()
                        }

                        values[PHONE] = readString(
                            profileData, "nomor_telepon", "phone"
                        )
                        values[NIM] = readString(profileData, "nim")
                        values[PRODI] = readString(
                            profileData, "program_studi", "prodi"
                        )
                        values[INSTITUSI] = readString(
                            profileData, "nama_instansi", "institusi"
                        )
                        values[JABATAN] = readString(profileData, "jabatan")
                        values[FAKULTAS] = readString(profileData, "fakultas")

                        val editor = sp(ctx).edit()
                        allFields.forEach { field ->
                            editor.putString(
                                key(uid, role, mode, field),
                                values[field].orEmpty()
                            )
                        }
                        editor.apply()

                        onComplete(true)
                    }
                    .addOnFailureListener {
                        onComplete(false)
                    }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun clear(ctx: Context) {
        sp(ctx).edit().clear().apply()
    }

    fun roleLabel(role: String, mode: String): String = when {
        isTeacher(role) -> "Pengajar"
        mode == ModePembelajaranActivity.MODE_INSTITUSI -> "Mahasiswa"
        else -> "Pelajar"
    }

    /** Daftar (label, key) yang ditampilkan di Profil Saya sesuai peran dan mode. */
    fun fieldsFor(role: String, mode: String): List<Pair<String, String>> {
        val institusi = mode == ModePembelajaranActivity.MODE_INSTITUSI
        return if (isTeacher(role)) {
            if (institusi) listOf(
                "Nama Lengkap" to NAMA, "Email" to EMAIL, "Nomor Telepon" to PHONE,
                "Jabatan" to JABATAN, "Fakultas" to FAKULTAS,
                "Program Studi" to PRODI, "Institusi" to INSTITUSI
            ) else listOf(
                "Nama Lengkap" to NAMA, "Email" to EMAIL, "Nomor Telepon" to PHONE
            )
        } else {
            if (institusi) listOf(
                "Nama Lengkap" to NAMA, "Email" to EMAIL, "NIM" to NIM,
                "Program Studi" to PRODI, "Institusi" to INSTITUSI
            ) else listOf(
                "Nama Lengkap" to NAMA, "Email" to EMAIL, "Nomor Telepon" to PHONE
            )
        }
    }
}

/** Menyimpan dan menerapkan pilihan tema (terang / gelap / ikuti sistem). */
object ThemeStore {
    const val TERANG = "terang"
    const val GELAP = "gelap"
    const val SISTEM = "sistem"

    private const val PREFS = "theme_prefs"
    private const val KEY = "theme_mode"

    fun saved(ctx: Context): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, SISTEM) ?: SISTEM

    fun save(ctx: Context, mode: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, mode).apply()
    }

    fun apply(mode: String) {
        AppCompatDelegate.setDefaultNightMode(
            when (mode) {
                TERANG -> AppCompatDelegate.MODE_NIGHT_NO
                GELAP -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    fun applySaved(ctx: Context) = apply(saved(ctx))
}
