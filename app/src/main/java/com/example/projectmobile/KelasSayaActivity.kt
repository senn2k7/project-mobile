package com.example.snapchatsphlasscreen

import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class KelasSayaActivity : AppCompatActivity() {

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

        // 3. Filter Tabs (Semua, Institusi, Semester)
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

        val tabSemester = TextView(this).apply {
            text = "Semester ▾"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        tabsLayout.addView(tabSemester, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(tabsLayout, tabsParams)

        // 4. Class Cards List
        val classCardsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val classParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        classCardsContainer.addView(createClassCard("Pemrograman Mobile", "Kelas A • PM2025-A", "26 pelajar", "💻", Color.rgb(33, 96, 245)))
        classCardsContainer.addView(createClassCard("Pemrograman Mobile", "Kelas B • PM2025-B", "24 pelajar", "💻", Color.rgb(33, 96, 245)))
        classCardsContainer.addView(createClassCard("Sistem Cerdas", "Kelas A • SC2025-A", "32 pelajar", "🤖", Color.rgb(33, 96, 245)))
        classCardsContainer.addView(createClassCard("Sistem Cerdas", "Kelas B • SC2025-B", "27 pelajar", "🤖", Color.rgb(33, 96, 245)))
        classCardsContainer.addView(createClassCard("Basis Data", "Kelas A • BD2025-A", "26 pelajar", "🗄️", Color.rgb(76, 175, 80)))
        classCardsContainer.addView(createClassCard("Basis Data", "Kelas B • BD2025-B", "20 pelajar", "🗄️", Color.rgb(33, 96, 245)))

        contentLayout.addView(classCardsContainer, classParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 5. Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createClassCard(title: String, subtitle: String, studentCount: String, icon: String, iconBgColor: Int): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = icon
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(iconBgColor, 12)
        }
        card.addView(iconBox, LinearLayout.LayoutParams(dp(52), dp(52)))

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

        val subTv = TextView(this).apply {
            text = subtitle
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val subParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(subTv, subParams)

        val countTv = TextView(this).apply {
            text = studentCount
            textSize = 12f
            setTextColor(Color.rgb(33, 96, 245))
        }
        val countParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(4)
        }
        textCol.addView(countTv, countParams)

        card.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val optionsTv = TextView(this).apply {
            text = "•••"
            textSize = 16f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        card.addView(optionsTv, LinearLayout.LayoutParams(dp(24), dp(32)))

        val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
        val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR

        if (mode == ModePembelajaranActivity.MODE_INSTITUSI && role == PilihPeranActivity.ROLE_PENGAJAR) {
            card.isClickable = true
            card.isFocusable = true
            card.setOnClickListener {
                val detailIntent = Intent(this, DetailKelasActivity::class.java).apply {
                    putExtra(DetailKelasActivity.EXTRA_CLASS_NAME, title)
                    val code = subtitle.substringAfter("• ").trim()
                    putExtra(DetailKelasActivity.EXTRA_CLASS_CODE, code)
                    putExtra(DetailKelasActivity.EXTRA_LECTURER_NAME, "Budi Santoso")
                }
                startActivity(detailIntent)
            }
            optionsTv.isClickable = true
            optionsTv.isFocusable = true
            optionsTv.setOnClickListener {
                val detailIntent = Intent(this, DetailKelasActivity::class.java).apply {
                    putExtra(DetailKelasActivity.EXTRA_CLASS_NAME, title)
                    val code = subtitle.substringAfter("• ").trim()
                    putExtra(DetailKelasActivity.EXTRA_CLASS_CODE, code)
                    putExtra(DetailKelasActivity.EXTRA_LECTURER_NAME, "Budi Santoso")
                }
                startActivity(detailIntent)
            }
        }

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
            isClickable = true
            isFocusable = true
            setOnClickListener {
                when (label) {
                    "Beranda" -> {
                        val intent = Intent(this@KelasSayaActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@KelasSayaActivity, KelasSayaActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        val intent = Intent(this@KelasSayaActivity, TugasActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        Toast.makeText(this@KelasSayaActivity, "Membuka Asisten AI TemanStudi...", Toast.LENGTH_SHORT).show()
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@KelasSayaActivity, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
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
