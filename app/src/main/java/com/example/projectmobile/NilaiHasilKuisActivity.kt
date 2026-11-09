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

class NilaiHasilKuisActivity : AppCompatActivity() {

    private data class StudentGrade(
        val name: String,
        val nim: String,
        val score: Int?, // null if not finished
        val status: String // "selesai" or "belum"
    )

    private val allStudents = listOf(
        StudentGrade("Andi Pratama", "2201001", 90, "selesai"),
        StudentGrade("Siti Aminah", "2201002", 85, "selesai"),
        StudentGrade("Rina Wati", "2201003", 70, "selesai"),
        StudentGrade("Dewi Lestari", "2201004", 95, "selesai"),
        StudentGrade("Rizky Putra", "2201005", null, "belum")
    )

    private lateinit var studentListContainer: LinearLayout
    private lateinit var etSearch: EditText
    private var searchQuery = ""

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

        // 1. Header Top (Back button, Title "Nilai dan Hasil Kuis", Options menu)
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
            text = "Nilai dan Hasil Kuis"
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

        // 2. Top Context Card
        val contextCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val contextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val courseRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val courseIcon = TextView(this).apply {
            text = "🏛️"
            textSize = 16f
        }
        courseRow.addView(courseIcon)
        val courseTitle = TextView(this).apply {
            text = "Pemrograman Mobile – Kelas A"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(10), 0, 0, 0)
        }
        courseRow.addView(courseTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        contextCard.addView(courseRow)

        // Quiz Sub-card
        val quizSubCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = roundedBackground(Color.rgb(240, 246, 255), 12)
        }
        val qscParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val qIcon = TextView(this).apply {
            text = "📋"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 10)
        }
        quizSubCard.addView(qIcon, LinearLayout.LayoutParams(dp(44), dp(44)))

        val qTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val qTitle = TextView(this).apply {
            text = "Kuis Struktur Data"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        qTextCol.addView(qTitle)

        val qSub = TextView(this).apply {
            text = "Pertemuan 6 • 20 Jun 2025"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        qTextCol.addView(qSub)

        quizSubCard.addView(qTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val qArrow = TextView(this).apply {
            text = "›"
            textSize = 22f
            setTextColor(Color.rgb(33, 96, 245))
        }
        quizSubCard.addView(qArrow)

        contextCard.addView(quizSubCard, qscParams)
        contentLayout.addView(contextCard, contextParams)

        // 3. Summary Stats Row (28 Selesai, 4 Belum Selesai, 82 Rata-rata Nilai)
        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val statsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        statsRow.addView(createStatBox("28", "Selesai", Color.rgb(232, 245, 233), Color.rgb(46, 125, 50)), createStatParams(0))
        statsRow.addView(createStatBox("4", "Belum Selesai", Color.rgb(255, 235, 238), Color.rgb(198, 40, 40)), createStatParams(dp(8)))
        statsRow.addView(createStatBox("82", "Rata-rata Nilai", Color.rgb(232, 242, 255), Color.rgb(33, 96, 245)), createStatParams(dp(8)))

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
                    updateStudentList()
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        searchBar.addView(etSearch, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(searchBar, searchParams)

        // 5. Table Header ("Nama Pelajar" vs "Nilai & Status")
        val tableHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(8), dp(4), dp(4))
        }
        val thParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val thName = TextView(this).apply {
            text = "Nama Pelajar"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setTypeface(null, Typeface.BOLD)
        }
        tableHeader.addView(thName, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val thScore = TextView(this).apply {
            text = "Nilai & Status"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setTypeface(null, Typeface.BOLD)
        }
        tableHeader.addView(thScore)

        contentLayout.addView(tableHeader, thParams)

        // 6. Student List Container
        studentListContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val listParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        contentLayout.addView(studentListContainer, listParams)

        updateStudentList()

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 7. Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createStatBox(num: String, label: String, bgColor: Int, numColor: Int): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(14), dp(12), dp(14))
            background = roundedBackground(bgColor, 14)
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val numTv = TextView(this).apply {
            text = num
            textSize = 22f
            setTextColor(numColor)
            setTypeface(null, Typeface.BOLD)
        }
        box.addView(numTv)

        val labelTv = TextView(this).apply {
            text = label
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val lParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        box.addView(labelTv, lParams)

        return box
    }

    private fun createStatParams(marginStart: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            this.marginStart = marginStart
        }
    }

    private fun updateStudentList() {
        studentListContainer.removeAllViews()

        val filtered = if (searchQuery.isNotEmpty()) {
            allStudents.filter { it.name.contains(searchQuery, ignoreCase = true) }
        } else {
            allStudents
        }

        for (student in filtered) {
            studentListContainer.addView(createStudentCard(student))
        }
    }

    private fun createStudentCard(student: StudentGrade): View {
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

        val nameCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val nameTv = TextView(this).apply {
            text = student.name
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        nameCol.addView(nameTv)

        val isSelesai = student.status == "selesai"
        val badge = TextView(this).apply {
            text = if (isSelesai) "Selesai" else "Belum Selesai"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (isSelesai) Color.rgb(46, 125, 50) else Color.rgb(198, 40, 40))
            background = roundedBackground(if (isSelesai) Color.rgb(232, 245, 233) else Color.rgb(255, 235, 238), 8)
            setPadding(dp(8), dp(3), dp(8), dp(3))
        }
        val bParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(4)
        }
        nameCol.addView(badge, bParams)

        topRow.addView(nameCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val rightCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
        }

        val scoreTv = TextView(this).apply {
            text = if (isSelesai) "${student.score}" else "-"
            textSize = 22f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.END
        }
        rightCol.addView(scoreTv)

        if (isSelesai) {
            val btnLihatJawaban = TextView(this).apply {
                text = "Lihat Jawaban"
                textSize = 12f
                setTextColor(Color.rgb(33, 96, 245))
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                background = roundedBackground(Color.WHITE, 10, Color.rgb(33, 96, 245), 1)
                setPadding(dp(12), dp(6), dp(12), dp(6))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@NilaiHasilKuisActivity, DetailHasilKuisActivity::class.java).apply {
                        putExtra(DetailHasilKuisActivity.EXTRA_STUDENT_NAME, student.name)
                        putExtra(DetailHasilKuisActivity.EXTRA_STUDENT_NIM, student.nim)
                        putExtra(DetailHasilKuisActivity.EXTRA_STUDENT_SCORE, student.score ?: 0)
                    }
                    startActivity(intent)
                }
            }
            val bjParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(8)
            }
            rightCol.addView(btnLihatJawaban, bjParams)
        }

        topRow.addView(rightCol)
        card.addView(topRow)

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
                        val intent = Intent(this@NilaiHasilKuisActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@NilaiHasilKuisActivity, KelasSayaActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        val intent = Intent(this@NilaiHasilKuisActivity, TugasActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        Toast.makeText(this@NilaiHasilKuisActivity, "Membuka Asisten AI TemanStudi...", Toast.LENGTH_SHORT).show()
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@NilaiHasilKuisActivity, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
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
