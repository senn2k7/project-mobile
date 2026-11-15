package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmobile.UiKit

class PengajarSuksesActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TYPE = "extra_type" // "materi", "tugas", "kuis"
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val type = intent.getStringExtra(EXTRA_TYPE) ?: "materi"
        setContentView(createScreen(type))
    }

    private fun createScreen(type: String): View {
        val bgColor = Color.rgb(245, 250, 255)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(48), dp(24), dp(24))
        }

        // Green Circle Checkmark
        val circleIcon = TextView(this).apply {
            text = "✓"
            textSize = 36f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarSuksesActivity, Color.parseColor("#1FAF6E"), 40)
        }
        content.addView(circleIcon, LinearLayout.LayoutParams(dp(80), dp(80)))

        // Title
        val (titleText, subText, btnPrimaryText) = when (type) {
            "tugas" -> Triple("Tugas Berhasil Dibuat", "Tugas baru telah berhasil dibuat dan dibagikan ke kelas.", "Lihat Detail Tugas")
            "kuis" -> Triple("Kuis Berhasil Dibuat", "Kuis baru telah berhasil disimpan dan dijadwalkan.", "Lihat Detail Kuis")
            else -> Triple("Materi dan Tugas Berhasil Diunggah", "Materi berhasil disimpan dan telah tersedia di kelas.", "Lihat Detail Materi")
        }

        val tvTitle = TextView(this).apply {
            text = titleText
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        content.addView(tvTitle, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(20) })

        val tvSub = TextView(this).apply {
            text = subText
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        content.addView(tvSub, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })

        // Summary Card
        val summaryCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarSuksesActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(24) }
        }

        summaryCard.addView(createSummaryRow("📚 Kelas", "Pemrograman Mobile • Kelas A"))
        summaryCard.addView(createSummaryRow("📌 Pertemuan", "Pertemuan 2"))
        summaryCard.addView(createSummaryRow("📄 Status", "Telah dipublikasikan", isChip = true))

        content.addView(summaryCard)
        scrollView.addView(content)
        rootLayout.addView(scrollView)

        // Bottom Buttons Pinned
        val bottomBar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarSuksesActivity, Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        val btnPrimary = TextView(this).apply {
            text = btnPrimaryText
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarSuksesActivity, Color.parseColor("#1D5EFF"), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            setOnClickListener {
                if (type == "kuis") {
                    val intent = Intent(this@PengajarSuksesActivity, PengajarNilaiHasilKuisActivity::class.java).apply {
                        putExtra(PengajarNilaiHasilKuisActivity.EXTRA_KELAS_ID, "1")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@PengajarSuksesActivity, PengajarDetailKelasActivity::class.java).apply {
                        putExtra(PengajarDetailKelasActivity.EXTRA_KELAS_ID, "1")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
        bottomBar.addView(btnPrimary, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val btnKembali = TextView(this).apply {
            text = "Kembali ke Kelas"
            textSize = 14f
            setTextColor(Color.parseColor("#1D5EFF"))
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarSuksesActivity, Color.WHITE, 12, Color.parseColor("#1D5EFF"), 1)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            setOnClickListener {
                if (type == "kuis") {
                    val intent = Intent(this@PengajarSuksesActivity, PengajarNilaiHasilKuisActivity::class.java).apply {
                        putExtra(PengajarNilaiHasilKuisActivity.EXTRA_KELAS_ID, "1")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@PengajarSuksesActivity, PengajarDetailKelasActivity::class.java).apply {
                        putExtra(PengajarDetailKelasActivity.EXTRA_KELAS_ID, "1")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                }
            }
        }
        bottomBar.addView(btnKembali, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })

        rootLayout.addView(bottomBar)

        return rootLayout
    }

    private fun createSummaryRow(label: String, value: String, isChip: Boolean = false): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }
        val tvLabel = TextView(this).apply {
            text = label
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        row.addView(tvLabel, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        if (isChip) {
            val chip = TextView(this).apply {
                text = value
                textSize = 12f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#2E7D32"))
                background = UiKit.rounded(this@PengajarSuksesActivity, Color.parseColor("#E8F5E9"), 8)
                setPadding(dp(8), dp(4), dp(8), dp(4))
            }
            row.addView(chip)
        } else {
            val tvVal = TextView(this).apply {
                text = value
                textSize = 13f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
            }
            row.addView(tvVal)
        }
        return row
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
