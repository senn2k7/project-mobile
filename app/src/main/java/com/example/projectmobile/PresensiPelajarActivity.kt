package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Pelajar mengisi kode presensi untuk satu pertemuan. */
class PresensiPelajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var classCode = ""
    private var number = 0
    private var etCode: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        classCode = intent.getStringExtra(EXTRA_CODE) ?: ""
        number = intent.getIntExtra(EXTRA_NUMBER, 0)
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val info = ClassStore.byCode(this, classCode)
        if (info == null) {
            finish()
            return
        }
        setContentView(build(info))
    }

    /** Pertemuan yang dipilih, atau bila tidak ditentukan: pertemuan yang presensinya sedang dibuka. */
    private fun resolve(materials: List<TeacherMaterial>): Pair<Int, TeacherMaterial>? {
        if (materials.isEmpty()) return null
        if (number in 1..materials.size) return Pair(number, materials[number - 1])
        val openIdx = materials.indexOfFirst { AttendanceStore.session(this, classCode, it.id)?.open == true }
        val idx = if (openIdx >= 0) openIdx else materials.size - 1
        return Pair(idx + 1, materials[idx])
    }

    private fun build(info: ClassInfo): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Presensi Kelas") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        val picked = resolve(TeacherClassStore.materials(this, classCode))
        if (picked == null) {
            content.addView(infoCard("Presensi belum tersedia", "Pengajar belum menambahkan pertemuan pada kelas ini."))
            root.addView(scroll, UiKit.scrollParams())
            return root
        }
        val (num, material) = picked
        val session = AttendanceStore.session(this, classCode, material.id)
        val record = AttendanceStore.record(this, classCode, material.id)
        val isOpen = session?.open == true

        // Kelas & pertemuan
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, info.iconRes, Color.WHITE, UiKit.PRIMARY, 44, 12, 22))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, info.name, 15f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "Pertemuan $num  •  ${TeacherClassStore.formatDate(System.currentTimeMillis())}", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6, bottom = 14))

        // Status kehadiran
        val orange = Color.parseColor("#FFA62B")
        val statusText = when {
            record != null -> "Sudah Presensi"
            isOpen -> "Belum Presensi"
            else -> "Presensi Belum Dibuka"
        }
        val statusColor = when {
            record != null -> UiKit.SUCCESS
            isOpen -> orange
            else -> UiKit.MUTED
        }
        val status = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(16), px(16), px(16))
        }
        status.addView(UiKit.badge(this, R.drawable.ic_assignment_check, Color.WHITE, statusColor, 48, 12, 24))
        val sCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        sCol.addView(UiKit.tv(this, "Status Kehadiran", 12.5f, UiKit.MUTED))
        sCol.addView(
            UiKit.tv(this, statusText, 18f, statusColor, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        status.addView(sCol, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        content.addView(status)

        val bottom: View
        if (record != null) {
            content.addView(
                infoCard("Presensi tercatat", "Kamu hadir pada pukul ${AttendanceStore.formatTime(record.at)}."),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14)
            )
            bottom = UiKit.outlineButton(this, "Lihat Bukti Presensi") { openBerhasil(material.id, num) }
        } else if (isOpen) {
            val card = UiKit.card(this).apply { setPadding(px(16), px(4), px(16), px(16)) }
            card.addView(UiKit.fieldLabel(this, "Kode Presensi"))
            val et = UiKit.input(
                this, "Masukkan ${AttendanceStore.CODE_LENGTH} karakter kode", "",
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS, R.drawable.ic_lock
            )
            et.filters = arrayOf(InputFilter.LengthFilter(AttendanceStore.CODE_LENGTH), InputFilter.AllCaps())
            etCode = et
            card.addView(et)
            card.addView(
                UiKit.tv(this, "Kode diberikan oleh pengajar di kelas.", 12f, UiKit.MUTED),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
            )
            content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
            bottom = UiKit.primaryButton(this, "Kirim Presensi") { submit(material.id, num) }
        } else {
            content.addView(
                infoCard("Presensi belum dibuka", "Tunggu pengajar membuka presensi, lalu muat ulang halaman ini."),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14)
            )
            bottom = UiKit.outlineButton(this, "Muat Ulang") { render() }
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, bottom))
        return root
    }

    private fun infoCard(title: String, text: String): View {
        val card = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        card.addView(UiKit.tv(this, title, 15f, UiKit.NAVY, true))
        card.addView(
            UiKit.tv(this, text, 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6)
        )
        return card
    }

    private fun submit(materialId: String, num: Int) {
        val input = etCode?.text?.toString()?.trim() ?: ""
        if (input.length != AttendanceStore.CODE_LENGTH) {
            etCode?.error = "Kode terdiri dari ${AttendanceStore.CODE_LENGTH} karakter"
            etCode?.requestFocus()
            return
        }
        val error = AttendanceStore.submit(this, classCode, materialId, input)
        if (error != null) {
            etCode?.error = error
            etCode?.requestFocus()
            render()
            return
        }
        openBerhasil(materialId, num)
        finish()
    }

    private fun openBerhasil(materialId: String, num: Int) {
        val next = UiKit.withProfile(Intent(this, PresensiBerhasilActivity::class.java), role, mode)
        next.putExtra(PresensiBerhasilActivity.EXTRA_CODE, classCode)
        next.putExtra(PresensiBerhasilActivity.EXTRA_MATERIAL_ID, materialId)
        next.putExtra(PresensiBerhasilActivity.EXTRA_NUMBER, num)
        startActivity(next)
    }

    companion object {
        const val EXTRA_CODE = "extra_presensi_pelajar_code"
        const val EXTRA_NUMBER = "extra_presensi_pelajar_number"
    }
}
