package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Daftar kuis dari pengajar untuk satu kelas mandiri (sisi pelajar). */
class KuisPelajarMandiriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
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
        val quizzes = QuizStore.quizzes(this, info.code)
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Kuis") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(
            UiKit.tv(this, info.name, 14f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 8)
        )

        if (quizzes.isEmpty()) {
            val empty = UiKit.card(this).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(px(24), px(32), px(24), px(32))
            }
            empty.addView(UiKit.badge(this, R.drawable.ic_assignment_check, UiKit.PRIMARY, UiKit.SOFT, 64, 32, 30))
            empty.addView(
                UiKit.tv(this, "Belum Ada Kuis", 16f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 16)
            )
            empty.addView(
                UiKit.tv(this, "Kuis dari pengajar akan tampil di sini.", 13f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
            )
            content.addView(empty, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        } else {
            for (q in quizzes) content.addView(quizCard(q), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        }
        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun quizCard(q: Quiz): View {
        val attempt = QuizStore.attempt(this, q.id)
        val card = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(14), px(14), px(14), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next = UiKit.withProfile(Intent(this@KuisPelajarMandiriActivity, DetailKuisMandiriActivity::class.java), role, mode)
                next.putExtra(DetailKuisMandiriActivity.EXTRA_QUIZ_ID, q.id)
                startActivity(next)
            }
        }
        card.addView(UiKit.badge(this, R.drawable.ic_assignment_check, Color.WHITE, UiKit.PRIMARY, 48, 12, 24))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, q.title, 15f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "${q.questions.size} soal  •  ${q.durationMin} menit  •  Min. ${q.passScore}", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
        card.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        if (attempt == null) {
            card.addView(ClassUi.chip(this, "Belum dikerjakan"))
        } else if (QuizStore.isPassed(q, attempt)) {
            card.addView(ClassUi.chip(this, "Nilai ${attempt.percent}", UiKit.SUCCESS, UiKit.SUCCESS_SOFT))
        } else {
            card.addView(ClassUi.chip(this, "Nilai ${attempt.percent}", UiKit.DANGER, UiKit.DANGER_SOFT))
        }
        return card
    }

    companion object {
        const val EXTRA_CODE = "extra_kuis_pelajar_code"
    }
}
