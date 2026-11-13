package com.example.projectmobile

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

/** Tab "Nilai" pelajar: nilai tugas dan kuis dalam satu kelas, beserta rata-ratanya. */
class NilaiPelajarActivity : AppCompatActivity() {

    private var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code = intent.getStringExtra(EXTRA_CODE) ?: ""
    }

    override fun onResume() {
        super.onResume()
        val info = ClassStore.byCode(this, code)
        if (info == null) {
            finish()
            return
        }
        setContentView(build(info))
    }

    private fun build(info: ClassInfo): View {
        val tasks = TaskStore.tasks(this, info.code)
        val quizzes = QuizStore.quizzes(this, info.code)
        val taskScores = tasks.mapNotNull { TaskStore.submission(this, it.id)?.score }
        val quizScores = quizzes.mapNotNull { QuizStore.attempt(this, it.id)?.percent }
        val all = taskScores + quizScores

        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Nilai") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(
            UiKit.tv(this, info.name, 14f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 12)
        )

        // Ringkasan
        val summary = UiKit.card(this).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(px(16), px(20), px(16), px(20))
        }
        summary.addView(UiKit.tv(this, "Rata-rata Nilai", 13f, UiKit.MUTED))
        summary.addView(
            UiKit.tv(this, if (all.isEmpty()) "-" else "${all.average().roundToInt()}", 40f, UiKit.PRIMARY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        summary.addView(
            UiKit.tv(
                this,
                if (all.isEmpty()) "Belum ada nilai tugas atau kuis."
                else "Tugas dinilai ${taskScores.size}/${tasks.size}  •  Kuis dikerjakan ${quizScores.size}/${quizzes.size}",
                12.5f, UiKit.MUTED
            ),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        content.addView(summary)

        // Tugas
        content.addView(UiKit.tv(this, "Tugas", 16f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22))
        if (tasks.isEmpty()) {
            content.addView(emptyText("Belum ada tugas pada kelas ini."), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        }
        for (t in tasks) {
            val sub = TaskStore.submission(this, t.id)
            val right = if (sub?.score != null) "Nilai ${sub.score}" else TaskStore.statusOf(this, t)
            content.addView(
                row(t.title, right, sub?.score != null, sub?.feedback ?: ""),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
            )
        }

        // Kuis
        content.addView(UiKit.tv(this, "Kuis", 16f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22))
        if (quizzes.isEmpty()) {
            content.addView(emptyText("Belum ada kuis pada kelas ini."), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        }
        for (z in quizzes) {
            val a = QuizStore.attempt(this, z.id)
            val right = if (a != null) "Nilai ${a.percent}" else "Belum dikerjakan"
            val note = if (a != null) (if (QuizStore.isPassed(z, a)) "Lulus" else "Belum lulus") else ""
            content.addView(row(z.title, right, a != null, note), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10))
        }
        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun emptyText(text: String): View = UiKit.tv(this, text, 13f, UiKit.MUTED)

    private fun row(title: String, right: String, graded: Boolean, note: String): View {
        val card = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(14), px(16), px(14))
        }
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, title, 14.5f, UiKit.NAVY, true))
        if (note.isNotBlank()) {
            col.addView(
                UiKit.tv(this, note, 12f, UiKit.MUTED),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 3)
            )
        }
        card.addView(col, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        card.addView(
            UiKit.tv(this, right, 13.5f, if (graded) UiKit.SUCCESS else UiKit.MUTED, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 10)
        )
        return card
    }

    companion object {
        const val EXTRA_CODE = "extra_nilai_pelajar_code"
    }
}
