package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Hasil kuis: nilai, jumlah benar/salah, dan pembahasan tiap soal. */
class HasilKuisMandiriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var quizId = ""
    private var showReview = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        quizId = intent.getStringExtra(EXTRA_QUIZ_ID) ?: ""
        showReview = savedInstanceState?.getBoolean(STATE_REVIEW) ?: false
        render()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_REVIEW, showReview)
    }

    private fun render() {
        val q = QuizStore.byId(this, quizId)
        val a = QuizStore.attempt(this, quizId)
        if (q == null || a == null) {
            UiKit.toast(this, "Hasil kuis tidak ditemukan")
            finish()
            return
        }
        setContentView(build(q, a))
    }

    private fun build(q: Quiz, a: QuizAttempt): View {
        val passed = QuizStore.isPassed(q, a)
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Hasil Kuis") { finish() })
        val (scroll, content) = UiKit.scroll(this, padTop = 16)

        // Piala + nilai
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val circle = FrameLayout(this).apply { background = UiKit.oval(Color.parseColor("#FFC83D")) }
        circle.addView(
            UiKit.tv(this, "🏆", 56f, Color.WHITE).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
        )
        top.addView(circle, LinearLayout.LayoutParams(px(120), px(120)))
        top.addView(
            UiKit.tv(this, q.title, 14f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 20)
        )
        top.addView(
            UiKit.tv(this, "Nilai Kuis", 14f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        top.addView(
            UiKit.tv(this, "${a.correct}/${a.total} (${a.percent}%)", 30f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        top.addView(
            if (passed) ClassUi.chip(this, "Lulus", Color.WHITE, UiKit.SUCCESS)
            else ClassUi.chip(this, "Belum Lulus", Color.WHITE, UiKit.DANGER),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 12)
        )
        content.addView(top, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))

        // Benar / salah
        val stats = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(px(16), px(16), px(16), px(16))
        }
        stats.addView(statColumn("Jawaban Benar", a.correct), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        stats.addView(UiKit.divider(this), UiKit.params(this, px(1), px(48), start = 8, end = 16))
        stats.addView(statColumn("Jawaban Salah", a.total - a.correct), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        content.addView(stats, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 24))

        // Pembahasan
        if (showReview) {
            content.addView(
                UiKit.tv(this, "Pembahasan", 17f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22)
            )
            q.questions.forEachIndexed { i, question ->
                content.addView(reviewCard(i, question, a.answers.getOrNull(i) ?: -1), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            }
        }
        root.addView(scroll, UiKit.scrollParams())

        val review = UiKit.primaryButton(this, if (showReview) "Sembunyikan Pembahasan" else "Lihat Pembahasan") {
            showReview = !showReview
            render()
        }
        val home = UiKit.outlineButton(this, "Kembali ke Beranda") { goHome() }
        home.layoutParams = UiKit.params(this, UiKit.MATCH, px(56), top = 10)
        root.addView(UiKit.bottomBar(this, review, home))
        return root
    }

    private fun statColumn(label: String, value: Int): LinearLayout {
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, label, 13f, UiKit.MUTED))
        col.addView(UiKit.tv(this, "$value", 24f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        return col
    }

    private fun reviewCard(i: Int, q: QuizQuestion, chosen: Int): View {
        val card = UiKit.card(this).apply { setPadding(px(14), px(14), px(14), px(14)) }
        card.addView(UiKit.tv(this, "Soal ${i + 1}", 12f, UiKit.MUTED, true))
        card.addView(
            UiKit.tv(this, q.text, 15f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4)
        )
        q.options.forEachIndexed { k, text ->
            val isCorrect = k == q.correctIndex
            val wrongPick = chosen == k && !isCorrect
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = when {
                    isCorrect -> UiKit.rounded(this@HasilKuisMandiriActivity, UiKit.SUCCESS_SOFT, 10, UiKit.SUCCESS)
                    wrongPick -> UiKit.rounded(this@HasilKuisMandiriActivity, UiKit.DANGER_SOFT, 10, UiKit.DANGER)
                    else -> UiKit.rounded(this@HasilKuisMandiriActivity, Color.WHITE, 10, UiKit.BORDER)
                }
                setPadding(px(12), px(10), px(12), px(10))
            }
            row.addView(UiKit.tv(this, "ABCD"[k].toString(), 13f, UiKit.MUTED, true), LinearLayout.LayoutParams(px(24), UiKit.WRAP))
            row.addView(UiKit.tv(this, text, 13.5f, UiKit.NAVY), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
            if (isCorrect) {
                row.addView(UiKit.tv(this, "Benar", 11.5f, UiKit.SUCCESS, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8))
            } else if (wrongPick) {
                row.addView(UiKit.tv(this, "Jawabanmu", 11.5f, UiKit.DANGER, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8))
            }
            card.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        }
        if (chosen < 0) {
            card.addView(
                UiKit.tv(this, "Soal ini tidak dijawab.", 12f, UiKit.DANGER, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8)
            )
        }
        return card
    }

    private fun goHome() {
        val intent = UiKit.withProfile(Intent(this, HomeActivity::class.java), role, mode).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_QUIZ_ID = "extra_hasil_kuis_mandiri_id"
        private const val STATE_REVIEW = "state_hasil_review"
    }
}
