package com.example.projectmobile

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DiskusiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val courseName = intent.getStringExtra(EXTRA_COURSE_NAME) ?: "Pemrograman Mobile"
        setContentView(createScreen(courseName))
    }

    private fun createScreen(courseName: String): View {
        val bgColor = Color.rgb(245, 250, 255)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // 1. Header Top (Back button, Title "Diskusi [Course]", Subtitle "28 peserta", Options menu)
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(bgColor, 0)
            elevation = dp(2).toFloat()
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

        val headerTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val headerTitle = TextView(this).apply {
            text = "Diskusi $courseName"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        headerTextCol.addView(headerTitle)

        val headerSub = TextView(this).apply {
            text = "28 peserta"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val hsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        headerTextCol.addView(headerSub, hsParams)

        headerLayout.addView(headerTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val optionsMenu = TextView(this).apply {
            text = "⋮"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
        }
        headerLayout.addView(optionsMenu, LinearLayout.LayoutParams(dp(36), dp(36)))

        rootLayout.addView(headerLayout)

        // 2. Empty Chat Area (ScrollView / Container with no chat bubbles)
        val chatArea = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val emptyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(24), dp(24), dp(24))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val emptyText = TextView(this).apply {
            text = "Belum ada pesan diskusi.\nMulai percakapan dengan menulis pesan di bawah."
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
            setLineSpacing(dp(4).toFloat(), 1f)
        }
        emptyContainer.addView(emptyText)
        chatArea.addView(emptyContainer)
        rootLayout.addView(chatArea)

        // 3. Bottom Message Input Bar
        val inputBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        val emojiIcon = TextView(this).apply {
            text = "😊"
            textSize = 22f
            gravity = Gravity.CENTER
        }
        inputBar.addView(emojiIcon, LinearLayout.LayoutParams(dp(36), dp(36)))

        val attachIcon = TextView(this).apply {
            text = "📎"
            textSize = 20f
            gravity = Gravity.CENTER
        }
        val attachParams = LinearLayout.LayoutParams(dp(36), dp(36)).apply {
            marginStart = dp(8)
        }
        inputBar.addView(attachIcon, attachParams)

        val etMessage = EditText(this).apply {
            hint = "Tulis pesan..."
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = roundedBackground(Color.rgb(240, 246, 255), 24)
            setPadding(dp(16), dp(10), dp(16), dp(10))
            maxLines = 3
        }
        val etParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(12)
            marginEnd = dp(12)
        }
        inputBar.addView(etMessage, etParams)

        val sendButton = TextView(this).apply {
            text = "✈️"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = roundedBackground(Color.rgb(29, 94, 255), 22)
            isClickable = true
            isFocusable = true
        }
        inputBar.addView(sendButton, LinearLayout.LayoutParams(dp(44), dp(44)))

        rootLayout.addView(inputBar)

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
        const val EXTRA_COURSE_NAME = "extra_course_name"
    }
}
