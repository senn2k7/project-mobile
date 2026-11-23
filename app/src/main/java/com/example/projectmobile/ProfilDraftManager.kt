package com.example.projectmobile

import android.content.Context

object ProfilDraftManager {

    private fun prefs(context: Context, uid: String) =
        context.getSharedPreferences("profil_draft_$uid", Context.MODE_PRIVATE)

    fun save(
        context: Context,
        uid: String,
        nama: String,
        telepon: String,
        keahlian: String,
        instansi: String,
        pengalaman: String
    ) {
        prefs(context, uid).edit()
            .putString("nama", nama)
            .putString("telepon", telepon)
            .putString("keahlian", keahlian)
            .putString("instansi", instansi)
            .putString("pengalaman", pengalaman)
            .putBoolean("draft_tersedia", true)
            .apply()
    }

    fun restore(context: Context, uid: String): Map<String, String>? {
        val p = prefs(context, uid)
        if (!p.getBoolean("draft_tersedia", false)) return null

        return mapOf(
            "nama" to p.getString("nama", "").orEmpty(),
            "telepon" to p.getString("telepon", "").orEmpty(),
            "keahlian" to p.getString("keahlian", "").orEmpty(),
            "instansi" to p.getString("instansi", "").orEmpty(),
            "pengalaman" to p.getString("pengalaman", "").orEmpty()
        )
    }

    fun clear(context: Context, uid: String) {
        prefs(context, uid).edit().clear().apply()
    }

    fun hasDraft(context: Context, uid: String): Boolean {
        return prefs(context, uid).getBoolean("draft_tersedia", false)
    }
}
