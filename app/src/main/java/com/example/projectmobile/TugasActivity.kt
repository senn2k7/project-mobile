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

class TugasActivity : AppCompatActivity() {

    private data class TaskItem(
        val title: String,
        val meeting: String,
        val status: String, // "berlangsung" or "selesai"
        val deadline: String,
        val submissions: String,
        val icon: String,
        val iconBgColor: Int
    )

    private val allTasks = listOf(
        TaskItem(
            title = "Membuat UI Login Android",
            meeting = "Pertemuan 3",
            status = "berlangsung",
            deadline = "18 Jun 2025, 23.59",
            submissions = "18/25 mengumpulkan",
            icon = "💻",
            iconBgColor = Color.rgb(33, 96, 245)
        ),
        TaskItem(
            title = "Analisis Sistem",
            meeting = "Pertemuan 5",
            status = "berlangsung",
            deadline = "28 Jun 2025, 23.59",
            submissions = "10/25 mengumpulkan",
            icon = "💻",
            iconBgColor = Color.rgb(33, 96, 245)
        ),
        TaskItem(
            title = "Laporan Basis Data",
            meeting = "Pertemuan 7",
            status = "selesai",
            deadline = "15 Jun 2025, 23.59",
            submissions = "25/25 mengumpulkan",
            icon = "🗄️",
            iconBgColor = Color.rgb(76, 175, 80)
        )
    )

    private lateinit var tasksContainer: LinearLayout
    private lateinit var tabSemua: TextView
    private lateinit var tabBerlangsung: TextView
    private lateinit var tabSelesai: TextView

    private var currentFilter = "semua" // "semua", "berlangsung", "selesai"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val className = intent.getStringExtra(EXTRA_CLASS_NAME) ?: "Pemrograman Mobile"
        val classCode = intent.getStringExtra(EXTRA_CLASS_CODE) ?: "Kelas A • PM2025-A"

        setContentView(createScreen(className, classCode))
    }

    private fun createScreen(className: String, classCode: String): View {
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

        // 2. Top Course Banner Card
        val courseBanner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val bannerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val iconBox = TextView(this).apply {
            text = "💻"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        courseBanner.addView(iconBox, LinearLayout.LayoutParams(dp(44), dp(44)))

        val bannerTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val bannerTitle = TextView(this).apply {
            text = className
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        bannerTextCol.addView(bannerTitle)

        val bannerSub = TextView(this).apply {
            text = classCode
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val bsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        bannerTextCol.addView(bannerSub, bsParams)

        courseBanner.addView(bannerTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val arrowTv = TextView(this).apply {
            text = "›"
            textSize = 24f
            setTextColor(Color.rgb(33, 96, 245))
            gravity = Gravity.CENTER
        }
        courseBanner.addView(arrowTv, LinearLayout.LayoutParams(dp(24), dp(32)))

        contentLayout.addView(courseBanner, bannerParams)

        // 3. Filter Tabs Row (Semua, Tugas Berlangsung, Tugas Selesai)
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

        tabBerlangsung = TextView(this).apply {
            text = "Tugas Berlangsung"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("berlangsung") }
        }
        tabsLayout.addView(tabBerlangsung, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabSelesai = TextView(this).apply {
            text = "Tugas Selesai"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("selesai") }
        }
        tabsLayout.addView(tabSelesai, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(tabsLayout, tabsParams)

        // 4. Tasks Cards Container
        tasksContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val tasksParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        contentLayout.addView(tasksContainer, tasksParams)

        updateTaskList()

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 5. Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun setFilter(filter: String) {
        currentFilter = filter
        updateTabStyles()
        updateTaskList()
    }

    private fun updateTabStyles() {
        val activeBg = roundedBackground(Color.rgb(33, 96, 245), 10)
        val inactiveBg = roundedBackground(Color.TRANSPARENT, 10)

        // Reset all
        tabSemua.background = inactiveBg
        tabSemua.setTextColor(Color.rgb(117, 139, 169))
        tabSemua.setTypeface(null, Typeface.NORMAL)

        tabBerlangsung.background = inactiveBg
        tabBerlangsung.setTextColor(Color.rgb(117, 139, 169))
        tabBerlangsung.setTypeface(null, Typeface.NORMAL)

        tabSelesai.background = inactiveBg
        tabSelesai.setTextColor(Color.rgb(117, 139, 169))
        tabSelesai.setTypeface(null, Typeface.NORMAL)

        when (currentFilter) {
            "semua" -> {
                tabSemua.background = activeBg
                tabSemua.setTextColor(Color.WHITE)
                tabSemua.setTypeface(null, Typeface.BOLD)
            }
            "berlangsung" -> {
                tabBerlangsung.background = activeBg
                tabBerlangsung.setTextColor(Color.WHITE)
                tabBerlangsung.setTypeface(null, Typeface.BOLD)
            }
            "selesai" -> {
                tabSelesai.background = activeBg
                tabSelesai.setTextColor(Color.WHITE)
                tabSelesai.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun updateTaskList() {
        tasksContainer.removeAllViews()

        val filtered = when (currentFilter) {
            "berlangsung" -> allTasks.filter { it.status == "berlangsung" }
            "selesai" -> allTasks.filter { it.status == "selesai" }
            else -> allTasks
        }

        for (task in filtered) {
            tasksContainer.addView(createTaskCard(task))
        }
    }

    private fun createTaskCard(task: TaskItem): View {
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
            text = task.icon
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(task.iconBgColor, 12)
        }
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val titleTv = TextView(this).apply {
            text = task.title
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val meetTv = TextView(this).apply {
            text = task.meeting
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val mParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(meetTv, mParams)

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
            val intent = Intent(this, DetailTugasActivity::class.java).apply {
                putExtra(DetailTugasActivity.EXTRA_TASK_TITLE, task.title)
                putExtra(DetailTugasActivity.EXTRA_TASK_MEETING, "Pemrograman Mobile • ${task.meeting}")
                putExtra(DetailTugasActivity.EXTRA_TASK_STATUS, if (task.status == "berlangsung") "Tugas Berlangsung" else "Tugas Selesai")
                putExtra(DetailTugasActivity.EXTRA_TASK_DEADLINE, task.deadline)
            }
            startActivity(intent)
        }

        optionsTv.isClickable = true
        optionsTv.isFocusable = true
        optionsTv.setOnClickListener {
            val intent = Intent(this, DetailTugasActivity::class.java).apply {
                putExtra(DetailTugasActivity.EXTRA_TASK_TITLE, task.title)
                putExtra(DetailTugasActivity.EXTRA_TASK_MEETING, "Pemrograman Mobile • ${task.meeting}")
                putExtra(DetailTugasActivity.EXTRA_TASK_STATUS, if (task.status == "berlangsung") "Tugas Berlangsung" else "Tugas Selesai")
                putExtra(DetailTugasActivity.EXTRA_TASK_DEADLINE, task.deadline)
            }
            startActivity(intent)
        }

        // Status Badge Row
        val badge = TextView(this).apply {
            text = if (task.status == "berlangsung") "Tugas Berlangsung" else "Tugas Selesai"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (task.status == "berlangsung") Color.rgb(33, 96, 245) else Color.rgb(46, 125, 50))
            background = roundedBackground(if (task.status == "berlangsung") Color.rgb(232, 242, 255) else Color.rgb(232, 245, 233), 8)
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        val badgeParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        card.addView(badge, badgeParams)

        // Deadline & Submissions Info
        val infoRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val infoParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val deadlineTv = TextView(this).apply {
            text = "🗓️ ${task.deadline}"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        infoRow.addView(deadlineTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val subTv = TextView(this).apply {
            text = "☑️ ${task.submissions}"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        infoRow.addView(subTv)

        card.addView(infoRow, infoParams)

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
        nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
        nav.addView(createNavItem("📋", "Tugas", true), createNavParams())
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
                        val intent = Intent(this@TugasActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@TugasActivity, KelasSayaActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        val intent = Intent(this@TugasActivity, TugasActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        Toast.makeText(this@TugasActivity, "Membuka Asisten AI TemanStudi...", Toast.LENGTH_SHORT).show()
                    }
                    "Profil" -> {
                        Toast.makeText(this@TugasActivity, "Membuka Profil Pengajar...", Toast.LENGTH_SHORT).show()
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

    companion object {
        const val EXTRA_CLASS_NAME = "extra_class_name"
        const val EXTRA_CLASS_CODE = "extra_class_code"
    }
}
