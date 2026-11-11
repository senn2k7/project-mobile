package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Daftar kelas mandiri yang dikelola pengajar. */
class KelasMandiriPengajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
    }

    override fun onResume() {
        super.onResume()
        setContentView(build())
    }

    private fun build(): View {
        val list = TeacherClassStore.all(this)
        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padTop = 24)

        content.addView(UiKit.tv(this, "Kelas Saya", 26f, UiKit.NAVY, true))
        content.addView(
            UiKit.tv(
                this,
                if (list.isEmpty()) "Kelas mandiri yang Anda kelola akan tampil di sini."
                else "${list.size} kelas mandiri yang Anda kelola",
                13f, UiKit.MUTED
            ),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 16)
        )

        if (list.isEmpty()) {
            val empty = UiKit.card(this).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(px(24), px(32), px(24), px(32))
            }
            empty.addView(UiKit.badge(this, R.drawable.ic_book, UiKit.PRIMARY, UiKit.SOFT, 64, 32, 30))
            empty.addView(
                UiKit.tv(this, "Belum Ada Kelas", 16f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 16)
            )
            empty.addView(
                UiKit.tv(this, "Buat kelas pertama Anda untuk mulai mengundang pelajar.", 13f, UiKit.MUTED)
                    .apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
            )
            content.addView(empty)
        } else {
            for (c in list) {
                content.addView(classCard(c), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 12))
            }
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(
            UiKit.bottomBar(this, UiKit.primaryButton(this, "+ Buat Kelas Baru") {
                UiKit.open(this, BuatKelasActivity::class.java, role, mode)
            })
        )
        root.addView(UiKit.bottomNav(this, "Kelas", role, mode))
        return root
    }

    private fun classCard(c: TeacherClass): View {
        val card = UiKit.card(this).apply {
            setPadding(px(16), px(16), px(16), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val next = UiKit.withProfile(Intent(this@KelasMandiriPengajarActivity, KelolaKelasActivity::class.java), role, mode)
                next.putExtra(KelolaKelasActivity.EXTRA_CODE, c.code)
                startActivity(next)
            }
        }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(this, TeacherClassStore.iconFor(c.topic), Color.WHITE, UiKit.PRIMARY, 52, 14, 26))
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, c.name, 16f, UiKit.NAVY, true))
        col.addView(ClassUi.chip(this, "Kode: ${c.code}"), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        head.addView(UiKit.icon(this, R.drawable.ic_chevron_right, 22, UiKit.MUTED), LinearLayout.LayoutParams(px(22), px(22)))
        card.addView(head)

        if (c.description.isNotBlank()) {
            card.addView(
                UiKit.tv(this, c.description, 13f, UiKit.MUTED).apply {
                    maxLines = 2
                    ellipsize = TextUtils.TruncateAt.END
                },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12)
            )
        }
        card.addView(UiKit.divider(this), UiKit.params(this, UiKit.MATCH, px(1), top = 12, bottom = 10))

        val foot = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        foot.addView(UiKit.icon(this, R.drawable.ic_people, 16, UiKit.MUTED), LinearLayout.LayoutParams(px(16), px(16)))
        foot.addView(
            UiKit.tv(this, "0 / ${c.capacity} peserta", 12.5f, UiKit.MUTED),
            UiKit.params(this, 0, UiKit.WRAP, start = 6, weight = 1f)
        )
        foot.addView(UiKit.tv(this, "Dibuat ${TeacherClassStore.formatDate(c.createdAt)}", 12.5f, UiKit.MUTED))
        card.addView(foot)
        return card
    }
}
