package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
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

    // Segarkan profil dan dashboard berdasarkan data pengguna dari Firestore.
    override fun onResume() {
        super.onResume()

        val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE)
            ?: PilihPeranActivity.ROLE_PELAJAR
        val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN)
            ?: ModePembelajaranActivity.MODE_INSTITUSI

        setContentView(createDashboard(role, mode))

        ProfileStore.refresh(this, role, mode) {
            if (!isFinishing && !isDestroyed) {
                setContentView(createDashboard(role, mode))
            }
        }

        // Sinkronkan kelas Mandiri yang diikuti pelajar dari Firestore.
        if (
            role == PilihPeranActivity.ROLE_PELAJAR &&
            mode == ModePembelajaranActivity.MODE_MANDIRI
        ) {
            FirebaseManager.getJoinedTeacherClasses(
                onSuccess = { classes ->
                    runOnUiThread {
                        if (isFinishing || isDestroyed) return@runOnUiThread

                        ClassStore.syncCloudJoined(this, classes)
                        setContentView(createDashboard(role, mode))
                    }
                },
                onFailure = {
                    // Cache lokal tetap digunakan jika koneksi gagal.
                }
            )
        }
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

        // Avatar profil dari URL Cloudinary
        val avatarHandle = UiKit.avatar(this, 48, withCamera = false)
        avatarHandle.setPhotoUrl(
            ProfileStore.get(this, role, mode, ProfileStore.FOTO_PROFIL_URL)
        )
        avatarHandle.view.isClickable = true
        avatarHandle.view.isFocusable = true
        avatarHandle.view.setOnClickListener { UiKit.openProfile(this, role, mode) }
        headerLayout.addView(avatarHandle.view, LinearLayout.LayoutParams(dp(48), dp(48)))

        // Greeting text
        val greetingTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }

        val greetingTitle = TextView(this).apply {
            val nama = ProfileStore.get(this@HomeActivity, role, mode, ProfileStore.NAMA)
            text = if (nama.isNotBlank()) "Halo, $nama! 👋" else "Halo! 👋"
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
        val bellView = vectorBox(R.drawable.ic_bell, Color.rgb(25, 54, 95), 22, roundedBackground(Color.WHITE, 22, Color.rgb(220, 231, 244), 1))
        headerLayout.addView(bellView, LinearLayout.LayoutParams(dp(44), dp(44)))

        contentLayout.addView(headerLayout)

        // 2. Search Bar
        val searchBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
        }
        val searchIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_search)
            setColorFilter(Color.rgb(117, 139, 169))
            layoutParams = LinearLayout.LayoutParams(dp(20), dp(20))
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
            val filterIcon = vectorBox(R.drawable.ic_filter, Color.rgb(33, 96, 245), 22, roundedBackground(Color.TRANSPARENT, 0))
            searchBar.addView(filterIcon, LinearLayout.LayoutParams(dp(36), dp(36)))
        }

        val searchParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(20)
        }
        contentLayout.addView(searchBar, searchParams)
        if (mode == ModePembelajaranActivity.MODE_MANDIRI && role == PilihPeranActivity.ROLE_PELAJAR) {
            searchBar.isClickable = true
            searchBar.isFocusable = true
            searchBar.setOnClickListener {
                UiKit.open(this, PencarianPelajarActivity::class.java, PilihPeranActivity.ROLE_PELAJAR, ModePembelajaranActivity.MODE_MANDIRI)
            }
        }

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
            statsContainer.addView(createStatCard("🏫", "${ClassStore.joined(this).size}", "Kelas Aktif", Color.rgb(33, 96, 245)), createStatCardParams())
            statsContainer.addView(createStatCard("📄", "${TaskStore.pendingCount(this)}", "Tugas", Color.rgb(255, 152, 0)), createStatCardParams())
            statsContainer.addView(createStatCard("📋", "${QuizStore.pendingCount(this)}", "Kuis", Color.rgb(103, 58, 183)), createStatCardParams())
        } else {
            // Student Institusi Stats: 3 cards
            statsContainer.addView(createStatCard("🏫", "3", "Kelas Aktif", Color.rgb(33, 96, 245)), createStatCardParams())
            statsContainer.addView(createStatCard("📄", "2", "Tugas Pending", Color.rgb(255, 152, 0)), createStatCardParams())
            statsContainer.addView(createStatCard("📋", "1", "Kuis Mendatang", Color.rgb(103, 58, 183)), createStatCardParams())
        }

        // 4. Section: Kelas Saya
        val kelasHeader = createSectionHeader("Kelas Saya", showAction = true, onActionClick = {
            if (role == PilihPeranActivity.ROLE_PENGAJAR) {
                val intent = Intent(this, KelasSayaActivity::class.java).apply {
                    putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, mode)
                    putExtra(PilihPeranActivity.EXTRA_ROLE, role)
                }
                startActivity(intent)
            } else if (role == PilihPeranActivity.ROLE_PELAJAR && mode == ModePembelajaranActivity.MODE_INSTITUSI) {
                val intent = Intent(this, KelasPelajarInstitusiActivity::class.java).apply {
                    putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, mode)
                    putExtra(PilihPeranActivity.EXTRA_ROLE, role)
                }
                startActivity(intent)
            } else if (role == PilihPeranActivity.ROLE_PELAJAR && mode == ModePembelajaranActivity.MODE_MANDIRI) {
                UiKit.open(this, DaftarKelasActivity::class.java, role, mode)
            }
        })
        val kelasHeaderParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        contentLayout.addView(kelasHeader, kelasHeaderParams)

        if (role == PilihPeranActivity.ROLE_PELAJAR &&
            mode == ModePembelajaranActivity.MODE_MANDIRI
        ) {
            contentLayout.addView(
                UiKit.primaryButton(this, "Gabung Kelas") {
                    UiKit.open(
                        this,
                        GabungKelasActivity::class.java,
                        PilihPeranActivity.ROLE_PELAJAR,
                        ModePembelajaranActivity.MODE_MANDIRI
                    )
                },
                UiKit.params(this, LinearLayout.LayoutParams.MATCH_PARENT, dp(52), top = 12)
            )
        }

        if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            contentLayout.addView(createTeacherClassCard("Pemrograman Mobile", "Universitas • Kelas A", "28 pelajar", "💻"))
            contentLayout.addView(createTeacherClassCard("Sistem Cerdas", "Universitas • Kelas B", "32 pelajar", "🤖"))
            contentLayout.addView(createTeacherClassCard("Basis Data", "Universitas • Kelas C", "26 pelajar", "🗄️"))
        } else if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
            val joinedClasses = ClassStore.joined(this)
            if (joinedClasses.isEmpty()) {
                contentLayout.addView(createMandiriEmptyClassCard())
            } else {
                for (joinedClass in joinedClasses) {
                    val joinedCard = ClassUi.homeCard(this, joinedClass) {
                        val detail = UiKit.withProfile(Intent(this, DetailKelasPelajarActivity::class.java), role, mode)
                        detail.putExtra(DetailKelasPelajarActivity.EXTRA_CODE, joinedClass.code)
                        startActivity(detail)
                    }
                    contentLayout.addView(joinedCard, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        topMargin = dp(8)
                    })
                }

            }
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
                val agenda = AgendaStore.upcoming(this, 4)
                if (agenda.isEmpty()) {
                    contentLayout.addView(createMandiriEmptyAgendaCard())
                } else {
                    for (a in agenda) {
                        val color = when (a.kind) {
                            AgendaStore.KIND_PRESENSI -> Color.rgb(76, 175, 80)
                            AgendaStore.KIND_TUGAS -> Color.rgb(244, 67, 54)
                            else -> Color.rgb(33, 96, 245)
                        }
                        val icon = when (a.kind) {
                            AgendaStore.KIND_PRESENSI -> "✅"
                            AgendaStore.KIND_TUGAS -> "📄"
                            else -> "📋"
                        }
                        contentLayout.addView(createAgendaItem(a.title, a.dateText, color, icon) { openAgenda(a) })
                    }
                }
            } else {
                contentLayout.addView(createAgendaItem("Tugas: Tugas Mandiri", "12 Jun 2025", Color.rgb(244, 67, 54), "📄"))
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
        val role = PilihPeranActivity.ROLE_PENGAJAR
        val mode = ModePembelajaranActivity.MODE_MANDIRI
        val bgColor = Color.rgb(245, 250, 255)
        val myClasses = TeacherClassStore.all(this)
        val hasClasses = myClasses.isNotEmpty()

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

        // ---------------- Header: foto, sapaan, nama, lonceng ----------------
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val avatarHandle = UiKit.avatar(this, 48, withCamera = false)
        avatarHandle.setPhotoUrl(ProfileStore.get(this, role, mode, ProfileStore.FOTO_PROFIL_URL))
        avatarHandle.view.isClickable = true
        avatarHandle.view.isFocusable = true
        avatarHandle.view.setOnClickListener { UiKit.openProfile(this, role, mode) }
        headerLayout.addView(avatarHandle.view, LinearLayout.LayoutParams(dp(48), dp(48)))

        val greetingTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        greetingTextLayout.addView(
            TextView(this).apply {
                text = "${greetingByTime()} 👋"
                textSize = 13f
                setTextColor(Color.rgb(117, 139, 169))
                includeFontPadding = false
            }
        )
        val nama = ProfileStore.get(this, role, mode, ProfileStore.NAMA)
        greetingTextLayout.addView(
            TextView(this).apply {
                text = if (nama.isNotBlank()) nama else "Pengajar"
                textSize = 20f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                includeFontPadding = false
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(3)
            }
        )
        headerLayout.addView(greetingTextLayout, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val bellView = vectorBox(R.drawable.ic_bell, Color.rgb(25, 54, 95), 22, roundedBackground(Color.WHITE, 22, Color.rgb(220, 231, 244), 1))
        bellView.contentDescription = "Notifikasi"
        bellView.isClickable = true
        bellView.isFocusable = true
        bellView.elevation = dp(2).toFloat()
        bellView.setOnClickListener {
            startActivity(Intent(this, NotifikasiActivity::class.java))
        }
        applyRipple(bellView, 22, Color.argb(40, 29, 94, 255))
        headerLayout.addView(bellView, LinearLayout.LayoutParams(dp(44), dp(44)))

        contentLayout.addView(headerLayout)

        // ---------------- Banner Kelas Mandiri (biru tua yang tenang) ----------------
        val banner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(24, 48, 92), Color.rgb(38, 78, 150))
            ).apply {
                cornerRadius = dp(20).toFloat()
                setStroke(dp(1), Color.argb(40, 255, 255, 255))
            }
            elevation = dp(3).toFloat()
        }

        banner.addView(
            ImageView(this).apply { setImageResource(R.drawable.ill_school_building) },
            LinearLayout.LayoutParams(dp(84), dp(84))
        )

        val bannerText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }
        bannerText.addView(
            TextView(this).apply {
                text = "Kelas Mandiri"
                textSize = 18f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
            }
        )
        bannerText.addView(
            TextView(this).apply {
                text = "Kelola pembelajaran, bagikan kode, dan pantau aktivitas belajar pelajar dengan mudah."
                textSize = 13f
                setTextColor(Color.argb(215, 255, 255, 255))
                setLineSpacing(dp(2).toFloat(), 1f)
            },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(4)
            }
        )
        if (hasClasses) {
            val totalPeserta = myClasses.sumOf { TeacherClassStore.participantCount(this, it.code) }
            bannerText.addView(
                TextView(this).apply {
                    text = "${myClasses.size} Kelas  •  $totalPeserta Peserta"
                    textSize = 12f
                    setTextColor(Color.WHITE)
                    setTypeface(null, Typeface.BOLD)
                    setPadding(dp(10), dp(4), dp(10), dp(4))
                    background = roundedBackground(Color.argb(45, 255, 255, 255), 12)
                },
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = dp(10)
                }
            )
        }
        banner.addView(bannerText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        contentLayout.addView(
            banner,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(20)
            }
        )

        // ---------------- Akses Cepat ----------------
        contentLayout.addView(
            createSectionHeader("Akses Cepat", showAction = false),
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(24)
            }
        )

        val aksesContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        fun aksesItemParams(last: Boolean) =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                if (!last) marginEnd = dp(10)
            }

        aksesContainer.addView(
            createAksesCepatCard(
                R.drawable.ic_add_circle_outline,
                Color.rgb(33, 96, 245), Color.rgb(96, 145, 255), Color.rgb(234, 242, 255),
                "Buat Kelas", true
            ) { openBuatKelas() },
            aksesItemParams(false)
        )
        aksesContainer.addView(
            createAksesCepatCard(
                R.drawable.ic_share,
                Color.rgb(124, 77, 230), Color.rgb(170, 132, 255), Color.rgb(243, 237, 255),
                "Bagikan Kode", hasClasses
            ) { bagikanKode() },
            aksesItemParams(false)
        )
        aksesContainer.addView(
            createAksesCepatCard(
                R.drawable.ic_description,
                Color.rgb(22, 163, 74), Color.rgb(78, 205, 128), Color.rgb(232, 248, 238),
                "Kelola Materi", hasClasses
            ) { kelolaMateri() },
            aksesItemParams(true)
        )

        contentLayout.addView(
            aksesContainer,
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(12)
            }
        )

        // ---------------- Kelas yang Anda Kelola ----------------
        contentLayout.addView(
            createTeacherSectionHeader("Kelas yang Anda Kelola", hasClasses) { openKelasMandiri() },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(24)
            }
        )
        if (!hasClasses) {
            contentLayout.addView(createMandiriTeacherEmptyClassCard())
        } else {
            myClasses.take(3).forEach { contentLayout.addView(createTeacherClassCard(it)) }
        }

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // Bottom Navigation Bar
        rootLayout.addView(createBottomNav(role, mode))

        return rootLayout
    }

    /**
     * Kartu Akses Cepat: latar bergradasi tipis, ikon bergradasi dengan bayangan,
     * dan panah di lingkaran kecil. Diredupkan bila fitur belum bisa dipakai (belum ada kelas).
     */
    private fun createAksesCepatCard(
        iconRes: Int,
        accent: Int,
        accentLight: Int,
        pastel: Int,
        label: String,
        enabled: Boolean,
        onClick: () -> Unit
    ): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(14), dp(12), dp(14))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.WHITE, pastel)
            ).apply {
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), Color.rgb(220, 231, 244))
            }
            elevation = dp(3).toFloat()
            alpha = if (enabled) 1f else 0.6f
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }
        applyRipple(card, 16, Color.argb(40, Color.red(accent), Color.green(accent), Color.blue(accent)))

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox = vectorBox(
            iconRes, Color.WHITE, 20,
            GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(accentLight, accent)).apply {
                cornerRadius = dp(12).toFloat()
            }
        )
        iconBox.elevation = dp(2).toFloat()
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(38), dp(38)))

        topRow.addView(View(this), LinearLayout.LayoutParams(0, 1, 1f))

        val chevron = vectorBox(R.drawable.ic_chevron_right, accent, 16, roundedBackground(Color.WHITE, 12))
        topRow.addView(chevron, LinearLayout.LayoutParams(dp(24), dp(24)))
        card.addView(topRow)

        card.addView(
            TextView(this).apply {
                text = label
                textSize = 13f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                includeFontPadding = false
                maxLines = 1
            },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(14)
            }
        )

        return card
    }

    /** Judul bagian dengan tombol "Lihat Semua" berbentuk chip (hanya bila ada kelas). */
    private fun createTeacherSectionHeader(title: String, showAction: Boolean, onActionClick: () -> Unit): View {
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(
            TextView(this).apply {
                text = title
                textSize = 18f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
            },
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        if (showAction) {
            val chip = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                minimumHeight = dp(36)
                setPadding(dp(14), dp(6), dp(8), dp(6))
                background = roundedBackground(Color.rgb(232, 242, 255), 18)
                isClickable = true
                isFocusable = true
                contentDescription = "Lihat semua kelas"
                setOnClickListener { onActionClick() }
            }
            applyRipple(chip, 18, Color.argb(40, 29, 94, 255))
            chip.addView(
                TextView(this).apply {
                    text = "Lihat Semua"
                    textSize = 13f
                    setTextColor(Color.rgb(29, 94, 255))
                    setTypeface(null, Typeface.BOLD)
                }
            )
            chip.addView(
                ImageView(this).apply {
                    setImageResource(R.drawable.ic_chevron_right)
                    setColorFilter(Color.rgb(29, 94, 255))
                },
                LinearLayout.LayoutParams(dp(18), dp(18))
            )
            header.addView(chip)
        }
        return header
    }

    /** Efek riak sentuhan dengan sudut membulat (khusus dasbor pengajar mandiri). */
    private fun applyRipple(view: View, radiusDp: Int, rippleColor: Int) {
        val mask = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = dp(radiusDp).toFloat()
        }
        view.foreground = android.graphics.drawable.RippleDrawable(
            android.content.res.ColorStateList.valueOf(rippleColor),
            null,
            mask
        )
    }

    /** Sapaan sesuai jam perangkat. */
    private fun greetingByTime(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 4..10 -> "Selamat pagi"
            in 11..14 -> "Selamat siang"
            in 15..17 -> "Selamat sore"
            else -> "Selamat malam"
        }
    }

    /** Tombol biru dengan ikon plus-lingkaran di kiri teks (khusus dasbor pengajar mandiri). */
    private fun createTeacherPrimaryButton(label: String, onClick: () -> Unit): View {
        val button = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }
        applyRipple(button, 12, Color.argb(70, 255, 255, 255))
        button.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_add_circle_outline)
                setColorFilter(Color.WHITE)
            },
            LinearLayout.LayoutParams(dp(20), dp(20))
        )
        button.addView(
            TextView(this).apply {
                text = label
                textSize = 15f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                setPadding(dp(8), 0, 0, 0)
            }
        )
        return button
    }

    private fun openBuatKelas() {
        UiKit.open(this, BuatKelasActivity::class.java, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_MANDIRI)
    }

    private fun openKelasMandiri() {
        UiKit.open(this, KelasMandiriPengajarActivity::class.java, PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_MANDIRI)
    }

    private fun openKelasDetail(c: TeacherClass) {
        val next = UiKit.withProfile(
            Intent(this, KelolaKelasActivity::class.java),
            PilihPeranActivity.ROLE_PENGAJAR, ModePembelajaranActivity.MODE_MANDIRI
        )
        next.putExtra(KelolaKelasActivity.EXTRA_CODE, c.code)
        startActivity(next)
    }

    private fun bagikanKode() {
        val list = TeacherClassStore.all(this)
        if (list.isEmpty()) {
            UiKit.toast(this, "Buat kelas terlebih dahulu")
        } else if (list.size == 1) {
            TeacherClassStore.share(this, list[0])
        } else {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Bagikan kode kelas")
                .setItems(list.map { "${it.name} (${it.code})" }.toTypedArray()) { _, i ->
                    TeacherClassStore.share(this, list[i])
                }
                .show()
        }
    }

    private fun kelolaMateri() {
        val list = TeacherClassStore.all(this)
        if (list.isEmpty()) {
            UiKit.toast(this, "Buat kelas terlebih dahulu")
        } else if (list.size == 1) {
            openKelasDetail(list[0])
        } else {
            openKelasMandiri()
        }
    }

    private fun createTeacherClassCard(c: TeacherClass): View {
        val card = UiKit.card(this).apply {
            setPadding(dp(16), dp(14), dp(16), dp(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { openKelasDetail(c) }
        }
        applyRipple(card, 16, Color.argb(30, 29, 94, 255))

        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, TeacherClassStore.iconFor(c.topic), Color.WHITE, UiKit.PRIMARY, 48, 12, 24))

        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, c.name, 15f, UiKit.NAVY, true))

        val info = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // Chip kode kelas: ketuk untuk menyalin
        val chip = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(4), dp(8), dp(4))
            background = roundedBackground(Color.rgb(232, 242, 255), 8)
            isClickable = true
            isFocusable = true
            contentDescription = "Salin kode kelas"
            setOnClickListener { TeacherClassStore.copyCode(this@HomeActivity, c) }
        }
        applyRipple(chip, 8, Color.argb(40, 29, 94, 255))
        chip.addView(UiKit.tv(this, "Kode: ${c.code}", 12f, UiKit.PRIMARY, true).apply { maxLines = 1 })
        chip.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_copy)
                setColorFilter(UiKit.PRIMARY)
            },
            UiKit.params(this, dp(14), dp(14), start = 6)
        )
        info.addView(chip)

        // Jumlah peserta
        info.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_people)
                setColorFilter(UiKit.MUTED)
            },
            UiKit.params(this, dp(16), dp(16), start = 12)
        )
        info.addView(
            UiKit.tv(
                this,
                TeacherClassStore.participantCount(this, c.code).let { n ->
                    if (TeacherClassStore.isUnlimited(c.capacity)) "$n peserta" else "$n/${c.capacity} peserta"
                },
                12f,
                UiKit.MUTED
            ).apply { maxLines = 1 },
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 4)
        )

        col.addView(info, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8))

        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        head.addView(UiKit.icon(this, R.drawable.ic_chevron_right, 22, UiKit.MUTED), LinearLayout.LayoutParams(dp(22), dp(22)))
        card.addView(head)
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(12) }
        return card
    }

    private fun createMandiriTeacherEmptyClassCard(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = roundedBackground(Color.WHITE, 20, Color.rgb(220, 231, 244), 1)
            elevation = dp(2).toFloat()
        }

        card.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ill_teacher_empty)
                adjustViewBounds = true
            },
            LinearLayout.LayoutParams(dp(220), dp(140))
        )

        card.addView(
            TextView(this).apply {
                text = "Belum Ada Kelas"
                textSize = 18f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(12)
            }
        )

        card.addView(
            TextView(this).apply {
                text = "Mulai buat kelas pertama Anda untuk mengundang pelajar dan membagikan materi pembelajaran."
                textSize = 13f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.CENTER
                setLineSpacing(dp(2).toFloat(), 1f)
            },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(6)
            }
        )

        card.addView(
            createTeacherPrimaryButton("Buat Kelas Baru") { openBuatKelas() },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(18)
            }
        )

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12) }
            addView(card)
        }
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

        val iconTv = vectorBox(emojiRes(icon), Color.WHITE, 20, roundedBackground(accentColor, 8))
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

        val iconBox = vectorBox(R.drawable.ic_book, Color.rgb(33, 96, 245), 30, roundedBackground(Color.rgb(232, 242, 255), 32))
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
            setOnClickListener {
                UiKit.open(this@HomeActivity, GabungKelasActivity::class.java, PilihPeranActivity.ROLE_PELAJAR, ModePembelajaranActivity.MODE_MANDIRI)
            }
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

        val iconBox = vectorBox(R.drawable.ic_calendar, Color.rgb(33, 96, 245), 24, roundedBackground(Color.rgb(232, 242, 255), 24))
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

        val iconBox = vectorBox(R.drawable.ic_book, Color.WHITE, 24, roundedBackground(Color.rgb(33, 96, 245), 12))
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
            text = meeting
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

        val iconBox = vectorBox(emojiRes(icon), Color.WHITE, 26, roundedBackground(Color.rgb(33, 96, 245), 12))
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

    private fun openAgenda(a: AgendaItem) {
        val role = PilihPeranActivity.ROLE_PELAJAR
        val mode = ModePembelajaranActivity.MODE_MANDIRI
        val next = when (a.kind) {
            AgendaStore.KIND_PRESENSI -> UiKit.withProfile(Intent(this, PresensiPelajarActivity::class.java), role, mode)
                .putExtra(PresensiPelajarActivity.EXTRA_CODE, a.classCode)
                .putExtra(PresensiPelajarActivity.EXTRA_NUMBER, a.number)
            AgendaStore.KIND_TUGAS -> UiKit.withProfile(Intent(this, DetailTugasPelajarActivity::class.java), role, mode)
                .putExtra(DetailTugasPelajarActivity.EXTRA_TASK_ID, a.refId)
            else -> UiKit.withProfile(Intent(this, DetailKuisMandiriActivity::class.java), role, mode)
                .putExtra(DetailKuisMandiriActivity.EXTRA_QUIZ_ID, a.refId)
        }
        startActivity(next)
    }

    private fun createAgendaItem(title: String, date: String, badgeColor: Int, icon: String, onClick: (() -> Unit)? = null): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val iconBox = vectorBox(emojiRes(icon), Color.WHITE, 18, roundedBackground(badgeColor, 8))
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
        if (onClick != null) {
            itemWrapper.isClickable = true
            itemWrapper.isFocusable = true
            itemWrapper.setOnClickListener { onClick() }
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
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val navRole = this@HomeActivity.intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
                val navMode = this@HomeActivity.intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
                when (label) {
                    "Beranda" -> {
                        val intent = Intent(this@HomeActivity, HomeActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, navRole)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, navMode)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Kelas" -> {
                        if (navRole == PilihPeranActivity.ROLE_PELAJAR && navMode == ModePembelajaranActivity.MODE_MANDIRI) {
                            UiKit.openTab(this@HomeActivity, DaftarKelasActivity::class.java, navRole, navMode)
                            return@setOnClickListener
                        }
                        if (navRole == PilihPeranActivity.ROLE_PENGAJAR && navMode == ModePembelajaranActivity.MODE_MANDIRI) {
                            openKelasMandiri()
                            return@setOnClickListener
                        }
                        val intent = Intent(this@HomeActivity, KelasSayaActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, navRole)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, navMode)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Tugas", "Aktivitas" -> {
                        if (navRole == PilihPeranActivity.ROLE_PELAJAR && navMode == ModePembelajaranActivity.MODE_MANDIRI) {
                            UiKit.openTab(this@HomeActivity, TugasPelajarActivity::class.java, navRole, navMode)
                            return@setOnClickListener
                        }
                        if (navRole == PilihPeranActivity.ROLE_PENGAJAR && navMode == ModePembelajaranActivity.MODE_MANDIRI) {
                            UiKit.openTab(this@HomeActivity, TugasMandiriPengajarActivity::class.java, navRole, navMode)
                            return@setOnClickListener
                        }
                        val intent = Intent(this@HomeActivity, TugasActivity::class.java).apply {
                            putExtra(PilihPeranActivity.EXTRA_ROLE, navRole)
                            putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, navMode)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "AI" -> {
                        if (navMode == ModePembelajaranActivity.MODE_MANDIRI) {
                            UiKit.openTab(this@HomeActivity, AiAsistenMandiriActivity::class.java, navRole, navMode)
                            return@setOnClickListener
                        }
                        val intent = Intent(this@HomeActivity, AiAssistantActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                    }
                    "Profil" -> {
                        UiKit.openProfile(this@HomeActivity, navRole, navMode)
                    }
                }
            }
        }

        item.addView(UiKit.navIcon(this, icon, isSelected))

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

    /** Kotak berlatar berisi satu ikon vektor di tengah (menggantikan emoji). */
    private fun vectorBox(res: Int, tint: Int, glyphDp: Int, boxBackground: GradientDrawable): FrameLayout {
        return FrameLayout(this).apply {
            background = boxBackground
            addView(
                ImageView(this@HomeActivity).apply {
                    setImageResource(res)
                    setColorFilter(tint)
                },
                FrameLayout.LayoutParams(dp(glyphDp), dp(glyphDp), Gravity.CENTER)
            )
        }
    }

    /** Memetakan emoji lama ke ikon vektor yang setara. */
    private fun emojiRes(emoji: String): Int = when (emoji) {
        "🏫" -> R.drawable.ic_book
        "📄" -> R.drawable.ic_description
        "📋" -> R.drawable.ic_assignment_check
        "👥" -> R.drawable.ic_people
        "➕" -> R.drawable.ic_add
        "🔗" -> R.drawable.ic_share
        "✅" -> R.drawable.ic_check_circle
        "💻" -> R.drawable.ic_code
        "🤖" -> R.drawable.ic_bot
        "📖", "📚" -> R.drawable.ic_book
        "📅" -> R.drawable.ic_calendar
        else -> if (emoji.startsWith("\uD83D\uDDC4")) R.drawable.ic_database else R.drawable.ic_book
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
