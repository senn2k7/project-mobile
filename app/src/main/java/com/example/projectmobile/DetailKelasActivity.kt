package com.example.projectmobile

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

class DetailKelasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val className = intent.getStringExtra(EXTRA_CLASS_NAME) ?: "Pemrograman Mobile"
        val classCode = intent.getStringExtra(EXTRA_CLASS_CODE) ?: "PM2025-A"
        val lecturerName = intent.getStringExtra(EXTRA_LECTURER_NAME) ?: "Budi Santoso"

        setContentView(createScreen(className, classCode, lecturerName))
    }

    private fun createScreen(className: String, classCode: String, lecturerName: String): View {
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

        // 1. Header Top (Back button, Title "Detail Kelas", Options menu)
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

        val optionsMenu = TextView(this).apply {
            text = "⋮"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
        }
        headerLayout.addView(optionsMenu, LinearLayout.LayoutParams(dp(36), dp(36)))

        contentLayout.addView(headerLayout)

        // 2. Course Info Card
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
            text = "💻"
            textSize = 24f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
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

        val courseSubTv = TextView(this).apply {
            text = "Universitas"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val csParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        courseTextCol.addView(courseSubTv, csParams)

        val courseCodeTv = TextView(this).apply {
            text = "Kode Kelas: $classCode"
            textSize = 12f
            setTextColor(Color.rgb(33, 96, 245))
        }
        val ccParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(4)
        }
        courseTextCol.addView(courseCodeTv, ccParams)

        courseTopRow.addView(courseTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        courseCard.addView(courseTopRow)

        // Instructor Row inside Course Card
        val instructorRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(14), 0, 0)
        }
        val instParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(14)
        }

        // Divider
        val divider = View(this).apply {
            background = roundedBackground(Color.rgb(230, 238, 248), 1)
        }
        courseCard.addView(divider, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)))

        val initials = lecturerName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        val avatarTv = TextView(this).apply {
            text = initials
            textSize = 14f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(215, 230, 255), 20)
        }
        instructorRow.addView(avatarTv, LinearLayout.LayoutParams(dp(40), dp(40)))

        val instTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val instNameTv = TextView(this).apply {
            text = lecturerName
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        instTextCol.addView(instNameTv)

        val instRoleTv = TextView(this).apply {
            text = "Pengajar"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        instTextCol.addView(instRoleTv)

        instructorRow.addView(instTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        courseCard.addView(instructorRow, instParams)

        // Description Section
        val descTitle = TextView(this).apply {
            text = "Deskripsi Kelas"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val dtParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        courseCard.addView(descTitle, dtParams)

        val descBody = TextView(this).apply {
            text = "Mata kuliah ini membahas konsep dasar pengembangan aplikasi mobile menggunakan Android Studio, mulai dari UI, komponen, hingga..."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setLineSpacing(dp(3).toFloat(), 1f)
        }
        val dbParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        courseCard.addView(descBody, dbParams)

        val editDescBtn = TextView(this).apply {
            text = "✏️ Edit Deskripsi"
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
        }
        val edParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(10)
        }
        courseCard.addView(editDescBtn, edParams)

        contentLayout.addView(courseCard, courseParams)

        // 3. Tabs Row (Materi, Tugas, Lihat Hasil Kuis, Buat Kuis, Diskusi)
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
        tabsLayout.addView(createTabItem("Lihat Hasil", false), createTabItemParams())
        tabsLayout.addView(createTabItem("Buat Kuis", false), createTabItemParams())
        tabsLayout.addView(createTabItem("Diskusi", false), createTabItemParams())

        contentLayout.addView(tabsLayout, tabsParams)

        // 4. Presensi Buttons Row
        val presensiRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val presensiParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val btnPresensiManual = TextView(this).apply {
            text = "Presensi Manual"
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(232, 242, 255), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@DetailKelasActivity, PresensiManualActivity::class.java)
                startActivity(intent)
            }
        }
        presensiRow.addView(btnPresensiManual, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val btnPresensiQr = TextView(this).apply {
            text = "Presensi dengan QR Code"
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(232, 242, 255), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }
        val qrParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(8)
        }
        presensiRow.addView(btnPresensiQr, qrParams)

        contentLayout.addView(presensiRow, presensiParams)

        // 5. Section: Daftar Materi
        val materiHeader = createSectionHeader("Daftar Materi", showAction = false)
        val materiHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(materiHeader, materiHeaderParams)

        // Material Cards List
        val materiListContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val materiListParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        materiListContainer.addView(createMaterialCard("Pengenalan Android Studio.pdf", "2.4 MB", "📄", Color.rgb(244, 67, 54)))
        materiListContainer.addView(createMaterialCard("Struktur Project.zip", "5.1 MB", "📦", Color.rgb(255, 152, 0)))
        materiListContainer.addView(createMaterialCard("Modul Pertemuan 1.pdf", "3.2 MB", "📄", Color.rgb(244, 67, 54)))

        contentLayout.addView(materiListContainer, materiListParams)

        // + Tambah Materi Button
        val btnTambahMateri = TextView(this).apply {
            text = "+ Tambah Materi"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
        }
        val btnTambahParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(btnTambahMateri, btnTambahParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 6. Bottom Navigation Bar
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
                    val intent = Intent(this@DetailKelasActivity, TugasActivity::class.java).apply {
                        putExtra(TugasActivity.EXTRA_CLASS_NAME, intent.getStringExtra(EXTRA_CLASS_NAME) ?: "Pemrograman Mobile")
                        putExtra(TugasActivity.EXTRA_CLASS_CODE, "Kelas A • " + (intent.getStringExtra(EXTRA_CLASS_CODE) ?: "PM2025-A"))
                    }
                    startActivity(intent)
                }
            } else if (title == "Buat Kuis") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@DetailKelasActivity, BuatKuisActivity::class.java)
                    startActivity(intent)
                }
            } else if (title == "Lihat Hasil") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@DetailKelasActivity, NilaiHasilKuisActivity::class.java)
                    startActivity(intent)
                }
            } else if (title == "Diskusi") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@DetailKelasActivity, DiskusiActivity::class.java).apply {
                        putExtra(DiskusiActivity.EXTRA_COURSE_NAME, intent.getStringExtra(EXTRA_CLASS_NAME) ?: "Pemrograman Mobile")
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun createTabItemParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
    }

    private fun createSectionHeader(title: String, showAction: Boolean = true): View {
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val titleTv = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        header.addView(titleTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        if (showAction) {
            val actionTv = TextView(this).apply {
                text = "Lihat Semua >"
                textSize = 13f
                setTextColor(Color.rgb(33, 96, 245))
            }
            header.addView(actionTv)
        }

        return header
    }

    private fun createMaterialCard(title: String, fileSize: String, icon: String, iconBgColor: Int): View {
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

        val optionsTv = TextView(this).apply {
            text = "•••"
            textSize = 16f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        card.addView(optionsTv, LinearLayout.LayoutParams(dp(24), dp(32)))

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
                        val intent = Intent(this@DetailKelasActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@DetailKelasActivity, KelasSayaActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        val intent = Intent(this@DetailKelasActivity, TugasActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        val intent = Intent(this@DetailKelasActivity, AiAssistantActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Profil" -> {
                        Toast.makeText(this@DetailKelasActivity, "Membuka Profil Pengajar...", Toast.LENGTH_SHORT).show()
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
        const val EXTRA_LECTURER_NAME = "extra_lecturer_name"
    }
}
