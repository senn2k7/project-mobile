package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarDetailKelasActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS_ID = "extra_kelas_id"
    }

    private val viewModel: DetailKelasViewModel by viewModels {
        val kelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: "1"
        DetailKelasViewModelFactory(kelasId)
    }

    private lateinit var tabMateri: TextView
    private lateinit var tabTugas: TextView
    private lateinit var tabLihatHasilKuis: TextView
    private lateinit var tabBuatKuis: TextView
    private lateinit var tabDiskusi: TextView

    private lateinit var tvNamaKuliah: TextView
    private lateinit var tvInstitusiKelas: TextView
    private lateinit var tvKodeKelas: TextView
    private lateinit var tvNamaPengajar: TextView
    private lateinit var tvDeskripsi: TextView

    private lateinit var tvSectionTitle: TextView
    private lateinit var rvDetailKelas: RecyclerView
    private lateinit var tvEmptyDetail: TextView
    private lateinit var btnBottomAction: TextView

    private lateinit var materiAdapter: PengajarMateriAdapter

    private var currentKelasId: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        currentKelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: "1"

        setContentView(R.layout.activity_pengajar_detail_kelas)

        bindViews()
        setupListeners()
        setupRecyclerView()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.kelasState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            val kelas = state.data as Kelas
                            tvNamaKuliah.text = kelas.namaMataKuliah
                            tvInstitusiKelas.text = "${kelas.jenisInstitusi} • ${kelas.namaKelas}"
                            tvKodeKelas.text = "Kode Kelas: ${kelas.kodeKelas}"
                            tvDeskripsi.text = if (kelas.deskripsi.isNotBlank()) kelas.deskripsi else "Belum ada deskripsi"
                        }
                    }
                }

                launch {
                    viewModel.selectedTab.collectLatest { tab ->
                        updateTabSelection(tab)
                    }
                }

                launch {
                    viewModel.materiState.collectLatest { state ->
                        if (viewModel.selectedTab.value == 0) {
                            renderMateri(state)
                        }
                    }
                }

                launch {
                    viewModel.tugasState.collectLatest { state ->
                        if (viewModel.selectedTab.value == 1) {
                            renderTugas(state)
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

        tvNamaKuliah = findViewById(R.id.tvNamaKuliah)
        tvInstitusiKelas = findViewById(R.id.tvInstitusiKelas)
        tvKodeKelas = findViewById(R.id.tvKodeKelas)
        tvNamaPengajar = findViewById(R.id.tvNamaPengajar)
        tvDeskripsi = findViewById(R.id.tvDeskripsi)

        tabMateri = findViewById(R.id.tabMateri)
        tabTugas = findViewById(R.id.tabTugas)
        tabLihatHasilKuis = findViewById(R.id.tabLihatHasilKuis)
        tabBuatKuis = findViewById(R.id.tabBuatKuis)
        tabDiskusi = findViewById(R.id.tabDiskusi)

        tvSectionTitle = findViewById(R.id.tvSectionTitle)
        rvDetailKelas = findViewById(R.id.rvDetailKelas)
        tvEmptyDetail = findViewById(R.id.tvEmptyDetail)
        btnBottomAction = findViewById(R.id.btnBottomAction)
    }

    private fun setupRecyclerView() {
        rvDetailKelas.layoutManager = LinearLayoutManager(this)
        materiAdapter = PengajarMateriAdapter(
            onItemClick = { materi ->
                Toast.makeText(this, "Materi: ${materi.judul}", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = { _, _ ->
                Toast.makeText(this, "Menu Materi", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupListeners() {
        tabMateri.setOnClickListener { viewModel.setSelectedTab(0) }
        tabTugas.setOnClickListener { viewModel.setSelectedTab(1) }
        tabLihatHasilKuis.setOnClickListener {
            val intent = Intent(this, PengajarNilaiHasilKuisActivity::class.java).apply {
                putExtra(PengajarNilaiHasilKuisActivity.EXTRA_KELAS_ID, currentKelasId)
            }
            startActivity(intent)
        }
        tabBuatKuis.setOnClickListener {
            val intent = Intent(this, PengajarBuatKuisActivity::class.java).apply {
                putExtra(PengajarBuatKuisActivity.EXTRA_KELAS_ID, currentKelasId)
            }
            startActivity(intent)
        }
        tabDiskusi.setOnClickListener {
            viewModel.setSelectedTab(4)
            Toast.makeText(this, "Diskusi (Placeholder)", Toast.LENGTH_SHORT).show()
        }

        btnBottomAction.setOnClickListener {
            val tab = viewModel.selectedTab.value
            if (tab == 0) {
                val intent = Intent(this, PengajarTambahKontenActivity::class.java).apply {
                    putExtra(PengajarTambahKontenActivity.EXTRA_TAB_INDEX, 0)
                    putExtra(PengajarTambahKontenActivity.EXTRA_KELAS_ID, currentKelasId)
                }
                startActivity(intent)
            } else if (tab == 1) {
                val intent = Intent(this, PengajarTambahKontenActivity::class.java).apply {
                    putExtra(PengajarTambahKontenActivity.EXTRA_TAB_INDEX, 1)
                    putExtra(PengajarTambahKontenActivity.EXTRA_KELAS_ID, currentKelasId)
                }
                startActivity(intent)
            }
        }
    }

    private fun updateTabSelection(tab: Int) {
        val activeBg = AppCompatResources.getDrawable(this, R.drawable.bg_tab_selected)
        val inactiveBg = AppCompatResources.getDrawable(this, R.drawable.bg_tab_unselected)
        val activeColor = Color.WHITE
        val inactiveColor = Color.parseColor("#7186A5")

        val tabs = listOf(tabMateri, tabTugas, tabLihatHasilKuis, tabBuatKuis, tabDiskusi)
        tabs.forEachIndexed { idx, tv ->
            if (idx == tab) {
                tv.background = activeBg
                tv.setTextColor(activeColor)
                tv.setTypeface(null, Typeface.BOLD)
            } else {
                tv.background = inactiveBg
                tv.setTextColor(inactiveColor)
                tv.setTypeface(null, Typeface.NORMAL)
            }
        }

        if (tab == 0) {
            tvSectionTitle.text = "Daftar Materi"
            btnBottomAction.text = "+ Tambah Materi"
            btnBottomAction.visibility = View.VISIBLE
        } else if (tab == 1) {
            tvSectionTitle.text = "Daftar Tugas"
            btnBottomAction.text = "+ Buat Tugas"
            btnBottomAction.visibility = View.VISIBLE
        } else {
            tvSectionTitle.text = ""
            btnBottomAction.visibility = View.GONE
        }
    }

    private fun renderMateri(state: PengajarUiState<List<Materi>>) {
        if (state is PengajarUiState.Success<*>) {
            @Suppress("UNCHECKED_CAST")
            val list = state.data as List<Materi>
            if (list.isEmpty()) {
                rvDetailKelas.visibility = View.GONE
                tvEmptyDetail.visibility = View.VISIBLE
                tvEmptyDetail.text = "Belum ada materi"
            } else {
                rvDetailKelas.adapter = materiAdapter
                materiAdapter.submitList(list)
                rvDetailKelas.visibility = View.VISIBLE
                tvEmptyDetail.visibility = View.GONE
            }
        } else {
            rvDetailKelas.visibility = View.GONE
            tvEmptyDetail.visibility = View.VISIBLE
            tvEmptyDetail.text = "Belum ada materi"
        }
    }

    private fun renderTugas(state: PengajarUiState<List<Tugas>>) {
        if (state is PengajarUiState.Success<*>) {
            @Suppress("UNCHECKED_CAST")
            val list = state.data as List<Tugas>
            if (list.isEmpty()) {
                rvDetailKelas.visibility = View.GONE
                tvEmptyDetail.visibility = View.VISIBLE
                tvEmptyDetail.text = "Belum ada tugas"
            } else {
                rvDetailKelas.visibility = View.GONE
                tvEmptyDetail.visibility = View.VISIBLE
                tvEmptyDetail.text = "${list.size} tugas tersedia"
            }
        } else {
            rvDetailKelas.visibility = View.GONE
            tvEmptyDetail.visibility = View.VISIBLE
            tvEmptyDetail.text = "Belum ada tugas"
        }
    }
}
