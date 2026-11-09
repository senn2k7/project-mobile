package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.projectmobile.R

class ModePembelajaranActivity : AppCompatActivity() {

    // Mode default
    private var selectedMode = MODE_INSTITUSI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pengaturan status bar
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // Memanggil layout
        setContentView(R.layout.activity_mode_pembelajaran)

        // Mengatur system bar
        val root = findViewById<LinearLayout>(R.id.modeRoot)

        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                left + bars.left,
                top + bars.top,
                right + bars.right,
                bottom + bars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)

        // Menghubungkan komponen UI
        val cardInstitusi =
            findViewById<LinearLayout>(R.id.cardInstitusi)

        val cardMandiri =
            findViewById<LinearLayout>(R.id.cardMandiri)

        val btnLanjutkan =
            findViewById<TextView>(R.id.btnLanjutkan)

        // Fungsi memperbarui tampilan pilihan
        fun refreshSelection() {

            if (selectedMode == MODE_INSTITUSI) {

                cardInstitusi.setBackgroundResource(
                    R.drawable.bg_mode_selected
                )

                cardMandiri.setBackgroundResource(
                    R.drawable.bg_mode_unselected
                )

            } else {

                cardInstitusi.setBackgroundResource(
                    R.drawable.bg_mode_unselected
                )

                cardMandiri.setBackgroundResource(
                    R.drawable.bg_mode_selected
                )
            }
        }

        // Klik Mode Institusi
        cardInstitusi.setOnClickListener {

            selectedMode = MODE_INSTITUSI

            refreshSelection()
        }

        // Klik Kelas Mandiri
        cardMandiri.setOnClickListener {

            selectedMode = MODE_MANDIRI

            refreshSelection()
        }

        // Tombol Lanjutkan
        btnLanjutkan.setOnClickListener {

            val intent = Intent(
                this,
                PilihPeranActivity::class.java
            )

            intent.putExtra(
                EXTRA_MODE_PEMBELAJARAN,
                selectedMode
            )

            startActivity(intent)
        }

        // Menampilkan pilihan default
        refreshSelection()
    }

    companion object {

        const val EXTRA_MODE_PEMBELAJARAN =
            "extra_mode_pembelajaran"

        const val MODE_INSTITUSI = "institusi"

        const val MODE_MANDIRI = "mandiri"
    }
}