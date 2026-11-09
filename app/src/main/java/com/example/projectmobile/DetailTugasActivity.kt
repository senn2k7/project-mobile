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

class DetailTugasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Membuat UI Login Android"
        val taskMeeting = intent.getStringExtra(EXTRA_TASK_MEETING) ?: "Pemrograman Mobile • Pertemuan 3"
        val taskStatus = intent.getStringExtra(EXTRA_TASK_STATUS) ?: "Tugas Berlangsung"
        val taskDeadline = intent.getStringExtra(EXTRA_TASK_DEADLINE) ?: "20 Jun 2025, 23.59"

        setContentView(createScreen(taskTitle, taskMeeting, taskStatus, taskDeadline))
    }

    private fun createScreen(taskTitle: String, taskMeeting: String, taskStatus: String, taskDeadline: String): View {
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

        // 1. Header Top (Back button, Title "Detail Tugas", Options menu)
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
            text = "Detail Tugas"
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
        val isBerlangsung = taskStatus.contains("Berlangsung", ignoreCase = true)
        val badge = TextView(this).apply {
            text = taskStatus
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (isBerlangsung) Color.rgb(33, 96, 245) else Color.rgb(46, 125, 50))
            background = roundedBackground(if (isBerlangsung) Color.rgb(232, 242, 255) else Color.rgb(232, 245, 233), 8)
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        val badgeParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        taskSummaryCard.addView(badge, badgeParams)

        contentLayout.addView(taskSummaryCard, summaryParams)

        // 3. Section: Deskripsi Tugas
        val descSection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val descParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val descTitle = TextView(this).apply {
            text = "Deskripsi Tugas"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        descSection.addView(descTitle)

        val descBody = TextView(this).apply {
            text = "Buat tampilan UI Login sesuai dengan desain yang telah diberikan. Gunakan komponen Material Design dan pastikan aplikasi dapat dijalankan."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setLineSpacing(dp(3).toFloat(), 1f)
        }
        val dbParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        descSection.addView(descBody, dbParams)

        // Deadline Row
        val deadlineRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val dParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val deadlineIcon = TextView(this).apply {
            text = "📅"
            textSize = 16f
            gravity = Gravity.CENTER
        }
        deadlineRow.addView(deadlineIcon, LinearLayout.LayoutParams(dp(28), dp(28)))

        val deadlineTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), 0, 0, 0)
        }
        val deadlineLabel = TextView(this).apply {
            text = "Batas Pengumpulan"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        deadlineTextCol.addView(deadlineLabel)

        val deadlineVal = TextView(this).apply {
            text = taskDeadline
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        deadlineTextCol.addView(deadlineVal)

        deadlineRow.addView(deadlineTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        descSection.addView(deadlineRow, dParams)

        // Tipe Tugas Row
        val typeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val tParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val typeIcon = TextView(this).apply {
            text = "⚪"
            textSize = 14f
            gravity = Gravity.CENTER
        }
        typeRow.addView(typeIcon, LinearLayout.LayoutParams(dp(28), dp(28)))

        val typeTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), 0, 0, 0)
        }
        val typeLabel = TextView(this).apply {
            text = "Tipe Tugas"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        typeTextCol.addView(typeLabel)

        val typeVal = TextView(this).apply {
            text = "Individu"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        typeTextCol.addView(typeVal)

        typeRow.addView(typeTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        descSection.addView(typeRow, tParams)

        contentLayout.addView(descSection, descParams)

        // 4. Section: Lampiran Tugas
        val lampiranHeader = createSectionHeader("Lampiran Tugas", showAction = false)
        val lampiranHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(lampiranHeader, lampiranHeaderParams)

        val attachmentsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        val attParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        attachmentsContainer.addView(createAttachmentCard("Desain UI Login.png", "1.2 MB"))
        attachmentsContainer.addView(createAttachmentCard("Panduan Pengerjaan.pdf", "2.1 MB"))

        contentLayout.addView(attachmentsContainer, attParams)

        // 5. Section: Status Pengumpulan
        val statusHeader = createSectionHeader("Status Pengumpulan", showAction = false)
        val statusHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(statusHeader, statusHeaderParams)

        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val statsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val statCard1 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.rgb(232, 245, 233), 16)
        }
        val num1 = TextView(this).apply {
            text = "18"
            textSize = 28f
            setTextColor(Color.rgb(46, 125, 50))
            setTypeface(null, Typeface.BOLD)
        }
        statCard1.addView(num1)
        val label1 = TextView(this).apply {
            text = "Mengumpulkan"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        statCard1.addView(label1)
        statsRow.addView(statCard1, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val statCard2 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.rgb(255, 235, 238), 16)
        }
        val num2 = TextView(this).apply {
            text = "7"
            textSize = 28f
            setTextColor(Color.rgb(198, 40, 40))
            setTypeface(null, Typeface.BOLD)
        }
        statCard2.addView(num2)
        val label2 = TextView(this).apply {
            text = "Belum Mengumpulkan"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        statCard2.addView(label2)
        val card2Params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(12)
        }
        statsRow.addView(statCard2, card2Params)

        contentLayout.addView(statsRow, statsParams)

        // 6. Action Buttons (Lihat Pengumpulan, Edit Tugas)
        val btnLihat = TextView(this).apply {
            text = "Lihat Pengumpulan"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@DetailTugasActivity, PengumpulanTugasActivity::class.java).apply {
                    putExtra(PengumpulanTugasActivity.EXTRA_TASK_TITLE, intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Membuat UI Login Android")
                    putExtra(PengumpulanTugasActivity.EXTRA_TASK_MEETING, "Pemrograman Mobile • Pertemuan 3")
                }
                startActivity(intent)
            }
        }
        val blParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(btnLihat, blParams)

        val btnEdit = TextView(this).apply {
            text = "Edit Tugas"
            textSize = 15f
            setTextColor(Color.rgb(33, 96, 245))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 16, Color.rgb(33, 96, 245), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
        }
        val beParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        contentLayout.addView(btnEdit, beParams)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 7. Bottom Navigation Bar
        val bottomNav = createBottomNav()
        rootLayout.addView(bottomNav)

        return rootLayout
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

    private fun createAttachmentCard(title: String, fileSize: String): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = "📄"
            textSize = 18f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(244, 67, 54), 10)
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
                        val intent = Intent(this@DetailTugasActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        val intent = Intent(this@DetailTugasActivity, KelasSayaActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        val intent = Intent(this@DetailTugasActivity, TugasActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        Toast.makeText(this@DetailTugasActivity, "Membuka Asisten AI TemanStudi...", Toast.LENGTH_SHORT).show()
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@DetailTugasActivity, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_INSTITUSI)
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
        const val EXTRA_TASK_STATUS = "extra_task_status"
        const val EXTRA_TASK_DEADLINE = "extra_task_deadline"
    }
}
