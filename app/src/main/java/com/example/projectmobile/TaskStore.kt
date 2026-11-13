package com.example.projectmobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class TaskAttachment(val name: String, val size: String, val color: Int)

data class TaskInfo(
    val id: String,
    val classCode: String,
    val className: String,
    val title: String,
    val deadlineMillis: Long,
    val instructions: List<String>,
    val attachments: List<TaskAttachment>
)

data class Submission(
    val answer: String,
    val files: List<String>,
    val submittedAt: Long,
    val score: Int?,
    val feedback: String = ""
)

/**
 * Data tugas untuk pelajar kelas mandiri.
 * Daftar tugas masih DATA CONTOH (dibuat dari kelas yang diikuti) sampai terhubung ke server.
 * Pengumpulan tugas disimpan di perangkat (nama file saja, belum diunggah).
 */
object TaskStore {

    const val STATUS_BELUM = "Belum Dikerjakan"
    const val STATUS_MENUNGGU = "Menunggu Penilaian"
    const val STATUS_DINILAI = "Dinilai"
    const val STATUS_TERLAMBAT = "Terlambat"

    private const val PREFS = "task_prefs"
    private const val KEY_BASE = "task_base_time"
    private const val KEY_SUB = "submission_"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun baseTime(ctx: Context): Long {
        val saved = sp(ctx).getLong(KEY_BASE, 0L)
        if (saved != 0L) return saved
        val now = System.currentTimeMillis()
        sp(ctx).edit().putLong(KEY_BASE, now).apply()
        return now
    }

    private fun deadline(ctx: Context, plusDays: Int): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = baseTime(ctx)
        cal.add(Calendar.DAY_OF_YEAR, plusDays)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 0)
        return cal.timeInMillis
    }

    /** Tugas dari semua kelas mandiri yang diikuti (atau satu kelas bila classCode diberikan). */
    fun tasks(ctx: Context, classCode: String? = null): List<TaskInfo> {
        val list = mutableListOf<TaskInfo>()
        for (c in ClassStore.joined(ctx)) {
            if (classCode != null && c.code != classCode) continue
            if (TeacherClassStore.byCode(ctx, c.code) != null) {
                for (t in TeacherClassStore.tasks(ctx, c.code)) {
                    list.add(
                        TaskInfo(
                            id = t.id,
                            classCode = c.code,
                            className = c.name,
                            title = t.title,
                            deadlineMillis = t.deadlineMillis,
                            instructions = t.instructions.split("\n").map { it.trim() }.filter { it.isNotEmpty() },
                            attachments = emptyList()
                        )
                    )
                }
                continue
            }
            list.add(
                TaskInfo(
                    id = "${c.code}-T1",
                    classCode = c.code,
                    className = c.name,
                    title = "Membuat UI Login Android",
                    deadlineMillis = deadline(ctx, 7),
                    instructions = listOf(
                        "Buat tampilan UI Login sesuai dengan desain.",
                        "Gunakan Kotlin dan XML/Jetpack Compose.",
                        "Sertakan validasi input dan error message."
                    ),
                    attachments = listOf(
                        TaskAttachment("Desain UI Login.pdf", "1.2 MB", 0xFFFF6B7A.toInt()),
                        TaskAttachment("Contoh Kode.zip", "2.4 MB", 0xFF1D5EFF.toInt())
                    )
                )
            )
            list.add(
                TaskInfo(
                    id = "${c.code}-T2",
                    classCode = c.code,
                    className = c.name,
                    title = "Latihan Activity dan Intent",
                    deadlineMillis = deadline(ctx, 14),
                    instructions = listOf(
                        "Buat dua Activity yang saling berpindah menggunakan Intent.",
                        "Kirim data sederhana dari Activity pertama ke kedua.",
                        "Lampirkan tangkapan layar hasil aplikasi."
                    ),
                    attachments = listOf(
                        TaskAttachment("Panduan Intent.pdf", "0.9 MB", 0xFFFF6B7A.toInt())
                    )
                )
            )
        }
        return list
    }

    fun byId(ctx: Context, id: String): TaskInfo? = tasks(ctx).firstOrNull { it.id == id }

    // ------------------------------------------------------------ Pengumpulan

    fun submission(ctx: Context, taskId: String): Submission? {
        val raw = sp(ctx).getString(KEY_SUB + taskId, null) ?: return null
        return try {
            val o = JSONObject(raw)
            val files = mutableListOf<String>()
            val arr = o.optJSONArray("files") ?: JSONArray()
            for (i in 0 until arr.length()) files.add(arr.getString(i))
            Submission(
                answer = o.optString("answer", ""),
                files = files,
                submittedAt = o.optLong("at", 0L),
                score = if (o.has("score")) o.getInt("score") else null,
                feedback = o.optString("feedback", "")
            )
        } catch (e: Exception) {
            null
        }
    }

    fun submit(ctx: Context, taskId: String, answer: String, files: List<String>): Long {
        val now = System.currentTimeMillis()
        val o = JSONObject()
            .put("answer", answer)
            .put("files", JSONArray(files))
            .put("at", now)
        sp(ctx).edit().putString(KEY_SUB + taskId, o.toString()).apply()
        return now
    }

    /** Pengajar memberi nilai (0-100) dan catatan pada pengumpulan yang ada. */
    fun grade(ctx: Context, taskId: String, score: Int, feedback: String): Boolean {
        val raw = sp(ctx).getString(KEY_SUB + taskId, null) ?: return false
        return try {
            val o = JSONObject(raw)
            o.put("score", score.coerceIn(0, 100))
            o.put("feedback", feedback)
            sp(ctx).edit().putString(KEY_SUB + taskId, o.toString()).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun cancel(ctx: Context, taskId: String) {
        sp(ctx).edit().remove(KEY_SUB + taskId).apply()
    }

    // ------------------------------------------------------------ Status

    fun statusOf(ctx: Context, task: TaskInfo): String {
        val sub = submission(ctx, task.id)
        if (sub != null) return if (sub.score != null) STATUS_DINILAI else STATUS_MENUNGGU
        return if (System.currentTimeMillis() > task.deadlineMillis) STATUS_TERLAMBAT else STATUS_BELUM
    }

    fun isDone(ctx: Context, task: TaskInfo) = submission(ctx, task.id) != null

    /** Jumlah tugas yang belum dikumpulkan (semua kelas). */
    fun pendingCount(ctx: Context): Int = tasks(ctx).count { !isDone(ctx, it) }

    /** Jumlah tugas di satu kelas. */
    fun countFor(ctx: Context, classCode: String): Int = tasks(ctx, classCode).size

    fun format(millis: Long): String =
        SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(millis))
}
