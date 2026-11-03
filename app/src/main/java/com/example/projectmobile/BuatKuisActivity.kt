package com.example.snapchatsphlasscreen

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BuatKuisActivity : AppCompatActivity() {

    private val durations = listOf("5 menit", "10 menit", "15 menit", "20 menit", "30 menit", "45 menit", "1 jam", "1.5 jam", "2 jam")
    private var selectedDurationIndex = 3 // default 20 menit

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

        // 1. Header Top (Back button, Title "Buat Kuis", Options menu)
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
            text = "Buat Kuis"
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

        // 2. Pilih Kelas
        contentLayout.addView(createFieldLabel("Pilih Kelas"))
        val classSelector = createDropdownField("Pemrograman Mobile – Kelas A")
        val csParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
        }
        contentLayout.addView(classSelector, csParams)

        // 3. Pertemuan ke-
        contentLayout.addView(createFieldLabel("Pertemuan ke-"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
        val meetingSelector = createDropdownField("Pertemuan 6")
        val msParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
        }
        contentLayout.addView(meetingSelector, msParams)

        // 4. Judul Kuis
        contentLayout.addView(createFieldLabel("Judul Kuis"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
        val etTitle = EditText(this).apply {
            hint = "Contoh: Kuis Struktur Data"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            maxLines = 1
        }
        val titleParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
        }
        contentLayout.addView(etTitle, titleParams)

        // 5. Instruksi
        contentLayout.addView(createFieldLabel("Instruksi"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
        
        val instructionBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            setPadding(dp(16), dp(12), dp(16), dp(8))
        }
        val instParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(130)).apply {
            topMargin = dp(6)
        }

        val etInstruction = EditText(this).apply {
            hint = "Tulis instruksi pengerjaan kuis..."
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = null
            gravity = Gravity.TOP
            setPadding(0, 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }
        instructionBox.addView(etInstruction)

        val charCounter = TextView(this).apply {
            text = "0/500"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.END
        }
        instructionBox.addView(charCounter)

        etInstruction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val len = s?.length ?: 0
                charCounter.text = "$len/500"
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        contentLayout.addView(instructionBox, instParams)

        // 6. Row for Durasi Pengerjaan, Nilai Minimal, Jumlah Soal
        val row3 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val row3Params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        // Durasi Pengerjaan (Flexible dropdown with minutes/hours)
        val col1 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val labelDurasi = createFieldLabel("Durasi Pengerjaan")
        col1.addView(labelDurasi)

        val durationSelector = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), 0, dp(12), 0)
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            isClickable = true
            isFocusable = true
        }
        val tvDurationVal = TextView(this).apply {
            text = durations[selectedDurationIndex]
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
        }
        durationSelector.addView(tvDurationVal, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val tvArrow = TextView(this).apply {
            text = "▾"
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
        }
        durationSelector.addView(tvArrow)

        durationSelector.setOnClickListener {
            // Cycle through flexible durations on click
            selectedDurationIndex = (selectedDurationIndex + 1) % durations.size
            tvDurationVal.text = durations[selectedDurationIndex]
        }

        val dsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
        }
        col1.addView(durationSelector, dsParams)
        row3.addView(col1, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f))

        // Nilai Minimal
        val col2 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val labelNilai = createFieldLabel("Nilai Minimal")
        val lNilaiParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginStart = dp(8)
        }
        col2.addView(labelNilai, lNilaiParams)

        val etNilai = EditText(this).apply {
            setText("70")
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            maxLines = 1
        }
        val eNilaiParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
            marginStart = dp(8)
        }
        col2.addView(etNilai, eNilaiParams)
        row3.addView(col2, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.9f))

        // Jumlah Soal
        val col3 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val labelSoal = createFieldLabel("Jumlah Soal")
        val lSoalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginStart = dp(8)
        }
        col3.addView(labelSoal, lSoalParams)

        val etSoal = EditText(this).apply {
            setText("10")
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            setPadding(dp(12), dp(12), dp(12), dp(12))
            maxLines = 1
        }
        val eSoalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
            marginStart = dp(8)
        }
        col3.addView(etSoal, eSoalParams)
        row3.addView(col3, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.9f))

        contentLayout.addView(row3, row3Params)

        // 7. Tambah Soal Section (Dashed upload card)
        contentLayout.addView(createFieldLabel("Tambah Soal"), LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(20) })

        val uploadCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 2) // dashed effect simulated by stroke
            isClickable = true
            isFocusable = true
        }
        val uploadParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }

        val uploadIconBox = TextView(this).apply {
            text = "📄+"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(33, 96, 245))
            background = roundedBackground(Color.rgb(232, 242, 255), 12)
        }
        uploadCard.addView(uploadIconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

        val uploadTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }
        val uploadTitle = TextView(this).apply {
            text = "Upload file soal (opsional)"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        uploadTextCol.addView(uploadTitle)

        val uploadSub = TextView(this).apply {
            text = "Format: PDF, DOCX, ZIP"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val usParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        uploadTextCol.addView(uploadSub, usParams)

        uploadCard.addView(uploadTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        contentLayout.addView(uploadCard, uploadParams)

        // 8. Action Buttons (Upload Kuis, Kembali Draf)
        val btnUpload = TextView(this).apply {
            text = "Upload Kuis"
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
        val buParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(28)
        }
        contentLayout.addView(btnUpload, buParams)

        val btnDraf = TextView(this).apply {
            text = "Kembali Draf"
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { finish() }
        }
        val bdParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        contentLayout.addView(btnDraf, bdParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 9. Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createFieldLabel(labelText: String): TextView {
        return TextView(this).apply {
            text = labelText
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun createDropdownField(value: String): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), 0, dp(16), 0)
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
        }

        val tv = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
        }
        container.addView(tv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val arrow = TextView(this).apply {
            text = "▾"
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
        }
        container.addView(arrow)

        return container
    }

    private fun createBottomNav(): View {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        nav.addView(createNavItem("🏠", "Beranda", false), createNavParams())
        nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
        nav.addView(createNavItem("📋", "Tugas", false), createNavParams())
        nav.addView(createNavItem("🤖", "AI", false), createNavParams())
        nav.addView(createNavItem("👤", "Profil", false), createNavParams())

        return nav
    }

    private fun createNavItem(icon: String, label: String, isSelected: Boolean): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(6), dp(4), dp(6))
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 18f
        }
        item.addView(iconTv)

        val labelTv = TextView(this).apply {
            text = label
            textSize = 11f
            setTextColor(if (isSelected) Color.rgb(33, 96, 245) else Color.rgb(117, 139, 169))
            if (isSelected) setTypeface(null, Typeface.BOLD)
        }
        val labelParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        item.addView(labelTv, labelParams)

        return item
    }

    private fun createNavParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
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
