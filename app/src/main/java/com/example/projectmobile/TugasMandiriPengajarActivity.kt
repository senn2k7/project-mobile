package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Menu bawah "Tugas" untuk pengajar mode mandiri: semua tugas dari semua kelas buatannya. */
class TugasMandiriPengajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var filter = FILTER_SEMUA

    private data class Item(val task: TeacherTask, val cls: TeacherClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        filter = savedInstanceState?.getString(STATE_FILTER) ?: FILTER_SEMUA
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_FILTER, filter)
    }

    override fun onResume() {
        super.onResume()
        setContentView(build())
    }

    private fun build(): View {
        val classes = TeacherClassStore.all(this)
        val all = classes
            .flatMap { c -> TeacherClassStore.tasks(this, c.code).map { Item(it, c) } }
            .sortedBy { it.task.deadlineMillis }
        val now = System.currentTimeMillis()
        val shown = when (filter) {
            FILTER_AKTIF -> all.filter { it.task.deadlineMillis >= now }
            FILTER_SELESAI -> all.filter { it.task.deadlineMillis < now }
            else -> all
        }

        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padTop = 24)

        content.addView(UiKit.tv(this, "Tugas", 26f, UiKit.NAVY, true))
        content.addView(
            UiKit.tv(
                this,
                if (all.isEmpty()) "Tugas dari kelas mandiri Anda akan tampil di sini."
                else "${all.size} tugas dari ${classes.size} kelas mandiri",
                13f, UiKit.MUTED
            ),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 16)
        )

        // Filter
        val tabs = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = UiKit.rounded(this@TugasMandiriPengajarActivity, UiKit.SOFT, 12)
            setPadding(px(4), px(4), px(4), px(4))
        }
        for (label in listOf(FILTER_SEMUA, FILTER_AKTIF, FILTER_SELESAI)) {
            val on = label == filter
            val t = UiKit.tv(this, label, 13f, if (on) Color.WHITE else UiKit.MUTED, on).apply {
                gravity = Gravity.CENTER
                if (on) background = UiKit.rounded(this@TugasMandiriPengajarActivity, UiKit.PRIMARY, 10)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    if (filter != label) {
                        filter = label
                        setContentView(build())
                    }
                }
            }
            tabs.addView(t, UiKit.params(this, 0, px(38), weight = 1f))
        }
        content.addView(tabs, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 4))

        if (shown.isEmpty()) {
            val empty = UiKit.card(this).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(px(24), px(32), px(24), px(32))
            }
            empty.addView(UiKit.badge(this, R.drawable.ic_assignment_check, UiKit.PRIMARY, UiKit.SOFT, 64, 32, 30))
            empty.addView(
                UiKit.tv(this, "Belum Ada Tugas", 16f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 16)
            )
            empty.addView(
                UiKit.tv(
                    this,
                    if (classes.isEmpty()) "Buat kelas terlebih dahulu, lalu tambahkan tugas untuk pelajar."
                    else "Tidak ada tugas pada filter ini. Buat tugas baru dengan tombol di bawah.",
                    13f, UiKit.MUTED
                ).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
            )
            content.addView(empty, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        } else {
            for (item in shown) {
                content.addView(taskCard(item, now), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            }
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, UiKit.primaryButton(this, "+ Buat Tugas") { buatTugas() }))
        root.addView(UiKit.bottomNav(this, "Tugas", role, mode))
        return root
    }

    private fun taskCard(item: Item, now: Long): View {
        val t = item.task
        val active = t.deadlineMillis >= now
        val card = UiKit.card(this).apply {
            setPadding(px(16), px(14), px(16), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next = UiKit.withProfile(Intent(this@TugasMandiriPengajarActivity, KelolaKelasActivity::class.java), role, mode)
                next.putExtra(KelolaKelasActivity.EXTRA_CODE, item.cls.code)
                next.putExtra(KelolaKelasActivity.EXTRA_TAB, "Tugas")
                startActivity(next)
            }
        }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(
            UiKit.badge(
                this, R.drawable.ic_assignment_check, Color.WHITE,
                if (active) UiKit.PRIMARY else UiKit.SUCCESS, 48, 12, 24
            )
        )
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, t.title, 15f, UiKit.NAVY, true))
        col.addView(ClassUi.chip(this, item.cls.name), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        head.addView(
            if (active) ClassUi.chip(this, "Berlangsung")
            else ClassUi.chip(this, "Selesai", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)
        )
        card.addView(head)
        card.addView(UiKit.divider(this), UiKit.params(this, UiKit.MATCH, px(1), top = 12, bottom = 10))

        val submitted = if (TaskStore.submission(this, t.id) != null) 1 else 0
        val foot = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        foot.addView(UiKit.icon(this, R.drawable.ic_calendar, 16, UiKit.MUTED), LinearLayout.LayoutParams(px(16), px(16)))
        foot.addView(
            UiKit.tv(this, "Tenggat: ${TaskStore.format(t.deadlineMillis)}", 12f, UiKit.MUTED),
            UiKit.params(this, 0, UiKit.WRAP, start = 6, weight = 1f)
        )
        foot.addView(UiKit.tv(this, "$submitted pelajar mengumpulkan", 12f, UiKit.NAVY, true))
        card.addView(foot)
        return card
    }

    private fun buatTugas() {
        val classes = TeacherClassStore.all(this)
        if (classes.isEmpty()) {
            UiKit.toast(this, "Buat kelas terlebih dahulu")
        } else if (classes.size == 1) {
            openTambah(classes[0])
        } else {
            AlertDialog.Builder(this)
                .setTitle("Pilih kelas")
                .setItems(classes.map { it.name }.toTypedArray()) { _, i -> openTambah(classes[i]) }
                .show()
        }
    }

    private fun openTambah(c: TeacherClass) {
        val next = UiKit.withProfile(Intent(this, TambahKontenActivity::class.java), role, mode)
        next.putExtra(TambahKontenActivity.EXTRA_CODE, c.code)
        next.putExtra(TambahKontenActivity.EXTRA_TYPE, TambahKontenActivity.TYPE_TUGAS)
        startActivity(next)
    }

    companion object {
        private const val STATE_FILTER = "state_tugas_filter"
        private const val FILTER_SEMUA = "Semua"
        private const val FILTER_AKTIF = "Berlangsung"
        private const val FILTER_SELESAI = "Selesai"
    }
}
