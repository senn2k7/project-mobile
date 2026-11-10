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

class KuisBerhasilActivity : AppCompatActivity() {

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
            setPadding(dp(16), dp(32), dp(16), dp(24))
            setBackgroundColor(bgColor)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // 1. Green Checkmark Circle
        val checkmarkBox = TextView(this).apply {
            text = "✓"
            textSize = 36f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(46, 125, 50), 48)
        }
        contentLayout.addView(checkmarkBox, LinearLayout.LayoutParams(dp(80), dp(80)))

        // 2. Title "Kuis Berhasil Diunggah"
        val titleTv = TextView(this).apply {
            text = "Kuis Berhasil Diunggah"
            textSize = 22f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        val tParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(titleTv, tParams)

        // 3. Subtitle
        val subTv = TextView(this).apply {
            text = "Kuis telah disimpan dan siap dikerjakan oleh pelajar."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        val sParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        contentLayout.addView(subTv, sParams)

        // 4. Summary Card
        val summaryCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val cardParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(28)
        }

        summaryCard.addView(createSummaryRow("📅", "Kelas", "Pemrograman Mobile – Kelas A"))
        summaryCard.addView(createSummaryRow("📅", "Pertemuan", "Pertemuan 6"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
        summaryCard.addView(createSummaryRow("📋", "Jumlah Soal", "10 soal"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
        summaryCard.addView(createSummaryRow("⏱️", "Durasi", "20 menit"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
        summaryCard.addView(createStatusRow("📅", "Status", "Telah dipublikasikan"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })

        contentLayout.addView(summaryCard, cardParams)

        // 5. Action Buttons (Lihat Kuis -> NilaiHasilKuisActivity, Kembali ke Kelas -> DetailKelasActivity)
        val btnLihatKuis = TextView(this).apply {
            text = "Lihat Kuis"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@KuisBerhasilActivity, NilaiHasilKuisActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val blParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(40)
        }
        contentLayout.addView(btnLihatKuis, blParams)

        val btnKembaliKeKelas = TextView(this).apply {
            text = "Kembali ke Kelas"
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@KuisBerhasilActivity, DetailKelasActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                finish()
            }
        }
        val bkParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        contentLayout.addView(btnKembaliKeKelas, bkParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    private fun createSummaryRow(icon: String, label: String, value: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 16f
            gravity = Gravity.CENTER
        }
        row.addView(iconTv, LinearLayout.LayoutParams(dp(28), dp(28)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val labelTv = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(labelTv)

        val valueTv = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(valueTv)

        row.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        return row
    }

    private fun createStatusRow(icon: String, label: String, statusText: String): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 16f
            gravity = Gravity.CENTER
        }
        row.addView(iconTv, LinearLayout.LayoutParams(dp(28), dp(28)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val labelTv = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(labelTv)

        val statusBadge = TextView(this).apply {
            text = statusText
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.rgb(46, 125, 50))
            background = roundedBackground(Color.rgb(232, 245, 233), 8)
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        val sbParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(statusBadge, sbParams)

        row.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        return row
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
}