package com.example.snapchatsphlasscreen

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

private data class MenuItem(val icon: Int, val title: String, val action: () -> Unit)

/** Menu profil: versi pelajar (kartu menu) dan versi pengajar (dengan bottom navigation). */
class ProfilMenuActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
    }

    override fun onResume() {
        super.onResume()
        setContentView(if (ProfileStore.isTeacher(role)) buildTeacher() else buildStudent())
    }

    private fun go(target: Class<*>) = UiKit.open(this, target, role, mode)

    /** Daftar menu bersama: (ikon, judul, tujuan). */
    private fun menuItems(): List<MenuItem> {
        val items = mutableListOf<MenuItem>()
        items.add(MenuItem(R.drawable.ic_person, "Profil Saya") { go(ProfilSayaActivity::class.java) })
        if (ProfileStore.isTeacher(role) && mode == ModePembelajaranActivity.MODE_INSTITUSI) {
            items.add(MenuItem(R.drawable.ic_institution, "Informasi Institusi") { go(InformasiInstitusiActivity::class.java) })
        }
        items.add(MenuItem(R.drawable.ic_sun, "Tampilan") { go(TampilanActivity::class.java) })
        items.add(MenuItem(R.drawable.ic_shield, "Keamanan") { go(KeamananActivity::class.java) })
        items.add(MenuItem(R.drawable.ic_bell, "Pengaturan Notifikasi") { go(NotifikasiActivity::class.java) })
        if (!ProfileStore.isTeacher(role)) {
            items.add(MenuItem(R.drawable.ic_book, "Mode Pembelajaran") {
                startActivity(android.content.Intent(this, ModePembelajaranActivity::class.java))
            })
        }
        items.add(MenuItem(R.drawable.ic_headset, "Bantuan & Dukungan") { go(BantuanActivity::class.java) })
        items.add(MenuItem(R.drawable.ic_description, "Kebijakan Privasi") { go(KebijakanPrivasiActivity::class.java) })
        items.add(MenuItem(R.drawable.ic_info, "Tentang TemanStudi") { go(TentangActivity::class.java) })
        return items
    }

    // ---------------------------------------------------------------- Pelajar

    private fun buildStudent(): View {
        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padH = 20, padTop = 20)

        val card = UiKit.card(this).apply { setPadding(px(8), px(8), px(8), px(12)) }

        // Header: avatar, nama, email, peran, tombol tutup
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            setPadding(px(8), px(8), px(8), px(8))
        }
        val avatar = UiKit.avatar(this, 56, withCamera = true)
        header.addView(avatar.view)

        val info = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        info.addView(UiKit.tv(this, ProfileStore.get(this, role, ProfileStore.NAMA), 18f, UiKit.NAVY, true))
        info.addView(
            UiKit.tv(this, ProfileStore.get(this, role, ProfileStore.EMAIL), 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        info.addView(
            UiKit.tv(this, ProfileStore.roleLabel(role, mode), 12f, UiKit.PRIMARY),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        header.addView(info, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))

        val close = ImageView(this).apply {
            setImageResource(R.drawable.ic_close)
            setColorFilter(UiKit.MUTED)
            contentDescription = "Tutup"
            isClickable = true
            isFocusable = true
            setOnClickListener { finish() }
        }
        header.addView(close, LinearLayout.LayoutParams(px(28), px(28)))
        card.addView(header)

        for ((iconRes, title, action) in menuItems()) {
            card.addView(
                UiKit.menuRow(this, iconRes, title, onClick = action),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4)
            )
        }

        card.addView(
            UiKit.dangerButton(this, "Keluar") { UiKit.showLogoutDialog(this, role, mode) },
            UiKit.params(this, UiKit.MATCH, px(52), top = 16, start = 8, end = 8)
        )

        content.addView(card)
        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    // ---------------------------------------------------------------- Pengajar

    private fun buildTeacher(): View {
        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padH = 20, padTop = 16)

        // Bar atas: logo + nama aplikasi, lonceng, profil
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.splash_logo)
                contentDescription = "Logo TemanStudi"
            },
            LinearLayout.LayoutParams(px(32), px(32))
        )
        top.addView(
            UiKit.tv(this, "TemanStudi", 22f, UiKit.NAVY, true),
            UiKit.params(this, 0, UiKit.WRAP, start = 8, weight = 1f)
        )
        top.addView(
            UiKit.icon(this, R.drawable.ic_bell, 24, UiKit.PRIMARY).apply {
                isClickable = true
                isFocusable = true
                setOnClickListener { UiKit.toast(this@ProfilMenuActivity, "Belum ada notifikasi baru") }
            },
            LinearLayout.LayoutParams(px(24), px(24))
        )
        top.addView(
            UiKit.icon(this, R.drawable.ic_person, 24, UiKit.PRIMARY).apply {
                isClickable = true
                isFocusable = true
                setOnClickListener { go(ProfilSayaActivity::class.java) }
            },
            UiKit.params(this, px(24), px(24), start = 16)
        )
        content.addView(top)

        val nama = ProfileStore.get(this, role, ProfileStore.NAMA)
        content.addView(
            UiKit.tv(this, "Halo, $nama!", 22f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 20)
        )
        content.addView(
            UiKit.tv(this, "Selamat mengajar hari ini.", 14f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )

        // Kartu profil ringkas
        val profileCard = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(16), px(16), px(16))
            isClickable = true
            isFocusable = true
            setOnClickListener { go(ProfilSayaActivity::class.java) }
        }
        profileCard.addView(UiKit.avatar(this, 64, withCamera = false).view)
        val pInfo = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        pInfo.addView(UiKit.tv(this, nama, 16f, UiKit.NAVY, true))
        pInfo.addView(
            UiKit.tv(this, ProfileStore.get(this, role, ProfileStore.EMAIL), 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        pInfo.addView(
            UiKit.tv(this, "Pengajar", 12f, UiKit.PRIMARY, true).apply {
                background = UiKit.rounded(this@ProfilMenuActivity, UiKit.SOFT, 12)
                setPadding(px(12), px(4), px(12), px(4))
            },
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8)
        )
        profileCard.addView(pInfo, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        profileCard.addView(
            UiKit.icon(this, R.drawable.ic_chevron_right, 22, UiKit.MUTED),
            LinearLayout.LayoutParams(px(22), px(22))
        )
        content.addView(profileCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 20))

        // Kartu menu dengan pemisah
        val menuCard = UiKit.card(this)
        val items = menuItems()
        items.forEachIndexed { index, (iconRes, title, action) ->
            menuCard.addView(UiKit.menuRow(this, iconRes, title, iconTint = UiKit.PRIMARY, onClick = action))
            if (index < items.lastIndex) menuCard.addView(UiKit.divider(this))
        }
        content.addView(menuCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        content.addView(
            UiKit.dangerButton(this, "Keluar") { UiKit.showLogoutDialog(this, role, mode) },
            UiKit.params(this, UiKit.MATCH, px(52), top = 16)
        )

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomNav(this, "Profil", role, mode))
        return root
    }
}
