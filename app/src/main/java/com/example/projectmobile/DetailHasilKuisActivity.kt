package com.example.snapchatsphlasscreen

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

class DetailHasilKuisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val studentName = intent.getStringExtra(EXTRA_STUDENT_NAME) ?: "Andi Pratama"
        val studentNim = intent.getStringExtra(EXTRA_STUDENT_NIM) ?: "2201001"
        val studentScore = intent.getIntExtra(EXTRA_STUDENT_SCORE, 90)

        setContentView(createScreen(studentName, studentNim, studentScore))
    }

    private fun createScreen(studentName: String, studentNim: String, studentScore: Int): View {
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

        // 1. Header Top (Back button, Title "Detail Hasil Kuis", Options menu)
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
            text = "Detail Hasil Kuis"
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

        // 2. Student Header Card
        val studentCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val studentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val initials = studentName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        val avatar = TextView(this).apply {
            text = initials
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(215, 230, 255), 24)
        }
        studentCard.addView(avatar, LinearLayout.LayoutParams(dp(48), dp(48)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }
        val nameTv = TextView(this).apply {
            text = studentName
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(nameTv)

        val nimTv = TextView(this).apply {
            text = studentNim
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val nParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(nimTv, nParams)

        studentCard.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        // Score Box (light green)
        val scoreBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(14), dp(10), dp(14), dp(10))
            background = roundedBackground(Color.rgb(232, 245, 233), 12)
        }
        val scoreVal = TextView(this).apply {
            text = "$studentScore"
            textSize = 22f
            setTextColor(Color.rgb(46, 125, 50))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        scoreBox.addView(scoreVal)
        val scoreSub = TextView(this).apply {
            text = "Dari 100"
            textSize = 10f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        scoreBox.addView(scoreSub)

        studentCard.addView(scoreBox)
        contentLayout.addView(studentCard, studentParams)

        // 3. Section: Informasi Pengerjaan
        val infoHeader = createSectionHeader("Informasi Pengerjaan")
        val infoHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(infoHeader, infoHeaderParams)

        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val icParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        infoCard.addView(createInfoItem("⏱️", "Waktu Pengerjaan", "18 menit"), createInfoItemParams(0))
        infoCard.addView(createInfoItem("📅", "Tanggal", "20 Jun 2025, 14:30"), createInfoItemParams(dp(16)))

        contentLayout.addView(infoCard, icParams)

        // 4. Section: Jawaban Siswa
        val answerHeader = createSectionHeader("Jawaban Siswa")
        val answerHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(answerHeader, answerHeaderParams)

        val answersContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val ansParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        answersContainer.addView(createAnswerCard("1. Apa kepanjangan dari BFS?", "c. Breadth First Search", true))
        answersContainer.addView(createAnswerCard("2. Kompleksitas waktu BFS adalah...", "b. O(V + E)", true))
        answersContainer.addView(createAnswerCard("3. Manakah struktur data yang digunakan pada BFS?", "a. Queue", false))

        contentLayout.addView(answersContainer, ansParams)

        // 5. Button: Kembali ke Daftar Nilai
        val btnKembali = TextView(this).apply {
            text = "Kembali ke Daftar Nilai"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { finish() }
        }
        val bkParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(28)
        }
        contentLayout.addView(btnKembali, bkParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    private fun createSectionHeader(title: String): TextView {
        return TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun createInfoItem(icon: String, label: String, value: String): View {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 18f
            gravity = Gravity.CENTER
        }
        col.addView(iconTv, LinearLayout.LayoutParams(dp(28), dp(28)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        val labelTv = TextView(this).apply {
            text = label
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(labelTv)

        val valueTv = TextView(this).apply {
            text = value
            textSize = 13f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(valueTv)

        col.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return col
    }

    private fun createInfoItemParams(marginStart: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            this.marginStart = marginStart
        }
    }

    private fun createAnswerCard(question: String, answer: String, isCorrect: Boolean): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val qTv = TextView(this).apply {
            text = question
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        card.addView(qTv)

        val ansBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = roundedBackground(if (isCorrect) Color.rgb(232, 245, 233) else Color.rgb(255, 235, 238), 12)
        }
        val abParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val iconTv = TextView(this).apply {
            text = if (isCorrect) "✓" else "✕"
            textSize = 16f
            setTextColor(if (isCorrect) Color.rgb(46, 125, 50) else Color.rgb(198, 40, 40))
            setTypeface(null, Typeface.BOLD)
        }
        ansBox.addView(iconTv)

        val ansTv = TextView(this).apply {
            text = answer
            textSize = 14f
            setTextColor(if (isCorrect) Color.rgb(46, 125, 50) else Color.rgb(198, 40, 40))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(10), 0, 0, 0)
        }
        ansBox.addView(ansTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val badgeIcon = TextView(this).apply {
            text = if (isCorrect) "✓" else "✕"
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(if (isCorrect) Color.rgb(46, 125, 50) else Color.rgb(198, 40, 40))
            background = roundedBackground(Color.WHITE, 12)
        }
        ansBox.addView(badgeIcon, LinearLayout.LayoutParams(dp(28), dp(28)))

        card.addView(ansBox, abParams)

        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(12)
            }
            layoutParams = lp
            addView(card)
        }
        return wrapper
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
        const val EXTRA_STUDENT_NAME = "extra_student_name"
        const val EXTRA_STUDENT_NIM = "extra_student_nim"
        const val EXTRA_STUDENT_SCORE = "extra_student_score"
    }
}