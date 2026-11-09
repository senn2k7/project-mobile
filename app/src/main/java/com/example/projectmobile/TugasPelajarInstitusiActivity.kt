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

class TugasPelajarInstitusiActivity : AppCompatActivity() {

    private lateinit var tabSemua: TextView
    private lateinit var tabBelum: TextView
    private lateinit var tabSelesai: TextView
    private lateinit var tasksContainer: LinearLayout

    private var currentFilter = "semua" // "semua", "belum", "selesai"

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

        // 1. Header Top (Back button, Title "Tugas", Options menu)
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
            text = "Tugas"
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

        // 2. Filter Tabs Row (Semua, Belum Dikerjakan, Selesai)
        val tabsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = roundedBackground(Color.rgb(232, 240, 252), 12)
        }
        val tabsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(16)
        }

        tabSemua = TextView(this).apply {
            text = "Semua"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("semua") }
        }
        tabsLayout.addView(tabSemua, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabBelum = TextView(this).apply {
            text = "Belum Dikerjakan"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("belum") }
        }
        tabsLayout.addView(tabBelum, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabSelesai = TextView(this).apply {
            text = "Selesai"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("selesai") }
        }
        tabsLayout.addView(tabSelesai, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(tabsLayout, tabsParams)

        // 3. Tasks Cards Container (Left empty for database population)
        tasksContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val tasksParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        contentLayout.addView(tasksContainer, tasksParams)

        updateTabStyles()

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 4. Student Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun setFilter(filter: String) {
        currentFilter = filter
        updateTabStyles()
    }

    private fun updateTabStyles() {
        val activeBg = roundedBackground(Color.rgb(33, 96, 245), 10)
        val inactiveBg = roundedBackground(Color.TRANSPARENT, 10)

        tabSemua.background = inactiveBg
        tabSemua.setTextColor(Color.rgb(117, 139, 169))
        tabSemua.setTypeface(null, Typeface.NORMAL)

        tabBelum.background = inactiveBg
        tabBelum.setTextColor(Color.rgb(117, 139, 169))
        tabBelum.setTypeface(null, Typeface.NORMAL)

        tabSelesai.background = inactiveBg
        tabSelesai.setTextColor(Color.rgb(117, 139, 169))
        tabSelesai.setTypeface(null, Typeface.NORMAL)

        when (currentFilter) {
            "semua" -> {
                tabSemua.background = activeBg
                tabSemua.setTextColor(Color.WHITE)
                tabSemua.setTypeface(null, Typeface.BOLD)
            }
            "belum" -> {
                tabBelum.background = activeBg
                tabBelum.setTextColor(Color.WHITE)
                tabBelum.setTypeface(null, Typeface.BOLD)
            }
            "selesai" -> {
                tabSelesai.background = activeBg
                tabSelesai.setTextColor(Color.WHITE)
                tabSelesai.setTypeface(null, Typeface.BOLD)
            }
        }
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
        nav.addView(createNavItem("🤖", "AI", false), createNavParams())
        nav.addView(createNavItem("📋", "Aktivitas", true), createNavParams())
        nav.addView(createNavItem("👤", "Profil", false), createNavParams())

        return nav
    }

    private fun createNavItem(icon: String, label: String, isSelected: Boolean): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(6), dp(4), dp(6))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                when (label) {
                    "Beranda" -> {
                        val intent = Intent(this@TugasPelajarInstitusiActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PELAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@TugasPelajarInstitusiActivity, KelasPelajarInstitusiActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Aktivitas", "Tugas" -> {
                        val intent = Intent(this@TugasPelajarInstitusiActivity, TugasPelajarInstitusiActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        val intent = Intent(this@TugasPelajarInstitusiActivity, AiAssistantActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@TugasPelajarInstitusiActivity, PilihPeranActivity.ROLE_PELAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
                    }
                }
            }
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
