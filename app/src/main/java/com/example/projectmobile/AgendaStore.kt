package com.example.projectmobile

import android.content.Context

data class AgendaItem(
    val kind: String,
    val title: String,
    val subtitle: String,
    val dateText: String,
    val classCode: String,
    val refId: String,
    val number: Int
)

/** Agenda mendatang pelajar mandiri: presensi yang dibuka, tugas belum dikumpulkan, kuis belum dikerjakan. */
object AgendaStore {

    const val KIND_PRESENSI = "presensi"
    const val KIND_TUGAS = "tugas"
    const val KIND_KUIS = "kuis"

    fun upcoming(ctx: Context, limit: Int): List<AgendaItem> {
        val now = System.currentTimeMillis()
        val joined = ClassStore.joined(ctx)
        val out = mutableListOf<AgendaItem>()

        // Presensi yang sedang dibuka dan belum diisi
        for (c in joined) {
            TeacherClassStore.materials(ctx, c.code).forEachIndexed { i, m ->
                val s = AttendanceStore.session(ctx, c.code, m.id)
                if (s != null && s.open && AttendanceStore.record(ctx, c.code, m.id) == null) {
                    out.add(
                        AgendaItem(
                            KIND_PRESENSI, "Presensi: ${m.title}", c.name,
                            "Dibuka ${AttendanceStore.formatTime(s.openedAt)}", c.code, m.id, i + 1
                        )
                    )
                }
            }
        }

        // Tugas yang belum dikumpulkan dan belum lewat tenggat
        TaskStore.tasks(ctx)
            .filter { !TaskStore.isDone(ctx, it) && it.deadlineMillis >= now }
            .sortedBy { it.deadlineMillis }
            .forEach {
                out.add(AgendaItem(KIND_TUGAS, "Tugas: ${it.title}", it.className, TaskStore.format(it.deadlineMillis), it.classCode, it.id, 0))
            }

        // Kuis yang belum dikerjakan
        for (c in joined) {
            QuizStore.quizzes(ctx, c.code)
                .filter { QuizStore.attempt(ctx, it.id) == null }
                .forEach { out.add(AgendaItem(KIND_KUIS, "Kuis: ${it.title}", c.name, "Belum dikerjakan", c.code, it.id, 0)) }
        }
        return out.take(limit)
    }
}
