package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity

/** Daftar kelas mandiri yang dikelola pengajar (pencarian, urutan, dan kartu ringkas sesuai Figma). */
class KelasMandiriPengajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    private var query = ""
    private var newestFirst = true
    private var listBox: LinearLayout? = null
    private var countLabel: android.widget.TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
    }

    override fun onResume() {
        super.onResume()
        setContentView(build())
        renderList()
    }

    private fun openBuatKelas() = UiKit.open(this, BuatKelasActivity::class.java, role, mode)

    private fun build(): View {
        val root = UiKit.root(this)

        // Bar atas: kembali, judul, menu
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(20), px(16), px(20), px(8))
        }
        bar.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_back)
                contentDescription = "Kembali"
                isClickable = true
                isFocusable = true
                setOnClickListener { finish() }
            },
            LinearLayout.LayoutParams(px(32), px(32))
        )
        bar.addView(
            UiKit.tv(this, "Kelas Saya", 20f, UiKit.NAVY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 16, weight = 1f)
        )
        val more = UiKit.icon(this, R.drawable.ic_more_vert, 24, UiKit.NAVY).apply {
            isClickable = true
            isFocusable = true
        }
        more.setOnClickListener { anchor ->
            val popup = PopupMenu(this, anchor)
            popup.menu.add(0, 1, 0, "Buat kelas baru")
            popup.setOnMenuItemClickListener { openBuatKelas(); true }
            popup.show()
        }
        bar.addView(more, LinearLayout.LayoutParams(px(24), px(24)))
        root.addView(bar)

        val (scroll, content) = UiKit.scroll(this)

        // Pencarian + tombol urutan
        val searchRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val et = UiKit.input(this, "Cari kelas...", query, InputType.TYPE_CLASS_TEXT, R.drawable.ic_search)
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
            background = UiKit.rounded(this@KelasMandiriPengajarActivity, Color.WHITE, 12, UiKit.BORDER)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                newestFirst = !newestFirst
                UiKit.toast(
                    this@KelasMandiriPengajarActivity,
                    if (newestFirst) "Urutan: terbaru" else "Urutan: A–Z"
                )
                renderList()
            }
            addView(
                UiKit.icon(this@KelasMandiriPengajarActivity, R.drawable.ic_filter, 24, UiKit.PRIMARY),
                FrameLayout.LayoutParams(px(24), px(24), Gravity.CENTER)
            )
        }
        searchRow.addView(sortBtn, UiKit.params(this, px(52), px(52), start = 10))
        content.addView(searchRow, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4))

        val count = UiKit.tv(this, "", 12.5f, UiKit.MUTED)
        countLabel = count
        content.addView(count, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14, bottom = 8))

        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        listBox = box
        content.addView(box)

        root.addView(scroll, UiKit.scrollParams())
        root.addView(
            UiKit.bottomBar(this, UiKit.primaryButton(this, "+ Buat Kelas Baru") { openBuatKelas() })
        )
        root.addView(UiKit.bottomNav(this, "Kelas", role, mode))
        return root
    }

    private fun renderList() {
        val box = listBox ?: return
        box.removeAllViews()

        val all = TeacherClassStore.all(this)
        var items = all
        if (query.isNotEmpty()) {
            items = items.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.code.contains(query, ignoreCase = true) ||
                    it.topic.contains(query, ignoreCase = true)
            }
        }
        items = if (newestFirst) items.sortedByDescending { it.createdAt } else items.sortedBy { it.name.lowercase() }

        countLabel?.text = when {
            all.isEmpty() -> "Kelas mandiri yang Anda kelola akan tampil di sini."
            query.isNotEmpty() -> "${items.size} dari ${all.size} kelas"
            else -> "${all.size} kelas mandiri yang Anda kelola"
        }

        if (items.isEmpty()) {
            val empty = UiKit.card(this).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(px(24), px(32), px(24), px(32))
            }
            empty.addView(UiKit.badge(this, R.drawable.ic_book, UiKit.PRIMARY, UiKit.SOFT, 64, 32, 30))
            empty.addView(
                UiKit.tv(this, if (all.isEmpty()) "Belum Ada Kelas" else "Kelas tidak ditemukan", 16f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 16)
            )
            empty.addView(
                UiKit.tv(
                    this,
                    if (all.isEmpty()) "Buat kelas pertama Anda untuk mulai mengundang pelajar."
                    else "Coba kata kunci lain.",
                    13f, UiKit.MUTED
                ).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6)
            )
            box.addView(empty)
            return
        }

        for (c in items) {
            box.addView(classCard(c), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 12))
        }
    }

    private fun openKelola(c: TeacherClass) {
        val next = UiKit.withProfile(Intent(this, KelolaKelasActivity::class.java), role, mode)
        next.putExtra(KelolaKelasActivity.EXTRA_CODE, c.code)
        startActivity(next)
    }

    private fun classCard(c: TeacherClass): View {
        val card = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(14), px(14), px(8), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { openKelola(c) }
        }
        card.addView(UiKit.badge(this, TeacherClassStore.iconFor(c.topic), Color.WHITE, UiKit.PRIMARY, 48, 12, 26))

        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, c.name, 15f, UiKit.NAVY, true).apply {
            maxLines = 2
            ellipsize = TextUtils.TruncateAt.END
        })
        col.addView(
            UiKit.tv(this, "Kode: ${c.code} • ${c.topic}", 12f, UiKit.MUTED).apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 3)
        )
        col.addView(
            UiKit.tv(this, "${TeacherClassStore.participantCount(this, c.code)} pelajar", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3)
        )
        card.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))

        // Menu titik tiga (area sentuh 40dp supaya mudah ditekan)
        val menu = FrameLayout(this).apply {
            isClickable = true
            isFocusable = true
            setOnClickListener { anchor ->
                val popup = PopupMenu(this@KelasMandiriPengajarActivity, anchor)
                popup.menu.add(0, 1, 0, "Kelola kelas")
                popup.menu.add(0, 2, 1, "Bagikan kode")
                popup.menu.add(0, 3, 2, "Salin kode")
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        1 -> openKelola(c)
                        2 -> TeacherClassStore.share(this@KelasMandiriPengajarActivity, c)
                        3 -> TeacherClassStore.copyCode(this@KelasMandiriPengajarActivity, c)
                    }
                    true
                }
                popup.show()
            }
            addView(
                UiKit.icon(this@KelasMandiriPengajarActivity, R.drawable.ic_more_vert, 22, UiKit.MUTED),
                FrameLayout.LayoutParams(px(22), px(22), Gravity.CENTER)
            )
        }
        card.addView(menu, LinearLayout.LayoutParams(px(40), px(40)))
        return card
    }
}
