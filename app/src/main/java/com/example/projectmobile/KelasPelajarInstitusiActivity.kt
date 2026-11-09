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

class KelasPelajarInstitusiActivity : AppCompatActivity() {

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

        // 1. Header Top (Back button, Title "Kelas Saya", Options menu)
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
            text = "Kelas Saya"
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

        // 2. Search Bar & Filter Button
        val searchContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val searchParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(20)
        }

        val searchBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
        }

        val searchIcon = TextView(this).apply {
            text = "🔍"
            textSize = 16f
        }
        searchBar.addView(searchIcon)

        val searchText = TextView(this).apply {
            text = "Cari kelas..."
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(dp(12), 0, 0, 0)
        }
        searchBar.addView(searchText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        searchContainer.addView(searchBar, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        val filterBtn = TextView(this).apply {
            text = "☰"
            textSize = 18f
            setTextColor(Color.rgb(33, 96, 245))
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
        }
        val filterParams = LinearLayout.LayoutParams(dp(48), dp(48)).apply {
            marginStart = dp(12)
        }
        searchContainer.addView(filterBtn, filterParams)

        contentLayout.addView(searchContainer, searchParams)

        // 3. Filter Tabs (Semua, Institusi, Mandiri)
        val tabsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = roundedBackground(Color.rgb(232, 240, 252), 12)
        }
        val tabsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(16)
        }

        val tabSemua = TextView(this).apply {
            text = "Semua"
            textSize = 13f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 10)
        }
        tabsLayout.addView(tabSemua, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        val tabInstitusi = TextView(this).apply {
            text = "Institusi"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        tabsLayout.addView(tabInstitusi, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        val tabMandiri = TextView(this).apply {
            text = "Mandiri"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        tabsLayout.addView(tabMandiri, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(tabsLayout, tabsParams)

        // 4. Student Class Cards List
        val classCardsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val classParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        classCardsContainer.addView(createStudentClassCard("Pemrograman Mobile", "Dr. Budi Santoso • Teknologi Informasi", "Pertemuan 2: UI Android", "Besok, 10:00", "32 peserta", "💻"))
        classCardsContainer.addView(createStudentClassCard("Sistem Cerdas", "Dr. Rina Wati • Informatika", "Pertemuan 1: Pengenalan AI", "Jumat, 08:00", "28 peserta", "🧠"))
        classCardsContainer.addView(createStudentClassCard("Basis Data", "Dr. Ahmad Fauzi • Sistem Informasi", "Pertemuan 3: Normalisasi", "Sabtu, 10:00", "26 peserta", "🗄️"))

        contentLayout.addView(classCardsContainer, classParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 5. Student Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createStudentClassCard(title: String, lecturerInfo: String, meeting: String, schedule: String, studentCount: String, icon: String): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox = TextView(this).apply {
            text = icon
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(52), dp(52)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), 0, 0, 0)
        }

        val titleTv = TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val lecturerTv = TextView(this).apply {
            text = lecturerInfo
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val lecParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(lecturerTv, lecParams)

        // Badge "Institusi"
        val badge = TextView(this).apply {
            text = "Institusi"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.rgb(33, 96, 245))
            background = roundedBackground(Color.rgb(232, 242, 255), 8)
            setPadding(dp(8), dp(3), dp(8), dp(3))
        }
        val bParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        textCol.addView(badge, bParams)

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val optionsTv = TextView(this).apply {
            text = "•••"
            textSize = 16f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        topRow.addView(optionsTv, LinearLayout.LayoutParams(dp(24), dp(32)))

        card.addView(topRow)

        card.isClickable = true
        card.isFocusable = true
        card.setOnClickListener {
            val intent = Intent(this, DetailKelasPelajarInstitusiActivity::class.java).apply {
                putExtra(DetailKelasPelajarInstitusiActivity.EXTRA_CLASS_NAME, title)
            }
            startActivity(intent)
        }

        optionsTv.isClickable = true
        optionsTv.isFocusable = true
        optionsTv.setOnClickListener {
            val intent = Intent(this, DetailKelasPelajarInstitusiActivity::class.java).apply {
                putExtra(DetailKelasPelajarInstitusiActivity.EXTRA_CLASS_NAME, title)
            }
            startActivity(intent)
        }

        // Meeting & Schedule bottom row
        val bottomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val botParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(14)
        }

        val meetingTv = TextView(this).apply {
            text = "📖 $meeting"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        bottomRow.addView(meetingTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val countTv = TextView(this).apply {
            text = "👥 $studentCount"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        bottomRow.addView(countTv)

        card.addView(bottomRow, botParams)

        // Schedule row
        val schedRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val schedParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        val scheduleTv = TextView(this).apply {
            text = "⏰ $schedule"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        schedRow.addView(scheduleTv)
        card.addView(schedRow, schedParams)

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

    private fun createBottomNav(): View {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        nav.addView(createNavItem("🏠", "Beranda", false), createNavParams())
        nav.addView(createNavItem("📚", "Kelas", true), createNavParams())
        nav.addView(createNavItem("🤖", "AI", false), createNavParams())
        nav.addView(createNavItem("📋", "Aktivitas", false), createNavParams())
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
                        val intent = Intent(this@KelasPelajarInstitusiActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PELAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@KelasPelajarInstitusiActivity, KelasPelajarInstitusiActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Aktivitas", "Tugas" -> {
                        Toast.makeText(this@KelasPelajarInstitusiActivity, "Membuka Aktivitas Pelajar...", Toast.LENGTH_SHORT).show()
                    }
                    "AI" -> {
                        val intent = Intent(this@KelasPelajarInstitusiActivity, AiAssistantActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@KelasPelajarInstitusiActivity, PilihPeranActivity.ROLE_PELAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
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
