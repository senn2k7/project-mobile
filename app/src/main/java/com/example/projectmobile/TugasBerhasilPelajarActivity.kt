package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.projectmobile.ui.pengajar.institusi.FormatTanggal

class TugasBerhasilPelajarActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_TITLE = "extra_task_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Membuat UI Login Android"
        setContentView(R.layout.activity_tugas_berhasil_pelajar)

        findViewById<TextView>(R.id.tvJudulTugas).text = taskTitle
        findViewById<TextView>(R.id.tvWaktuKumpul).text = FormatTanggal.formatLengkap(System.currentTimeMillis())

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
