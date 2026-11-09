package com.example.snapchatsphlasscreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Halaman Tentang TemanStudi. */
class TentangActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())
    }

    private fun versionName(): String = try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }

    private fun build(): View {
        val version = versionName()
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Tentang TemanStudi") { finish() })
        val (scroll, content) = UiKit.scroll(this, padTop = 24)

        val head = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        head.addView(UiKit.tv(this, "TemanStudi", 32f, UiKit.NAVY, true))
        head.addView(
            UiKit.tv(this, "v$version", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
        )
        head.addView(
            UiKit.tv(this, "Belajar cerdas, masa depan cerah", 14f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 12)
        )
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 24))

        content.addView(
            UiKit.tv(
                this,
                "TemanStudi adalah platform pembelajaran berbasis digital dengan dukungan kecerdasan buatan. Dirancang untuk membantu proses belajar yang lebih mudah, interaktif, dan efisien.",
                13.5f, UiKit.MUTED
            ).apply { setLineSpacing(px(3).toFloat(), 1f) },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 28)
        )

        val card = UiKit.card(this)
        card.addView(UiKit.menuRow(this, R.drawable.ic_info, "Pengembang", "Tim TemanStudi") {
            AlertDialog.Builder(this)
                .setTitle("Pengembang")
                .setMessage("Tim TemanStudi")
                .setPositiveButton("Tutup", null)
                .show()
        })
        card.addView(UiKit.divider(this))
        card.addView(UiKit.menuRow(this, R.drawable.ic_phone_android, "Versi", "Versi aplikasi $version") {
            UiKit.toast(this, "TemanStudi versi $version")
        })
        card.addView(UiKit.divider(this))
        card.addView(UiKit.menuRow(this, R.drawable.ic_globe, "Website", "www.TemanStudi.id") {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.temanstudi.id")))
            } catch (e: Exception) {
                UiKit.toast(this, "Tidak ada aplikasi untuk membuka tautan")
            }
        })
        content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 28))

        root.addView(scroll, UiKit.scrollParams())
        root.addView(
            UiKit.tv(this, "Dibuat dengan ♥ untuk pendidikan", 12f, UiKit.MUTED).apply {
                gravity = Gravity.CENTER
                setPadding(0, px(8), 0, px(20))
            },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP)
        )
        return root
    }
}