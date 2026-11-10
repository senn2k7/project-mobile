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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MulaiKuisPelajarActivity : AppCompatActivity() {

    private var selectedOptionIndex = 1 // default option B selected as in mockup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(createScreen())
    }

    private fun createScreen(): View {
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

        // 1. Header Top (Back button, Title "Soal 1 dari 10", Timer "19:45")
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
            text = "Soal 1 dari 10"
            textSize = 18f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(12), 0, 0, 0)
        }
        headerLayout.addView(headerTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val timerTv = TextView(this).apply {
            text = "⏱️ 19:45"
            textSize = 15f
            setTextColor(Color.rgb(244, 67, 54))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }
        headerLayout.addView(timerTv)

        contentLayout.addView(headerLayout)

        // Progress bar indicator
        val progressBarBg = LinearLayout(this).apply {
            background = roundedBackground(Color.rgb(220, 230, 245), 4)
        }
        val progressBarFill = TextView(this).apply {
            background = roundedBackground(Color.rgb(33, 96, 245), 4)
        }
        progressBarBg.addView(progressBarFill, LinearLayout.LayoutParams(dp(100), dp(6)))
        val pbParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(6)).apply {
            topMargin = dp(16)
            bottomMargin = dp(24)
        }
        contentLayout.addView(progressBarBg, pbParams)

        // 2. Question Text
        val questionTv = TextView(this).apply {
            text = "1. Apa yang dimaksud dengan machine learning?"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setLineSpacing(dp(4).toFloat(), 1f)
        }
        contentLayout.addView(questionTv)

        // 3. Options Container
        val optionsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val opParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }

        val options = listOf(
            Pair("A", "Bahasa pemrograman"),
            Pair("B", "Cabang AI yang mempelajari data"),
            Pair("C", "Sistem operasi"),
            Pair("D", "Basis data")
        )

        for ((idx, opt) in options.withIndex()) {
            optionsContainer.addView(createOptionCard(idx, opt.first, opt.second))
        }

        contentLayout.addView(optionsContainer, opParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 4. Bottom Navigation Buttons Row (Sebelumnya, Selanjutnya)
        val bottomNavRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        val btnSebelumnya = TextView(this).apply {
            text = "Sebelumnya"
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@MulaiKuisPelajarActivity, "Ini soal pertama", Toast.LENGTH_SHORT).show()
            }
        }
        bottomNavRow.addView(btnSebelumnya, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val btnSelanjutnya = TextView(this).apply {
            text = "Selanjutnya"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@MulaiKuisPelajarActivity, HasilKuisPelajarActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val bsParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(12)
        }
        bottomNavRow.addView(btnSelanjutnya, bsParams)

        rootLayout.addView(bottomNavRow)

        return rootLayout
    }

    private fun createOptionCard(index: Int, letter: String, optionText: String): View {
        val isSelected = (index == selectedOptionIndex)

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(
                if (isSelected) Color.rgb(240, 246, 255) else Color.WHITE,
                16,
                if (isSelected) Color.rgb(33, 96, 245) else Color.rgb(220, 231, 244),
                if (isSelected) 2 else 1
            )
            elevation = dp(1).toFloat()
            isClickable = true
            isFocusable = true
            setOnClickListener {
                selectedOptionIndex = index
                setContentView(createScreen())
            }
        }

        val circleBadge = TextView(this).apply {
            text = letter
            textSize = 14f
            setTextColor(if (isSelected) Color.WHITE else Color.rgb(117, 139, 169))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(if (isSelected) Color.rgb(33, 96, 245) else Color.rgb(240, 246, 255), 16)
        }
        card.addView(circleBadge, LinearLayout.LayoutParams(dp(36), dp(36)))

        val textTv = TextView(this).apply {
            text = "$letter. $optionText"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setPadding(dp(16), 0, 0, 0)
        }
        card.addView(textTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

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
        const val EXTRA_QUIZ_TITLE = "extra_quiz_title"
    }
}
