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

class DetailKelasPelajarInstitusiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val className = intent.getStringExtra(EXTRA_CLASS_NAME) ?: "Pengenalan Android Studio"
        setContentView(createScreen(className))
    }

    private fun createScreen(className: String): View {
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

        // 1. Header Top (Back button, Title "Detail Kelas")
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
            text = "Detail Kelas"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        headerLayout.addView(headerTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        contentLayout.addView(headerLayout)

        // 2. Top Course Card
        val courseCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val courseParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val courseTopRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val courseIconBox = TextView(this).apply {
            text = "T"
            textSize = 26f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(13, 110, 253), 12)
        }
        courseTopRow.addView(courseIconBox, LinearLayout.LayoutParams(dp(56), dp(56)))

        val courseTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), 0, 0, 0)
        }

        val courseTitleTv = TextView(this).apply {
            text = className
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        courseTextCol.addView(courseTitleTv)

        val badge = TextView(this).apply {
            text = "Pertemuan 1"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.rgb(33, 96, 245))
            background = roundedBackground(Color.rgb(232, 242, 255), 8)
            setPadding(dp(10), dp(3), dp(10), dp(3))
        }
        val bParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        courseTextCol.addView(badge, bParams)

        courseTopRow.addView(courseTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        courseCard.addView(courseTopRow)

        // Lecturer Info Row inside Course Card
        val lecturerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val lecParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val avatarTv = TextView(this).apply {
            text = "BS"
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(215, 230, 255), 20)
        }
        lecturerRow.addView(avatarTv, LinearLayout.LayoutParams(dp(38), dp(38)))

        val lecTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val lecNameTv = TextView(this).apply {
            text = "Dr. Budi Santoso"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        lecTextCol.addView(lecNameTv)

        val lecDateTv = TextView(this).apply {
            text = "12 Mei 2025"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        lecTextCol.addView(lecDateTv)

        lecturerRow.addView(lecTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        courseCard.addView(lecturerRow, lecParams)

        contentLayout.addView(courseCard, courseParams)

        // 3. Tabs Row (Materi, Tugas, Kuis, Diskusi, Presensi)
        val tabsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = roundedBackground(Color.rgb(232, 240, 252), 12)
        }
        val tabsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(16)
        }

        tabsLayout.addView(createTabItem("Materi", true), createTabItemParams())
        tabsLayout.addView(createTabItem("Tugas", false), createTabItemParams())
        tabsLayout.addView(createTabItem("Kuis", false), createTabItemParams())
        tabsLayout.addView(createTabItem("Diskusi", false), createTabItemParams())
        tabsLayout.addView(createTabItem("Presensi", false), createTabItemParams())

        contentLayout.addView(tabsLayout, tabsParams)

        // 4. Section: Pendahuluan
        val pendahuluanTitle = TextView(this).apply {
            text = "Pendahuluan"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val ptParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(pendahuluanTitle, ptParams)

        val pendahuluanBody = TextView(this).apply {
            text = "Android Studio adalah Integrated Development Environment (IDE) resmi dari Google untuk mengembangkan aplikasi Android. Dalam pertemuan ini, kita akan mempelajari dasar-dasar Android Studio, struktur proyek, dan konfigurasi awal."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setLineSpacing(dp(3).toFloat(), 1f)
        }
        val pbParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        contentLayout.addView(pendahuluanBody, pbParams)

        // 5. Section: File Materi
        val fileHeader = TextView(this).apply {
            text = "📁 File Materi"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val fhParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(fileHeader, fhParams)

        val filesContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val fcParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        filesContainer.addView(createFileCard("Panduan Android Studio.pdf", "2.5 MB", "📄", Color.rgb(244, 67, 54), "Buka"))
        filesContainer.addView(createFileCard("Pengenalan Android Studio.pptx", "4.2 MB", "📊", Color.rgb(255, 152, 0), "Unduh"))
        filesContainer.addView(createFileCard("Latihan Praktik.docx", "1.1 MB", "📝", Color.rgb(33, 96, 245), "Unduh"))

        contentLayout.addView(filesContainer, fcParams)

        // 6. Bottom Action Buttons Row (Buka Materi, Tandai Selesai)
        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val actParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }

        val btnBukaMateri = TextView(this).apply {
            text = "Buka Materi"
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@DetailKelasPelajarInstitusiActivity, "Membuka materi...", Toast.LENGTH_SHORT).show()
            }
        }
        actionRow.addView(btnBukaMateri, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val btnTandaiSelesai = TextView(this).apply {
            text = "Tandai Selesai"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(46, 125, 50), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@DetailKelasPelajarInstitusiActivity, "Materi ditandai selesai!", Toast.LENGTH_SHORT).show()
            }
        }
        val tsParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(12)
        }
        actionRow.addView(btnTandaiSelesai, tsParams)

        contentLayout.addView(actionRow, actParams)

        // 7. Floating / CTA Button: Tanyakan Materi ke AI
        val btnAi = TextView(this).apply {
            text = "✨ Tanyakan Materi ke AI"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@DetailKelasPelajarInstitusiActivity, AiAssistantActivity::class.java)
                startActivity(intent)
            }
        }
        val aiParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        contentLayout.addView(btnAi, aiParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 8. Student Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createTabItem(title: String, isSelected: Boolean): View {
        return TextView(this).apply {
            text = title
            textSize = 12f
            setTextColor(if (isSelected) Color.WHITE else Color.rgb(117, 139, 169))
            if (isSelected) setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            if (isSelected) {
                background = roundedBackground(Color.rgb(33, 96, 245), 10)
            }
            setPadding(dp(6), dp(8), dp(6), dp(8))
            if (title == "Tugas") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@DetailKelasPelajarInstitusiActivity, TugasPelajarInstitusiActivity::class.java)
                    startActivity(intent)
                }
            } else if (title == "Kuis") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@DetailKelasPelajarInstitusiActivity, DetailKuisPelajarInstitusiActivity::class.java).apply {
                        putExtra(DetailKuisPelajarInstitusiActivity.EXTRA_QUIZ_TITLE, "Kuis Sistem Cerdas")
                        putExtra(DetailKuisPelajarInstitusiActivity.EXTRA_MEETING, "Pertemuan 2")
                    }
                    startActivity(intent)
                }
            } else if (title == "Diskusi") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@DetailKelasPelajarInstitusiActivity, DiskusiPelajarInstitusiActivity::class.java).apply {
                        putExtra(DiskusiPelajarInstitusiActivity.EXTRA_COURSE_NAME, "Pemrograman Mobile")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun createTabItemParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
    }

    private fun createFileCard(title: String, fileSize: String, icon: String, iconBgColor: Int, actionText: String): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = icon
            textSize = 18f
            gravity = Gravity.CENTER
            background = roundedBackground(iconBgColor, 10)
        }
        card.addView(iconBox, LinearLayout.LayoutParams(dp(44), dp(44)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val titleTv = TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val sizeTv = TextView(this).apply {
            text = fileSize
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val sizeParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(sizeTv, sizeParams)

        card.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val actionTv = TextView(this).apply {
            text = actionText
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@DetailKelasPelajarInstitusiActivity, "$actionText file: $title", Toast.LENGTH_SHORT).show()
            }
        }
        card.addView(actionTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(10)
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
                        val intent = Intent(this@DetailKelasPelajarInstitusiActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PELAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@DetailKelasPelajarInstitusiActivity, KelasPelajarInstitusiActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Aktivitas", "Tugas" -> {
                        Toast.makeText(this@DetailKelasPelajarInstitusiActivity, "Membuka Aktivitas Pelajar...", Toast.LENGTH_SHORT).show()
                    }
                    "AI" -> {
                        val intent = Intent(this@DetailKelasPelajarInstitusiActivity, AiAssistantActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@DetailKelasPelajarInstitusiActivity, PilihPeranActivity.ROLE_PELAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
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
    }
}
