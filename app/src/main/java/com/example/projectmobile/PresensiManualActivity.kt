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

class PresensiManualActivity : AppCompatActivity() {

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

        // 1. Header Top (Back button, Title "Presensi Manual", Options menu)
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
            text = "Presensi Manual"
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

        // 2. Class Dropdown Selector
        val classSelector = createDropdownField("Pemrograman Mobile – Kelas A")
        val csParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(16)
        }
        contentLayout.addView(classSelector, csParams)

        // 3. Pertemuan ke- Label & Dropdown
        val labelPertemuan = TextView(this).apply {
            text = "Pertemuan ke-"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val lpParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(labelPertemuan, lpParams)

        val meetingSelector = createDropdownField("Pertemuan 3")
        val msParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(6)
        }
        contentLayout.addView(meetingSelector, msParams)

        // 4. Search Bar
        val searchBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
        }
        val searchParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(16)
        }

        val searchIcon = TextView(this).apply {
            text = "🔍"
            textSize = 16f
        }
        searchBar.addView(searchIcon)

        val etSearch = EditText(this).apply {
            hint = "Cari nama pelajar..."
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = null
            setPadding(dp(12), 0, 0, 0)
            maxLines = 1
        }
        searchBar.addView(etSearch, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(searchBar, searchParams)

        // 5. Empty Student List Container (Left empty for database population)
        val studentListContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val slParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        contentLayout.addView(studentListContainer, slParams)

        // 6. Legend Row (H: Hadir, A: Alasan, S: Sakit, I: Izin)
        val legendRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        val legendParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }

        legendRow.addView(createLegendItem("H", "Hadir", Color.rgb(33, 96, 245)), createLegendParams())
        legendRow.addView(createLegendItem("A", "Alasan", Color.rgb(244, 67, 54)), createLegendParams())
        legendRow.addView(createLegendItem("S", "Sakit", Color.rgb(0, 150, 136)), createLegendParams())
        legendRow.addView(createLegendItem("I", "Izin", Color.rgb(103, 58, 183)), createLegendParams())

        contentLayout.addView(legendRow, legendParams)

        // 7. Action Button: Akhiri Presensi
        val btnAkhiri = TextView(this).apply {
            text = "Akhiri Presensi"
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
        val btnParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(28)
        }
        contentLayout.addView(btnAkhiri, btnParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        return rootLayout
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

    private fun createLegendItem(letter: String, label: String, badgeColor: Int): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val badge = TextView(this).apply {
            text = letter
            textSize = 12f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(badgeColor, 12)
        }
        item.addView(badge, LinearLayout.LayoutParams(dp(24), dp(24)))

        val labelTv = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(dp(6), 0, 0, 0)
        }
        item.addView(labelTv)

        return item
    }

    private fun createLegendParams(): LinearLayout.LayoutParams {
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
