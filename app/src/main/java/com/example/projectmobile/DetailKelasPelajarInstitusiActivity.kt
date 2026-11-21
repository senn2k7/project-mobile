package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.DetailKelasViewModel
import com.example.projectmobile.ui.pengajar.institusi.DetailKelasViewModelFactory
import com.example.projectmobile.ui.pengajar.institusi.FormatTanggal
import com.example.projectmobile.ui.pengajar.institusi.Kelas
import com.example.projectmobile.ui.pengajar.institusi.Materi
import com.example.projectmobile.ui.pengajar.institusi.NameUtils
import com.example.projectmobile.ui.pengajar.institusi.PengajarUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailKelasPelajarInstitusiActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS_ID = "extra_kelas_id"
    }

    private val viewModel: DetailKelasViewModel by viewModels {
        val kelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: ""
        DetailKelasViewModelFactory(kelasId)
    }

    private lateinit var tvJudulMateri: TextView
    private lateinit var tvChipPertemuan: TextView
    private lateinit var tvDosenAvatar: TextView
    private lateinit var tvDosenNama: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvPendahuluanDesc: TextView
    private lateinit var rvFileMateri: RecyclerView
    private lateinit var tabMateri: TextView
    private lateinit var tabTugas: TextView
    private lateinit var tabKuis: TextView
    private lateinit var tabDiskusi: TextView
    private lateinit var tabPresensi: TextView
    private lateinit var btnBukaMateri: TextView
    private lateinit var btnTandaiSelesai: TextView
    private lateinit var btnTanyakanAi: TextView

    private lateinit var materiAdapter: PelajarFileMateriAdapter
    private var currentClassName: String = "Pengenalan Android Studio"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val kelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: ""
        if (kelasId.isBlank()) {
            Toast.makeText(this, "Kelas tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_detail_kelas_pelajar_institusi)

        bindViews()
        setupRecyclerView()
        setupListeners()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.kelasState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            val kelas = state.data as Kelas
                            currentClassName = kelas.namaMataKuliah
                            tvJudulMateri.text = kelas.namaMataKuliah
                            tvChipPertemuan.text = "Pertemuan ${kelas.pertemuanKe}"
                            tvDosenNama.text = kelas.dosenPengampu
                            tvDosenAvatar.text = NameUtils.getInisial(kelas.dosenPengampu)
                            tvDosenAvatar.background = UiKit.rounded(this@DetailKelasPelajarInstitusiActivity, NameUtils.getWarnaAvatar(kelas.dosenPengampu), 16)
                            tvTanggal.text = FormatTanggal.formatTanpaTahun(kelas.tanggalPertemuan)
                            tvPendahuluanDesc.text = if (kelas.deskripsi.isNotBlank()) kelas.deskripsi else "Belum ada deskripsi materi."
                        }
                    }
                }

                launch {
                    viewModel.materiState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as List<Materi>
                            materiAdapter.submitList(list)
                        } else {
                            materiAdapter.submitList(emptyList())
                        }
                    }
                }
            }
        }
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            Toast.makeText(this, "Menu Kelas", Toast.LENGTH_SHORT).show()
        }

        tvJudulMateri = findViewById(R.id.tvJudulMateri)
        tvChipPertemuan = findViewById(R.id.tvChipPertemuan)
        tvDosenAvatar = findViewById(R.id.tvDosenAvatar)
        tvDosenNama = findViewById(R.id.tvDosenNama)
        tvTanggal = findViewById(R.id.tvTanggal)
        tvPendahuluanDesc = findViewById(R.id.tvPendahuluanDesc)
        rvFileMateri = findViewById(R.id.rvFileMateri)

        tabMateri = findViewById(R.id.tabMateri)
        tabTugas = findViewById(R.id.tabTugas)
        tabKuis = findViewById(R.id.tabKuis)
        tabDiskusi = findViewById(R.id.tabDiskusi)
        tabPresensi = findViewById(R.id.tabPresensi)

        btnBukaMateri = findViewById(R.id.btnBukaMateri)
        btnTandaiSelesai = findViewById(R.id.btnTandaiSelesai)
        btnTanyakanAi = findViewById(R.id.btnTanyakanAi)
    }

    private fun setupRecyclerView() {
        rvFileMateri.layoutManager = LinearLayoutManager(this)
        materiAdapter = PelajarFileMateriAdapter { materi ->
            Toast.makeText(this, "Buka file: ${materi.namaFile.ifBlank { materi.judul }}", Toast.LENGTH_SHORT).show()
        }
        rvFileMateri.adapter = materiAdapter
    }

    private fun setupListeners() {
        tabTugas.setOnClickListener {
            val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
            val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
            val intent = UiKit.withProfile(Intent(this, TugasPelajarInstitusiActivity::class.java), role, mode)
            startActivity(intent)
        }

        tabKuis.setOnClickListener {
            val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
            val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
            val intent = UiKit.withProfile(Intent(this, DetailKuisPelajarInstitusiActivity::class.java), role, mode).apply {
                putExtra(DetailKuisPelajarInstitusiActivity.EXTRA_QUIZ_TITLE, "Kuis $currentClassName")
                putExtra(DetailKuisPelajarInstitusiActivity.EXTRA_MEETING, "Pertemuan 1")
            }
            startActivity(intent)
        }

        tabDiskusi.setOnClickListener {
            val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
            val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
            val intent = UiKit.withProfile(Intent(this, DiskusiPelajarInstitusiActivity::class.java), role, mode).apply {
                putExtra(DiskusiPelajarInstitusiActivity.EXTRA_COURSE_NAME, currentClassName)
            }
            startActivity(intent)
        }

        tabPresensi.setOnClickListener {
            Toast.makeText(this, "Presensi (TODO)", Toast.LENGTH_SHORT).show()
        }

        btnBukaMateri.setOnClickListener {
            Toast.makeText(this, "Membuka materi...", Toast.LENGTH_SHORT).show()
        }

        btnTandaiSelesai.setOnClickListener {
            Toast.makeText(this, "Materi ditandai selesai (TODO - belum tersimpan)", Toast.LENGTH_SHORT).show()
        }

        btnTanyakanAi.setOnClickListener {
            // TODO: Sambungkan ke KelolaAiAsistenPelajarActivity dengan extra konteks (nama kelas + pertemuan)
            @Suppress("UNUSED_VARIABLE")
            val intent = Intent(this, KelolaAiAsistenPelajarActivity::class.java).apply {
                putExtra("extra_konteks_kelas", currentClassName)
                putExtra("extra_konteks_pertemuan", "Pertemuan 1")
            }
            // startActivity(intent)
            Toast.makeText(this, "Kelola AI Asisten (TODO - belum dikerjakan)", Toast.LENGTH_SHORT).show()
        }
    }
}
