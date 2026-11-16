package com.example.projectmobile.ui.pengajar.institusi

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
import com.example.projectmobile.R
import com.example.projectmobile.UiKit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarDetailTugasActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TUGAS_ID = "extra_tugas_id"
    }

    private val viewModel: DetailTugasViewModel by viewModels {
        val tugasId = intent.getStringExtra(EXTRA_TUGAS_ID) ?: ""
        DetailTugasViewModelFactory(tugasId)
    }

    private lateinit var tvJudulTugas: TextView
    private lateinit var tvSubtitleTugas: TextView
    private lateinit var tvBadgeTugas: TextView
    private lateinit var tvDeskripsiTugas: TextView
    private lateinit var tvBatasTugas: TextView
    private lateinit var tvTipeTugas: TextView
    private lateinit var rvLampiran: RecyclerView
    private lateinit var tvEmptyLampiran: TextView
    private lateinit var tvStatSudah: TextView
    private lateinit var tvStatBelum: TextView
    private lateinit var btnLihatPengumpulan: TextView
    private lateinit var btnEditTugas: TextView

    private lateinit var lampiranAdapter: PengajarLampiranAdapter
    private var currentTugasId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        currentTugasId = intent.getStringExtra(EXTRA_TUGAS_ID) ?: ""

        setContentView(R.layout.activity_pengajar_detail_tugas)

        bindViews()
        setupRecyclerView()
        setupListeners()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tugasState.collectLatest { state ->
                        when (state) {
                            is PengajarUiState.Success<*> -> {
                                val tugas = state.data as Tugas
                                tvJudulTugas.text = tugas.judul
                                tvSubtitleTugas.text = "Pemrograman Mobile • Pertemuan ${tugas.pertemuanKe}"
                                tvDeskripsiTugas.text = if (tugas.deskripsi.isNotBlank()) tugas.deskripsi else "Belum ada deskripsi"
                                tvBatasTugas.text = FormatTanggal.formatLengkap(tugas.tenggat)
                                tvTipeTugas.text = tugas.tipe.replaceFirstChar { it.uppercase() }

                                val isBerlangsung = TaskStatusUtils.hitungStatus(tugas.tenggat) == "Berlangsung"
                                tvBadgeTugas.text = if (isBerlangsung) "Tugas Berlangsung" else "Tugas Selesai"
                                tvBadgeTugas.setTextColor(if (isBerlangsung) Color.parseColor("#1D5EFF") else Color.parseColor("#2E7D32"))
                                tvBadgeTugas.background = UiKit.rounded(this@PengajarDetailTugasActivity, if (isBerlangsung) Color.parseColor("#E8F2FF") else Color.parseColor("#E8F5E9"), 8)

                                tvStatSudah.text = tugas.jumlahMengumpulkan.toString()
                                tvStatBelum.text = (tugas.totalPelajar - tugas.jumlahMengumpulkan).toString()
                            }
                            is PengajarUiState.Error -> {
                                tvJudulTugas.text = state.message
                                tvSubtitleTugas.text = "-"
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.lampiranState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as List<LampiranTugas>
                            if (list.isEmpty()) {
                                rvLampiran.visibility = View.GONE
                                tvEmptyLampiran.visibility = View.VISIBLE
                            } else {
                                rvLampiran.visibility = View.VISIBLE
                                tvEmptyLampiran.visibility = View.GONE
                                lampiranAdapter.submitList(list)
                            }
                        } else {
                            rvLampiran.visibility = View.GONE
                            tvEmptyLampiran.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            Toast.makeText(this, "Menu Tugas", Toast.LENGTH_SHORT).show()
        }

        tvJudulTugas = findViewById(R.id.tvJudulTugas)
        tvSubtitleTugas = findViewById(R.id.tvSubtitleTugas)
        tvBadgeTugas = findViewById(R.id.tvBadgeTugas)
        tvDeskripsiTugas = findViewById(R.id.tvDeskripsiTugas)
        tvBatasTugas = findViewById(R.id.tvBatasTugas)
        tvTipeTugas = findViewById(R.id.tvTipeTugas)
        rvLampiran = findViewById(R.id.rvLampiran)
        tvEmptyLampiran = findViewById(R.id.tvEmptyLampiran)
        tvStatSudah = findViewById(R.id.tvStatSudah)
        tvStatBelum = findViewById(R.id.tvStatBelum)
        btnLihatPengumpulan = findViewById(R.id.btnLihatPengumpulan)
        btnEditTugas = findViewById(R.id.btnEditTugas)
    }

    private fun setupRecyclerView() {
        rvLampiran.layoutManager = LinearLayoutManager(this)
        lampiranAdapter = PengajarLampiranAdapter(
            onItemClick = { lampiran ->
                Toast.makeText(this, "Lampiran: ${lampiran.nama}", Toast.LENGTH_SHORT).show()
            }
        )
        rvLampiran.adapter = lampiranAdapter
    }

    private fun setupListeners() {
        btnLihatPengumpulan.setOnClickListener {
            // TODO: Sambungkan ulang ke PengajarPengumpulanTugasActivity setelah terdaftar di AndroidManifest.xml
            @Suppress("UNUSED_VARIABLE")
            val intent = Intent(this, PengajarPengumpulanTugasActivity::class.java).apply {
                putExtra(PengajarPengumpulanTugasActivity.EXTRA_TUGAS_ID, currentTugasId)
            }
            // startActivity(intent)
            Toast.makeText(this, "Pengumpulan Tugas (TODO - belum dikerjakan)", Toast.LENGTH_SHORT).show()
        }

        btnEditTugas.setOnClickListener {
            Toast.makeText(this, "Edit Tugas (TODO)", Toast.LENGTH_SHORT).show()
        }
    }
}
