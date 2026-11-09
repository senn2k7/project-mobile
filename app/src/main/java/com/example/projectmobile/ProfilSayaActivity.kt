package com.example.snapchatsphlasscreen

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Halaman "Profil Saya": ringkasan data diri dan tombol Edit Profil. */
class ProfilSayaActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
    }

    // Dibangun ulang saat kembali dari Edit Profil agar data terbaru tampil.
    override fun onResume() {
        super.onResume()
        setContentView(if (ProfileStore.isTeacher(role)) buildTeacher() else buildStudent())
    }

    private fun openEdit() = UiKit.open(this, EditProfilActivity::class.java, role, mode)

    private fun value(field: String) = ProfileStore.get(this, role, field)

    // ---------------------------------------------------------------- Pelajar

    private fun buildStudent(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Profil Saya") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        val head = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        head.addView(UiKit.avatar(this, 96, withCamera = true).view)
        head.addView(
            UiKit.tv(this, value(ProfileStore.NAMA), 24f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 16)
        )
        head.addView(
            UiKit.tv(this, value(ProfileStore.EMAIL), 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
        head.addView(
            UiKit.tv(this, ProfileStore.roleLabel(role, mode), 13f, UiKit.PRIMARY),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))

        content.addView(
            UiKit.tv(this, "Informasi Pribadi", 18f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 28, bottom = 4)
        )

        for ((label, field) in ProfileStore.fieldsFor(role, mode)) {
            val shown = value(field).ifBlank { "-" }
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, px(10), 0, px(10))
                addView(UiKit.tv(this@ProfilSayaActivity, label, 12.5f, UiKit.MUTED))
                addView(
                    UiKit.tv(this@ProfilSayaActivity, shown, 15f, UiKit.NAVY),
                    UiKit.params(this@ProfilSayaActivity, UiKit.WRAP, UiKit.WRAP, top = 4)
                )
                // Menekan "Institusi" membuka detail institusi
                if (field == ProfileStore.INSTITUSI) {
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        UiKit.open(this@ProfilSayaActivity, InformasiInstitusiActivity::class.java, role, mode)
                    }
                }
            }
            content.addView(row)
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, UiKit.outlineButton(this, "Edit Profil") { openEdit() }))
        return root
    }

    // ---------------------------------------------------------------- Pengajar

    private fun buildTeacher(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Profil Saya") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        val headCard = UiKit.card(this).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(px(16), px(24), px(16), px(24))
        }
        headCard.addView(UiKit.avatar(this, 84, withCamera = true).view)
        headCard.addView(
            UiKit.tv(this, value(ProfileStore.NAMA), 20f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14)
        )
        headCard.addView(
            UiKit.tv(this, value(ProfileStore.EMAIL), 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
        headCard.addView(
            UiKit.tv(this, "Pengajar", 12f, UiKit.PRIMARY, true).apply {
                background = UiKit.rounded(this@ProfilSayaActivity, UiKit.SOFT, 12)
                setPadding(px(14), px(4), px(14), px(4))
            },
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 10)
        )
        content.addView(headCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4))

        content.addView(
            UiKit.tv(this, "Informasi Pribadi", 18f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 24, bottom = 12)
        )

        val table = UiKit.card(this)
        val fields = ProfileStore.fieldsFor(role, mode)
        fields.forEachIndexed { index, (label, field) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(px(16), px(14), px(16), px(14))
                addView(
                    UiKit.tv(this@ProfilSayaActivity, label, 13f, UiKit.MUTED),
                    UiKit.params(this@ProfilSayaActivity, px(110), UiKit.WRAP)
                )
                addView(
                    UiKit.tv(this@ProfilSayaActivity, value(field).ifBlank { "-" }, 14f, UiKit.NAVY),
                    UiKit.params(this@ProfilSayaActivity, 0, UiKit.WRAP, start = 8, weight = 1f)
                )
            }
            table.addView(row)
            if (index < fields.lastIndex) table.addView(UiKit.divider(this))
        }
        content.addView(table)

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, UiKit.primaryButton(this, "Edit Profil") { openEdit() }))
        return root
    }
}
