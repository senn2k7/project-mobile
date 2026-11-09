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
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
        val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI

        setContentView(createDashboard(role, mode))
    }

    private fun createDashboard(role: String, mode: String): View {
        if (role == PilihPeranActivity.ROLE_PENGAJAR && mode == ModePembelajaranActivity.MODE_MANDIRI) {
            return createTeacherMandiriDashboard()
        }

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

        // 1. Header Top (Profile, Greeting, Notification)
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // Avatar placeholder circle
        val avatarView = TextView(this).apply {
            text = "👤"
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(215, 230, 255), 24)
        }
        avatarView.isClickable = true
        avatarView.isFocusable = true
        avatarView.setOnClickListener { UiKit.openProfile(this, role, mode) }
        headerLayout.addView(avatarView, LinearLayout.LayoutParams(dp(48), dp(48)))

        // Greeting text
        val greetingTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }

        val greetingTitle = TextView(this).apply {
            text = if (role == PilihPeranActivity.ROLE_PENGAJAR) "Halo, Pak Budi! 👋" else "Halo, Siti! 👋"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            includeFontPadding = false
        }
        greetingTextLayout.addView(greetingTitle)

        val greetingSubtitle = TextView(this).apply {
            text = "Selamat belajar di TemanStudi"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            includeFontPadding = false
        }
        val subParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        greetingTextLayout.addView(greetingSubtitle, subParams)

        headerLayout.addView(greetingTextLayout, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        // Notification Bell
        val bellView = TextView(this).apply {
            text = "🔔"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 22, Color.rgb(220, 231, 244), 1)
        }
        headerLayout.addView(bellView, LinearLayout.LayoutParams(dp(44), dp(44)))

        contentLayout.addView(headerLayout)

        // 2. Search Bar
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
            text = if (role == PilihPeranActivity.ROLE_PENGAJAR) "Cari kelas, materi, atau tugas..." else "Cari kelas atau materi..."
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(dp(12), 0, 0, 0)
        }
        searchBar.addView(searchText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        if (mode == ModePembelajaranActivity.MODE_MANDIRI && role == PilihPeranActivity.ROLE_PELAJAR) {
            val filterIcon = TextView(this).apply {
                text = "☰"
                textSize = 18f
                setTextColor(Color.rgb(33, 96, 245))
                gravity = Gravity.CENTER
            }
            searchBar.addView(filterIcon, LinearLayout.LayoutParams(dp(36), dp(36)))
        }

        val searchParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(searchBar, searchParams)

        // 3. Stat Cards Row
        val statsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val statsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        contentLayout.addView(statsContainer, statsParams)

        if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            // Teacher Stats: 4 cards
            statsContainer.addView(createStatCard("🏫", "4", "Kelas Diampu", Color.rgb(33, 96, 245)), createStatCardParams())
            statsContainer.addView(createStatCard("📄", "12", "Tugas Aktif", Color.rgb(255, 152, 0)), createStatCardParams())
            statsContainer.addView(createStatCard("📋", "3", "Perlu Dinilai", Color.rgb(103, 58, 183)), createStatCardParams())
            statsContainer.addView(createStatCard("👥", "86", "Pelajar", Color.rgb(76, 175, 80)), createStatCardParams())
        } else if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
            // Student Mandiri Stats: 3 cards (0, 0, 0)
            statsContainer.addView(createStatCard("🏫", "0", "Kelas Aktif", Color.rgb(33, 96, 245)), createStatCardParams())
            statsContainer.addView(createStatCard("📄", "0", "Tugas", Color.rgb(255, 152, 0)), createStatCardParams())
            statsContainer.addView(createStatCard("📋", "0", "Kuis", Color.rgb(103, 58, 183)), createStatCardParams())
        } else {
            // Student Institusi Stats: 3 cards
            statsContainer.addView(createStatCard("🏫", "3", "Kelas Aktif", Color.rgb(33, 96, 245)), createStatCardParams())
            statsContainer.addView(createStatCard("📄", "2", "Tugas Pending", Color.rgb(255, 152, 0)), createStatCardParams())
            statsContainer.addView(createStatCard("📋", "1", "Kuis Mendatang", Color.rgb(103, 58, 183)), createStatCardParams())
        }

        // 4. Section: Kelas Saya
        val kelasHeader = createSectionHeader("Kelas Saya", showAction = true, onActionClick = if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            {
                val intent = Intent(this, KelasSayaActivity::class.java).apply {
                    putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, mode)
                    putExtra(PilihPeranActivity.EXTRA_ROLE, role)
                }
                startActivity(intent)
            }
        } else null)
        val kelasHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(kelasHeader, kelasHeaderParams)

        if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            contentLayout.addView(createTeacherClassCard("Pemrograman Mobile", "Universitas • Kelas A", "28 pelajar", "💻"))
            contentLayout.addView(createTeacherClassCard("Sistem Cerdas", "Universitas • Kelas B", "32 pelajar", "🤖"))
            contentLayout.addView(createTeacherClassCard("Basis Data", "Universitas • Kelas C", "26 pelajar", "🗄️"))
        } else if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
            contentLayout.addView(createMandiriEmptyClassCard())
        } else {
            contentLayout.addView(createStudentClassCard("Pemrograman Mobile", "Dr. Budi Santoso", 65, "Pertemuan 2: UI Android", "Besok, 10:00"))
            contentLayout.addView(createStudentClassCard("Sistem Cerdas", "Dr. Rina Wati", 40, "Pertemuan 1: Pengenalan AI", "Jumat, 08:00"))
            contentLayout.addView(createStudentClassCard("Basis Data", "Dr. Ahmad Fauzi", 75, "Pertemuan 3: Normalisasi", "Sabtu, 10:00"))
        }

        // 5. Section: Agenda Mendatang
        if (role == PilihPeranActivity.ROLE_PELAJAR) {
            val agendaHeader = createSectionHeader("Agenda Mendatang")
            val agendaHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(24)
            }
            contentLayout.addView(agendaHeader, agendaHeaderParams)

            if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
                contentLayout.addView(createMandiriEmptyAgendaCard())
            } else {
                contentLayout.addView(createAgendaItem("Tugas: Membuat UI Login Android", "12 Jun 2025", Color.rgb(244, 67, 54), "📄"))
                contentLayout.addView(createAgendaItem("Kuis: Sistem Cerdas", "15 Jun 2025", Color.rgb(33, 96, 245), "📋"))

                // AI Banner
                val aiBanner = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(16), dp(16), dp(16), dp(16))
                    background = roundedBackground(Color.rgb(108, 92, 231), 16)
                }

                val aiBannerTextLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val aiTitle = TextView(this).apply {
                    text = "Ada materi yang sulit?"
                    textSize = 14f
                    setTextColor(Color.WHITE)
                }
                aiBannerTextLayout.addView(aiTitle)

                val aiSubtitle = TextView(this).apply {
                    text = "Tanya TemanStudi."
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.WHITE)
                }
                aiBannerTextLayout.addView(aiSubtitle)
                aiBanner.addView(aiBannerTextLayout)

                val aiButton = TextView(this).apply {
                    text = "Mulai Bertanya"
                    textSize = 13f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.rgb(33, 96, 245))
                    gravity = Gravity.CENTER
                    setPadding(dp(16), dp(10), dp(16), dp(10))
                    background = roundedBackground(Color.WHITE, 20)
                }
                aiBanner.addView(aiButton)

                val aiBannerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp(24)
                }
                contentLayout.addView(aiBanner, aiBannerParams)
            }
        }

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 6. Bottom Navigation Bar
        val bottomNav = createBottomNav(role, mode)
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createTeacherMandiriDashboard(): View {
        val bgColor = Color.rgb(245, 250, 255)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

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

        // Header Top
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val avatarView = TextView(this).apply {
            text = "👤"
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(215, 230, 255), 24)
        }
        avatarView.isClickable = true
        avatarView.isFocusable = true
        avatarView.setOnClickListener { UiKit.openProfile(this, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_MANDIRI) }
        headerLayout.addView(avatarView, LinearLayout.LayoutParams(dp(48), dp(48)))

        val greetingTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }

        val greetingTitle = TextView(this).apply {
            text = "Halo, Pengajar! 👋"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            includeFontPadding = false
        }
        greetingTextLayout.addView(greetingTitle)

        val greetingSubtitle = TextView(this).apply {
            text = "Kelola kelas mandiri Anda"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            includeFontPadding = false
        }
        val subParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        greetingTextLayout.addView(greetingSubtitle, subParams)

        headerLayout.addView(greetingTextLayout, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val bellView = TextView(this).apply {
            text = "🔔"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.WHITE, 22, Color.rgb(220, 231, 244), 1)
        }
        headerLayout.addView(bellView, LinearLayout.LayoutParams(dp(44), dp(44)))

        contentLayout.addView(headerLayout)

        // Hero Card: Kelas Mandiri
        val heroCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val heroTop = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val heroIconBox = TextView(this).apply {
            text = "🏫"
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        heroTop.addView(heroIconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

        val heroTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val heroTitle = TextView(this).apply {
            text = "Kelas Mandiri"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        heroTextCol.addView(heroTitle)

        val heroSub = TextView(this).apply {
            text = "Buat kelas dan bagikan kode untuk mulai mengajar."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val hsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        heroTextCol.addView(heroSub, hsParams)

        heroTop.addView(heroTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        heroCard.addView(heroTop)

        val btnBuatKelasBaru = TextView(this).apply {
            text = "+ Buat Kelas Baru"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            isClickable = true
            isFocusable = true
        }
        val bcnParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        heroCard.addView(btnBuatKelasBaru, bcnParams)

        val heroParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(heroCard, heroParams)

        // Section: Akses Cepat
        val aksesHeader = createSectionHeader("Akses Cepat", showAction = false)
        val aksesHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(aksesHeader, aksesHeaderParams)

        val aksesContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val aksesParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        aksesContainer.addView(createAksesCepatCard("➕", "Buat Kelas"), createStatCardParams())
        aksesContainer.addView(createAksesCepatCard("🔗", "Bagikan Kode"), createStatCardParams())
        aksesContainer.addView(createAksesCepatCard("📄", "Kelola Materi"), createStatCardParams())

        contentLayout.addView(aksesContainer, aksesParams)

        // Section: Kelas yang Anda Kelola
        val kelasHeader = createSectionHeader("Kelas yang Anda Kelola", showAction = true, onActionClick = {
            val intent = Intent(this, KelasSayaActivity::class.java).apply {
                putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_MANDIRI)
                putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PENGAJAR)
            }
            startActivity(intent)
        })
        val kelasHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(kelasHeader, kelasHeaderParams)
        contentLayout.addView(createMandiriTeacherEmptyClassCard())

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // Bottom Navigation Bar
        val bottomNav = createBottomNav(PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_MANDIRI)
        rootLayout.addView(bottomNav)

        return rootLayout
    }

    private fun createAksesCepatCard(icon: String, label: String): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(14), dp(12), dp(14))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 18f
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = roundedBackground(Color.rgb(232, 242, 255), 8)
            gravity = Gravity.CENTER
        }
        topRow.addView(iconTv, LinearLayout.LayoutParams(dp(36), dp(36)))

        val arrowTv = TextView(this).apply {
            text = "›"
            textSize = 20f
            setTextColor(Color.rgb(33, 96, 245))
            gravity = Gravity.END
        }
        topRow.addView(arrowTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        card.addView(topRow)

        val labelTv = TextView(this).apply {
            text = label
            textSize = 13f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            includeFontPadding = false
        }
        val labelParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        card.addView(labelTv, labelParams)

        return card
    }

    private fun createMandiriTeacherEmptyClassCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(28), dp(24), dp(28))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = "🏫"
            textSize = 32f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(232, 242, 255), 32)
        }
        card.addView(iconBox, LinearLayout.LayoutParams(dp(64), dp(64)))

        val titleTv = TextView(this).apply {
            text = "Belum Ada Kelas"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        val tParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        card.addView(titleTv, tParams)

        val subTv = TextView(this).apply {
            text = "Buat kelas pertama Anda untuk mulai mengundang pelajar."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        val sParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        card.addView(subTv, sParams)

        val btnBuat = TextView(this).apply {
            text = "+ Buat Kelas"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 12)
            setPadding(dp(24), dp(14), dp(24), dp(14))
            isClickable = true
            isFocusable = true
        }
        val bParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        card.addView(btnBuat, bParams)

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

    private fun createSectionHeader(title: String, showAction: Boolean = true, onActionClick: (() -> Unit)? = null): View {
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
                if (onActionClick != null) {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { onActionClick() }
                }
            }
            header.addView(actionTv)
        }

        return header
    }

    private fun createStatCard(icon: String, count: String, label: String, accentColor: Int): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(14), dp(12), dp(14))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 18f
            setPadding(dp(8), dp(8), dp(8), dp(8))
            background = roundedBackground(accentColor, 8)
            gravity = Gravity.CENTER
        }
        card.addView(iconTv, LinearLayout.LayoutParams(dp(36), dp(36)))

        val countTv = TextView(this).apply {
            text = count
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            includeFontPadding = false
        }
        val countParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(10)
        }
        card.addView(countTv, countParams)

        val labelTv = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
            includeFontPadding = false
        }
        val labelParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        card.addView(labelTv, labelParams)

        return card
    }

    private fun createStatCardParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginEnd = dp(8)
        }
    }

    private fun createMandiriEmptyClassCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(28), dp(24), dp(28))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = "📖"
            textSize = 32f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(232, 242, 255), 32)
        }
        card.addView(iconBox, LinearLayout.LayoutParams(dp(64), dp(64)))

        val titleTv = TextView(this).apply {
            text = "Kamu belum bergabung dengan kelas"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        val tParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        card.addView(titleTv, tParams)

        val subTv = TextView(this).apply {
            text = "Masukkan kode kelas dari pengajar untuk mulai belajar."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        val sParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        card.addView(subTv, sParams)

        val btnJoin = TextView(this).apply {
            text = "Gabung Kelas"
            textSize = 15f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 16)
            setPadding(dp(24), dp(14), dp(24), dp(14))
            isClickable = true
            isFocusable = true
        }
        val bParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        card.addView(btnJoin, bParams)

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

    private fun createMandiriEmptyAgendaCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = "📅"
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(232, 242, 255), 24)
        }
        card.addView(iconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val titleTv = TextView(this).apply {
            text = "Belum ada agenda"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val subTv = TextView(this).apply {
            text = "Jadwal pertemuan, tugas, atau kuis akan tampil di sini."
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val sParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(subTv, sParams)

        card.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

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

    private fun createStudentClassCard(title: String, lecturer: String, progress: Int, meeting: String, schedule: String): View {
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
            text = "📚"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(44), dp(44)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        val titleTv = TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val lecturerTv = TextView(this).apply {
            text = lecturer
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val lecParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        textCol.addView(lecturerTv, lecParams)

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val optionsTv = TextView(this).apply {
            text = "•••"
            textSize = 16f
            setTextColor(Color.rgb(117, 139, 169))
        }
        topRow.addView(optionsTv)

        card.addView(topRow)

        // Progress bar simulation
        val progressRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val progParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(14)
        }

        val progressBarBg = LinearLayout(this).apply {
            background = roundedBackground(Color.rgb(230, 238, 248), 4)
        }
        val fillWidth = (200 * (progress / 100f)).toInt()
        val progressBarFill = TextView(this).apply {
            background = roundedBackground(Color.rgb(33, 96, 245), 4)
        }
        progressBarBg.addView(progressBarFill, LinearLayout.LayoutParams(dp(fillWidth.coerceAtLeast(20)), dp(6)))

        progressRow.addView(progressBarBg, LinearLayout.LayoutParams(0, dp(6), 1f))

        val progText = TextView(this).apply {
            text = "$progress%"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(dp(12), 0, 0, 0)
        }
        progressRow.addView(progText)

        card.addView(progressRow, progParams)

        // Meeting & Schedule bottom row
        val bottomRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val botParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val meetingTv = TextView(this).apply {
            text = "📖 $meeting"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        bottomRow.addView(meetingTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val scheduleTv = TextView(this).apply {
            text = "⏰ $schedule"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        bottomRow.addView(scheduleTv)

        card.addView(bottomRow, botParams)

        val cardView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(12)
            }
            layoutParams = lp
            addView(card)
        }
        return cardView
    }

    private fun createTeacherClassCard(title: String, subtitle: String, studentCount: String, icon: String): View {
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
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
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
        }
        card.addView(optionsTv)

        val cardView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(12)
            }
            layoutParams = lp
            addView(card)
        }
        return cardView
    }

    private fun createAgendaItem(title: String, date: String, badgeColor: Int, icon: String): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = TextView(this).apply {
            text = icon
            textSize = 16f
            gravity = Gravity.CENTER
            background = roundedBackground(badgeColor, 8)
        }
        item.addView(iconBox, LinearLayout.LayoutParams(dp(36), dp(36)))

        val titleTv = TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(12), 0, 0, 0)
        }
        item.addView(titleTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val dateTv = TextView(this).apply {
            text = date
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        item.addView(dateTv)

        val itemWrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(10)
            }
            layoutParams = lp
            addView(item)
        }
        return itemWrapper
    }

    private fun createBottomNav(role: String, mode: String): View {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        if (role == PilihPeranActivity.ROLE_PENGAJAR && mode == ModePembelajaranActivity.MODE_MANDIRI) {
            nav.addView(createNavItem("🏠", "Beranda", true), createNavParams())
            nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
            nav.addView(createNavItem("📋", "Tugas", false), createNavParams())
            nav.addView(createNavItem("🤖", "AI", false), createNavParams())
            nav.addView(createNavItem("👤", "Profil", false), createNavParams())
        } else if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            nav.addView(createNavItem("🏠", "Beranda", true), createNavParams())
            nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
            nav.addView(createNavItem("📋", "Tugas", false), createNavParams())
            nav.addView(createNavItem("🤖", "AI", false), createNavParams())
            nav.addView(createNavItem("👤", "Profil", false), createNavParams())
        } else if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
            nav.addView(createNavItem("🏠", "Beranda", true), createNavParams())
            nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
            nav.addView(createNavItem("📋", "Tugas", false), createNavParams())
            nav.addView(createNavItem("🤖", "AI", false), createNavParams())
            nav.addView(createNavItem("👤", "Profil", false), createNavParams())
        } else {
            nav.addView(createNavItem("🏠", "Beranda", true), createNavParams())
            nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
            nav.addView(createNavItem("🤖", "AI", false), createNavParams())
            nav.addView(createNavItem("📋", "Aktivitas", false), createNavParams())
            nav.addView(createNavItem("👤", "Profil", false), createNavParams())
        }

        return nav
    }

    private fun createNavItem(icon: String, label: String, isSelected: Boolean): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(6), dp(4), dp(6))
            if (label == "Profil") {
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val navRole = this@HomeActivity.intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
                    val navMode = this@HomeActivity.intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
                    UiKit.openProfile(this@HomeActivity, navRole, navMode)
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
