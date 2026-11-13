package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Bukti presensi: status Hadir, tanggal, dan waktu. */
class PresensiBerhasilActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        val code = intent.getStringExtra(EXTRA_CODE) ?: ""
        val materialId = intent.getStringExtra(EXTRA_MATERIAL_ID) ?: ""
        val number = intent.getIntExtra(EXTRA_NUMBER, 0)
        val info = ClassStore.byCode(this, code)
        val record = AttendanceStore.record(this, code, materialId)
        if (info == null || record == null) {
            finish()
            return
        }
        setContentView(build(info, number, record))
    }

    private fun row(label: String, value: View): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, px(8), 0, px(8))
            addView(UiKit.tv(this@PresensiBerhasilActivity, label, 13f, UiKit.MUTED), LinearLayout.LayoutParams(px(90), UiKit.WRAP))
            addView(value)
        }

    private fun build(info: ClassInfo, number: Int, record: AttendanceRecord): View {
        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padTop = 48)

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val circle = FrameLayout(this).apply { background = UiKit.oval(UiKit.SUCCESS) }
        circle.addView(
            UiKit.tv(this, "✓", 54f, Color.WHITE, true).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
        )
        top.addView(circle, LinearLayout.LayoutParams(px(100), px(100)))
        top.addView(
            UiKit.tv(this, "Presensi Berhasil", 26f, UiKit.NAVY, true).apply { gravity = Gravity.CENTER },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 24, bottom = 8)
        )
        content.addView(top, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))

        val card = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(12)) }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, info.iconRes, Color.WHITE, UiKit.PRIMARY, 44, 12, 22))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, info.name, 16f, UiKit.NAVY, true))
        if (number > 0) {
            col.addView(
                UiKit.tv(this, "Pertemuan $number", 12.5f, UiKit.MUTED),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
            )
        }
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        card.addView(head)
        card.addView(UiKit.divider(this), UiKit.params(this, UiKit.MATCH, px(1), top = 14, bottom = 6))
        card.addView(row("Status", ClassUi.chip(this, "Hadir", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)))
        card.addView(row("Tanggal", UiKit.tv(this, TeacherClassStore.formatDate(record.at), 14f, UiKit.NAVY, true)))
        card.addView(row("Waktu", UiKit.tv(this, AttendanceStore.formatTime(record.at), 14f, UiKit.NAVY, true)))
        content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, UiKit.primaryButton(this, "Kembali ke Beranda") { goHome() }))
        return root
    }

    private fun goHome() {
        val intent = UiKit.withProfile(Intent(this, HomeActivity::class.java), role, mode).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_CODE = "extra_presensi_berhasil_code"
        const val EXTRA_MATERIAL_ID = "extra_presensi_berhasil_material"
        const val EXTRA_NUMBER = "extra_presensi_berhasil_number"
    }
}
