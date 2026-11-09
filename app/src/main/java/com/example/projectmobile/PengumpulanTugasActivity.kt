package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.widget.Toast
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

class PengumpulanTugasActivity : AppCompatActivity() {

    private data class StudentSubmission(
        val name: String,
        val status: String, // "sudah" or "belum"
        val time: String,
        val fileName: String? = null,
        val fileSize: String? = null,
        val fileIcon: String? = null,
        val fileIconBgColor: Int? = null
    )

    private val allStudents = listOf(
        StudentSubmission(
            name = "Nadia Putri",
            status = "sudah",
            time = "Dikumpulkan 20 Jun, 20.14",
            fileName = "UI_Login_Nadia.zip",
            fileSize = "1.8 MB",
            fileIcon = "📄",
            fileIconBgColor = Color.rgb(244, 67, 54)
        ),
        StudentSubmission(
            name = "Rizky Pratama",
            status = "sudah",
            time = "Dikumpulkan 20 Jun, 21.03",
            fileName = "LoginScreen.kt",
            fileSize = "5.2 MB",
            fileIcon = "💻",
            fileIconBgColor = Color.rgb(33, 96, 245)
        ),
        StudentSubmission(
            name = "Siti Aulia",
            status = "sudah",
            time = "Dikumpulkan 20 Jun, 22.10",
            fileName = "Desain_Login.pdf",
            fileSize = "2.4 MB",
            fileIcon = "📄",
            fileIconBgColor = Color.rgb(244, 67, 54)
        ),
        StudentSubmission(
            name = "Andi Saputra",
            status = "belum",
            time = "Belum mengumpulkan tugas"
        )
    )

    private lateinit var listContainer: LinearLayout
    private lateinit var tabSemua: TextView
    private lateinit var tabSudah: TextView
    private lateinit var tabBelum: TextView
    private lateinit var etSearch: EditText

    private var currentFilter = "semua" // "semua", "sudah", "belum"
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Membuat UI Login Android"
        val taskMeeting = intent.getStringExtra(EXTRA_TASK_MEETING) ?: "Pemrograman Mobile • Pertemuan 3"

        setContentView(createScreen(taskTitle, taskMeeting))
    }

    private fun createScreen(taskTitle: String, taskMeeting: String): View {
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

        // 1. Header Top (Back button, Title "Pengumpulan Tugas")
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
            text = "Pengumpulan Tugas"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        headerLayout.addView(headerTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        contentLayout.addView(headerLayout)

        // 2. Top Task Summary Card
        val taskSummaryCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val summaryParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox = TextView(this).apply {
            text = "💻"
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val titleTv = TextView(this).apply {
            text = taskTitle
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val meetTv = TextView(this).apply {
            text = taskMeeting
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val mParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(meetTv, mParams)

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        taskSummaryCard.addView(topRow)

        // Status Badge
        val badge = TextView(this).apply {
            text = "Tugas Berlangsung"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.rgb(33, 96, 245))
            background = roundedBackground(Color.rgb(232, 242, 255), 8)
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        val badgeParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        taskSummaryCard.addView(badge, badgeParams)

        contentLayout.addView(taskSummaryCard, summaryParams)

        // 3. Stat Cards Row (18 Mengumpulkan / 7 Belum Mengumpulkan)
        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val statsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val statCard1 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.rgb(232, 245, 233), 16)
        }
        val num1Row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val num1 = TextView(this).apply {
            text = "18"
            textSize = 28f
            setTextColor(Color.rgb(46, 125, 50))
            setTypeface(null, Typeface.BOLD)
        }
        num1Row.addView(num1, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val icon1 = TextView(this).apply {
            text = "📄"
            textSize = 18f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(200, 230, 201), 10)
        }
        num1Row.addView(icon1, LinearLayout.LayoutParams(dp(36), dp(36)))
        statCard1.addView(num1Row)

        val label1 = TextView(this).apply {
            text = "Mengumpulkan"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val l1Params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(4)
        }
        statCard1.addView(label1, l1Params)
        statsRow.addView(statCard1, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val statCard2 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.rgb(255, 235, 238), 16)
        }
        val num2Row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val num2 = TextView(this).apply {
            text = "7"
            textSize = 28f
            setTextColor(Color.rgb(198, 40, 40))
            setTypeface(null, Typeface.BOLD)
        }
        num2Row.addView(num2, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val icon2 = TextView(this).apply {
            text = "📄"
            textSize = 18f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(255, 205, 210), 10)
        }
        num2Row.addView(icon2, LinearLayout.LayoutParams(dp(36), dp(36)))
        statCard2.addView(num2Row)

        val label2 = TextView(this).apply {
            text = "Belum Mengumpulkan"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val l2Params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(4)
        }
        statCard2.addView(label2, l2Params)

        val card2Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(12)
        }
        statsRow.addView(statCard2, card2Params)

        contentLayout.addView(statsRow, statsParams)

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

        etSearch = EditText(this).apply {
            hint = "Cari nama pelajar..."
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = null
            setPadding(dp(12), 0, 0, 0)
            maxLines = 1
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchQuery = s?.toString()?.trim() ?: ""
                    updateList()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        searchBar.addView(etSearch, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(searchBar, searchParams)

        // 5. Filter Tabs Row (Semua, Sudah mengumpulkan, Belum)
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
            text = "Semua (25)"
            textSize = 11f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("semua") }
        }
        tabsLayout.addView(tabSemua, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabSudah = TextView(this).apply {
            text = "Sudah mengumpulkan (18)"
            textSize = 11f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("sudah") }
        }
        tabsLayout.addView(tabSudah, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabBelum = TextView(this).apply {
            text = "Belum (7)"
            textSize = 11f
            gravity = Gravity.CENTER
            setOnClickListener { setFilter("belum") }
        }
        tabsLayout.addView(tabBelum, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(tabsLayout, tabsParams)

        // 6. Student List Container
        listContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val listParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        contentLayout.addView(listContainer, listParams)

        updateList()

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 7. Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun setFilter(filter: String) {
        currentFilter = filter
        updateTabStyles()
        updateList()
    }

    private fun updateTabStyles() {
        val activeBg = roundedBackground(Color.rgb(33, 96, 245), 10)
        val inactiveBg = roundedBackground(Color.TRANSPARENT, 10)

        tabSemua.background = inactiveBg
        tabSemua.setTextColor(Color.rgb(117, 139, 169))
        tabSemua.setTypeface(null, Typeface.NORMAL)

        tabSudah.background = inactiveBg
        tabSudah.setTextColor(Color.rgb(117, 139, 169))
        tabSudah.setTypeface(null, Typeface.NORMAL)

        tabBelum.background = inactiveBg
        tabBelum.setTextColor(Color.rgb(117, 139, 169))
        tabBelum.setTypeface(null, Typeface.NORMAL)

        when (currentFilter) {
            "semua" -> {
                tabSemua.background = activeBg
                tabSemua.setTextColor(Color.WHITE)
                tabSemua.setTypeface(null, Typeface.BOLD)
            }
            "sudah" -> {
                tabSudah.background = activeBg
                tabSudah.setTextColor(Color.WHITE)
                tabSudah.setTypeface(null, Typeface.BOLD)
            }
            "belum" -> {
                tabBelum.background = activeBg
                tabBelum.setTextColor(Color.WHITE)
                tabBelum.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun updateList() {
        listContainer.removeAllViews()

        var filtered = when (currentFilter) {
            "sudah" -> allStudents.filter { it.status == "sudah" }
            "belum" -> allStudents.filter { it.status == "belum" }
            else -> allStudents
        }

        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        val sudahList = filtered.filter { it.status == "sudah" }
        val belumList = filtered.filter { it.status == "belum" }

        if (sudahList.isNotEmpty() && (currentFilter == "semua" || currentFilter == "sudah")) {
            val sectionHeader = createSectionTitle("Sudah Mengumpulkan (${sudahList.size})")
            listContainer.addView(sectionHeader)
            for (student in sudahList) {
                listContainer.addView(createStudentCard(student))
            }
        }

        if (belumList.isNotEmpty() && (currentFilter == "semua" || currentFilter == "belum")) {
            val sectionHeader = createSectionTitle("Belum Mengumpulkan (${belumList.size})")
            val shParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                if (sudahList.isNotEmpty()) topMargin = dp(20)
            }
            listContainer.addView(sectionHeader, shParams)
            for (student in belumList) {
                listContainer.addView(createStudentCard(student))
            }
        }
    }

    private fun createSectionTitle(title: String): TextView {
        return TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun createStudentCard(student: StudentSubmission): View {
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

        // Avatar placeholder
        val initials = student.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        val avatar = TextView(this).apply {
            text = initials
            textSize = 14f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(215, 230, 255), 24)
        }
        topRow.addView(avatar, LinearLayout.LayoutParams(dp(48), dp(48)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val nameTv = TextView(this).apply {
            text = student.name
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(nameTv)

        val timeTv = TextView(this).apply {
            text = student.time
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val tParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(timeTv, tParams)

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        // Status Badge ("Terkumpul" or "Belum mengumpulkan")
        val isSudah = student.status == "sudah"
        val badge = TextView(this).apply {
            text = if (isSudah) "Terkumpul" else "Belum mengumpulkan"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (isSudah) Color.rgb(46, 125, 50) else Color.rgb(198, 40, 40))
            background = roundedBackground(if (isSudah) Color.rgb(232, 245, 233) else Color.rgb(255, 235, 238), 8)
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }
        topRow.addView(badge)

        val arrowTv = TextView(this).apply {
            text = "›"
            textSize = 24f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        val aParams = LinearLayout.LayoutParams(dp(24), dp(32)).apply {
            marginStart = dp(8)
        }
        topRow.addView(arrowTv, aParams)

        card.addView(topRow)

        // Attached File Card if present
        if (student.fileName != null) {
            val fileCard = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(12), dp(10), dp(12), dp(10))
                background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            }
            val fcParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(14)
            }

            val fileIconBox = TextView(this).apply {
                text = student.fileIcon ?: "📄"
                textSize = 16f
                gravity = Gravity.CENTER
                background = roundedBackground(student.fileIconBgColor ?: Color.rgb(244, 67, 54), 8)
            }
            fileCard.addView(fileIconBox, LinearLayout.LayoutParams(dp(36), dp(36)))

            val fileTextCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, 0, 0)
            }

            val fileTitleTv = TextView(this).apply {
                text = student.fileName
                textSize = 13f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
            }
            fileTextCol.addView(fileTitleTv)

            val fileSizeTv = TextView(this).apply {
                text = student.fileSize
                textSize = 11f
                setTextColor(Color.rgb(117, 139, 169))
            }
            fileTextCol.addView(fileSizeTv)

            fileCard.addView(fileTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            card.addView(fileCard, fcParams)
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
                        val intent = Intent(this@PengumpulanTugasActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@PengumpulanTugasActivity, KelasSayaActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        val intent = Intent(this@PengumpulanTugasActivity, TugasActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        Toast.makeText(this@PengumpulanTugasActivity, "Membuka Asisten AI TemanStudi...", Toast.LENGTH_SHORT).show()
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@PengumpulanTugasActivity, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
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
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_TASK_MEETING = "extra_task_meeting"
    }
}
