package com.example.snapchatsphlasscreen

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import android.content.res.ColorStateList

/** Halaman Tampilan: pilih tema terang, gelap, atau ikuti sistem. */
class TampilanActivity : AppCompatActivity() {

    private var selected = ThemeStore.SISTEM
    private val radios = LinkedHashMap<String, ImageView>()
    private val cards = LinkedHashMap<String, LinearLayout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selected = ThemeStore.saved(this)
        setContentView(build())
        refresh()
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Tampilan") { finish() })
        root.addView(UiKit.divider(this))
        val (scroll, content) = UiKit.scroll(this, padTop = 16)

        content.addView(UiKit.tv(this, "TEMA APLIKASI", 11.5f, UiKit.MUTED, true))

        addOption(content, ThemeStore.TERANG, "Terang", "Latar putih, nyaman di ruangan terang",
            UiKit.badge(this, R.drawable.ic_sun, Color.parseColor("#F5A623"), Color.WHITE, 40, 10, 24))
        addOption(content, ThemeStore.GELAP, "Gelap", "Hemat baterai, nyaman saat malam hari",
            UiKit.badge(this, R.drawable.ic_moon, Color.WHITE, UiKit.NAVY, 40, 10, 22))
        addOption(content, ThemeStore.SISTEM, "Ikuti Sistem", "Menyesuaikan pengaturan Android",
            UiKit.badge(this, R.drawable.ic_phone_android, UiKit.PRIMARY, Color.WHITE, 40, 10, 22))

        // Pratinjau
        content.addView(
            UiKit.tv(this, "PRATINJAU", 11.5f, UiKit.MUTED, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 20)
        )
        val preview = UiKit.card(this).apply {
            setPadding(px(14), px(14), px(14), px(14))
        }
        val previewHead = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        previewHead.addView(UiKit.badge(this, R.drawable.ic_phone_android, Color.WHITE, UiKit.PRIMARY, 40, 10, 22))
        val previewText = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        previewText.addView(UiKit.tv(this, "Pemrograman Mobile", 14f, UiKit.NAVY, true))
        previewText.addView(
            UiKit.tv(this, "Pertemuan 5 · Kamis, 29 Okt 2026", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        previewHead.addView(previewText, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        preview.addView(previewHead)
        val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = 65
            progressTintList = ColorStateList.valueOf(UiKit.PRIMARY)
            progressBackgroundTintList = ColorStateList.valueOf(UiKit.SOFT)
        }
        preview.addView(bar, UiKit.params(this, UiKit.MATCH, px(8), top = 12))
        content.addView(preview, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))

        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah: Batal dan Terapkan
        val buttons = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        buttons.addView(
            UiKit.outlineButton(this, "Batal") { finish() },
            UiKit.params(this, 0, px(56), end = 6, weight = 1f)
        )
        buttons.addView(
            UiKit.primaryButton(this, "Terapkan") { applyTheme() },
            UiKit.params(this, 0, px(56), start = 6, weight = 1.4f)
        )
        root.addView(UiKit.bottomBar(this, buttons))
        return root
    }

    private fun addOption(parent: LinearLayout, key: String, title: String, subtitle: String, iconBox: View) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(14), px(14), px(14), px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                selected = key
                refresh()
            }
        }
        // Kotak ikon bergaris tipis seperti di desain
        (iconBox as? android.widget.FrameLayout)?.let {
            if (key != ThemeStore.GELAP) it.background = UiKit.rounded(this, Color.WHITE, 10, UiKit.BORDER)
        }
        card.addView(iconBox)

        val texts = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        texts.addView(UiKit.tv(this, title, 15f, UiKit.NAVY, true))
        texts.addView(
            UiKit.tv(this, subtitle, 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        card.addView(texts, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))

        val radio = ImageView(this)
        card.addView(radio, LinearLayout.LayoutParams(px(24), px(24)))

        radios[key] = radio
        cards[key] = card
        parent.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10))
    }

    private fun refresh() {
        for ((key, card) in cards) {
            val on = key == selected
            card.background = if (on) {
                UiKit.rounded(this, UiKit.SOFT, 14, UiKit.PRIMARY, 2)
            } else {
                UiKit.rounded(this, Color.WHITE, 14, UiKit.BORDER)
            }
            radios[key]?.setImageResource(if (on) R.drawable.bg_radio_on else R.drawable.bg_radio_off)
        }
    }

    private fun applyTheme() {
        ThemeStore.save(this, selected)
        UiKit.toast(this, "Tema diterapkan")
        ThemeStore.apply(selected)
        finish()
    }
}
