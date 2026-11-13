package com.example.projectmobile

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

data class AttendanceSession(
    val classCode: String,
    val materialId: String,
    val code: String,
    val open: Boolean,
    val openedAt: Long
)

data class AttendanceRecord(val classCode: String, val materialId: String, val at: Long)

/**
 * Presensi per pertemuan (satu materi = satu pertemuan) untuk kelas mandiri.
 * Pengajar membuka sesi dan mendapat kode, pelajar memasukkan kode itu.
 * Disimpan di perangkat (SharedPreferences) sampai terhubung ke server.
 */
object AttendanceStore {

    private const val PREFS = "attendance_prefs"
    private const val KEY_SESSIONS = "sessions"
    private const val KEY_RECORDS = "records"
    private const val CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    const val CODE_LENGTH = 6

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun readSessions(ctx: Context): MutableList<AttendanceSession> {
        val raw = sp(ctx).getString(KEY_SESSIONS, null) ?: return mutableListOf()
        val out = mutableListOf<AttendanceSession>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    AttendanceSession(
                        classCode = o.getString("classCode"),
                        materialId = o.getString("materialId"),
                        code = o.getString("code"),
                        open = o.optBoolean("open"),
                        openedAt = o.optLong("openedAt")
                    )
                )
            }
        } catch (e: Exception) {
            // data rusak: abaikan
        }
        return out
    }

    private fun writeSessions(ctx: Context, list: List<AttendanceSession>) {
        val arr = JSONArray()
        for (s in list) {
            arr.put(
                JSONObject().apply {
                    put("classCode", s.classCode)
                    put("materialId", s.materialId)
                    put("code", s.code)
                    put("open", s.open)
                    put("openedAt", s.openedAt)
                }
            )
        }
        sp(ctx).edit().putString(KEY_SESSIONS, arr.toString()).apply()
    }

    private fun readRecords(ctx: Context): MutableList<AttendanceRecord> {
        val raw = sp(ctx).getString(KEY_RECORDS, null) ?: return mutableListOf()
        val out = mutableListOf<AttendanceRecord>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(AttendanceRecord(o.getString("classCode"), o.getString("materialId"), o.optLong("at")))
            }
        } catch (e: Exception) {
            // data rusak: abaikan
        }
        return out
    }

    private fun writeRecords(ctx: Context, list: List<AttendanceRecord>) {
        val arr = JSONArray()
        for (r in list) {
            arr.put(
                JSONObject().apply {
                    put("classCode", r.classCode)
                    put("materialId", r.materialId)
                    put("at", r.at)
                }
            )
        }
        sp(ctx).edit().putString(KEY_RECORDS, arr.toString()).apply()
    }

    private fun newCode(): String = (1..CODE_LENGTH).map { CODE_CHARS.random() }.joinToString("")

    fun session(ctx: Context, classCode: String, materialId: String): AttendanceSession? =
        readSessions(ctx).firstOrNull { it.classCode == classCode && it.materialId == materialId }

    /** Membuka presensi dengan kode baru (membuka ulang menghasilkan kode baru). */
    fun open(ctx: Context, classCode: String, materialId: String): AttendanceSession {
        val list = readSessions(ctx).filter { !(it.classCode == classCode && it.materialId == materialId) }.toMutableList()
        val s = AttendanceSession(classCode, materialId, newCode(), true, System.currentTimeMillis())
        list.add(s)
        writeSessions(ctx, list)
        return s
    }

    fun close(ctx: Context, classCode: String, materialId: String) {
        val list = readSessions(ctx).map {
            if (it.classCode == classCode && it.materialId == materialId) it.copy(open = false) else it
        }
        writeSessions(ctx, list)
    }

    fun record(ctx: Context, classCode: String, materialId: String): AttendanceRecord? =
        readRecords(ctx).firstOrNull { it.classCode == classCode && it.materialId == materialId }

    fun attendedCount(ctx: Context, classCode: String, materialId: String): Int =
        readRecords(ctx).count { it.classCode == classCode && it.materialId == materialId }

    /** Mengembalikan null bila berhasil, atau pesan kesalahan bila gagal. */
    fun submit(ctx: Context, classCode: String, materialId: String, input: String): String? {
        val s = session(ctx, classCode, materialId) ?: return "Pengajar belum membuka presensi."
        if (!s.open) return "Presensi sudah ditutup."
        if (record(ctx, classCode, materialId) != null) return "Kamu sudah melakukan presensi."
        if (!s.code.equals(input.trim(), ignoreCase = true)) return "Kode presensi salah."
        val list = readRecords(ctx)
        list.add(AttendanceRecord(classCode, materialId, System.currentTimeMillis()))
        writeRecords(ctx, list)
        return null
    }

    fun formatTime(millis: Long): String =
        SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date(millis))
}
