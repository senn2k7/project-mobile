package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Pengajar mengelola materi dan tugas untuk satu kelas mandiri. */
class KelolaKelasActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var code = ""
    private var tab = TAB_MATERI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        code = intent.getStringExtra(EXTRA_CODE) ?: ""
        tab = savedInstanceState?.getString(STATE_TAB) ?: intent.getStringExtra(EXTRA_TAB) ?: TAB_MATERI
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_TAB, tab)
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val c = TeacherClassStore.byCode(this, code)
        if (c == null) {
            finish()
            return
        }
        setContentView(build(c))
    }

    private fun pill(label: String, onClick: () -> Unit): TextView =
        UiKit.tv(this, label, 13f, UiKit.PRIMARY, true).apply {
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@KelolaKelasActivity, UiKit.SOFT, 10)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

    /** Bar atas: kembali, judul "Detail Kelas", dan menu titik tiga. */
    private fun topBarWithMenu(c: TeacherClass): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(20), px(16), px(12), px(8))
        }
        bar.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_back)
                contentDescription = "Kembali"
                isClickable = true
                isFocusable = true
                setOnClickListener { finish() }
            },
            LinearLayout.LayoutParams(px(32), px(32))
        )
        bar.addView(UiKit.tv(this, "Detail Kelas", 20f, UiKit.NAVY, true), UiKit.params(this, 0, UiKit.WRAP, start = 16, weight = 1f))
        bar.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_more_vert)
                setColorFilter(UiKit.NAVY)
                setPadding(px(8), px(8), px(8), px(8))
                contentDescription = "Menu kelas"
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    ClassActionSheet.show(
                        this@KelolaKelasActivity,
                        "Aksi Kelas ${c.name}",
                        listOf(
                            ClassActionSheet.Row(R.drawable.ic_info, "Info Kelas") {
                                val next = UiKit.withProfile(Intent(this@KelolaKelasActivity, KelasBerhasilActivity::class.java), role, mode)
                                next.putExtra(KelasBerhasilActivity.EXTRA_CODE, c.code)
                                startActivity(next)
                            },
                            ClassActionSheet.Row(R.drawable.ic_copy, "Salin Kode", c.code) { TeacherClassStore.copyCode(this@KelolaKelasActivity, c) },
                            ClassActionSheet.Row(R.drawable.ic_share, "Bagikan Kode") { TeacherClassStore.share(this@KelolaKelasActivity, c) }
                        )
                    )
                }
            },
            LinearLayout.LayoutParams(px(40), px(40))
        )
        return bar
    }

    /** Kartu kapasitas terisi (bar) bila kapasitas dibatasi. */
    private fun capacityBlock(c: TeacherClass): View {
        val n = TeacherClassStore.participantCount(this, c.code)
        val unlimited = TeacherClassStore.isUnlimited(c.capacity)
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(UiKit.tv(this, "Kapasitas terisi", 12f, UiKit.MUTED), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        row.addView(
            UiKit.tv(this, if (unlimited) "$n peserta • Tanpa batas" else "$n/${c.capacity}", 12f, UiKit.NAVY, true)
        )
        box.addView(row)
        if (!unlimited && c.capacity > 0) {
            val track = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = c.capacity.toFloat()
                background = UiKit.rounded(this@KelolaKelasActivity, Color.rgb(226, 236, 252), 4)
            }
            val filled = n.coerceAtMost(c.capacity)
            if (filled > 0) {
                track.addView(
                    View(this).apply { background = UiKit.rounded(this@KelolaKelasActivity, UiKit.PRIMARY, 4) },
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, filled.toFloat())
                )
            }
            box.addView(track, UiKit.params(this, UiKit.MATCH, px(8), top = 8))
        }
        return box
    }

    private fun squareButton(iconRes: Int, desc: String, onClick: () -> Unit): View =
        FrameLayout(this).apply {
            background = UiKit.rounded(this@KelolaKelasActivity, UiKit.SOFT, 12)
            contentDescription = desc
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            addView(
                ImageView(this@KelolaKelasActivity).apply {
                    setImageResource(iconRes)
                    setColorFilter(UiKit.PRIMARY)
                },
                FrameLayout.LayoutParams(px(20), px(20), Gravity.CENTER)
            )
        }

    private fun build(c: TeacherClass): View {
        val materials = TeacherClassStore.materials(this, c.code)
        val tasks = TeacherClassStore.tasks(this, c.code)
        val quizzes = QuizStore.quizzes(this, c.code)

        val root = UiKit.root(this)
        root.addView(topBarWithMenu(c))
        val (scroll, content) = UiKit.scroll(this)

        val white45 = Color.argb(45, 255, 255, 255)

        // ---------------- Banner kelas (senada dengan banner beranda) ----------------
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(18), px(18), px(18), px(18))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(24, 48, 92), Color.rgb(38, 78, 150))
            ).apply {
                cornerRadius = px(20).toFloat()
                setStroke(px(1), Color.argb(40, 255, 255, 255))
            }
            elevation = px(3).toFloat()
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(UiKit.badge(this, TeacherClassStore.iconFor(c.topic), Color.WHITE, white45, 56, 16, 28))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, c.name, 20f, Color.WHITE, true))
        col.addView(
            UiKit.tv(this, "Kelas Mandiri", 11.5f, Color.WHITE, true).apply {
                background = UiKit.rounded(this@KelolaKelasActivity, white45, 8)
                setPadding(px(10), px(3), px(10), px(3))
            },
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        top.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        header.addView(top)

        val ownerName = ProfileStore.get(this, role, mode, ProfileStore.NAMA).ifBlank { "Pengajar" }
        val owner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        owner.addView(
            TextView(this).apply {
                text = ownerName.take(1).uppercase()
                gravity = Gravity.CENTER
                textSize = 14f
                setTextColor(Color.WHITE)
                setTypeface(null, android.graphics.Typeface.BOLD)
                background = UiKit.oval(white45)
            },
            LinearLayout.LayoutParams(px(36), px(36))
        )
        val ownerCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        ownerCol.addView(UiKit.tv(this, ownerName, 13f, Color.WHITE, true))
        ownerCol.addView(UiKit.tv(this, "Pengajar kelas", 11.5f, Color.argb(200, 255, 255, 255)))
        owner.addView(ownerCol, UiKit.params(this, 0, UiKit.WRAP, start = 10, weight = 1f))
        header.addView(owner, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))
        content.addView(header)

        // ---------------- Ringkasan angka ----------------
        val statsCard = UiKit.card(this).apply { setPadding(px(12), px(14), px(12), px(14)) }
        statsCard.addView(
            ClassCards.statsRow(this, TeacherClassStore.participantCount(this, c.code), materials.size, tasks.size, quizzes.size)
        )
        content.addView(statsCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))

        // ---------------- Tentang kelas ----------------
        val about = UiKit.card(this).apply { setPadding(px(16), px(14), px(16), px(16)) }
        about.addView(UiKit.tv(this, "Tentang Kelas", 14f, UiKit.NAVY, true))
        about.addView(
            UiKit.tv(
                this,
                c.description.ifBlank { "Belum ada deskripsi kelas." },
                13f, UiKit.MUTED
            ).apply { setLineSpacing(px(2).toFloat(), 1f) },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6)
        )
        val metaChips = listOf(c.topic, c.category, c.level).filter { it.isNotBlank() }
        if (metaChips.isNotEmpty()) {
            val chipRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            metaChips.forEach { chipRow.addView(ClassUi.chip(this, it), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, end = 6)) }
            val chipScroll = HorizontalScrollView(this).apply {
                isHorizontalScrollBarEnabled = false
                addView(chipRow)
            }
            about.addView(chipScroll, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        }
        content.addView(about, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))

        // ---------------- Kode kelas + kapasitas ----------------
        val codeCard = UiKit.card(this).apply { setPadding(px(16), px(14), px(16), px(16)) }
        codeCard.addView(UiKit.tv(this, "Kode Kelas", 12f, UiKit.MUTED))
        val codeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        codeRow.addView(
            UiKit.tv(this, c.code, 24f, UiKit.NAVY, true).apply { letterSpacing = 0.1f },
            UiKit.params(this, 0, UiKit.WRAP, weight = 1f)
        )
        codeRow.addView(
            squareButton(R.drawable.ic_copy, "Salin kode") { TeacherClassStore.copyCode(this, c) },
            UiKit.params(this, px(42), px(42))
        )
        codeRow.addView(
            squareButton(R.drawable.ic_share, "Bagikan kode") { TeacherClassStore.share(this, c) },
            UiKit.params(this, px(42), px(42), start = 8)
        )
        codeCard.addView(codeRow, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6))
        codeCard.addView(capacityBlock(c), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
        content.addView(codeCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))

        // ---------------- Tab segmen (lebar sama, semua terlihat) ----------------
        val tabs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = UiKit.rounded(this@KelolaKelasActivity, Color.WHITE, 16, UiKit.BORDER)
            setPadding(px(4), px(4), px(4), px(4))
        }
        for (label in listOf(TAB_MATERI, TAB_TUGAS, TAB_KUIS, TAB_DISKUSI)) {
            val on = label == tab
            val count = when (label) {
                TAB_MATERI -> materials.size
                TAB_TUGAS -> tasks.size
                TAB_KUIS -> quizzes.size
                else -> -1
            }
            val text = if (count >= 0) "$label $count" else label
            val t = UiKit.tv(this, text, 13f, if (on) Color.WHITE else UiKit.MUTED, on).apply {
                gravity = Gravity.CENTER
                maxLines = 1
                if (on) background = UiKit.rounded(this@KelolaKelasActivity, UiKit.PRIMARY, 12)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    if (label == TAB_DISKUSI) {
                        val next = UiKit.withProfile(Intent(this@KelolaKelasActivity, DiskusiKelasActivity::class.java), role, mode)
                        next.putExtra(DiskusiKelasActivity.EXTRA_CODE, c.code)
                        startActivity(next)
                    } else if (tab != label) {
                        tab = label
                        render()
                    }
                }
            }
            tabs.addView(t, UiKit.params(this, 0, px(40), weight = 1f))
        }
        content.addView(tabs, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Judul daftar + jumlah
        val listHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        listHeader.addView(
            UiKit.tv(
                this,
                when (tab) {
                    TAB_MATERI -> "Daftar Pertemuan"
                    TAB_TUGAS -> "Daftar Tugas"
                    else -> "Daftar Kuis"
                },
                16f, UiKit.NAVY, true
            ),
            UiKit.params(this, 0, UiKit.WRAP, weight = 1f)
        )
        listHeader.addView(
            UiKit.tv(
                this,
                when (tab) {
                    TAB_MATERI -> "${materials.size} pertemuan"
                    TAB_TUGAS -> "${tasks.size} tugas"
                    else -> "${quizzes.size} kuis"
                },
                12f, UiKit.MUTED
            )
        )
        content.addView(listHeader, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 20))

        // Daftar
        if (tab == TAB_MATERI) {
            if (materials.isEmpty()) {
                content.addView(
                    emptyCard("Belum ada materi", "Tambahkan materi pertama agar pelajar bisa mulai belajar."),
                    UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12)
                )
            }
            materials.forEachIndexed { i, m ->
                content.addView(materialCard(i + 1, m), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            }
        } else if (tab == TAB_KUIS) {
            if (quizzes.isEmpty()) {
                content.addView(
                    emptyCard("Belum ada kuis", "Buat kuis pilihan ganda agar pelajar bisa menguji pemahaman."),
                    UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12)
                )
            }
            for (z in quizzes) {
                content.addView(quizCard(z), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            }
        } else {
            if (tasks.isEmpty()) {
                content.addView(
                    emptyCard("Belum ada tugas", "Buat tugas dengan tenggat agar pelajar bisa mengumpulkan jawaban."),
                    UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12)
                )
            }
            for (t in tasks) {
                content.addView(taskCard(t), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            }
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(
            UiKit.bottomBar(
                this,
                UiKit.primaryButton(
                    this,
                    when (tab) {
                        TAB_MATERI -> "+ Tambah Materi"
                        TAB_TUGAS -> "+ Tambah Tugas"
                        else -> "+ Buat Kuis"
                    }
                ) {
                    if (tab == TAB_KUIS) {
                        val next = UiKit.withProfile(Intent(this, BuatKuisMandiriActivity::class.java), role, mode)
                        next.putExtra(BuatKuisMandiriActivity.EXTRA_CODE, c.code)
                        startActivity(next)
                    } else {
                        val next = UiKit.withProfile(Intent(this, TambahKontenActivity::class.java), role, mode)
                        next.putExtra(TambahKontenActivity.EXTRA_CODE, c.code)
                        next.putExtra(TambahKontenActivity.EXTRA_TYPE, if (tab == TAB_MATERI) TambahKontenActivity.TYPE_MATERI else TambahKontenActivity.TYPE_TUGAS)
                        startActivity(next)
                    }
                }
            )
        )
        root.addView(UiKit.bottomNav(this, "Kelas", role, mode))
        return root
    }

    private fun emptyCard(title: String, subtitle: String): View {
        val card = UiKit.card(this).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(px(24), px(28), px(24), px(28))
        }
        card.addView(UiKit.badge(this, R.drawable.ic_folder, UiKit.PRIMARY, UiKit.SOFT, 56, 28, 26))
        card.addView(
            UiKit.tv(this, title, 15f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14)
        )
        card.addView(
            UiKit.tv(this, subtitle, 12.5f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        return card
    }

    /** Menu titik tiga pada kartu konten; hapus dipilih lewat bottom sheet, lalu dikonfirmasi. */
    private fun moreMenu(kind: String, title: String, onDelete: () -> Unit): View =
        ImageView(this).apply {
            setImageResource(R.drawable.ic_more_vert)
            setColorFilter(UiKit.MUTED)
            setPadding(px(8), px(8), px(8), px(8))
            contentDescription = "Menu $kind"
            isClickable = true
            isFocusable = true
            setOnClickListener {
                ClassActionSheet.show(
                    this@KelolaKelasActivity,
                    title,
                    listOf(
                        ClassActionSheet.Row(R.drawable.ic_delete, "Hapus ${kind.replaceFirstChar { it.uppercase() }}", danger = true) {
                            confirmDelete(kind, title, onDelete)
                        }
                    )
                )
            }
            layoutParams = LinearLayout.LayoutParams(px(36), px(36))
        }

    private fun confirmDelete(kind: String, title: String, action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Hapus $kind?")
            .setMessage("\"$title\" akan dihapus dari kelas.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                action()
                render()
            }
            .show()
    }

    private fun materialCard(number: Int, m: TeacherMaterial): View {
        val card = UiKit.card(this).apply { setPadding(px(14), px(14), px(14), px(14)) }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, R.drawable.ic_description, Color.WHITE, UiKit.PRIMARY, 44, 12, 22))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, "Pertemuan $number", 12f, UiKit.MUTED))
        col.addView(UiKit.tv(this, m.title, 15f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2))
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        head.addView(moreMenu("materi", m.title) { TeacherClassStore.deleteMaterial(this, m.id) })
        card.addView(head)
        if (m.description.isNotBlank()) {
            card.addView(
                UiKit.tv(this, m.description, 13f, UiKit.MUTED).apply {
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
            )
        }
        val file = if (m.fileName.isNotBlank()) "Lampiran: ${m.fileName}" else "Tanpa lampiran"
        card.addView(
            UiKit.tv(this, "$file  •  ${TeacherClassStore.formatDate(m.createdAt)}", 11.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8)
        )
        val session = AttendanceStore.session(this, code, m.id)
        val presensiLabel = when {
            session == null -> "Presensi: Belum dibuka"
            session.open -> "Presensi: Dibuka"
            else -> "Presensi: Ditutup"
        }
        card.addView(
            pill(presensiLabel) {
                val next = UiKit.withProfile(Intent(this, PresensiPengajarActivity::class.java), role, mode)
                next.putExtra(PresensiPengajarActivity.EXTRA_CLASS_CODE, code)
                next.putExtra(PresensiPengajarActivity.EXTRA_MATERIAL_ID, m.id)
                next.putExtra(PresensiPengajarActivity.EXTRA_NUMBER, number)
                startActivity(next)
            },
            UiKit.params(this, UiKit.MATCH, px(38), top = 10)
        )
        return card
    }

    private fun quizCard(z: Quiz): View {
        val attempt = QuizStore.attempt(this, z.id)
        val card = UiKit.card(this).apply {
            setPadding(px(14), px(14), px(14), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next = UiKit.withProfile(Intent(this@KelolaKelasActivity, HasilKuisPengajarActivity::class.java), role, mode)
                next.putExtra(HasilKuisPengajarActivity.EXTRA_QUIZ_ID, z.id)
                startActivity(next)
            }
        }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, R.drawable.ic_assignment_check, Color.WHITE, UiKit.PRIMARY, 44, 12, 22))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, z.title, 15f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "${z.questions.size} soal  •  ${z.durationMin} menit  •  Min. ${z.passScore}", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3)
        )
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        head.addView(moreMenu("kuis", z.title) { QuizStore.delete(this, z.id) })
        card.addView(head)
        card.addView(
            UiKit.tv(
                this,
                if (attempt == null) "Belum dikerjakan pelajar" else "Dikerjakan  •  nilai ${attempt.percent}",
                11.5f, UiKit.MUTED
            ),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
        )
        return card
    }

    private fun taskCard(t: TeacherTask): View {
        val card = UiKit.card(this).apply {
            setPadding(px(14), px(14), px(14), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next = UiKit.withProfile(Intent(this@KelolaKelasActivity, PenilaianTugasActivity::class.java), role, mode)
                next.putExtra(PenilaianTugasActivity.EXTRA_CLASS_CODE, code)
                next.putExtra(PenilaianTugasActivity.EXTRA_TASK_ID, t.id)
                startActivity(next)
            }
        }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, R.drawable.ic_assignment_check, Color.WHITE, UiKit.PRIMARY, 44, 12, 22))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, t.title, 15f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "Tenggat: ${TaskStore.format(t.deadlineMillis)}", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3)
        )
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        head.addView(moreMenu("tugas", t.title) { TeacherClassStore.deleteTask(this, t.id) })
        card.addView(head)
        if (t.instructions.isNotBlank()) {
            card.addView(
                UiKit.tv(this, t.instructions, 13f, UiKit.MUTED).apply {
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
            )
        }
        val sub = TaskStore.submission(this, t.id)
        card.addView(
            UiKit.tv(
                this,
                when {
                    sub == null -> "Belum ada pengumpulan  •  ketuk untuk detail"
                    sub.score != null -> "Dinilai ${sub.score}  •  ketuk untuk detail"
                    else -> "1 pengumpulan menunggu penilaian"
                },
                11.5f, if (sub != null && sub.score == null) UiKit.PRIMARY else UiKit.MUTED, sub != null && sub.score == null
            ),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
        )
        return card
    }

    companion object {
        const val EXTRA_CODE = "extra_kelola_kelas_code"
        const val EXTRA_TAB = "extra_kelola_kelas_tab"
        private const val STATE_TAB = "state_kelola_tab"
        const val TAB_MATERI = "Materi"
        const val TAB_TUGAS = "Tugas"
        const val TAB_KUIS = "Kuis"
        const val TAB_DISKUSI = "Diskusi"
    }
}
