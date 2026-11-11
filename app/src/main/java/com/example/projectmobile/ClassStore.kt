package com.example.projectmobile

import android.content.Context

data class ClassInfo(
    val code: String,
    val name: String,
    val teacher: String,
    val field: String,
    val participants: Int,
    val type: String,
    val description: String,
    val iconRes: Int,
    val nextMeeting: String,
    val schedule: String
)

data class Meeting(
    val number: Int,
    val title: String,
    val materials: Int,
    val date: String,
    val description: String = "",
    val fileName: String = "",
    val custom: Boolean = false
)

/**
 * Data kelas untuk pelajar.
 * Daftar kelas di bawah masih DATA CONTOH sampai terhubung ke server.
 * Kelas mandiri yang diikuti disimpan di perangkat.
 */
object ClassStore {

    const val TYPE_INSTITUSI = "Institusi"
    const val TYPE_MANDIRI = "Mandiri"

    private const val PREFS = "class_prefs"
    private const val KEY_JOINED = "joined_codes"
    private const val KEY_DONE = "done_meetings"

    private val institusiClasses = listOf(
        ClassInfo(
            "PM-2025-ABC1", "Pemrograman Mobile", "Dr. Budi Santoso", "Teknologi Informasi", 32,
            TYPE_INSTITUSI,
            "Mata kuliah ini membahas konsep dasar pemrograman mobile menggunakan Android Studio, mulai dari pengenalan hingga pembuatan aplikasi sederhana.",
            R.drawable.ic_code, "Pertemuan 2: UI Android", "Besok, 10:00"
        ),
        ClassInfo(
            "SC-2025-ABC2", "Sistem Cerdas", "Dr. Rina Wati", "Informatika", 28,
            TYPE_INSTITUSI,
            "Mata kuliah ini memperkenalkan konsep kecerdasan buatan, representasi pengetahuan, dan penerapannya pada sistem cerdas.",
            R.drawable.ic_chip, "Pertemuan 1: Pengenalan AI", "Jumat, 08:00"
        ),
        ClassInfo(
            "BD-2025-ABC3", "Basis Data", "Dr. Ahmad Fauzi", "Sistem Informasi", 26,
            TYPE_INSTITUSI,
            "Mata kuliah ini membahas perancangan basis data relasional, normalisasi, dan penggunaan SQL.",
            R.drawable.ic_database, "Pertemuan 3: Normalisasi", "Sabtu, 10:00"
        )
    )

    // Kelas yang bisa diikuti lewat kode (contoh). Kode contoh: PM-2025-AB31
    private val joinable = listOf(
        ClassInfo(
            "PM-2025-AB31", "Pemrograman Mobile", "Budi Santoso", "Pemrograman Mobile", 28,
            TYPE_MANDIRI,
            "Kelas mandiri untuk belajar dasar pemrograman mobile menggunakan Android Studio bersama pengajar.",
            R.drawable.ic_code, "Pertemuan 1: Pengenalan Android Studio", "Belum dijadwalkan"
        )
    )

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Mencari kelas dari kode. TODO: ganti dengan permintaan ke server. */
    fun find(code: String): ClassInfo? =
        joinable.firstOrNull { it.code.equals(code.trim(), ignoreCase = true) }

    fun joined(ctx: Context): List<ClassInfo> {
        val codes = sp(ctx).getStringSet(KEY_JOINED, emptySet()) ?: emptySet()
        val sample = joinable.filter { it.code in codes }
        val own = TeacherClassStore.all(ctx).filter { it.code in codes }.map { fromTeacher(ctx, it) }
        return (sample + own).sortedBy { it.name }
    }

    fun isJoined(ctx: Context, code: String): Boolean = joined(ctx).any { it.code == code }

    fun join(ctx: Context, code: String) {
        val set = (sp(ctx).getStringSet(KEY_JOINED, emptySet()) ?: emptySet()).toMutableSet()
        set.add(code)
        sp(ctx).edit().putStringSet(KEY_JOINED, set).apply()
    }

    fun leave(ctx: Context, code: String) {
        val set = (sp(ctx).getStringSet(KEY_JOINED, emptySet()) ?: emptySet()).toMutableSet()
        set.remove(code)
        sp(ctx).edit().putStringSet(KEY_JOINED, set).apply()
    }

    /** Kelas yang ditampilkan sesuai mode pembelajaran. */
    fun classesFor(ctx: Context, mode: String): List<ClassInfo> =
        if (mode == ModePembelajaranActivity.MODE_INSTITUSI) institusiClasses else joined(ctx)

    fun byCode(code: String): ClassInfo? =
        (institusiClasses + joinable).firstOrNull { it.code == code }

    // ------------------------------------------------- Kelas buatan pengajar

    /** Kelas mandiri buatan pengajar (di perangkat ini) dalam bentuk ClassInfo. */
    private fun fromTeacher(ctx: Context, t: TeacherClass): ClassInfo {
        val first = TeacherClassStore.materials(ctx, t.code).firstOrNull()
        return ClassInfo(
            code = t.code,
            name = t.name,
            teacher = ProfileStore.get(ctx, PilihPeranActivity.ROLE_PENGAJAR, ProfileStore.NAMA),
            field = t.topic,
            participants = 1,
            type = TYPE_MANDIRI,
            description = t.description.ifBlank { "Kelas mandiri ${t.name}." },
            iconRes = TeacherClassStore.iconFor(t.topic),
            nextMeeting = if (first != null) "Pertemuan 1: ${first.title}" else "Belum ada materi",
            schedule = "Belum dijadwalkan"
        )
    }

    /** Mencari kelas dari kode, termasuk kelas buatan pengajar di perangkat ini. */
    fun find(ctx: Context, code: String): ClassInfo? =
        find(code) ?: TeacherClassStore.byCode(ctx, code.trim())?.let { fromTeacher(ctx, it) }

    fun byCode(ctx: Context, code: String): ClassInfo? =
        byCode(code) ?: TeacherClassStore.byCode(ctx, code)?.let { fromTeacher(ctx, it) }

    fun meetings(ctx: Context, info: ClassInfo): List<Meeting> {
        val own = TeacherClassStore.byCode(ctx, info.code)
        if (own != null) {
            return TeacherClassStore.materials(ctx, own.code).mapIndexed { i, m ->
                Meeting(i + 1, m.title, 1, TeacherClassStore.formatDate(m.createdAt), m.description, m.fileName, true)
            }
        }
        return meetings(info)
    }

    fun totalMaterials(ctx: Context, info: ClassInfo): Int = meetings(ctx, info).sumOf { it.materials }

    // ----------------------------------------------------------- Pertemuan

    fun meetings(info: ClassInfo): List<Meeting> = when (info.name) {
        "Sistem Cerdas" -> listOf(
            Meeting(1, "Pengenalan AI", 3, "13 Mei 2025"),
            Meeting(2, "Representasi Pengetahuan", 4, "20 Mei 2025"),
            Meeting(3, "Pencarian dan Heuristik", 3, "27 Mei 2025")
        )
        "Basis Data" -> listOf(
            Meeting(1, "Konsep Basis Data", 3, "14 Mei 2025"),
            Meeting(2, "Model ER", 4, "21 Mei 2025"),
            Meeting(3, "Normalisasi", 3, "28 Mei 2025")
        )
        else -> listOf(
            Meeting(1, "Pengenalan Android Studio", 3, "12 Mei 2025"),
            Meeting(2, "UI/UX Android", 4, "19 Mei 2025"),
            Meeting(3, "Activity dan Intent", 3, "26 Mei 2025")
        )
    }

    fun totalMaterials(info: ClassInfo): Int = meetings(info).sumOf { it.materials }

    // ----------------------------------------------------------- Progres

    private fun doneKey(code: String, number: Int) = "$code|$number"

    fun isDone(ctx: Context, code: String, number: Int): Boolean {
        val set = sp(ctx).getStringSet(KEY_DONE, emptySet()) ?: emptySet()
        return doneKey(code, number) in set
    }

    fun setDone(ctx: Context, code: String, number: Int, done: Boolean) {
        val set = (sp(ctx).getStringSet(KEY_DONE, emptySet()) ?: emptySet()).toMutableSet()
        if (done) set.add(doneKey(code, number)) else set.remove(doneKey(code, number))
        sp(ctx).edit().putStringSet(KEY_DONE, set).apply()
    }

    fun progress(ctx: Context, info: ClassInfo): Int {
        val list = meetings(ctx, info)
        if (list.isEmpty()) return 0
        val done = list.count { isDone(ctx, info.code, it.number) }
        return done * 100 / list.size
    }

    /** "Selesai", "Sedang" (pertemuan pertama yang belum selesai), atau "Belum". */
    fun statusOf(ctx: Context, info: ClassInfo, meeting: Meeting): String {
        if (isDone(ctx, info.code, meeting.number)) return "Selesai"
        val firstOpen = meetings(ctx, info).firstOrNull { !isDone(ctx, info.code, it.number) }
        return if (firstOpen?.number == meeting.number) "Sedang" else "Belum"
    }
}
