package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
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

    private fun build(c: TeacherClass): View {
        val materials = TeacherClassStore.materials(this, c.code)
        val tasks = TeacherClassStore.tasks(this, c.code)

        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Kelola Kelas") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        // Header kelas
        val header = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(UiKit.badge(this, TeacherClassStore.iconFor(c.topic), Color.WHITE, UiKit.PRIMARY, 52, 14, 26))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, c.name, 17f, UiKit.NAVY, true))
        col.addView(ClassUi.chip(this, "Kode: ${c.code}"), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        top.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        header.addView(top)
        header.addView(
            UiKit.tv(this, "${materials.size} materi  •  ${tasks.size} tugas  •  0/${c.capacity} peserta", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12)
        )
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(
            pill("Info Kelas") {
                val next = UiKit.withProfile(Intent(this, KelasBerhasilActivity::class.java), role, mode)
                next.putExtra(KelasBerhasilActivity.EXTRA_CODE, c.code)
                startActivity(next)
            },
            UiKit.params(this, 0, px(38), end = 6, weight = 1f)
        )
        actions.addView(
            pill("Bagikan Kode") { TeacherClassStore.share(this, c) },
            UiKit.params(this, 0, px(38), start = 6, weight = 1f)
        )
        header.addView(actions, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
        content.addView(header)

        // Tab Materi / Tugas
        val tabs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = UiKit.rounded(this@KelolaKelasActivity, UiKit.SOFT, 12)
            setPadding(px(4), px(4), px(4), px(4))
        }
        for (label in listOf(TAB_MATERI, TAB_TUGAS)) {
            val on = label == tab
            val count = if (label == TAB_MATERI) materials.size else tasks.size
            val t = UiKit.tv(this, "$label ($count)", 13f, if (on) Color.WHITE else UiKit.MUTED, on).apply {
                gravity = Gravity.CENTER
                if (on) background = UiKit.rounded(this@KelolaKelasActivity, UiKit.PRIMARY, 10)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    if (tab != label) {
                        tab = label
                        render()
                    }
                }
            }
            tabs.addView(t, UiKit.params(this, 0, px(38), weight = 1f))
        }
        content.addView(tabs, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16, bottom = 4))

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
                UiKit.primaryButton(this, if (tab == TAB_MATERI) "+ Tambah Materi" else "+ Tambah Tugas") {
                    val next = UiKit.withProfile(Intent(this, TambahKontenActivity::class.java), role, mode)
                    next.putExtra(TambahKontenActivity.EXTRA_CODE, c.code)
                    next.putExtra(TambahKontenActivity.EXTRA_TYPE, if (tab == TAB_MATERI) TambahKontenActivity.TYPE_MATERI else TambahKontenActivity.TYPE_TUGAS)
                    startActivity(next)
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

    private fun deleteLabel(onClick: () -> Unit): TextView =
        UiKit.tv(this, "Hapus", 12f, UiKit.DANGER, true).apply {
            setPadding(px(8), px(6), px(4), px(6))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
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
        head.addView(deleteLabel { confirmDelete("materi", m.title) { TeacherClassStore.deleteMaterial(this, m.id) } })
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
        return card
    }

    private fun taskCard(t: TeacherTask): View {
        val card = UiKit.card(this).apply { setPadding(px(14), px(14), px(14), px(14)) }
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
        head.addView(deleteLabel { confirmDelete("tugas", t.title) { TeacherClassStore.deleteTask(this, t.id) } })
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
        return card
    }

    companion object {
        const val EXTRA_CODE = "extra_kelola_kelas_code"
        const val EXTRA_TAB = "extra_kelola_kelas_tab"
        private const val STATE_TAB = "state_kelola_tab"
        private const val TAB_MATERI = "Materi"
        private const val TAB_TUGAS = "Tugas"
    }
}
