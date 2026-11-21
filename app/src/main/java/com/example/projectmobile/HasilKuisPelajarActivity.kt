package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HasilKuisPelajarActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUIZ_TITLE = "extra_quiz_title"
        const val EXTRA_NILAI = "extra_nilai"
        const val EXTRA_BENAR = "extra_benar"
        const val EXTRA_TOTAL = "extra_total"
        const val EXTRA_LULUS = "extra_lulus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val nilai = intent.getIntExtra(EXTRA_NILAI, 80)
        val benar = intent.getIntExtra(EXTRA_BENAR, 8)
        val total = intent.getIntExtra(EXTRA_TOTAL, 10)
        val lulus = intent.getBooleanExtra(EXTRA_LULUS, true)

        setContentView(R.layout.activity_hasil_kuis_pelajar)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.tvSkorBesar).text = "$benar/$total ($nilai%)"

        val tvBadge = findViewById<TextView>(R.id.tvBadgeLulus)
        tvBadge.text = if (lulus) "Lulus" else "Tidak Lulus"
        tvBadge.background = UiKit.rounded(this, if (lulus) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"), 8)

        findViewById<TextView>(R.id.tvJawabanBenar).text = "Jawaban Benar: $benar"
        findViewById<TextView>(R.id.tvJawabanSalah).text = "Jawaban Salah: ${total - benar}"

        findViewById<TextView>(R.id.btnLihatPembahasan).setOnClickListener {
            Toast.makeText(this, "Membuka pembahasan kuis (TODO)", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.btnKembaliBeranda).setOnClickListener {
            val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
            val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
            val intent = UiKit.getBerandaIntent(this, role, mode)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
