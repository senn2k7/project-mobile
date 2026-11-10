package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Halaman "Gabung Kelas Mandiri": masukkan kode, verifikasi, lalu gabung. */
class GabungKelasActivity : AppCompatActivity() {

    private lateinit var etCode: EditText
    private lateinit var resultSection: LinearLayout
    private var found: ClassInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(
            UiKit.tv(this, "Gabung Kelas Mandiri", 26f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8)
        )
        content.addView(
            UiKit.tv(this, "Masukkan kode yang diberikan pengajar.", 14f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6, bottom = 20)
        )

        // Kartu input kode
        val inputCard = UiKit.card(this).apply {
            setPadding(px(16), px(16), px(16), px(16))
        }
        inputCard.addView(UiKit.tv(this, "Kode Kelas", 14f, UiKit.NAVY, true))
        etCode = UiKit.input(
            this, "Masukkan kode kelas", "",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, R.drawable.ic_code
        )
        inputCard.addView(etCode, UiKit.params(this, UiKit.MATCH, px(52), top = 10))
        inputCard.addView(
            UiKit.primaryButton(this, "Verifikasi Kode") { verify() },
            UiKit.params(this, UiKit.MATCH, px(56), top = 14)
        )
        inputCard.addView(
            UiKit.infoBox(this, "Pastikan kode kelas sesuai dengan yang diberikan pengajar."),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14)
        )
        content.addView(inputCard)

        // Hasil verifikasi (disembunyikan sampai kode valid)
        resultSection = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }
        content.addView(resultSection, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 20))

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun verify() {
        etCode.error = null
        val code = etCode.text.toString().trim()
        if (code.isEmpty()) {
            etCode.error = "Masukkan kode kelas"
            return
        }
        val info = ClassStore.find(code)
        if (info == null) {
            found = null
            resultSection.visibility = View.GONE
            etCode.error = "Kode kelas tidak ditemukan. Periksa kembali kode dari pengajar."
            return
        }
        if (ClassStore.isJoined(this, info.code)) {
            found = null
            resultSection.visibility = View.GONE
            etCode.error = "Kamu sudah bergabung di kelas ini"
            return
        }
        found = info
        showResult(info)
    }

    private fun detailRow(iconRes: Int, label: String, value: String): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, px(8), 0, px(8))
            addView(UiKit.icon(this@GabungKelasActivity, iconRes, 20, UiKit.MUTED), LinearLayout.LayoutParams(px(20), px(20)))
            addView(
                UiKit.tv(this@GabungKelasActivity, label, 13.5f, UiKit.MUTED),
                UiKit.params(this@GabungKelasActivity, px(110), UiKit.WRAP, start = 12)
            )
            addView(UiKit.tv(this@GabungKelasActivity, value, 13.5f, UiKit.NAVY, true))
        }

    private fun showResult(info: ClassInfo) {
        resultSection.removeAllViews()
        val card = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }

        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val check = FrameLayout(this).apply { background = UiKit.oval(UiKit.SUCCESS) }
        check.addView(
            UiKit.tv(this, "✓", 26f, Color.WHITE, true).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
        )
        head.addView(check, LinearLayout.LayoutParams(px(52), px(52)))
        val texts = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        texts.addView(UiKit.tv(this, "Kelas ditemukan", 16f, UiKit.NAVY, true))
        texts.addView(
            UiKit.tv(this, "Kode kelas valid. Silakan tinjau informasi kelas terlebih dahulu.", 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        head.addView(texts, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        card.addView(head)

        // Ringkasan kelas
        val info2 = UiKit.card(this).apply { setPadding(px(14), px(14), px(14), px(10)) }
        val title = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        title.addView(UiKit.badge(this, info.iconRes, Color.WHITE, UiKit.PRIMARY, 44, 12, 24))
        title.addView(
            UiKit.tv(this, info.name, 15f, UiKit.NAVY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f)
        )
        title.addView(ClassUi.chip(this, ClassUi.chipLabel(info.type)))
        info2.addView(title)
        info2.addView(UiKit.divider(this), UiKit.params(this, UiKit.MATCH, px(1), top = 12, bottom = 4))
        info2.addView(detailRow(R.drawable.ic_person, "Pengajar", info.teacher))
        info2.addView(detailRow(R.drawable.ic_book, "Mata Pelajaran", info.name))
        info2.addView(detailRow(R.drawable.ic_people, "Jumlah Peserta", "${info.participants} Peserta"))
        card.addView(info2, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        card.addView(
            UiKit.primaryButton(this, "Gabung Kelas") { joinClass() },
            UiKit.params(this, UiKit.MATCH, px(56), top = 16)
        )
        resultSection.addView(card)
        resultSection.visibility = View.VISIBLE
    }

    private fun joinClass() {
        val info = found ?: return
        ClassStore.join(this, info.code)
        UiKit.toast(this, "Berhasil bergabung ke kelas ${info.name}")
        finish()
    }
}
