package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailKuisPelajarInstitusiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val quizTitle = intent.getStringExtra(EXTRA_QUIZ_TITLE) ?: "Kuis Sistem Cerdas"
        val meetingText = intent.getStringExtra(EXTRA_MEETING) ?: "Pertemuan 2"

        setContentView(createScreen(quizTitle, meetingText))
    }

    private fun createScreen(quizTitle: String, meetingText: String): View {
        val bgColor = Color.rgb(245, 250, 255)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Main Scrollable Content
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(24))
            setBackgroundColor(bgColor)
        }

        // 1. Header Top (Back button, Title "Kuis Sistem Cerdas", Options menu)
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val backButton = TextView(this).apply {
            text = "‹"
            textSize = 36f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
            includeFontPadding = false
            setOnClickListener { finish() }
        }
        headerLayout.addView(backButton, LinearLayout.LayoutParams(dp(32), dp(44)))

        val headerTitle = TextView(this).apply {
            text = quizTitle
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        headerLayout.addView(headerTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val optionsMenu = TextView(this).apply {
            text = "⋮"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
        }
        headerLayout.addView(optionsMenu, LinearLayout.LayoutParams(dp(36), dp(36)))

        contentLayout.addView(headerLayout)

        // Meeting Subtitle
        val meetingTv = TextView(this).apply {
            text = meetingText
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val mtParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
            marginStart = dp(8)
        }
        contentLayout.addView(meetingTv, mtParams)

        // 2. Info Card (20 menit | Nilai minimal 70)
        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val icParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val durationCol = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val clockIcon = TextView(this).apply {
            text = "⏱️"
            textSize = 18f
        }
        durationCol.addView(clockIcon)
        val durationVal = TextView(this).apply {
            text = "20 menit"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(10), 0, 0, 0)
        }
        durationCol.addView(durationVal)
        infoCard.addView(durationCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val scoreCol = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val shieldIcon = TextView(this).apply {
            text = "🛡️"
            textSize = 18f
        }
        scoreCol.addView(shieldIcon)
        val scoreVal = TextView(this).apply {
            text = "Nilai minimal 70"
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(dp(10), 0, 0, 0)
        }
        scoreCol.addView(scoreVal)
        infoCard.addView(scoreCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f))

        contentLayout.addView(infoCard, icParams)

        // 3. Section: Instruksi
        val instCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val inParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val instTitle = TextView(this).apply {
            text = "Instruksi"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        instCard.addView(instTitle)

        val instructions = listOf(
            "1. Baca setiap soal dengan teliti.",
            "2. Pilih jawaban yang paling tepat.",
            "3. Kuis akan otomatis dikirim setelah selesai."
        )

        for (instr in instructions) {
            val tv = TextView(this).apply {
                text = instr
                textSize = 13f
                setTextColor(Color.rgb(117, 139, 169))
            }
            val ipParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(8)
            }
            instCard.addView(tv, ipParams)
        }

        contentLayout.addView(instCard, inParams)

        // 4. Primary Button: Mulai Kuis (Launches MulaiKuisPelajarActivity)
        val btnMulai = TextView(this).apply {
            text = "🔀 Mulai Kuis"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@DetailKuisPelajarInstitusiActivity, MulaiKuisPelajarActivity::class.java).apply {
                    putExtra(MulaiKuisPelajarActivity.EXTRA_QUIZ_TITLE, quizTitle)
                }
                startActivity(intent)
            }
        }
        val bmParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(32)
        }
        contentLayout.addView(btnMulai, bmParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    private fun roundedBackground(color: Int, radiusDp: Int, strokeColor: Int? = null, strokeWidthDp: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
            if (strokeColor != null) {
                setStroke(dp(strokeWidthDp), strokeColor)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    companion object {
        const val EXTRA_QUIZ_TITLE = "extra_quiz_title"
        const val EXTRA_MEETING = "extra_meeting"
    }
}
