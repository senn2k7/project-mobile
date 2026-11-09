package com.example.snapchatsphlasscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.content.res.ColorStateList

/** Layar "Memproses..." saat keluar akun, lalu kembali ke halaman Login. */
class KeluarActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var successCard: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())

        // Selama memproses, tombol kembali dinonaktifkan.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { /* tidak melakukan apa-apa */ }
        })

        // Keluar dari akun dan hapus data profil lokal.
        FirebaseAuthManager.signOut()
        ProfileStore.clear(this)

        handler.postDelayed({ successCard.visibility = View.VISIBLE }, 1500L)
        handler.postDelayed({ goToLogin() }, 2800L)
    }

    private fun build(): View {
        val root = UiKit.root(this)

        val center = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        center.addView(
            FrameLayout(this).apply { background = UiKit.oval(UiKit.SOFT) },
            LinearLayout.LayoutParams(px(160), px(160))
        )
        center.addView(
            UiKit.tv(this, "Memproses...", 22f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 28)
        )
        center.addView(
            UiKit.tv(this, "Mohon tunggu sebentar", 14f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8)
        )
        val spinner = ProgressBar(this).apply {
            isIndeterminate = true
            indeterminateTintList = ColorStateList.valueOf(UiKit.PRIMARY)
        }
        center.addView(spinner, UiKit.params(this, px(40), px(40), top = 24))
        root.addView(center, LinearLayout.LayoutParams(UiKit.MATCH, 0, 1f))

        // Kartu sukses di bagian bawah
        successCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(16), px(16), px(16))
            background = UiKit.rounded(this@KeluarActivity, android.graphics.Color.WHITE, 14, UiKit.SUCCESS)
            visibility = View.INVISIBLE
            addView(
                UiKit.icon(this@KeluarActivity, R.drawable.ic_check_circle, 24, UiKit.SUCCESS),
                LinearLayout.LayoutParams(px(24), px(24))
            )
            addView(
                UiKit.tv(this@KeluarActivity, "Akun Anda Berhasil Keluar", 14f, UiKit.NAVY, true),
                UiKit.params(this@KeluarActivity, UiKit.WRAP, UiKit.WRAP, start = 12)
            )
        }
        root.addView(UiKit.bottomBar(this, successCard))
        return root
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}
