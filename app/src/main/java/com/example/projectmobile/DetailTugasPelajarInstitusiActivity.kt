package com.example.projectmobile

import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailTugasPelajarInstitusiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Membuat UI Login Android"
        val courseName = intent.getStringExtra(EXTRA_COURSE_NAME) ?: "Pemrograman Mobile"

        setContentView(createScreen(taskTitle, courseName))
    }

    private fun createScreen(taskTitle: String, courseName: String): View {
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

        // 1. Header Top (Back button, Title "Detail Tugas", Options menu)
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
            text = "Detail Tugas"
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

        // 2. Top Task Info Card
        val taskCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val taskParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val iconBox = TextView(this).apply {
            text = "T"
            textSize = 26f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(13, 110, 253), 12)
        }
        taskCard.addView(iconBox, LinearLayout.LayoutParams(dp(52), dp(52)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), 0, 0, 0)
        }
        val titleTv = TextView(this).apply {
            text = taskTitle
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val courseTv = TextView(this).apply {
            text = courseName
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val cParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(courseTv, cParams)

        taskCard.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        contentLayout.addView(taskCard, taskParams)

        // 3. Deadline Card
        val deadlineCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val dParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val clockIcon = TextView(this).apply {
            text = "📅"
            textSize = 18f
            gravity = Gravity.CENTER
        }
        deadlineCard.addView(clockIcon, LinearLayout.LayoutParams(dp(32), dp(32)))

        val deadlineCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val deadlineLabel = TextView(this).apply {
            text = "Batas Pengumpulan"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        deadlineCol.addView(deadlineLabel)

        val deadlineVal = TextView(this).apply {
            text = "12 Jun 2025, 23:59"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        deadlineCol.addView(deadlineVal)

        deadlineCard.addView(deadlineCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        contentLayout.addView(deadlineCard, dParams)

        // 4. Section: Instruksi (Empty/placeholder ready for DB population)
        val instHeader = TextView(this).apply {
            text = "Instruksi"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val ihParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(instHeader, ihParams)

        val instBody = TextView(this).apply {
            text = "Instruksi pengerjaan tugas akan diisi otomatis oleh pengajar..."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val ibParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        contentLayout.addView(instBody, ibParams)

        // 5. Section: Lampiran (Empty/placeholder ready for DB population)
        val lampHeader = TextView(this).apply {
            text = "📎 Lampiran"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val lhParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(lampHeader, lhParams)

        val lampBody = TextView(this).apply {
            text = "Belum ada lampiran file dari pengajar."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val lbParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        contentLayout.addView(lampBody, lbParams)

        // 6. Section: Jawaban Anda
        val jawabHeader = TextView(this).apply {
            text = "Jawaban Anda"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val jhParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(jawabHeader, jhParams)

        val etAnswer = EditText(this).apply {
            hint = "Tulis jawaban atau penjelasan..."
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            gravity = Gravity.TOP
        }
        val etParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(140)).apply {
            topMargin = dp(8)
        }
        contentLayout.addView(etAnswer, etParams)

        // 7. Button: Lampirkan File
        val btnLampirkan = TextView(this).apply {
            text = "+ Lampirkan File"
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@DetailTugasPelajarInstitusiActivity, "Lampirkan file...", Toast.LENGTH_SHORT).show()
            }
        }
        val blParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        contentLayout.addView(btnLampirkan, blParams)

        // 8. Primary Button: Kumpulkan Tugas (Launches TugasBerhasilPelajarActivity)
        val btnKumpulkan = TextView(this).apply {
            text = "Kumpulkan Tugas"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@DetailTugasPelajarInstitusiActivity, TugasBerhasilPelajarActivity::class.java).apply {
                    putExtra(TugasBerhasilPelajarActivity.EXTRA_TASK_TITLE, taskTitle)
                }
                startActivity(intent)
                finish()
            }
        }
        val bkParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(btnKumpulkan, bkParams)

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
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_COURSE_NAME = "extra_course_name"
    }
}
