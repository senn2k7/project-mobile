package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Halaman "Kelas Saya" untuk pelajar: pencarian, filter jenis kelas, dan daftar kelas. */
class DaftarKelasActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    private var filter = "Semua"
    private var query = ""
    private var ascending = true

    private lateinit var listContainer: LinearLayout
    private val chipViews = LinkedHashMap<String, android.widget.TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
    }

    // Dibangun ulang saat kembali agar kelas baru / progres terbaru tampil.
    override fun onResume() {
        super.onResume()
        setContentView(build())
        renderList()
    }

    private fun build(): View {
        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padTop = 20)

        content.addView(UiKit.tv(this, "Kelas Saya", 26f, UiKit.NAVY, true))

        // Pencarian + tombol urutkan
        val searchRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val et = UiKit.input(this, "Cari kelas...", query, android.text.InputType.TYPE_CLASS_TEXT, R.drawable.ic_search)
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                query = s?.toString()?.trim().orEmpty()
                renderList()
            }
        })
        searchRow.addView(et, UiKit.params(this, 0, px(52), weight = 1f))
        val sortBtn = FrameLayout(this).apply {
            background = UiKit.rounded(this@DaftarKelasActivity, Color.WHITE, 12, UiKit.BORDER)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                ascending = !ascending
                UiKit.toast(this@DaftarKelasActivity, if (ascending) "Diurutkan A–Z" else "Diurutkan Z–A")
                renderList()
            }
            addView(
                UiKit.icon(this@DaftarKelasActivity, R.drawable.ic_filter, 24, UiKit.PRIMARY),
                FrameLayout.LayoutParams(px(24), px(24), Gravity.CENTER)
            )
        }
        searchRow.addView(sortBtn, UiKit.params(this, px(52), px(52), start = 10))
        content.addView(searchRow, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Chip filter
        val chips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        chipViews.clear()
        for (label in listOf("Semua", "Institusi", "Mandiri")) {
            val chip = UiKit.tv(this, label, 13.5f, UiKit.MUTED, true).apply {
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
        content.addView(chips, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))
        refreshChips()

        listContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(listContainer, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomNav(this, "Kelas", role, mode))
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

        var items = ClassStore.classesFor(this, mode)
        if (filter != "Semua") items = items.filter { it.type == filter }
        if (query.isNotEmpty()) {
            items = items.filter {
                it.name.contains(query, ignoreCase = true) || it.teacher.contains(query, ignoreCase = true)
            }
        }
        items = if (ascending) items.sortedBy { it.name } else items.sortedByDescending { it.name }

        if (items.isEmpty()) {
            val empty = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(px(16), px(48), px(16), px(16))
            }
            empty.addView(
                UiKit.tv(this, "Belum ada kelas", 16f, UiKit.NAVY, true).apply { gravity = Gravity.CENTER }
            )
            empty.addView(
                UiKit.tv(this, "Kelas yang kamu ikuti akan tampil di sini.", 13f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6)
            )
            if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
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

        for (info in items) {
            listContainer.addView(
                ClassUi.listCard(this, info) { openClass(info) },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 12)
            )
        }
    }

    private fun openClass(info: ClassInfo) {
        val intent = UiKit.withProfile(
            android.content.Intent(this, DetailKelasPelajarActivity::class.java), role, mode
        ).apply { putExtra(DetailKelasPelajarActivity.EXTRA_CODE, info.code) }
        startActivity(intent)
    }
}
