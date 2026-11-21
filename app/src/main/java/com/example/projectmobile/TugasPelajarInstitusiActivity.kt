package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import com.example.projectmobile.ui.pengajar.institusi.PengumpulanRepository
import com.example.projectmobile.ui.pengajar.institusi.TugasRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TugasPelajarInstitusiActivity : AppCompatActivity() {

    private val tugasRepo: TugasRepository = PengajarRepositoryProvider.provideTugasRepo()
    private val pengumpulanRepo: PengumpulanRepository = PengajarRepositoryProvider.providePengumpulanRepo()

    private lateinit var rvTugasPelajar: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var tabSemua: TextView
    private lateinit var tabBelum: TextView
    private lateinit var tabSelesai: TextView
    private lateinit var adapter: PelajarTugasAdapter

    private var currentFilter = "semua" // "semua", "belum", "selesai"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_tugas_pelajar_institusi)

        bindViews()
        setupRecyclerView()
        setupBottomNav()
        setupListeners()
        loadTugas()
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {}

        rvTugasPelajar = findViewById(R.id.rvTugasPelajar)
        tvEmpty = findViewById(R.id.tvEmpty)
        tabSemua = findViewById(R.id.tabSemua)
        tabBelum = findViewById(R.id.tabBelum)
        tabSelesai = findViewById(R.id.tabSelesai)
    }

    private fun setupRecyclerView() {
        rvTugasPelajar.layoutManager = LinearLayoutManager(this)
        adapter = PelajarTugasAdapter { tugas ->
            val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
            val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
            val intent = UiKit.withProfile(Intent(this, DetailTugasPelajarInstitusiActivity::class.java), role, mode).apply {
                putExtra(DetailTugasPelajarInstitusiActivity.EXTRA_TUGAS_ID, tugas.id)
                putExtra(DetailTugasPelajarInstitusiActivity.EXTRA_TASK_TITLE, tugas.judul)
                putExtra(DetailTugasPelajarInstitusiActivity.EXTRA_COURSE_NAME, "Pemrograman Mobile")
            }
            startActivity(intent)
        }
        rvTugasPelajar.adapter = adapter
    }

    private fun setupBottomNav() {
        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottomNavContainer)
        bottomNavContainer.addView(PelajarBottomNavHelper.create(this, "Aktivitas"))
    }

    private fun setupListeners() {
        tabSemua.setOnClickListener {
            currentFilter = "semua"
            updateTabUI()
            loadTugas()
        }
        tabBelum.setOnClickListener {
            currentFilter = "belum"
            updateTabUI()
            loadTugas()
        }
        tabSelesai.setOnClickListener {
            currentFilter = "selesai"
            updateTabUI()
            loadTugas()
        }
    }

    private fun updateTabUI() {
        val selectedBg = UiKit.rounded(this, Color.parseColor("#1D5EFF"), 20)
        val unselectedBg = UiKit.rounded(this, Color.TRANSPARENT, 20)

        tabSemua.background = if (currentFilter == "semua") selectedBg else unselectedBg
        tabSemua.setTextColor(if (currentFilter == "semua") Color.WHITE else Color.parseColor("#7186A5"))

        tabBelum.background = if (currentFilter == "belum") selectedBg else unselectedBg
        tabBelum.setTextColor(if (currentFilter == "belum") Color.WHITE else Color.parseColor("#7186A5"))

        tabSelesai.background = if (currentFilter == "selesai") selectedBg else unselectedBg
        tabSelesai.setTextColor(if (currentFilter == "selesai") Color.WHITE else Color.parseColor("#7186A5"))
    }

    private fun loadTugas() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    tugasRepo.tugasUntukPelajar("pelajar_me"),
                    pengumpulanRepo.getPengumpulanPelajar("pelajar_me")
                ) { tugasList, pengumpulanList ->
                    val now = System.currentTimeMillis()
                    val items = mutableListOf<TugasItemUi>()
                    val submittedMap = pengumpulanList.associateBy { it.tugasId }

                    for (t in tugasList) {
                        val submission = submittedMap[t.id]
                        val nilai = submission?.nilai
                        val isSudahKumpul = submission != null

                        val (statusText, textColor, bgColor, filterGroup) = when {
                            nilai != null -> Quad("Dinilai ($nilai)", Color.rgb(46, 125, 50), Color.rgb(232, 245, 233), "selesai")
                            isSudahKumpul -> Quad("Menunggu Penilaian", Color.rgb(255, 152, 0), Color.rgb(255, 243, 224), "selesai")
                            now > t.tenggat -> Quad("Terlambat", Color.rgb(244, 67, 54), Color.rgb(255, 235, 238), "belum")
                            else -> Quad("Belum Dikerjakan", Color.rgb(244, 67, 54), Color.rgb(255, 235, 238), "belum")
                        }

                        if (currentFilter == "semua" || 
                            (currentFilter == "belum" && filterGroup == "belum") ||
                            (currentFilter == "selesai" && filterGroup == "selesai")) {
                            items.add(TugasItemUi(t, statusText, textColor, bgColor))
                        }
                    }
                    items
                }.collectLatest { items ->
                    if (items.isEmpty()) {
                        rvTugasPelajar.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        rvTugasPelajar.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                        adapter.submitList(items)
                    }
                }
            }
        }
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
