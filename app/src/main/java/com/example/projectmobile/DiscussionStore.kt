package com.example.projectmobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class DiscussionMessage(
    val id: String,
    val classCode: String,
    val role: String,
    val sender: String,
    val text: String,
    val at: Long
)

/**
 * Pesan diskusi per kelas mandiri. Disimpan di perangkat (SharedPreferences)
 * sampai terhubung ke server, jadi pengajar dan pelajar baru saling melihat
 * pesan kalau memakai perangkat yang sama.
 */
object DiscussionStore {

    private const val PREFS = "discussion_prefs"
    private const val KEY = "messages"
    const val MAX_LENGTH = 1000

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun read(ctx: Context): MutableList<DiscussionMessage> {
        val raw = sp(ctx).getString(KEY, null) ?: return mutableListOf()
        val out = mutableListOf<DiscussionMessage>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    DiscussionMessage(
                        id = o.getString("id"),
                        classCode = o.getString("classCode"),
                        role = o.optString("role"),
                        sender = o.optString("sender"),
                        text = o.optString("text"),
                        at = o.optLong("at")
                    )
                )
            }
        } catch (e: Exception) {
            // data rusak: abaikan
        }
        return out
    }

    private fun write(ctx: Context, list: List<DiscussionMessage>) {
        val arr = JSONArray()
        for (m in list) {
            arr.put(
                JSONObject().apply {
                    put("id", m.id)
                    put("classCode", m.classCode)
                    put("role", m.role)
                    put("sender", m.sender)
                    put("text", m.text)
                    put("at", m.at)
                }
            )
        }
        sp(ctx).edit().putString(KEY, arr.toString()).apply()
    }

    /** Pesan sebuah kelas, yang paling lama di atas. */
    fun messages(ctx: Context, classCode: String): List<DiscussionMessage> =
        read(ctx).filter { it.classCode == classCode }.sortedBy { it.at }

    fun count(ctx: Context, classCode: String): Int = read(ctx).count { it.classCode == classCode }

    fun send(ctx: Context, classCode: String, role: String, text: String): DiscussionMessage {
        val clean = text.trim().take(MAX_LENGTH)
        val name = ProfileStore.get(ctx, role, ProfileStore.NAMA).ifBlank {
            if (role == PilihPeranActivity.ROLE_PENGAJAR) "Pengajar" else "Pelajar"
        }
        val list = read(ctx)
        val now = System.currentTimeMillis()
        val m = DiscussionMessage("D$now", classCode, role, name, clean, now)
        list.add(m)
        write(ctx, list)
        return m
    }
}
