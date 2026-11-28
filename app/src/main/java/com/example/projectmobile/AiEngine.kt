package com.example.projectmobile

import android.content.Context
import java.util.Locale

/** Konteks belajar yang dipilih: satu kelas, dan (opsional) satu pertemuan di dalamnya. */
data class AiContext(
    val key: String,
    val label: String,
    val classCode: String?,
    val className: String,
    val topic: String,
    val number: Int,
    val meetingTitle: String?,
    val meetingDescription: String,
    val fileName: String
)

/** Penghasil jawaban asisten. Ganti mesin lewat AiConfig.engine untuk menyambung ke layanan AI sungguhan. */
interface AiEngine {
    fun reply(ctx: Context, c: AiContext, role: String, prompt: String): String
}

object AiConfig {
    /** Mesin yang dipakai layar AI Asisten mandiri. Bawaannya mesin lokal (tanpa internet). */
    @Volatile
    var engine: AiEngine = LocalAiEngine

    fun suggestions(role: String): List<String> =
        if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            listOf("Ringkas perkembangan kelas", "Berikan ide soal kuis", "Saran pertemuan berikutnya", "Bantu tulis instruksi tugas")
        } else {
            listOf("Jelaskan materi dengan sederhana", "Buatkan rangkuman", "Berikan contoh soal", "Bantu aku memahami kode program")
        }
}

/**
 * Mesin jawaban lokal (mode demo): menyusun jawaban dari materi, tugas, kuis, dan nilai
 * yang ada di kelas. Bukan model AI online, jadi tidak tahu isi di luar data kelas.
 */
object LocalAiEngine : AiEngine {

    private fun has(p: String, vararg keys: String) = keys.any { p.contains(it) }

    private fun sentences(text: String): List<String> =
        text.split(Regex("(?<=[.!?])\\s+|\\n+")).map { it.trim() }.filter { it.length > 2 }

    private fun codes(ctx: Context, c: AiContext, role: String): List<String> {
        if (c.classCode != null) return listOf(c.classCode)
        return if (role == PilihPeranActivity.ROLE_PENGAJAR) TeacherClassStore.all(ctx).map { it.code }
        else ClassStore.joined(ctx).map { it.code }
    }

    override fun reply(ctx: Context, c: AiContext, role: String, prompt: String): String {
        val p = prompt.lowercase(Locale.ROOT).trim()
        val teacher = role == PilihPeranActivity.ROLE_PENGAJAR
        return when {
            p.length < 22 && has(p, "halo", "hai", "hello", "selamat") -> greeting(c, teacher)
            teacher && has(p, "perkembangan", "ringkas kelas", "statistik") -> classProgress(ctx, c)
            teacher && has(p, "ide soal", "soal kuis") -> quizIdeas(c)
            teacher && has(p, "saran", "berikutnya") -> nextMeeting(ctx, c)
            teacher && has(p, "instruksi") -> assignmentText(c)
            has(p, "rangkum", "ringkas", "ringkasan") -> summary(ctx, c)
            has(p, "soal", "latihan", "kuis") -> practice(ctx, c)
            has(p, "tugas", "tenggat", "deadline", "jadwal") -> tasks(ctx, c, role)
            has(p, "nilai") -> grades(ctx, c, role)
            has(p, "presensi", "absen", "hadir") -> attendance(ctx, c)
            has(p, "kode", "program", "coding", "error", "bug") -> codeHelp()
            has(p, "jelaskan", "apa itu", "maksud", "sederhana", "paham", "belajar") -> explain(c)
            else -> fallback(teacher)
        }
    }

    private fun greeting(c: AiContext, teacher: Boolean): String =
        if (teacher) "Halo! Aku siap membantu mengelola ${c.className.ifBlank { "kelasmu" }}. Coba minta ringkasan perkembangan kelas atau ide soal kuis."
        else "Halo! Aku siap membantu belajar ${c.meetingTitle ?: c.className.ifBlank { "hari ini" }}. Mau dijelaskan, dirangkum, atau dilatih dengan soal?"

    private fun explain(c: AiContext): String {
        val title = c.meetingTitle
            ?: return "Pilih pertemuan pada Konteks Belajar di atas dulu, supaya penjelasanku sesuai materi yang kamu maksud."
        val pts = sentences(c.meetingDescription)
        return if (pts.isEmpty()) {
            "Materi \"$title\" belum punya deskripsi dari pengajar, jadi aku baru tahu judulnya.\n\nCoba mulai dari tiga pertanyaan ini:\n1. Apa tujuan mempelajari $title?\n2. Istilah apa yang belum kamu pahami?\n3. Contoh nyatanya seperti apa?\n\nBuka juga lampiran materi atau tanyakan ke pengajar di Diskusi."
        } else {
            "Secara sederhana, \"$title\" membahas: ${pts.take(2).joinToString(" ")}\n\nCara memahaminya: baca ulang poin tersebut, lalu jelaskan dengan kata-katamu sendiri. Kalau bisa menjelaskannya ke teman, berarti kamu sudah paham."
        }
    }

    private fun summary(ctx: Context, c: AiContext): String {
        val title = c.meetingTitle
        if (title == null) {
            val list = c.classCode?.let { code -> ClassStore.byCode(ctx, code)?.let { ClassStore.meetings(ctx, it) } } ?: emptyList()
            return if (list.isEmpty()) "Kelas ini belum punya pertemuan. Rangkuman bisa dibuat setelah pengajar menambahkan materi."
            else "Kelas ${c.className} berisi ${list.size} pertemuan:\n" + list.joinToString("\n") { "${it.number}. ${it.title}" } +
                "\n\nPilih satu pertemuan pada Konteks Belajar untuk rangkuman yang lebih rinci."
        }
        val pts = sentences(c.meetingDescription).take(4)
        return if (pts.isEmpty()) {
            "Materi \"$title\" belum memiliki deskripsi, jadi aku belum bisa merangkum isinya." +
                (if (c.fileName.isNotBlank()) " Isinya ada di lampiran ${c.fileName}." else "")
        } else {
            "Rangkuman \"$title\":\n" + pts.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n") +
                "\n\nTips: tutup catatan ini lalu tulis ulang poinnya dengan kata-katamu sendiri."
        }
    }

    private fun practice(ctx: Context, c: AiContext): String {
        val quizzes = c.classCode?.let { QuizStore.quizzes(ctx, it) } ?: emptyList()
        val questions = quizzes.flatMap { it.questions }.take(3)
        if (questions.isNotEmpty()) {
            return "Latihan dari kuis kelasmu:\n" + questions.mapIndexed { i, q -> "${i + 1}. ${q.text}" }.joinToString("\n") +
                "\n\nCoba jawab dulu tanpa melihat materi, lalu cek lewat tab Kuis."
        }
        val t = c.meetingTitle ?: c.className.ifBlank { "materi ini" }
        return "Contoh soal latihan untuk $t:\n1. Jelaskan dengan kata-katamu sendiri apa yang dimaksud dengan $t.\n2. Sebutkan tiga poin terpenting dari $t.\n3. Berikan satu contoh penerapan $t dalam kehidupan sehari-hari.\n\nKelas ini belum punya kuis, jadi soal di atas hanya contoh umum."
    }

    private fun tasks(ctx: Context, c: AiContext, role: String): String {
        if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            val all = codes(ctx, c, role).flatMap { TeacherClassStore.tasks(ctx, it) }
            if (all.isEmpty()) return "Belum ada tugas yang kamu buat."
            return "Tugas yang kamu buat:\n" + all.take(5).joinToString("\n") { "• ${it.title} (tenggat ${TaskStore.format(it.deadlineMillis)})" }
        }
        val pending = codes(ctx, c, role).flatMap { TaskStore.tasks(ctx, it) }.filter { !TaskStore.isDone(ctx, it) }.sortedBy { it.deadlineMillis }
        if (pending.isEmpty()) return "Tidak ada tugas yang menunggu. Kerja bagus!"
        return "Tugas yang belum kamu kumpulkan:\n" + pending.take(5).joinToString("\n") {
            "• ${it.title} (${it.className}), tenggat ${TaskStore.format(it.deadlineMillis)}"
        } + "\n\nKerjakan dari tenggat terdekat dulu."
    }

    private fun grades(ctx: Context, c: AiContext, role: String): String {
        if (role == PilihPeranActivity.ROLE_PENGAJAR) return "Penilaian dilakukan di menu Tugas dan tab Kuis. Aku bisa meringkas perkembangan kelas, coba ketik \"ringkas perkembangan kelas\"."
        val cs = codes(ctx, c, role)
        val t = cs.flatMap { TaskStore.tasks(ctx, it) }.mapNotNull { TaskStore.submission(ctx, it.id)?.score }
        val q = cs.flatMap { QuizStore.quizzes(ctx, it) }.mapNotNull { QuizStore.attempt(ctx, it.id)?.percent }
        val all = t + q
        if (all.isEmpty()) return "Belum ada nilai tugas atau kuis. Nilai akan muncul setelah pengajar menilai atau kamu menyelesaikan kuis."
        return "Rata-rata nilaimu ${all.average().toInt()}, dari ${t.size} tugas dan ${q.size} kuis yang sudah bernilai. Detailnya ada di tab Nilai pada Detail Kelas."
    }

    private fun attendance(ctx: Context, c: AiContext): String {
        val code = c.classCode
        val material = if (code != null && c.number > 0) TeacherClassStore.materials(ctx, code).getOrNull(c.number - 1) else null
        if (code == null || material == null) return "Pilih pertemuan pada Konteks Belajar, lalu tanyakan lagi untuk melihat status presensinya."
        val record = AttendanceStore.record(ctx, code, material.id)
        val session = AttendanceStore.session(ctx, code, material.id)
        return when {
            record != null -> "Kamu sudah presensi untuk pertemuan ${c.number} pukul ${AttendanceStore.formatTime(record.at)}."
            session?.open == true -> "Presensi pertemuan ${c.number} sedang dibuka. Isi kodenya lewat tab Presensi pada materi."
            else -> "Presensi pertemuan ${c.number} belum dibuka pengajar."
        }
    }

    private fun codeHelp(): String =
        "Untuk memahami kode program, coba urutan ini:\n1. Cari bagian masukan (input) dan keluaran (output).\n2. Baca baris demi baris, tulis apa yang terjadi pada tiap variabel.\n3. Jalankan dengan contoh kecil dan cetak nilai di tengah proses.\n4. Kalau ada error, baca pesan pertamanya dan nomor barisnya.\n\nMode demo belum bisa membaca kode yang ditempel, jadi tanyakan potongan kode yang sulit ke pengajar lewat Diskusi."

    private fun fallback(teacher: Boolean): String =
        "Maaf, aku belum paham pertanyaan itu. Di mode demo aku bisa membantu soal:\n" +
            (if (teacher) "• perkembangan kelas\n• ide soal kuis\n• saran pertemuan berikutnya\n• instruksi tugas"
            else "• penjelasan dan rangkuman materi\n• contoh soal\n• tugas, nilai, dan presensi\n• cara memahami kode") +
            "\n\nCoba pilih salah satu saran di bawah."

    // ------------------------------------------------------------------ Pengajar

    private fun classProgress(ctx: Context, c: AiContext): String {
        val code = c.classCode ?: return "Pilih kelas pada Konteks Belajar dulu, lalu tanyakan lagi."
        val cls = TeacherClassStore.byCode(ctx, code) ?: return "Kelas tidak ditemukan."
        val materials = TeacherClassStore.materials(ctx, code)
        val tasks = TeacherClassStore.tasks(ctx, code)
        val quizzes = QuizStore.quizzes(ctx, code)
        val subs = tasks.mapNotNull { TaskStore.submission(ctx, it.id) }
        val graded = subs.count { it.score != null }
        val attempts = quizzes.count { QuizStore.attempt(ctx, it.id) != null }
        return "Ringkasan ${cls.name}:\n• Peserta: ${TeacherClassStore.participantCount(ctx, code)} ${if (TeacherClassStore.isUnlimited(cls.capacity)) "(tanpa batas)" else "dari ${cls.capacity}"}\n• Materi: ${materials.size} pertemuan\n• Tugas: ${tasks.size} (${subs.size} terkumpul, $graded sudah dinilai)\n• Kuis: ${quizzes.size} ($attempts sudah dikerjakan)" +
            (if (subs.size > graded) "\n\nAda ${subs.size - graded} pengumpulan yang menunggu penilaian." else "")
    }

    private fun quizIdeas(c: AiContext): String {
        val t = c.meetingTitle ?: c.className.ifBlank { "materi" }
        return "Ide soal kuis untuk $t:\n1. Pilihan ganda tentang definisi inti $t.\n2. Pilihan ganda berupa studi kasus singkat yang memakai $t.\n3. Soal \"mana yang BUKAN\" untuk menguji pemahaman batas $t.\n4. Soal urutan langkah penerapan $t.\n\nBuat soalnya di Kelola Kelas, tab Kuis."
    }

    private fun nextMeeting(ctx: Context, c: AiContext): String {
        val code = c.classCode ?: return "Pilih kelas pada Konteks Belajar dulu."
        val materials = TeacherClassStore.materials(ctx, code)
        if (materials.isEmpty()) return "Kelas ini belum punya materi. Mulai dengan pengantar: tujuan belajar, istilah dasar, dan satu contoh sederhana."
        val last = materials.last().title
        return "Setelah \"$last\" (pertemuan ${materials.size}), pertemuan ${materials.size + 1} bisa berisi:\n1. Latihan terapan dari \"$last\".\n2. Pembahasan kesalahan umum.\n3. Kuis singkat untuk mengukur pemahaman.\n\nTambahkan di Kelola Kelas, tab Materi."
    }

    private fun assignmentText(c: AiContext): String {
        val t = c.meetingTitle ?: c.className.ifBlank { "materi" }
        return "Contoh instruksi tugas untuk $t:\n1. Pelajari materi $t sampai selesai.\n2. Jawab pertanyaan pada lembar tugas dengan kata-katamu sendiri.\n3. Sertakan satu contoh penerapan.\n4. Kumpulkan sebelum tenggat melalui aplikasi.\n\nSalin ke kolom Instruksi saat menambah tugas (satu langkah per baris)."
    }
}
