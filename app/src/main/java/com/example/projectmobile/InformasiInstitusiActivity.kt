package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Halaman Informasi Institusi (versi pelajar ringkas, versi pengajar lengkap). */
class InformasiInstitusiActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        setContentView(if (ProfileStore.isTeacher(role)) buildTeacher() else buildStudent())
    }

    private fun value(field: String) = ProfileStore.get(this, role, field)

    private fun banner(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(px(14), px(12), px(14), px(12))
        background = UiKit.rounded(this@InformasiInstitusiActivity, UiKit.SUCCESS_SOFT, 12)
        addView(
            UiKit.icon(this@InformasiInstitusiActivity, R.drawable.ic_check_circle, 20, UiKit.SUCCESS),
            LinearLayout.LayoutParams(px(20), px(20))
        )
        addView(
            UiKit.tv(this@InformasiInstitusiActivity, "Terhubung dengan institusi terdaftar", 13f, UiKit.SUCCESS, true),
            UiKit.params(this@InformasiInstitusiActivity, 0, UiKit.WRAP, start = 10, weight = 1f)
        )
    }

    private fun labelValue(label: String, text: String): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, px(8), 0, px(8))
        addView(UiKit.tv(this@InformasiInstitusiActivity, label, 12.5f, UiKit.MUTED))
        addView(
            UiKit.tv(this@InformasiInstitusiActivity, text, 15f, UiKit.NAVY, true),
            UiKit.params(this@InformasiInstitusiActivity, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
    }

    // ---------------------------------------------------------------- Pelajar

    private fun buildStudent(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Informasi Institusi") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(banner())

        val card = UiKit.card(this)
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(UiKit.SOFT)
            setPadding(px(16), px(16), px(16), px(16))
        }
        head.addView(UiKit.icon(this, R.drawable.ic_institution, 32, UiKit.PRIMARY), LinearLayout.LayoutParams(px(32), px(32)))
        head.addView(
            UiKit.tv(this, value(ProfileStore.INSTITUSI), 15f, UiKit.NAVY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f)
        )
        card.addView(head)

        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(16), px(8), px(16), px(12))
        }
        // Data contoh dari desain; sambungkan ke server saat tersedia.
        body.addView(labelValue("Fakultas", "Fakultas Teknik"))
        body.addView(labelValue("Program Studi", value(ProfileStore.PRODI)))
        body.addView(labelValue("Tahun Ajaran", "2025 / 2026"))
        card.addView(body)
        content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        content.addView(
            UiKit.infoBox(this, "Informasi institusi dikelola oleh pihak institusi. Hubungi admin jika ada perubahan data."),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16)
        )

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    // ---------------------------------------------------------------- Pengajar

    private fun detailRow(iconRes: Int, label: String, text: String): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(px(16), px(12), px(16), px(12))
        addView(
            UiKit.icon(this@InformasiInstitusiActivity, iconRes, 22, UiKit.MUTED),
            LinearLayout.LayoutParams(px(22), px(22))
        )
        val col = LinearLayout(this@InformasiInstitusiActivity).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this@InformasiInstitusiActivity, label, 12f, UiKit.MUTED))
        col.addView(
            UiKit.tv(this@InformasiInstitusiActivity, text, 14f, UiKit.NAVY),
            UiKit.params(this@InformasiInstitusiActivity, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        addView(col, UiKit.params(this@InformasiInstitusiActivity, 0, UiKit.WRAP, start = 14, weight = 1f))
    }

    private fun listCard(vararg rows: View): LinearLayout {
        val card = UiKit.card(this)
        rows.forEachIndexed { i, r ->
            card.addView(r)
            if (i < rows.lastIndex) card.addView(UiKit.divider(this))
        }
        return card
    }

    private fun buildTeacher(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Informasi Institusi") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(banner())

        // Kartu nama institusi
        val head = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(14), px(14), px(14), px(14))
        }
        head.addView(UiKit.badge(this, R.drawable.ic_institution, UiKit.PRIMARY, UiKit.SOFT, 64, 14, 36))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, value(ProfileStore.INSTITUSI), 15f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "Perguruan Tinggi", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        col.addView(
            UiKit.tv(this, "Aktif", 11.5f, UiKit.SUCCESS, true).apply {
                background = UiKit.rounded(this@InformasiInstitusiActivity, UiKit.SUCCESS_SOFT, 8)
                setPadding(px(10), px(3), px(10), px(3))
            },
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))

        // Detail institusi (data contoh dari desain)
        content.addView(
            UiKit.tv(this, "Detail Institusi", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 20, bottom = 8)
        )
        content.addView(
            listCard(
                detailRow(R.drawable.ic_institution, "Jenis Institusi", "Perguruan Tinggi"),
                detailRow(R.drawable.ic_flag, "Alamat", "Jl. Pendidikan No. 123, Bandung\nJawa Barat, Indonesia"),
                detailRow(R.drawable.ic_email, "Email Administrator", "admin@uti.ac.id"),
                detailRow(R.drawable.ic_phone, "Nomor Telepon", "022-555-0142"),
                detailRow(R.drawable.ic_globe, "Website", "https://uti.ac.id")
            )
        )

        content.addView(
            UiKit.tv(this, "Informasi Akademik", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 20, bottom = 8)
        )
        content.addView(
            listCard(
                detailRow(R.drawable.ic_institution, "Fakultas", value(ProfileStore.FAKULTAS)),
                detailRow(R.drawable.ic_book, "Program Studi", value(ProfileStore.PRODI)),
                detailRow(R.drawable.ic_calendar, "Tahun Akademik", "2026 / 2027")
            )
        )

        content.addView(
            UiKit.infoBox(this, "Informasi institusi dikelola administrator. Hubungi administrator jika ada data yang perlu diperbarui."),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16)
        )

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, UiKit.outlineButton(this, "Kembali ke Profil") { finish() }))
        return root
    }
}
