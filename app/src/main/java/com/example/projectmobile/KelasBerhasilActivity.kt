package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Layar "Kelas Berhasil Dibuat" (kode kelas + informasi kelas).
 * Tanpa EXTRA_CREATED, layar ini menjadi "Detail Kelas" milik pengajar.
 */
class KelasBerhasilActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var created = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        created = intent.getBooleanExtra(EXTRA_CREATED, false)
    }

    // Dibangun ulang di onResume supaya perubahan dari layar Edit langsung terlihat.
    override fun onResume() {
        super.onResume()
        val c = intent.getStringExtra(EXTRA_CODE)?.let { TeacherClassStore.byCode(this, it) }
        if (c == null) {
            finish()
            return
        }
        setContentView(build(c))
    }

    private fun detailRow(iconRes: Int, label: String, value: String): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, px(7), 0, px(7))
            addView(UiKit.icon(this@KelasBerhasilActivity, iconRes, 18, UiKit.MUTED), LinearLayout.LayoutParams(px(18), px(18)))
            addView(
                UiKit.tv(this@KelasBerhasilActivity, label, 13f, UiKit.MUTED),
                UiKit.params(this@KelasBerhasilActivity, 0, UiKit.WRAP, start = 10, weight = 1f)
            )
            addView(UiKit.tv(this@KelasBerhasilActivity, value, 13f, UiKit.NAVY, true))
        }

    private fun build(c: TeacherClass): View {
        val root = UiKit.root(this)
        if (!created) root.addView(UiKit.topBar(this, "Detail Kelas") { finish() })
        val (scroll, content) = UiKit.scroll(this, padTop = if (created) 40 else 8)

        if (created) {
            val top = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
            }
            val outer = FrameLayout(this).apply { background = UiKit.oval(UiKit.SOFT) }
            val inner = FrameLayout(this).apply { background = UiKit.oval(UiKit.SUCCESS) }
            inner.addView(
                UiKit.tv(this, "✓", 44f, Color.WHITE, true).apply { gravity = Gravity.CENTER },
                FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
            )
            outer.addView(inner, FrameLayout.LayoutParams(px(84), px(84), Gravity.CENTER))
            top.addView(outer, LinearLayout.LayoutParams(px(140), px(140)))
            top.addView(
                UiKit.tv(this, "Kelas Berhasil Dibuat!", 24f, UiKit.NAVY, true).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 24)
            )
            top.addView(
                UiKit.tv(
                    this,
                    "Kelas Anda telah siap digunakan. Bagikan kode kelas berikut kepada pelajar untuk bergabung.",
                    14f, UiKit.MUTED
                ).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8, bottom = 8)
            )
            content.addView(top)
        }

        // Kartu kode kelas
        val codeCard = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(14)) }
        codeCard.addView(UiKit.tv(this, "Kode Kelas", 15f, UiKit.NAVY, true))
        val codeBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(this@KelasBerhasilActivity, UiKit.SOFT, 12)
            setPadding(px(16), px(14), px(12), px(14))
        }
        codeBox.addView(
            UiKit.tv(this, c.code, 30f, UiKit.PRIMARY, true).apply { gravity = Gravity.CENTER },
            UiKit.params(this, 0, UiKit.WRAP, weight = 1f)
        )
        val salin = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setOnClickListener { TeacherClassStore.copyCode(this@KelasBerhasilActivity, c) }
            addView(UiKit.icon(this@KelasBerhasilActivity, R.drawable.ic_copy, 20, UiKit.PRIMARY), LinearLayout.LayoutParams(px(20), px(20)))
            addView(
                UiKit.tv(this@KelasBerhasilActivity, "Salin", 11f, UiKit.PRIMARY, true),
                UiKit.params(this@KelasBerhasilActivity, UiKit.WRAP, UiKit.WRAP, top = 2)
            )
        }
        codeBox.addView(salin, LinearLayout.LayoutParams(UiKit.WRAP, UiKit.WRAP))
        codeCard.addView(codeBox, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        codeCard.addView(
            UiKit.tv(this, "Kode kelas ini dapat digunakan oleh pelajar untuk bergabung ke kelas Anda.", 12f, UiKit.MUTED)
                .apply { gravity = Gravity.CENTER },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
        )
        content.addView(codeCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Kartu informasi kelas
        val infoCard = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(10)) }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.tv(this, "Informasi Kelas", 15f, UiKit.NAVY, true), UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        val edit = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next = UiKit.withProfile(Intent(this@KelasBerhasilActivity, BuatKelasActivity::class.java), role, mode)
                next.putExtra(BuatKelasActivity.EXTRA_CODE, c.code)
                startActivity(next)
            }
            addView(UiKit.icon(this@KelasBerhasilActivity, R.drawable.ic_pencil, 16, UiKit.PRIMARY), LinearLayout.LayoutParams(px(16), px(16)))
            addView(
                UiKit.tv(this@KelasBerhasilActivity, "Edit", 13f, UiKit.PRIMARY, true),
                UiKit.params(this@KelasBerhasilActivity, UiKit.WRAP, UiKit.WRAP, start = 4)
            )
        }
        head.addView(edit)
        infoCard.addView(head)

        val title = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        title.addView(UiKit.badge(this, TeacherClassStore.iconFor(c.topic), Color.WHITE, UiKit.PRIMARY, 48, 12, 24))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, c.name, 15f, UiKit.NAVY, true))
        if (c.description.isNotBlank()) {
            col.addView(
                UiKit.tv(this, c.description, 12.5f, UiKit.MUTED),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
            )
        }
        title.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        infoCard.addView(title, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
        infoCard.addView(UiKit.divider(this), UiKit.params(this, UiKit.MATCH, px(1), top = 12, bottom = 4))
        infoCard.addView(detailRow(R.drawable.ic_people, "Kapasitas Peserta", "${c.capacity}"))
        infoCard.addView(detailRow(R.drawable.ic_book, "Mata Pelajaran", c.topic))
        infoCard.addView(detailRow(R.drawable.ic_flag, "Tingkat", c.level))
        infoCard.addView(detailRow(R.drawable.ic_lock, "Visibilitas", "Dengan Kode"))
        content.addView(infoCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah
        val share = UiKit.outlineButton(this, "Bagikan Kode") { TeacherClassStore.share(this, c) }
        if (created) {
            val start = UiKit.primaryButton(this, "Mulai Kelola Kelas") { openKelola(c) }
            share.layoutParams = UiKit.params(this, UiKit.MATCH, px(56), top = 10)
            root.addView(UiKit.bottomBar(this, start, share))
        } else {
            val kelola = UiKit.primaryButton(this, "Kelola Materi & Tugas") { openKelola(c) }
            share.layoutParams = UiKit.params(this, UiKit.MATCH, px(56), top = 10)
            root.addView(UiKit.bottomBar(this, kelola, share))
        }
        return root
    }

    private fun openKelola(c: TeacherClass) {
        val next = UiKit.withProfile(Intent(this, KelolaKelasActivity::class.java), role, mode)
        next.putExtra(KelolaKelasActivity.EXTRA_CODE, c.code)
        startActivity(next)
        finish()
    }

    private fun goHome() {
        val intent = UiKit.withProfile(Intent(this, HomeActivity::class.java), role, mode).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_CODE = "extra_kelas_berhasil_code"
        const val EXTRA_CREATED = "extra_kelas_berhasil_created"
    }
}
