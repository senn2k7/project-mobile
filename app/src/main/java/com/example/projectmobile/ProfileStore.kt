package com.example.snapchatsphlasscreen

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Penyimpanan data profil di perangkat (SharedPreferences).
 * Nama dan email diambil dari akun Firebase bila ada; sisanya memakai
 * contoh data dari desain Figma sampai terhubung ke server.
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

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun key(role: String, field: String) = "${role}_$field"

    fun isTeacher(role: String) = role == PilihPeranActivity.ROLE_PENGAJAR

    fun get(ctx: Context, role: String, field: String): String {
        val saved = sp(ctx).getString(key(role, field), null)
        return saved ?: default(role, field)
    }

    fun put(ctx: Context, role: String, field: String, value: String) {
        sp(ctx).edit().putString(key(role, field), value).apply()
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

    private fun default(role: String, field: String): String {
        val teacher = isTeacher(role)
        val user = try {
            FirebaseAuthManager.currentUser
        } catch (e: Exception) {
            null
        }
        return when (field) {
            NAMA -> user?.displayName?.takeIf { it.isNotBlank() }
                ?: user?.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
                ?: if (teacher) "Dr. Andi Pratama" else "Aldi Pratama"
            EMAIL -> user?.email?.takeIf { it.isNotBlank() }
                ?: if (teacher) "andi.pratama@uti.ac.id" else "aldi.pratama@email.com"
            PHONE -> if (teacher) "08123456789" else ""
            NIM -> "220411100123"
            PRODI -> "Teknik Informatika"
            INSTITUSI -> if (teacher) "Universitas Teknologi Indonesia" else "Universitas Trunojoyo Madura"
            JABATAN -> "Dosen / Pengajar"
            FAKULTAS -> "Fakultas Ilmu Komputer"
            else -> ""
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
