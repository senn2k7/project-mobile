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
    private var isJoining = false

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
            UiKit.params(this, UiKit.MATCH, px(52), top = 14)
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
        val code = etCode.text.toString().trim().uppercase()

        if (code.isEmpty()) {
            etCode.error = "Masukkan kode kelas"
            return
        }

        if (ClassStore.isJoined(this, code)) {
            found = null
            resultSection.visibility = View.GONE
            etCode.error = "Kamu sudah bergabung di kelas ini"
            return
        }

        resultSection.visibility = View.GONE
        found = null

        // Pertahankan kelas contoh yang tersedia secara lokal.
        val sampleInfo = ClassStore.find(code)
        if (sampleInfo != null) {
            found = sampleInfo
            showResult(sampleInfo)
            return
        }

        UiKit.toast(this, "Memeriksa kode kelas...")

        FirebaseManager.findTeacherClass(
            code = code,
            onSuccess = { info ->
                runOnUiThread {
                    if (ClassStore.isJoined(this, info.code)) {
                        found = null
                        resultSection.visibility = View.GONE
                        etCode.error = "Kamu sudah bergabung di kelas ini"
                        return@runOnUiThread
                    }

                    found = info
                    showResult(info)
                }
            },
            onFailure = { message ->
                runOnUiThread {
                    found = null
                    resultSection.visibility = View.GONE
                    etCode.error = message
                }
            }
        )
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
            UiKit.icon(this, R.drawable.ic_check, 28, Color.WHITE),
            FrameLayout.LayoutParams(px(28), px(28), Gravity.CENTER)
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
            UiKit.params(this, UiKit.MATCH, px(52), top = 16)
        )
        resultSection.addView(card)
        resultSection.visibility = View.VISIBLE
    }

    private fun joinClass() {
        val info = found ?: return

        if (isJoining) return
        isJoining = true

        // Kelas contoh tetap menggunakan penyimpanan lokal.
        if (ClassStore.find(info.code) != null) {
            ClassStore.join(this, info)
            UiKit.toast(this, "Berhasil bergabung ke kelas ${info.name}")
            finish()
            return
        }

        UiKit.toast(this, "Menyimpan keanggotaan kelas...")

        FirebaseManager.joinTeacherClass(
            code = info.code,
            onSuccess = { joinedInfo ->
                runOnUiThread {
                    if (isFinishing || isDestroyed) return@runOnUiThread

                    ClassStore.join(this, joinedInfo)
                    UiKit.toast(
                        this,
                        "Berhasil bergabung ke kelas ${joinedInfo.name}"
                    )
                    finish()
                }
            },
            onFailure = { message ->
                runOnUiThread {
                    if (isFinishing || isDestroyed) return@runOnUiThread

                    isJoining = false
                    UiKit.toast(this, message)
                }
            }
        )
    }
}
