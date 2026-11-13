package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Mengerjakan kuis: satu soal per layar, dengan timer, dan kirim otomatis saat waktu habis. */
class MulaiKuisMandiriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var quiz: Quiz? = null
    private var answers = IntArray(0)
    private var index = 0
    private var endAt = 0L
    private var timer: CountDownTimer? = null
    private var tvTimer: TextView? = null
    private var submitted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI

        val q = intent.getStringExtra(EXTRA_QUIZ_ID)?.let { QuizStore.byId(this, it) }
        if (q == null || q.questions.isEmpty()) {
            UiKit.toast(this, "Kuis tidak ditemukan")
            finish()
            return
        }
        quiz = q
        answers = savedInstanceState?.getIntArray(STATE_ANSWERS)?.takeIf { it.size == q.questions.size }
            ?: IntArray(q.questions.size) { -1 }
        index = (savedInstanceState?.getInt(STATE_INDEX) ?: 0).coerceIn(0, q.questions.size - 1)
        endAt = if (savedInstanceState != null && savedInstanceState.containsKey(STATE_END_AT)) {
            savedInstanceState.getLong(STATE_END_AT)
        } else {
            System.currentTimeMillis() + q.durationMin * 60_000L
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                confirmExit()
            }
        })

        render()
        startTimer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(STATE_ANSWERS, answers)
        outState.putInt(STATE_INDEX, index)
        outState.putLong(STATE_END_AT, endAt)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }

    private fun format(ms: Long): String {
        val total = (ms / 1000L).coerceAtLeast(0L)
        return String.format("%02d:%02d", total / 60, total % 60)
    }

    private fun startTimer() {
        timer?.cancel()
        val remaining = endAt - System.currentTimeMillis()
        if (remaining <= 0L) {
            submit(true)
            return
        }
        timer = object : CountDownTimer(remaining, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer?.text = format(millisUntilFinished)
            }

            override fun onFinish() {
                submit(true)
            }
        }.start()
    }

    private fun render() {
        val q = quiz ?: return
        setContentView(build(q))
    }

    private fun build(q: Quiz): View {
        val total = q.questions.size
        val question = q.questions[index]
        val root = UiKit.root(this)

        // Bar atas: kembali, nomor soal, timer
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(20), px(16), px(20), px(8))
        }
        top.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_back)
                contentDescription = "Kembali"
                isClickable = true
                isFocusable = true
                setOnClickListener { confirmExit() }
            }
        )
        top.addView(
            UiKit.tv(this, "Soal ${index + 1} dari $total", 15f, UiKit.PRIMARY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f)
        )
        val timerView = UiKit.tv(this, format(endAt - System.currentTimeMillis()), 15f, UiKit.DANGER, true)
        tvTimer = timerView
        top.addView(timerView)
        root.addView(top)

        // Progres
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = UiKit.rounded(this@MulaiKuisMandiriActivity, UiKit.SOFT, 4)
        }
        val fill = View(this).apply {
            background = UiKit.rounded(this@MulaiKuisMandiriActivity, UiKit.PRIMARY, 4)
        }
        bar.addView(fill, LinearLayout.LayoutParams(0, px(6), (index + 1).toFloat()))
        bar.addView(View(this), LinearLayout.LayoutParams(0, px(6), (total - index - 1).toFloat()))
        root.addView(bar, UiKit.params(this, UiKit.MATCH, px(6), start = 20, end = 20, top = 4))

        val (scroll, content) = UiKit.scroll(this)
        content.addView(
            UiKit.tv(this, "${index + 1}. ${question.text}", 20f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16, bottom = 8)
        )
        for (i in question.options.indices) {
            content.addView(
                optionCard(i, question.options[i], answers[index] == i),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12)
            )
        }
        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val prev = UiKit.outlineButton(this, "Sebelumnya") {
            if (index > 0) {
                index--
                render()
            }
        }
        if (index == 0) prev.alpha = 0.4f
        val isLast = index == total - 1
        val next = UiKit.primaryButton(this, if (isLast) "Selesai" else "Selanjutnya") {
            if (isLast) {
                finishQuiz()
            } else {
                index++
                render()
            }
        }
        row.addView(prev, UiKit.params(this, 0, px(56), end = 6, weight = 1f))
        row.addView(next, UiKit.params(this, 0, px(56), start = 6, weight = 1f))
        root.addView(UiKit.bottomBar(this, row))
        return root
    }

    private fun optionCard(i: Int, text: String, selected: Boolean): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = if (selected) UiKit.rounded(this@MulaiKuisMandiriActivity, UiKit.SOFT, 14, UiKit.PRIMARY)
            else UiKit.rounded(this@MulaiKuisMandiriActivity, Color.WHITE, 14, UiKit.BORDER)
            setPadding(px(14), px(14), px(14), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                answers[index] = i
                render()
            }
        }
        val letter = UiKit.tv(this, "ABCD"[i].toString(), 14f, if (selected) Color.WHITE else UiKit.MUTED, true).apply {
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@MulaiKuisMandiriActivity, if (selected) UiKit.PRIMARY else UiKit.SOFT, 18)
        }
        card.addView(letter, LinearLayout.LayoutParams(px(36), px(36)))
        card.addView(
            UiKit.tv(this, text, 15f, UiKit.NAVY),
            UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f)
        )
        return card
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setTitle("Keluar dari kuis?")
            .setMessage("Jawabanmu tidak akan disimpan.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Keluar") { _, _ ->
                timer?.cancel()
                finish()
            }
            .show()
    }

    private fun finishQuiz() {
        val unanswered = answers.count { it < 0 }
        if (unanswered > 0) {
            AlertDialog.Builder(this)
                .setTitle("Kirim kuis?")
                .setMessage("Masih ada $unanswered soal yang belum dijawab.")
                .setNegativeButton("Kembali", null)
                .setPositiveButton("Kirim") { _, _ -> submit(false) }
                .show()
        } else {
            submit(false)
        }
    }

    private fun submit(timeUp: Boolean) {
        val q = quiz ?: return
        if (submitted) return
        submitted = true
        timer?.cancel()
        QuizStore.saveAttempt(this, q, answers.toList())
        if (timeUp) UiKit.toast(this, "Waktu habis, kuis dikirim otomatis")
        val next = UiKit.withProfile(Intent(this, HasilKuisMandiriActivity::class.java), role, mode)
        next.putExtra(HasilKuisMandiriActivity.EXTRA_QUIZ_ID, q.id)
        startActivity(next)
        finish()
    }

    companion object {
        const val EXTRA_QUIZ_ID = "extra_mulai_kuis_mandiri_id"
        private const val STATE_ANSWERS = "state_kuis_answers"
        private const val STATE_INDEX = "state_kuis_index"
        private const val STATE_END_AT = "state_kuis_end_at"
    }
}
