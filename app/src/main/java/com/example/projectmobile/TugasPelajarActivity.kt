package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Daftar tugas untuk pelajar kelas mandiri, dengan filter Semua / Belum Dikerjakan / Selesai. */
class TugasPelajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var classCode: String? = null

    private var filter = FILTER_SEMUA
    private lateinit var listContainer: LinearLayout
    private val chipViews = LinkedHashMap<String, TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        classCode = intent.getStringExtra(EXTRA_CODE)
    }

    // Dibangun ulang saat kembali dari Detail Tugas agar status terbaru tampil.
    override fun onResume() {
        super.onResume()
        setContentView(build())
        renderList()
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Tugas") { finish() })
        val (scroll, content) = UiKit.scroll(this, padTop = 12)

        val chips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        chipViews.clear()
        for (label in listOf(FILTER_SEMUA, FILTER_BELUM, FILTER_SELESAI)) {
            val chip = UiKit.tv(this, label, 13f, UiKit.MUTED, true).apply {
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    filter = label
                    refreshChips()
                    renderList()
                }
            }
            chipViews[label] = chip
            chips.addView(chip, UiKit.params(this, 0, px(40), end = 8, weight = 1f))
        }
        content.addView(chips)
        refreshChips()

        listContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(listContainer, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun refreshChips() {
        for ((label, chip) in chipViews) {
            val on = label == filter
            chip.background = UiKit.rounded(this, if (on) UiKit.PRIMARY else UiKit.DISABLED, 12)
            chip.setTextColor(if (on) Color.WHITE else UiKit.MUTED)
        }
    }

    private fun renderList() {
        if (!::listContainer.isInitialized) return
        listContainer.removeAllViews()

        var items = TaskStore.tasks(this, classCode)
        items = when (filter) {
            FILTER_BELUM -> items.filter { !TaskStore.isDone(this, it) }
            FILTER_SELESAI -> items.filter { TaskStore.isDone(this, it) }
            else -> items
        }

        if (items.isEmpty()) {
            val empty = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(px(16), px(56), px(16), px(16))
            }
            empty.addView(UiKit.tv(this, "Belum ada tugas", 16f, UiKit.NAVY, true).apply { gravity = Gravity.CENTER })
            val hint = if (ClassStore.joined(this).isEmpty()) {
                "Gabung kelas dengan kode dari pengajar untuk melihat tugas."
            } else {
                "Tugas pada kategori ini akan tampil di sini."
            }
            empty.addView(
                UiKit.tv(this, hint, 13f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6)
            )
            if (ClassStore.joined(this).isEmpty()) {
                empty.addView(
                    UiKit.primaryButton(this, "Gabung Kelas") {
                        UiKit.open(this, GabungKelasActivity::class.java, role, mode)
                    },
                    UiKit.params(this, UiKit.MATCH, px(52), top = 20)
                )
            }
            listContainer.addView(empty)
            return
        }

        for (task in items) {
            listContainer.addView(taskCard(task), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 12))
        }
    }

    private fun statusChip(status: String, score: Int?): TextView = when (status) {
        TaskStore.STATUS_DINILAI -> ClassUi.chip(this, "Dinilai (${score ?: 0})", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)
        TaskStore.STATUS_MENUNGGU -> ClassUi.chip(this, status, Color.parseColor("#D98200"), Color.parseColor("#FFF1D6"))
        else -> ClassUi.chip(this, status, Color.parseColor("#E5484D"), UiKit.DANGER_SOFT)
    }

    private fun taskCard(task: TaskInfo): View {
        val status = TaskStore.statusOf(this, task)
        val score = TaskStore.submission(this, task.id)?.score

        val card = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            setPadding(px(16), px(16), px(12), px(16))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val detail = UiKit.withProfile(Intent(this@TugasPelajarActivity, DetailTugasPelajarActivity::class.java), role, mode)
                detail.putExtra(DetailTugasPelajarActivity.EXTRA_TASK_ID, task.id)
                startActivity(detail)
            }
        }
        card.addView(UiKit.badge(this, R.drawable.ic_description, Color.WHITE, UiKit.PRIMARY, 48, 12, 26))

        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, task.title, 15f, UiKit.NAVY, true))
        col.addView(UiKit.tv(this, task.className, 12.5f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4))

        val time = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        time.addView(UiKit.icon(this, R.drawable.ic_clock, 13, UiKit.MUTED), LinearLayout.LayoutParams(px(13), px(13)))
        time.addView(
            UiKit.tv(this, TaskStore.format(task.deadlineMillis), 11.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 6)
        )
        col.addView(time, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        col.addView(statusChip(status, score), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 10))
        card.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        card.addView(
            UiKit.icon(this, R.drawable.ic_more_vert, 22, UiKit.MUTED),
            LinearLayout.LayoutParams(px(22), px(22))
        )
        return card
    }

    companion object {
        const val EXTRA_CODE = "extra_tugas_class_code"
        private const val FILTER_SEMUA = "Semua"
        private const val FILTER_BELUM = "Belum Dikerjakan"
        private const val FILTER_SELESAI = "Selesai"
    }
}
