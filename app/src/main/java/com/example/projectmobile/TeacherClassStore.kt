package com.example.projectmobile

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

data class TeacherClass(
    val code: String,
    val name: String,
    val description: String,
    val topic: String,
    val category: String,
    val level: String,
    val capacity: Int,
    val createdAt: Long
)

/**
 * Kelas mandiri yang dibuat pengajar.
 * Disimpan di perangkat (SharedPreferences) sampai terhubung ke server.
 */
object TeacherClassStore {

    val TOPICS = listOf("Pemrograman", "Matematika", "Sains", "Bahasa", "Desain", "Bisnis", "Lainnya")
    val CATEGORIES = listOf("Akademik", "Keterampilan", "Persiapan Ujian", "Hobi", "Lainnya")
    val LEVELS = listOf("Pemula", "Menengah", "Lanjutan")
    const val MAX_DESC = 300

    private const val PREFS = "teacher_class_prefs"
    private const val KEY = "classes"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun iconFor(topic: String): Int = when (topic) {
        "Pemrograman" -> R.drawable.ic_code
        "Sains" -> R.drawable.ic_chip
        "Desain" -> R.drawable.ic_pencil
        "Bisnis" -> R.drawable.ic_briefcase
        "Bahasa" -> R.drawable.ic_globe
        else -> R.drawable.ic_book
    }

    private fun read(ctx: Context): MutableList<TeacherClass> {
        val raw = sp(ctx).getString(KEY, null) ?: return mutableListOf()
        val out = mutableListOf<TeacherClass>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                out.add(
                    TeacherClass(
                        code = o.getString("code"),
                        name = o.getString("name"),
                        description = o.optString("description"),
                        topic = o.optString("topic"),
                        category = o.optString("category"),
                        level = o.optString("level"),
                        capacity = o.optInt("capacity", 30),
                        createdAt = o.optLong("createdAt")
                    )
                )
            }
        } catch (e: Exception) {
            // data rusak: abaikan
        }
        return out
    }

    private fun write(ctx: Context, list: List<TeacherClass>) {
        val arr = JSONArray()
        for (c in list) {
            arr.put(
                JSONObject().apply {
                    put("code", c.code)
                    put("name", c.name)
                    put("description", c.description)
                    put("topic", c.topic)
                    put("category", c.category)
                    put("level", c.level)
                    put("capacity", c.capacity)
                    put("createdAt", c.createdAt)
                }
            )
        }
        sp(ctx).edit().putString(KEY, arr.toString()).apply()
    }

    /** Kelas terbaru di urutan pertama. */
    fun all(ctx: Context): List<TeacherClass> = read(ctx).sortedByDescending { it.createdAt }

    fun byCode(ctx: Context, code: String): TeacherClass? =
        read(ctx).firstOrNull { it.code.equals(code, ignoreCase = true) }

    private fun generateCode(ctx: Context, name: String): String {
        val initials = name.split(" ")
            .mapNotNull { w -> w.firstOrNull { it.isLetter() } }
            .take(2)
            .joinToString("")
            .uppercase()
        val prefix = if (initials.length >= 2) initials else (initials + "KM").take(2)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val existing = read(ctx).map { it.code }.toSet()
        var code = "$prefix$year"
        while (code in existing) {
            code = "$prefix$year${('A'..'Z').random()}"
        }
        return code
    }

    fun create(
        ctx: Context,
        name: String,
        description: String,
        topic: String,
        category: String,
        level: String,
        capacity: Int
    ): TeacherClass {
        val list = read(ctx)
        val c = TeacherClass(
            code = generateCode(ctx, name),
            name = name,
            description = description,
            topic = topic,
            category = category,
            level = level,
            capacity = capacity,
            createdAt = System.currentTimeMillis()
        )
        list.add(c)
        write(ctx, list)
        return c
    }

    fun update(ctx: Context, updated: TeacherClass) {
        val list = read(ctx)
        val idx = list.indexOfFirst { it.code == updated.code }
        if (idx >= 0) {
            list[idx] = updated
            write(ctx, list)
        }
    }

    fun count(ctx: Context): Int = read(ctx).size

    fun formatDate(millis: Long): String =
        SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Date(millis))

    fun copyCode(ctx: Context, c: TeacherClass) {
        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Kode kelas", c.code))
        UiKit.toast(ctx, "Kode kelas disalin")
    }

    fun share(activity: Activity, c: TeacherClass) {
        val text = "Gabung ke kelas \"${c.name}\" di TemanStudi dengan kode: ${c.code}"
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        activity.startActivity(Intent.createChooser(send, "Bagikan kode kelas"))
    }
}
