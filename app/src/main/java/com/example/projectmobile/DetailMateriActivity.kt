package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

/** Detail materi per pertemuan: pendahuluan, file materi, dan aksi belajar. */
class DetailMateriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var info: ClassInfo? = null
    private var meeting: Meeting? = null

    private data class MaterialFile(val name: String, val size: String, val color: Int, val action: String)

    private val files = listOf(
        MaterialFile("Panduan Android Studio.pdf", "2.5 MB", Color.parseColor("#FF6B7A"), "Buka"),
        MaterialFile("Pengenalan Android Studio.pptx", "4.2 MB", Color.parseColor("#FFA52F"), "Unduh"),
        MaterialFile("Latihan Praktik.docx", "1.1 MB", Color.parseColor("#1D5EFF"), "Unduh")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        val c = intent.getStringExtra(EXTRA_CODE)?.let { ClassStore.byCode(it) }
        val number = intent.getIntExtra(EXTRA_NUMBER, 1)
        info = c
        meeting = c?.let { ClassStore.meetings(it).firstOrNull { m -> m.number == number } }
        if (c == null || meeting == null) {
            UiKit.toast(this, "Materi tidak ditemukan")
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val c = info ?: return
        val m = meeting ?: return
        setContentView(build(c, m))
    }

    private fun build(c: ClassInfo, m: Meeting): View {
        val done = ClassStore.isDone(this, c.code, m.number)
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Detail Kelas") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        val card = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }

        // Judul materi
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val tBox = FrameLayout(this).apply { background = UiKit.rounded(this@DetailMateriActivity, UiKit.PRIMARY, 14) }
        tBox.addView(
            UiKit.tv(this, "T", 28f, Color.WHITE).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
        )
        head.addView(tBox, LinearLayout.LayoutParams(px(64), px(64)))
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleCol.addView(UiKit.tv(this, m.title, 18f, UiKit.NAVY, true))
        titleCol.addView(ClassUi.chip(this, "Pertemuan ${m.number}"), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8))
        head.addView(titleCol, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        card.addView(head)

        // Pengajar
        val teacher = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        teacher.addView(UiKit.avatar(this, 36, withCamera = false).view)
        val tCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        tCol.addView(UiKit.tv(this, c.teacher, 13f, UiKit.NAVY, true))
        tCol.addView(UiKit.tv(this, m.date, 11.5f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 1))
        teacher.addView(tCol, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 10))
        card.addView(teacher, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Tab
        val tabs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = UiKit.rounded(this@DetailMateriActivity, UiKit.SOFT, 12)
            setPadding(px(4), px(4), px(4), px(4))
        }
        for (label in listOf("Materi", "Tugas", "Kuis", "Diskusi", "Presensi")) {
            val on = label == "Materi"
            val t = UiKit.tv(this, label, 12f, if (on) Color.WHITE else UiKit.MUTED, on).apply {
                gravity = Gravity.CENTER
                if (on) background = UiKit.rounded(this@DetailMateriActivity, UiKit.PRIMARY, 10)
                isClickable = true
                isFocusable = true
                setOnClickListener { onTab(label, c) }
            }
            tabs.addView(t, UiKit.params(this, 0, px(34), weight = 1f))
        }
        card.addView(tabs, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Pendahuluan
        card.addView(
            UiKit.tv(this, "Pendahuluan", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 20)
        )
        card.addView(
            UiKit.tv(this, intro(m), 13.5f, UiKit.MUTED).apply { setLineSpacing(px(4).toFloat(), 1f) },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
        )

        // File materi
        card.addView(
            UiKit.tv(this, "File Materi", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 20, bottom = 4)
        )
        for (f in files) card.addView(fileRow(f), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))

        content.addView(card)
        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah
        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(
            UiKit.outlineButton(this, "Buka Materi") {
                UiKit.toast(this, "Membuka materi ${m.title}...")
            },
            UiKit.params(this, 0, px(52), end = 6, weight = 1f)
        )
        val doneBtn = UiKit.tv(this, if (done) "Selesai ✓" else "Tandai Selesai", 16f, if (done) UiKit.SUCCESS else Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            background = if (done) UiKit.rounded(this@DetailMateriActivity, UiKit.SUCCESS_SOFT, 14, UiKit.SUCCESS)
            else UiKit.rounded(this@DetailMateriActivity, UiKit.SUCCESS, 14)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                ClassStore.setDone(this@DetailMateriActivity, c.code, m.number, !done)
                UiKit.toast(
                    this@DetailMateriActivity,
                    if (!done) "Pertemuan ${m.number} ditandai selesai" else "Tanda selesai dibatalkan"
                )
                setContentView(build(c, m))
            }
        }
        buttons.addView(doneBtn, UiKit.params(this, 0, px(52), start = 6, weight = 1f))

        val ask = UiKit.primaryButton(this, "✦  Tanyakan Materi ke AI") {
            UiKit.toast(this, "Membuka Asisten AI TemanStudi...")
        }
        root.addView(UiKit.bottomBar(this, buttons, ask.also {
            it.layoutParams = UiKit.params(this, UiKit.MATCH, px(52), top = 10)
        }))
        return root
    }

    private fun intro(m: Meeting): String =
        if (m.number == 1 && m.title.contains("Android Studio")) {
            "Android Studio adalah Integrated Development Environment (IDE) resmi dari Google untuk mengembangkan aplikasi Android. Dalam pertemuan ini, kita akan mempelajari dasar-dasar Android Studio, struktur proyek, dan konfigurasi awal."
        } else {
            "Pada pertemuan ini kita membahas ${m.title}. Pelajari materi dan file pendukung di bawah, lalu kerjakan latihan yang diberikan pengajar."
        }

    private fun fileRow(f: MaterialFile): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(this@DetailMateriActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(10), px(10), px(14), px(10))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                UiKit.toast(this@DetailMateriActivity, if (f.action == "Buka") "Membuka ${f.name}..." else "Mengunduh ${f.name}...")
            }
        }
        row.addView(UiKit.badge(this, R.drawable.ic_description, Color.WHITE, f.color, 44, 10, 22))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, f.name, 13f, UiKit.NAVY, true))
        col.addView(UiKit.tv(this, f.size, 11.5f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2))
        row.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        row.addView(UiKit.tv(this, f.action, 13f, UiKit.PRIMARY, true))
        return row
    }

    private fun onTab(label: String, c: ClassInfo) {
        when (label) {
            "Materi" -> Unit
            "Diskusi" -> startActivity(
                Intent(this, DiskusiActivity::class.java).apply {
                    putExtra(DiskusiActivity.EXTRA_COURSE_NAME, c.name)
                }
            )
            else -> UiKit.toast(this, "Halaman $label akan segera hadir")
        }
    }

    companion object {
        const val EXTRA_CODE = "extra_materi_class_code"
        const val EXTRA_NUMBER = "extra_materi_number"
    }
}
