package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

/** Pengajar melihat hasil pengerjaan kuis dan ketepatan jawaban per soal. */
class HasilKuisPengajarActivity : AppCompatActivity() {

    private var quizId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizId = intent.getStringExtra(EXTRA_QUIZ_ID) ?: ""
    }

    override fun onResume() {
        super.onResume()
        val q = QuizStore.byId(this, quizId)
        if (q == null) {
            finish()
            return
        }
        setContentView(build(q))
    }

    private fun statColumn(label: String, value: String): LinearLayout {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        col.addView(UiKit.tv(this, value, 22f, UiKit.NAVY, true))
        col.addView(UiKit.tv(this, label, 12f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4))
        return col
    }

    private fun build(q: Quiz): View {
        val attempts = listOfNotNull(QuizStore.attempt(this, q.id))
        val average = if (attempts.isEmpty()) 0 else attempts.map { it.percent }.average().roundToInt()
        val passed = attempts.count { QuizStore.isPassed(q, it) }
        val className = TeacherClassStore.byCode(this, q.classCode)?.name ?: ""

        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Hasil Kuis") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        // Ringkasan
        val summary = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        summary.addView(UiKit.tv(this, q.title, 18f, UiKit.NAVY, true))
        summary.addView(
            UiKit.tv(this, "$className  •  ${q.questions.size} soal  •  ${q.durationMin} menit  •  Min. ${q.passScore}", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4)
        )
        summary.addView(UiKit.divider(this), UiKit.params(this, UiKit.MATCH, px(1), top = 14, bottom = 14))
        val stats = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        stats.addView(statColumn("Mengerjakan", "${attempts.size}"), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        stats.addView(statColumn("Rata-rata", if (attempts.isEmpty()) "-" else "$average"), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        stats.addView(statColumn("Lulus", "$passed"), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        summary.addView(stats)
        content.addView(summary, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6))

        if (attempts.isEmpty()) {
            val empty = UiKit.card(this).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(px(24), px(28), px(24), px(28))
            }
            empty.addView(UiKit.badge(this, R.drawable.ic_assignment_check, UiKit.PRIMARY, UiKit.SOFT, 56, 28, 26))
            empty.addView(
                UiKit.tv(this, "Belum ada yang mengerjakan", 15f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14)
            )
            empty.addView(
                UiKit.tv(this, "Hasil pelajar akan tampil di sini setelah kuis dikerjakan.", 12.5f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
            )
            content.addView(empty, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))
            root.addView(scroll, UiKit.scrollParams())
            return root
        }

        // Hasil pelajar
        content.addView(
            UiKit.tv(this, "Hasil Pelajar", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22)
        )
        val name = ProfileStore.get(this, PilihPeranActivity.ROLE_PELAJAR, ProfileStore.NAMA).ifBlank { "Pelajar" }
        for (a in attempts) {
            val ok = QuizStore.isPassed(q, a)
            val card = UiKit.card(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(px(16), px(14), px(16), px(14))
            }
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(UiKit.tv(this, name, 15f, UiKit.NAVY, true))
            col.addView(
                UiKit.tv(this, "${a.correct}/${a.total} benar  •  ${TaskStore.format(a.finishedAt)}", 12f, UiKit.MUTED),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3)
            )
            card.addView(col, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
            card.addView(
                UiKit.tv(this, "${a.percent}", 22f, if (ok) UiKit.SUCCESS else UiKit.DANGER, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, end = 10)
            )
            card.addView(
                if (ok) ClassUi.chip(this, "Lulus", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)
                else ClassUi.chip(this, "Belum Lulus", UiKit.DANGER, UiKit.DANGER_SOFT)
            )
            content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        }

        // Analisis per soal
        content.addView(
            UiKit.tv(this, "Analisis Soal", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22)
        )
        q.questions.forEachIndexed { i, question ->
            val correct = attempts.count { it.answers.getOrNull(i) == question.correctIndex }
            val pct = correct * 100 / attempts.size
            val row = UiKit.card(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(px(16), px(14), px(16), px(14))
            }
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(UiKit.tv(this, "Soal ${i + 1}", 12f, UiKit.MUTED, true))
            col.addView(
                UiKit.tv(this, question.text, 14f, UiKit.NAVY).apply {
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 2)
            )
            row.addView(col, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
            row.addView(
                UiKit.tv(this, "$pct% benar", 13f, if (pct >= 60) UiKit.SUCCESS else UiKit.DANGER, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 10)
            )
            content.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10))
        }
        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    companion object {
        const val EXTRA_QUIZ_ID = "extra_hasil_kuis_pengajar_id"
    }
}
