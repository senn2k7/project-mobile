package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Konfirmasi setelah tugas berhasil dikumpulkan. */
class TugasBerhasilActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        setContentView(build())
    }

    private fun detailRow(iconRes: Int, label: String, value: String): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, px(8), 0, px(8))
            addView(UiKit.icon(this@TugasBerhasilActivity, iconRes, 24, UiKit.MUTED), LinearLayout.LayoutParams(px(24), px(24)))
            val col = LinearLayout(this@TugasBerhasilActivity).apply { orientation = LinearLayout.VERTICAL }
            col.addView(UiKit.tv(this@TugasBerhasilActivity, label, 12.5f, UiKit.MUTED))
            col.addView(
                UiKit.tv(this@TugasBerhasilActivity, value, 15f, UiKit.NAVY, true),
                UiKit.params(this@TugasBerhasilActivity, UiKit.WRAP, UiKit.WRAP, top = 3)
            )
            addView(col, UiKit.params(this@TugasBerhasilActivity, UiKit.WRAP, UiKit.WRAP, start = 14))
        }

    private fun build(): View {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Tugas"
        val time = intent.getLongExtra(EXTRA_TIME, System.currentTimeMillis())

        val root = UiKit.root(this)
        val (scroll, content) = UiKit.scroll(this, padTop = 48)

        val circle = FrameLayout(this).apply { background = UiKit.oval(UiKit.SUCCESS) }
        circle.addView(
            UiKit.tv(this, "✓", 56f, Color.WHITE, true).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
        )
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        top.addView(circle, LinearLayout.LayoutParams(px(96), px(96)))
        top.addView(
            UiKit.tv(this, "Tugas Berhasil Dikumpulkan", 22f, UiKit.NAVY, true).apply { gravity = Gravity.CENTER },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 28)
        )
        top.addView(
            UiKit.tv(this, "Tugas kamu telah berhasil dikumpulkan.", 14f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10)
        )
        content.addView(top)

        val card = UiKit.card(this).apply { setPadding(px(18), px(14), px(18), px(14)) }
        card.addView(detailRow(R.drawable.ic_assignment_check, "Judul Tugas", title))
        card.addView(detailRow(R.drawable.ic_calendar, "Waktu Pengumpulan", TaskStore.format(time)))
        content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 28))

        content.addView(
            UiKit.primaryButton(this, "Kembali ke Beranda") { goHome() },
            UiKit.params(this, UiKit.MATCH, px(56), top = 24)
        )

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun goHome() {
        val intent = UiKit.withProfile(Intent(this, HomeActivity::class.java), role, mode).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_TITLE = "extra_tugas_berhasil_title"
        const val EXTRA_TIME = "extra_tugas_berhasil_time"
    }
}
