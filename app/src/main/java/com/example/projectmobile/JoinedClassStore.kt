package com.example.projectmobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Cache informasi kelas yang diikuti pelajar.
 * Terpisah dari TeacherClassStore karena kelas ini bukan milik perangkat pelajar.
 */
object JoinedClassStore {

    private const val PREFS = "joined_class_info_prefs"
    private const val KEY = "joined_classes"

    private fun sp(ctx: Context) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun read(ctx: Context): MutableList<ClassInfo> {
        val raw = sp(ctx).getString(KEY, null) ?: return mutableListOf()
        val result = mutableListOf<ClassInfo>()

        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                result.add(
                    ClassInfo(
                        code = item.optString("code"),
                        name = item.optString("name"),
                        teacher = item.optString("teacher"),
                        field = item.optString("field"),
                        participants = item.optInt("participants", 0),
                        type = item.optString("type", ClassStore.TYPE_MANDIRI),
                        description = item.optString("description"),
                        iconRes = item.optInt("iconRes", R.drawable.ic_book),
                        nextMeeting = item.optString("nextMeeting", "Belum ada materi"),
                        schedule = item.optString("schedule", "Belum dijadwalkan")
                    )
                )
            }
        } catch (_: Exception) {
            // Cache rusak: abaikan.
        }

        return result
    }

    private fun write(ctx: Context, classes: List<ClassInfo>) {
        val array = JSONArray()

        classes.forEach { item ->
            array.put(
                JSONObject().apply {
                    put("code", item.code)
                    put("name", item.name)
                    put("teacher", item.teacher)
                    put("field", item.field)
                    put("participants", item.participants)
                    put("type", item.type)
                    put("description", item.description)
                    put("iconRes", item.iconRes)
                    put("nextMeeting", item.nextMeeting)
                    put("schedule", item.schedule)
                }
            )
        }

        sp(ctx).edit().putString(KEY, array.toString()).apply()
    }

    fun all(ctx: Context): List<ClassInfo> = read(ctx)

    fun byCode(ctx: Context, code: String): ClassInfo? =
        read(ctx).firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }

    fun save(ctx: Context, info: ClassInfo) {
        val classes = read(ctx)
        val index = classes.indexOfFirst {
            it.code.equals(info.code, ignoreCase = true)
        }

        if (index >= 0) {
            classes[index] = info
        } else {
            classes.add(info)
        }

        write(ctx, classes)
    }

    /**
     * Mengganti cache kelas dari Firestore tanpa menghapus kelas contoh lokal.
     */
    fun replaceCloud(ctx: Context, classes: List<ClassInfo>) {
        val localSamples = read(ctx).filter {
            ClassStore.find(it.code) != null
        }

        val merged = (localSamples + classes)
            .distinctBy { it.code.uppercase() }

        write(ctx, merged)
    }

    fun remove(ctx: Context, code: String) {
        write(
            ctx,
            read(ctx).filterNot {
                it.code.equals(code.trim(), ignoreCase = true)
            }
        )
    }
}
