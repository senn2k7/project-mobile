package com.example.projectmobile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Detail kelas untuk pelajar: info kelas, kode, progres, tab, dan daftar pertemuan. */
class DetailKelasPelajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var info: ClassInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        info = intent.getStringExtra(EXTRA_CODE)?.let { ClassStore.byCode(this, it) }
        if (info == null) {
            UiKit.toast(this, "Kelas tidak ditemukan")
            finish()
        }
    }

    // Dibangun ulang saat kembali dari Detail Materi agar status & progres terbaru.
    override fun onResume() {
        super.onResume()
        val c = info ?: return
        setContentView(build(c))
    }

    private fun build(c: ClassInfo): View {
        val root = UiKit.root(this)

        // Bar atas: kembali, judul, menu
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(20), px(16), px(20), px(8))
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
        bar.addView(
            UiKit.tv(this, "Detail Kelas", 20f, UiKit.NAVY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 16, weight = 1f)
        )
        val more = UiKit.icon(this, R.drawable.ic_more_vert, 24, UiKit.NAVY).apply {
            isClickable = true
            isFocusable = true
        }
        more.setOnClickListener { showMenu(it, c) }
        bar.addView(more, LinearLayout.LayoutParams(px(24), px(24)))
        root.addView(bar)

        val (scroll, content) = UiKit.scroll(this)

        // Header kelas
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, R.drawable.ic_description, Color.WHITE, UiKit.PRIMARY, 64, 16, 34))
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleCol.addView(UiKit.tv(this, c.name, 20f, UiKit.NAVY, true))
        titleCol.addView(ClassUi.chip(this, ClassUi.chipLabel(c.type)), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        head.addView(titleCol, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4))

        // Pengajar
        val teacher = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        teacher.addView(UiKit.avatar(this, 40, withCamera = false).view)
        val tCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        tCol.addView(UiKit.tv(this, c.teacher, 13.5f, UiKit.NAVY, true))
        tCol.addView(UiKit.tv(this, "Dosen pengampu", 12f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 1))
        teacher.addView(tCol, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 12))
        content.addView(teacher, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        content.addView(
            UiKit.tv(this, c.description, 13.5f, UiKit.MUTED).apply { setLineSpacing(px(3).toFloat(), 1f) },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14)
        )

        // Kode kelas
        val codeCard = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(14), px(16), px(14))
        }
        val codeCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        codeCol.addView(UiKit.tv(this, "Kode Kelas", 12.5f, UiKit.MUTED))
        codeCol.addView(UiKit.tv(this, c.code, 20f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4))
        codeCard.addView(codeCol, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        codeCard.addView(
            UiKit.icon(this, R.drawable.ic_copy, 24, UiKit.PRIMARY).apply {
                isClickable = true
                isFocusable = true
                setOnClickListener { copyCode(c.code) }
            },
            LinearLayout.LayoutParams(px(24), px(24))
        )
        content.addView(codeCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 18))

        // Progres kelas
        val progress = ClassStore.progress(this, c)
        val progRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        progRow.addView(UiKit.tv(this, "Progres Kelas", 12.5f, UiKit.NAVY, true), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        progRow.addView(UiKit.tv(this, "$progress%", 12.5f, UiKit.MUTED))
        content.addView(progRow, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 18))
        content.addView(ClassUi.progressBar(this, progress), UiKit.params(this, UiKit.MATCH, px(8), top = 8))

        // Tab
        val tabs = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        for (label in listOf("Materi", "Tugas", "Kuis", "Diskusi", "Nilai")) {
            val on = label == "Materi"
            val t = UiKit.tv(this, label, 12.5f, if (on) Color.WHITE else UiKit.MUTED, true).apply {
                gravity = Gravity.CENTER
                background = UiKit.rounded(this@DetailKelasPelajarActivity, if (on) UiKit.PRIMARY else UiKit.DISABLED, 12)
                isClickable = true
                isFocusable = true
                setOnClickListener { onTab(label, c) }
            }
            tabs.addView(t, UiKit.params(this, 0, px(40), end = 6, weight = 1f))
        }
        content.addView(tabs, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 18))

        // Daftar pertemuan
        content.addView(
            UiKit.tv(this, "Daftar Pertemuan", 17f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22, bottom = 10)
        )
        val meetingList = ClassStore.meetings(this, c)
        if (meetingList.isEmpty()) {
            content.addView(UiKit.tv(this, "Pengajar belum menambahkan materi.", 13f, UiKit.MUTED))
        }
        for (m in meetingList) {
            content.addView(meetingCard(c, m), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 10))
        }

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun meetingCard(c: ClassInfo, m: Meeting): View {
        val status = ClassStore.statusOf(this, c, m)
        val card = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(14), px(14), px(14), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { openMeeting(c, m) }
        }
        val iconBg = if (status == "Selesai") Color.parseColor("#1B5FA8") else UiKit.PRIMARY
        card.addView(UiKit.badge(this, R.drawable.ic_description, Color.WHITE, iconBg, 48, 12, 24))

        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, "Pertemuan ${m.number}", 14f, UiKit.NAVY, true))
        col.addView(UiKit.tv(this, m.title, 13f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        col.addView(
            UiKit.tv(this, "${m.materials} materi · ${m.date}", 11.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
        card.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        card.addView(ClassUi.statusChip(this, status))
        return card
    }

    private fun openMeeting(c: ClassInfo, m: Meeting) {
        val intent = UiKit.withProfile(Intent(this, DetailMateriActivity::class.java), role, mode).apply {
            putExtra(DetailMateriActivity.EXTRA_CODE, c.code)
            putExtra(DetailMateriActivity.EXTRA_NUMBER, m.number)
        }
        startActivity(intent)
    }

    private fun onTab(label: String, c: ClassInfo) {
        when (label) {
            "Materi" -> Unit
            "Kuis" -> {
                val next = UiKit.withProfile(Intent(this, KuisPelajarMandiriActivity::class.java), role, mode)
                next.putExtra(KuisPelajarMandiriActivity.EXTRA_CODE, c.code)
                startActivity(next)
            }
            "Tugas" -> {
                val next = UiKit.withProfile(Intent(this, TugasPelajarActivity::class.java), role, mode)
                next.putExtra(TugasPelajarActivity.EXTRA_CODE, c.code)
                startActivity(next)
            }
            "Diskusi" -> startActivity(
                Intent(this, DiskusiActivity::class.java).apply {
                    putExtra(DiskusiActivity.EXTRA_COURSE_NAME, c.name)
                }
            )
            else -> UiKit.toast(this, "Halaman $label akan segera hadir")
        }
    }

    private fun copyCode(code: String) {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("Kode kelas", code))
        UiKit.toast(this, "Kode kelas disalin")
    }

    private fun showMenu(anchor: View, c: ClassInfo) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "Salin kode kelas")
        if (c.type == ClassStore.TYPE_MANDIRI) popup.menu.add(0, 2, 1, "Keluar dari kelas")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> copyCode(c.code)
                2 -> confirmLeave(c)
            }
            true
        }
        popup.show()
    }

    private fun confirmLeave(c: ClassInfo) {
        AlertDialog.Builder(this)
            .setTitle("Keluar dari kelas?")
            .setMessage("Kamu tidak akan lagi melihat kelas ${c.name}. Kamu bisa bergabung lagi dengan kode kelas.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Keluar") { _, _ ->
                ClassStore.leave(this, c.code)
                UiKit.toast(this, "Kamu keluar dari kelas ${c.name}")
                finish()
            }
            .show()
    }

    companion object {
        const val EXTRA_CODE = "extra_class_code_pelajar"
    }
}
