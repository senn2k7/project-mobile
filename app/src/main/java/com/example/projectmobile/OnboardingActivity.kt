package com.example.snapchatsphlasscreen

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class OnboardingActivity : AppCompatActivity() {

    private data class OnboardingPage(
        val illustrationRes: Int,
        val title: String,
        val description: String
    )

    private val pages = listOf(
        OnboardingPage(
            illustrationRes = R.drawable.onboarding_illustration,
            title = "Semua Kebutuhan Belajar dalam Satu Aplikasi",
            description = "Akses kelas, materi, tugas, kuis, ujian, presensi, diskusi dan nilai dalam satu platform yang mudah digunakan."
        ),
        OnboardingPage(
            illustrationRes = R.drawable.onboarding_illustration_2,
            title = "Kelola Kelas, Tugas, dan Nilai dengan Mudah",
            description = "Atur kelas, unggah materi, buat tugas, kelola presensi, dan pantau hasil belajar secara terorganisir."
        ),
        OnboardingPage(
            illustrationRes = R.drawable.onboarding_illustration_3,
            title = "Belajar Lebih Pintar Bersama AI",
            description = "Dapatkan penjelasan materi, rangkuman, contoh soal, dan latihan melalui chatbot AI yang selalu siap membantu."
        )
    )

    private var currentIndex = 0

    private lateinit var imgIllustration: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var btnLanjut: Button
    private lateinit var btnLewati: TextView

    private lateinit var indicator1: View
    private lateinit var indicator2: View
    private lateinit var indicator3: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        setContentView(R.layout.activity_onboarding)

        val rootView = findViewById<View>(R.id.onboardingRoot)

        // Simpan padding awal agar tidak bertambah setiap insets diterapkan.
        val initialLeft = rootView.paddingLeft
        val initialTop = rootView.paddingTop
        val initialRight = rootView.paddingRight
        val initialBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(rootView)

        val illustrationArea = findViewById<FrameLayout>(
            R.id.illustrationArea
        )

        imgIllustration = illustrationArea.getChildAt(0) as ImageView
        txtTitle = findViewById(R.id.txtOnboardingTitle)
        txtDescription = findViewById(R.id.txtOnboardingDescription)
        btnLanjut = findViewById(R.id.btnLanjut)
        btnLewati = findViewById(R.id.btnLewati)

        val indicatorsContainer = findViewById<LinearLayout>(
            R.id.pageIndicators
        )

        indicator1 = indicatorsContainer.getChildAt(0)
        indicator2 = indicatorsContainer.getChildAt(1)
        indicator3 = indicatorsContainer.getChildAt(2)

        updatePage()

        btnLanjut.setOnClickListener {
            if (currentIndex < pages.size - 1) {
                currentIndex++
                updatePage()
            } else {
                finishOnboarding()
            }
        }

        btnLewati.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updatePage() {
        val page = pages[currentIndex]

        imgIllustration.setImageResource(page.illustrationRes)
        txtTitle.text = page.title
        txtDescription.text = page.description

        btnLanjut.text = if (currentIndex == pages.size - 1) {
            "Mulai Sekarang"
        } else {
            "Lanjut"
        }

        indicator1.setBackgroundResource(
            if (currentIndex == 0) {
                R.drawable.indicator_active
            } else {
                R.drawable.indicator_inactive
            }
        )

        indicator2.setBackgroundResource(
            if (currentIndex == 1) {
                R.drawable.indicator_active
            } else {
                R.drawable.indicator_inactive
            }
        )

        indicator3.setBackgroundResource(
            if (currentIndex == 2) {
                R.drawable.indicator_active
            } else {
                R.drawable.indicator_inactive
            }
        )
    }

    private fun finishOnboarding() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
