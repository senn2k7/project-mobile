package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
import com.example.projectmobile.ui.pengajar.institusi.PengajarBottomNavHelper
import com.example.projectmobile.ui.pengajar.institusi.PengajarDetailTugasActivity
import com.example.projectmobile.ui.pengajar.institusi.PengajarTugasAdapter
import com.example.projectmobile.ui.pengajar.institusi.PengajarUiState
import com.example.projectmobile.ui.pengajar.institusi.Tugas
import com.example.projectmobile.ui.pengajar.institusi.TugasViewModel
import com.example.projectmobile.ui.pengajar.institusi.TugasViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TugasActivity : AppCompatActivity() {

    private val viewModel: TugasViewModel by viewModels {
        val kelasId = intent.getStringExtra("extra_kelas_id") ?: intent.getStringExtra(EXTRA_CLASS_CODE) ?: ""
        TugasViewModelFactory(kelasId)
    }

    private lateinit var rvTugas: RecyclerView
    private lateinit var tvEmptyTugas: TextView
    private lateinit var tabSemua: TextView
    private lateinit var tabBerlangsung: TextView
    private lateinit var tabSelesai: TextView

    private lateinit var tugasAdapter: PengajarTugasAdapter

    companion object {
        const val EXTRA_CLASS_NAME = "extra_class_name"
        const val EXTRA_CLASS_CODE = "extra_class_code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_pengajar_tugas)

        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottomNavContainer)
        bottomNavContainer.addView(PengajarBottomNavHelper.create(this, "Tugas"))

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnMenu).setOnClickListener {
            Toast.makeText(this, "Menu Tugas", Toast.LENGTH_SHORT).show()
        }

        tabSemua = findViewById(R.id.tabSemua)
        tabBerlangsung = findViewById(R.id.tabBerlangsung)
        tabSelesai = findViewById(R.id.tabSelesai)
        rvTugas = findViewById(R.id.rvTugas)
        tvEmptyTugas = findViewById(R.id.tvEmptyTugas)

        setupRecyclerView()
        setupListeners()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.kelasInfo.collectLatest { kelas ->
                        if (kelas != null) {
                            findViewById<TextView>(R.id.tvCourseTitle).text = kelas.namaMataKuliah
                            findViewById<TextView>(R.id.tvCourseCode).text = "${kelas.namaKelas} • ${kelas.kodeKelas}"
                        }
                    }
                }

                viewModel.tugasState.collectLatest { state ->
                    when (state) {
                        is PengajarUiState.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as List<Tugas>
                            if (list.isEmpty()) {
                                rvTugas.visibility = View.GONE
                                tvEmptyTugas.visibility = View.VISIBLE
                                tvEmptyTugas.text = "Belum ada tugas"
                            } else {
                                rvTugas.visibility = View.VISIBLE
                                tvEmptyTugas.visibility = View.GONE
                                tugasAdapter.submitList(list)
                            }
                        }
                        is PengajarUiState.Empty -> {
                            rvTugas.visibility = View.GONE
                            tvEmptyTugas.visibility = View.VISIBLE
                            tvEmptyTugas.text = "Belum ada tugas"
                        }
                        is PengajarUiState.CustomEmpty -> {
                            rvTugas.visibility = View.GONE
                            tvEmptyTugas.visibility = View.VISIBLE
                            tvEmptyTugas.text = state.message
                        }
                        is PengajarUiState.Error -> {
                            rvTugas.visibility = View.GONE
                            tvEmptyTugas.visibility = View.VISIBLE
                            tvEmptyTugas.text = state.message
                        }
                        else -> {
                            rvTugas.visibility = View.GONE
                            tvEmptyTugas.visibility = View.VISIBLE
                            tvEmptyTugas.text = "Memuat tugas..."
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        rvTugas.layoutManager = LinearLayoutManager(this)
        tugasAdapter = PengajarTugasAdapter(
            onItemClick = { task ->
                val intent = Intent(this, PengajarDetailTugasActivity::class.java).apply {
                    putExtra(PengajarDetailTugasActivity.EXTRA_TUGAS_ID, task.id)
                }
                startActivity(intent)
            },
            onMenuClick = { _, _ ->
                Toast.makeText(this, "Menu Tugas", Toast.LENGTH_SHORT).show()
            }
        )
        rvTugas.adapter = tugasAdapter
    }

    private fun setupListeners() {
        tabSemua.setOnClickListener {
            viewModel.setFilter("SEMUA")
            updateTabStyles("SEMUA")
        }
        tabBerlangsung.setOnClickListener {
            viewModel.setFilter("BERLANGSUNG")
            updateTabStyles("BERLANGSUNG")
        }
        tabSelesai.setOnClickListener {
            viewModel.setFilter("SELESAI")
            updateTabStyles("SELESAI")
        }
    }

    private fun updateTabStyles(filter: String) {
        val activeBg = AppCompatResources.getDrawable(this, R.drawable.bg_tab_selected)
        val inactiveBg = AppCompatResources.getDrawable(this, R.drawable.bg_tab_unselected)
        val activeColor = Color.WHITE
        val inactiveColor = Color.parseColor("#7186A5")

        tabSemua.background = inactiveBg
        tabSemua.setTextColor(inactiveColor)
        tabSemua.setTypeface(null, Typeface.NORMAL)

        tabBerlangsung.background = inactiveBg
        tabBerlangsung.setTextColor(inactiveColor)
        tabBerlangsung.setTypeface(null, Typeface.NORMAL)

        tabSelesai.background = inactiveBg
        tabSelesai.setTextColor(inactiveColor)
        tabSelesai.setTypeface(null, Typeface.NORMAL)

        when (filter) {
            "SEMUA" -> {
                tabSemua.background = activeBg
                tabSemua.setTextColor(activeColor)
                tabSemua.setTypeface(null, Typeface.BOLD)
            }
            "BERLANGSUNG" -> {
                tabBerlangsung.background = activeBg
                tabBerlangsung.setTextColor(activeColor)
                tabBerlangsung.setTypeface(null, Typeface.BOLD)
            }
            "SELESAI" -> {
                tabSelesai.background = activeBg
                tabSelesai.setTextColor(activeColor)
                tabSelesai.setTypeface(null, Typeface.BOLD)
            }
        }
    }
}
