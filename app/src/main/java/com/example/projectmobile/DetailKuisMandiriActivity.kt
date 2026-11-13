package com.example.projectmobile

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Ringkasan kuis sebelum dikerjakan: durasi, nilai minimal, dan instruksi. */
class DetailKuisMandiriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var quizId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        quizId = intent.getStringExtra(EXTRA_QUIZ_ID) ?: ""
    }

    override fun onResume() {
        super.onResume()
        val quiz = QuizStore.byId(this, quizId)
        if (quiz == null) {
            finish()
            return
        }
        setContentView(build(quiz))
    }

    private fun build(q: Quiz): View {
        val attempt = QuizStore.attempt(this, q.id)
        val className = ClassStore.byCode(this, q.classCode)?.name ?: ""
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, q.title) { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(
            UiKit.tv(this, className, 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 14)
        )

        // Durasi & nilai minimal
        val meta = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(16), px(16), px(16))
        }
        meta.addView(UiKit.icon(this, R.drawable.ic_clock, 22, UiKit.PRIMARY), LinearLayout.LayoutParams(px(22), px(22)))
        meta.addView(
            UiKit.tv(this, "${q.durationMin} menit", 15f, UiKit.NAVY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 10, weight = 1f)
        )
        meta.addView(UiKit.icon(this, R.drawable.ic_assignment_check, 20, UiKit.MUTED), LinearLayout.LayoutParams(px(20), px(20)))
        meta.addView(
            UiKit.tv(this, "Nilai minimal ${q.passScore}", 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 6)
        )
        content.addView(meta)

        // Instruksi
        val ins = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        ins.addView(UiKit.tv(this, "Instruksi", 16f, UiKit.NAVY, true))
        val lines = listOf(
            "1. Kuis terdiri dari ${q.questions.size} soal pilihan ganda.",
            "2. Pilih jawaban yang paling tepat, lalu tekan Selanjutnya.",
            "3. Kuis otomatis dikirim saat waktu habis atau setelah kamu menekan Selesai."
        )
        for (l in lines) {
            ins.addView(
                UiKit.tv(this, l, 13.5f, UiKit.MUTED),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8)
            )
        }
        content.addView(ins, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))

        // Hasil terakhir
        if (attempt != null) {
            val passed = QuizStore.isPassed(q, attempt)
            val res = UiKit.card(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(px(16), px(14), px(16), px(14))
                isClickable = true
                isFocusable = true
                setOnClickListener { openHasil(q) }
            }
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(UiKit.tv(this, "Hasil terakhir", 12f, UiKit.MUTED))
            col.addView(
                UiKit.tv(this, "${attempt.correct}/${attempt.total} benar  •  ${attempt.percent}%", 15f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
            )
            res.addView(col, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
            res.addView(
                if (passed) ClassUi.chip(this, "Lulus", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)
                else ClassUi.chip(this, "Belum Lulus", UiKit.DANGER, UiKit.DANGER_SOFT)
            )
            content.addView(res, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(
            UiKit.bottomBar(
                this,
                UiKit.primaryButton(this, if (attempt == null) "Mulai Kuis" else "Coba Lagi") {
                    val next = UiKit.withProfile(Intent(this, MulaiKuisMandiriActivity::class.java), role, mode)
                    next.putExtra(MulaiKuisMandiriActivity.EXTRA_QUIZ_ID, q.id)
                    startActivity(next)
                }
            )
        )
        return root
    }

    private fun openHasil(q: Quiz) {
        val next = UiKit.withProfile(Intent(this, HasilKuisMandiriActivity::class.java), role, mode)
        next.putExtra(HasilKuisMandiriActivity.EXTRA_QUIZ_ID, q.id)
        startActivity(next)
    }

    companion object {
        const val EXTRA_QUIZ_ID = "extra_detail_kuis_mandiri_id"
    }
}
