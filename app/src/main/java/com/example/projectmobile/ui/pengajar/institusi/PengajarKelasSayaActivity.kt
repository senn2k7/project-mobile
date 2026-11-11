package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.DetailKelasActivity
import com.example.projectmobile.R
import com.example.projectmobile.UiKit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarKelasSayaActivity : AppCompatActivity() {

    private val viewModel: PengajarKelasSayaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_pengajar_kelas_saya)

        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottomNavContainer)
        bottomNavContainer.addView(PengajarBottomNavHelper.create(this, "Kelas"))

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val rvKelas = findViewById<RecyclerView>(R.id.rvKelasSaya)
        rvKelas.layoutManager = LinearLayoutManager(this)
        val adapter = PengajarKelasAdapter(
            isBeranda = false,
            onItemClick = { kelas ->
                val intent = Intent(this, DetailKelasActivity::class.java).apply {
                    putExtra(DetailKelasActivity.EXTRA_CLASS_NAME, kelas.namaMataKuliah)
                    putExtra(DetailKelasActivity.EXTRA_CLASS_CODE, "${kelas.namaKelas} • ${kelas.kodeKelas}")
                }
                startActivity(intent)
            },
            onMenuClick = { kelas, _ ->
                Toast.makeText(this, "Menu: ${kelas.namaMataKuliah}", Toast.LENGTH_SHORT).show()
            }
        )
        rvKelas.adapter = adapter

        val etSearch = findViewById<EditText>(R.id.etSearchKelas)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val tabSemua = findViewById<TextView>(R.id.tabSemua)
        val tabInstitusi = findViewById<TextView>(R.id.tabInstitusi)
        val tabSemester = findViewById<TextView>(R.id.tabSemester)

        tabSemua.setOnClickListener {
            tabSemua.background = UiKit.rounded(this, Color.parseColor("#1D5EFF"), 10)
            tabSemua.setTextColor(Color.WHITE)
            tabInstitusi.background = UiKit.rounded(this, Color.parseColor("#E8F0FF"), 10)
            tabInstitusi.setTextColor(Color.parseColor("#7186A5"))
            viewModel.setFilterInstitusi(false)
        }

        tabInstitusi.setOnClickListener {
            tabInstitusi.background = UiKit.rounded(this, Color.parseColor("#1D5EFF"), 10)
            tabInstitusi.setTextColor(Color.WHITE)
            tabSemua.background = UiKit.rounded(this, Color.parseColor("#E8F0FF"), 10)
            tabSemua.setTextColor(Color.parseColor("#7186A5"))
            viewModel.setFilterInstitusi(true)
        }

        tabSemester.setOnClickListener {
            val current = viewModel.selectedSemester.value
            val next = if (current == "Semester 1") "Semester 2" else "Semester 1"
            tabSemester.text = "$next ▾"
            viewModel.setSemester(next)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.kelasState.collectLatest { state ->
                    val emptyLayout = findViewById<View>(R.id.layoutEmpty)
                    val emptyTitle = findViewById<TextView>(R.id.tvEmptyTitle)
                    val emptySubtitle = findViewById<TextView>(R.id.tvEmptySubtitle)

                    when (state) {
                        is PengajarUiState.Success<*> -> {
                            val data = state.data as List<PengajarKelas>
                            adapter.submitList(data)
                            rvKelas.visibility = View.VISIBLE
                            emptyLayout.visibility = View.GONE
                        }
                        is PengajarUiState.Empty -> {
                            adapter.submitList(emptyList())
                            rvKelas.visibility = View.GONE
                            emptyLayout.visibility = View.VISIBLE
                            if (etSearch.text.toString().isNotBlank()) {
                                emptyTitle.text = getString(R.string.empty_kelas_tidak_ditemukan)
                                emptySubtitle.text = "Coba kata kunci lain."
                            } else {
                                emptyTitle.text = getString(R.string.empty_belum_ada_kelas)
                                emptySubtitle.text = getString(R.string.empty_kelas_subtitle)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
