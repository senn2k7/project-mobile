package com.example.projectmobile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Pengajar membuka atau menutup presensi untuk satu pertemuan dan melihat rekap kehadiran. */
class PresensiPengajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var classCode = ""
    private var materialId = ""
    private var number = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        classCode = intent.getStringExtra(EXTRA_CLASS_CODE) ?: ""
        materialId = intent.getStringExtra(EXTRA_MATERIAL_ID) ?: ""
        number = intent.getIntExtra(EXTRA_NUMBER, 0)
        render()
    }

    private fun render() {
        val cls = TeacherClassStore.byCode(this, classCode)
        val material = TeacherClassStore.materials(this, classCode).firstOrNull { it.id == materialId }
        if (cls == null || material == null) {
            finish()
            return
        }
        setContentView(build(cls, material))
    }

    private fun build(cls: TeacherClass, m: TeacherMaterial): View {
        val session = AttendanceStore.session(this, classCode, materialId)
        val isOpen = session?.open == true
        val hadir = AttendanceStore.attendedCount(this, classCode, materialId)
        val record = AttendanceStore.record(this, classCode, materialId)

        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Presensi") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        // Kelas & pertemuan
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, TeacherClassStore.iconFor(cls.topic), Color.WHITE, UiKit.PRIMARY, 48, 12, 24))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, cls.name, 16f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "Pertemuan $number: ${m.title}", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6, bottom = 14))

        // Status presensi
        val status = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(16), px(16), px(16))
        }
        status.addView(
            UiKit.badge(
                this, R.drawable.ic_assignment_check, Color.WHITE,
                if (isOpen) UiKit.SUCCESS else Color.parseColor("#FFA62B"), 48, 12, 24
            )
        )
        val sCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        sCol.addView(UiKit.tv(this, "Status Presensi", 12.5f, UiKit.MUTED))
        sCol.addView(
            UiKit.tv(
                this,
                when {
                    session == null -> "Belum Dibuka"
                    isOpen -> "Sedang Dibuka"
                    else -> "Sudah Ditutup"
                },
                18f,
                when {
                    isOpen -> UiKit.SUCCESS
                    session == null -> UiKit.MUTED
                    else -> UiKit.DANGER
                },
                true
            ),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        status.addView(sCol, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        content.addView(status)

        // Kode presensi (hanya saat dibuka)
        if (isOpen && session != null) {
            val codeCard = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(14)) }
            codeCard.addView(UiKit.tv(this, "Kode Presensi", 15f, UiKit.NAVY, true))
            val box = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = UiKit.rounded(this@PresensiPengajarActivity, UiKit.SOFT, 12)
                setPadding(px(16), px(14), px(12), px(14))
            }
            box.addView(
                UiKit.tv(this, session.code, 32f, UiKit.PRIMARY, true).apply {
                    gravity = Gravity.CENTER
                    letterSpacing = 0.15f
                },
                UiKit.params(this, 0, UiKit.WRAP, weight = 1f)
            )
            val salin = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("Kode presensi", session.code))
                    UiKit.toast(this@PresensiPengajarActivity, "Kode presensi disalin")
                }
                addView(UiKit.icon(this@PresensiPengajarActivity, R.drawable.ic_copy, 20, UiKit.PRIMARY), LinearLayout.LayoutParams(px(20), px(20)))
                addView(
                    UiKit.tv(this@PresensiPengajarActivity, "Salin", 11f, UiKit.PRIMARY, true),
                    UiKit.params(this@PresensiPengajarActivity, UiKit.WRAP, UiKit.WRAP, top = 2)
                )
            }
            box.addView(salin)
            codeCard.addView(box, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            codeCard.addView(
                UiKit.tv(this, "Bagikan kode ini kepada pelajar di kelas. Kode berlaku sampai presensi ditutup.", 12f, UiKit.MUTED)
                    .apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
            )
            content.addView(codeCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
        }

        // Rekap kehadiran
        val rekap = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        rekap.addView(UiKit.tv(this, "Rekap Kehadiran", 15f, UiKit.NAVY, true))
        rekap.addView(
            UiKit.tv(this, "$hadir pelajar hadir", 22f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8)
        )
        rekap.addView(
            UiKit.tv(
                this,
                if (record != null) "Presensi tercatat pukul ${AttendanceStore.formatTime(record.at)}"
                else "Belum ada pelajar yang melakukan presensi.",
                12.5f, UiKit.MUTED
            ),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
        content.addView(rekap, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))

        root.addView(scroll, UiKit.scrollParams())
        val button = if (isOpen) {
            UiKit.outlineButton(this, "Tutup Presensi") {
                AttendanceStore.close(this, classCode, materialId)
                UiKit.toast(this, "Presensi ditutup")
                render()
            }
        } else {
            UiKit.primaryButton(this, if (session == null) "Buka Presensi" else "Buka Kembali Presensi") {
                AttendanceStore.open(this, classCode, materialId)
                UiKit.toast(this, "Presensi dibuka")
                render()
            }
        }
        root.addView(UiKit.bottomBar(this, button))
        return root
    }

    companion object {
        const val EXTRA_CLASS_CODE = "extra_presensi_pengajar_class"
        const val EXTRA_MATERIAL_ID = "extra_presensi_pengajar_material"
        const val EXTRA_NUMBER = "extra_presensi_pengajar_number"
    }
}
