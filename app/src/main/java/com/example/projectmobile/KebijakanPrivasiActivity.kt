package com.example.snapchatsphlasscreen

import android.view.Gravity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Halaman Kebijakan Privasi: ringkasan komitmen dan tombol baca selengkapnya. */
class KebijakanPrivasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Kebijakan Privasi") { finish() })

        val center = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(px(28), 0, px(28), 0)
        }

        val circle = FrameLayout(this).apply { background = UiKit.oval(UiKit.SOFT) }
        circle.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_shield)
                setColorFilter(UiKit.PRIMARY)
            },
            FrameLayout.LayoutParams(px(76), px(76), Gravity.CENTER)
        )
        center.addView(circle, LinearLayout.LayoutParams(px(160), px(160)))

        center.addView(
            UiKit.tv(this, "Kami berkomitmen untuk menjaga privasi dan keamanan Anda", 20f, UiKit.NAVY, true).apply {
                gravity = Gravity.CENTER
            },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 32)
        )
        center.addView(
            UiKit.tv(
                this,
                "Kebijakan privasi ini menjelaskan bagaimana kami mengumpulkan, menggunakan, dan melindungi informasi pribadi Anda saat menggunakan layanan TemanStudi.",
                14f, UiKit.MUTED
            ).apply {
                gravity = Gravity.CENTER
                setLineSpacing(px(3).toFloat(), 1f)
            },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14)
        )

        root.addView(center, LinearLayout.LayoutParams(UiKit.MATCH, 0, 1f))
        root.addView(UiKit.bottomBar(this, UiKit.primaryButton(this, "Baca Kebijakan Privasi") { showPolicy() }))
        return root
    }

    private fun showPolicy() {
        // TODO: ganti dengan teks kebijakan privasi resmi TemanStudi.
        val body = TextView(this).apply {
            text = POLICY_TEXT
            textSize = 14f
            setTextColor(UiKit.NAVY)
            setLineSpacing(px(3).toFloat(), 1f)
            setPadding(px(24), px(8), px(24), px(8))
        }
        val sv = ScrollView(this).apply { addView(body) }
        AlertDialog.Builder(this)
            .setTitle("Kebijakan Privasi")
            .setView(sv)
            .setPositiveButton("Mengerti", null)
            .show()
    }

    companion object {
        private const val POLICY_TEXT =
            "1. Data yang kami kumpulkan\n" +
            "Nama, email, nomor telepon, institusi, dan aktivitas belajar Anda di aplikasi.\n\n" +
            "2. Penggunaan data\n" +
            "Data digunakan untuk menyediakan kelas, tugas, kuis, presensi, dan fitur asisten AI.\n\n" +
            "3. Keamanan\n" +
            "Kami menjaga data Anda dengan kontrol akses dan tidak membagikannya kepada pihak lain tanpa izin.\n\n" +
            "4. Hak Anda\n" +
            "Anda dapat memperbarui atau meminta penghapusan data melalui menu Bantuan & Dukungan."
    }
}
