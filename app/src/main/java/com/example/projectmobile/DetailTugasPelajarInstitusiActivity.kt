package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.projectmobile.ui.pengajar.institusi.FileLampiranInput
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import com.example.projectmobile.ui.pengajar.institusi.PengumpulanRepository
import kotlinx.coroutines.launch

class DetailTugasPelajarInstitusiActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TUGAS_ID = "extra_tugas_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_COURSE_NAME = "extra_course_name"
    }

    private val pengumpulanRepo: PengumpulanRepository = PengajarRepositoryProvider.providePengumpulanRepo()

    private lateinit var tvJudulTugas: TextView
    private lateinit var tvMataKuliah: TextView
    private lateinit var tvBatasTenggat: TextView
    private lateinit var tvInstruksi: TextView
    private lateinit var etJawaban: EditText
    private lateinit var btnLampirkanFile: TextView
    private lateinit var btnKumpulkanTugas: TextView
    private lateinit var tvFileInfo: TextView

    private var tugasId: String = ""
    private var taskTitle: String = ""
    private var attachedFile: FileLampiranInput? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        tugasId = intent.getStringExtra(EXTRA_TUGAS_ID) ?: ""
        taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Membuat UI Login Android"
        val courseName = intent.getStringExtra(EXTRA_COURSE_NAME) ?: "Pemrograman Mobile"

        if (tugasId.isBlank()) {
            Toast.makeText(this, "Tugas tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_detail_tugas_pelajar_institusi)

        bindViews(taskTitle, courseName)
        setupListeners()
    }

    private fun bindViews(taskTitle: String, courseName: String) {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {}

        tvJudulTugas = findViewById(R.id.tvJudulTugas)
        tvMataKuliah = findViewById(R.id.tvMataKuliah)
        tvBatasTenggat = findViewById(R.id.tvBatasTenggat)
        tvInstruksi = findViewById(R.id.tvInstruksi)
        etJawaban = findViewById(R.id.etJawaban)
        btnLampirkanFile = findViewById(R.id.btnLampirkanFile)
        btnKumpulkanTugas = findViewById(R.id.btnKumpulkanTugas)
        tvFileInfo = findViewById(R.id.tvFileInfo)

        tvJudulTugas.text = taskTitle
        tvMataKuliah.text = courseName
        tvBatasTenggat.text = "12 Jun 2025, 23:59"
        tvInstruksi.text = "1. Buat tampilan UI Login sesuai dengan desain.\n2. Gunakan Kotlin dan XML/Jetpack Compose.\n3. Sertakan validasi input dan error message."
    }

    private fun setupListeners() {
        btnLampirkanFile.setOnClickListener {
            attachedFile = FileLampiranInput(
                uriString = "content://file_sample.pdf",
                nama = "Jawaban_Tugas_$taskTitle.pdf",
                tipeFile = "application/pdf",
                ukuranByte = 1500000L
            )
            tvFileInfo.visibility = View.VISIBLE
            tvFileInfo.text = "Terlampir: ${attachedFile?.nama}"
            Toast.makeText(this, "File berhasil dilampirkan", Toast.LENGTH_SHORT).show()
        }

        btnKumpulkanTugas.setOnClickListener {
            val jawabanText = etJawaban.text.toString().trim()

            if (jawabanText.isBlank() && attachedFile == null) {
                Toast.makeText(this, "Minimal isi jawaban teks atau lampirkan file!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = pengumpulanRepo.kumpulkanTugas(tugasId, "pelajar_me", jawabanText, attachedFile)
                if (result.isSuccess) {
                    val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
                    val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
                    val intent = UiKit.withProfile(Intent(this@DetailTugasPelajarInstitusiActivity, TugasBerhasilPelajarActivity::class.java), role, mode).apply {
                        putExtra(TugasBerhasilPelajarActivity.EXTRA_TASK_TITLE, taskTitle)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@DetailTugasPelajarInstitusiActivity, "Gagal mengumpulkan tugas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
