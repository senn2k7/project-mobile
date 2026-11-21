package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.DiskusiRepository
import com.example.projectmobile.ui.pengajar.institusi.KelasRepository
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiskusiPelajarInstitusiActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS_ID = "extra_kelas_id"
    }

    private val diskusiRepo: DiskusiRepository = PengajarRepositoryProvider.provideDiskusiRepo()
    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo()
    private val profilPelajarRepo = PengajarRepositoryProvider.provideProfilPelajarRepo()

    private lateinit var rvDiskusi: RecyclerView
    private lateinit var etPesan: EditText
    private lateinit var btnKirim: ImageView
    private lateinit var adapter: PelajarDiskusiAdapter

    private var kelasId: String = ""
    private var currentPelajarId: String = "pelajar_me"
    private var currentNamaPelajar: String = "Andi Pratama"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        kelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: ""

        if (kelasId.isBlank()) {
            Toast.makeText(this, "Kelas tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_diskusi_pelajar_institusi)

        bindViews()
        setupRecyclerView()
        setupListeners()
        loadKelasDanDiskusi()
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {}

        rvDiskusi = findViewById(R.id.rvDiskusi)
        etPesan = findViewById(R.id.etPesan)
        btnKirim = findViewById(R.id.btnKirim)

        findViewById<ImageView>(R.id.btnEmoji).setOnClickListener {
            Toast.makeText(this, "Fitur emoji (TODO)", Toast.LENGTH_SHORT).show()
        }
        findViewById<ImageView>(R.id.btnAttach).setOnClickListener {
            Toast.makeText(this, "Lampirkan file diskusi (TODO)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        rvDiskusi.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        adapter = PelajarDiskusiAdapter(currentPelajarId)
        rvDiskusi.adapter = adapter
    }

    private fun setupListeners() {
        btnKirim.setOnClickListener {
            val teks = etPesan.text.toString().trim()
            if (teks.isBlank()) {
                Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = diskusiRepo.kirimPesan(kelasId, currentPelajarId, currentNamaPelajar, teks)
                if (result.isSuccess) {
                    etPesan.setText("")
                } else {
                    Toast.makeText(this@DiskusiPelajarInstitusiActivity, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadKelasDanDiskusi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Load Class Details as Single Source of Truth
                launch {
                    kelasRepo.kelasById(kelasId).collectLatest { kelas ->
                        if (kelas != null) {
                            findViewById<TextView>(R.id.tvJudulDiskusi).text = "Diskusi ${kelas.namaMataKuliah}"
                            findViewById<TextView>(R.id.tvSubjudulPeserta).text = "${kelas.jumlahPelajar} peserta"
                        }
                    }
                }

                // 2. Get active student profile ID and Name
                launch {
                    profilPelajarRepo.getProfilPelajar().collectLatest { profil ->
                        if (profil != null && profil.uid.isNotBlank()) {
                            currentPelajarId = profil.uid
                            if (profil.nama.isNotBlank()) {
                                currentNamaPelajar = profil.nama
                            }
                        }
                    }
                }

                // 3. Observe messages reactively
                launch {
                    diskusiRepo.pesanPerKelas(kelasId).collectLatest { pesanList ->
                        adapter.submitList(pesanList) {
                            if (pesanList.isNotEmpty()) {
                                rvDiskusi.scrollToPosition(pesanList.size - 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
