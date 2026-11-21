package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailKuisPelajarInstitusiActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KUIS_ID = "extra_kuis_id"
        const val EXTRA_QUIZ_TITLE = "extra_quiz_title"
        const val EXTRA_MEETING = "extra_meeting"
    }

    private var kuisId: String = ""
    private var quizTitle: String = ""
    private var meetingText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        kuisId = intent.getStringExtra(EXTRA_KUIS_ID) ?: "k1"
        quizTitle = intent.getStringExtra(EXTRA_QUIZ_TITLE) ?: "Kuis Sistem Cerdas"
        meetingText = intent.getStringExtra(EXTRA_MEETING) ?: "Pertemuan 2"

        if (kuisId.isBlank()) {
            Toast.makeText(this, "Kuis tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_detail_kuis_pelajar_institusi)

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {}

        findViewById<TextView>(R.id.tvQuizTitle).text = quizTitle
        findViewById<TextView>(R.id.tvMeeting).text = meetingText
        findViewById<TextView>(R.id.tvDurasi).text = "20 menit"
        findViewById<TextView>(R.id.tvNilaiMinimal).text = "Nilai Min: 70"
        findViewById<TextView>(R.id.tvInstruksi).text = "1. Kerjakan soal pilihan ganda berikut dengan teliti.\n2. Waktu pengerjaan adalah 20 menit.\n3. Nilai minimal kelulusan adalah 70."
    }

    private fun setupListeners() {
        findViewById<TextView>(R.id.btnMulaiKuis).setOnClickListener {
            val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
            val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
            val intent = UiKit.withProfile(Intent(this, MulaiKuisPelajarActivity::class.java), role, mode).apply {
                putExtra(MulaiKuisPelajarActivity.EXTRA_KUIS_ID, kuisId)
                putExtra(MulaiKuisPelajarActivity.EXTRA_QUIZ_TITLE, quizTitle)
            }
            startActivity(intent)
            finish()
        }
    }
}
