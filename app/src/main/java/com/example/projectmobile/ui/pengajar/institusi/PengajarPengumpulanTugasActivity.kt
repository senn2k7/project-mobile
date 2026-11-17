package com.example.projectmobile.ui.pengajar.institusi

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

class PengajarPengumpulanTugasActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TUGAS_ID = "extra_tugas_id"
    }

    private val viewModel: PengumpulanViewModel by viewModels {
        val tugasId = intent.getStringExtra(EXTRA_TUGAS_ID) ?: "t1"
        PengumpulanViewModelFactory(tugasId)
    }

    private lateinit var tvStatSudah: TextView
    private lateinit var tvStatBelum: TextView
    private lateinit var chipSemua: TextView
    private lateinit var chipSudah: TextView
    private lateinit var chipBelum: TextView
    private lateinit var tvHeaderSudah: TextView
    private lateinit var tvHeaderBelum: TextView
    private lateinit var rvSudahMengumpulkan: RecyclerView
    private lateinit var rvBelumMengumpulkan: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearch: EditText

    private lateinit var sudahAdapter: PengajarSudahMengumpulkanAdapter
    private lateinit var belumAdapter: PengajarBelumMengumpulkanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_pengajar_pengumpulan_tugas)

        bindViews()
        setupRecyclerViews()
        setupListeners()
        observeViewModel()
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        tvStatSudah = findViewById(R.id.tvStatSudah)
        tvStatBelum = findViewById(R.id.tvStatBelum)
        chipSemua = findViewById(R.id.chipSemua)
        chipSudah = findViewById(R.id.chipSudah)
        chipBelum = findViewById(R.id.chipBelum)
        tvHeaderSudah = findViewById(R.id.tvHeaderSudah)
        tvHeaderBelum = findViewById(R.id.tvHeaderBelum)
        rvSudahMengumpulkan = findViewById(R.id.rvSudahMengumpulkan)
        rvBelumMengumpulkan = findViewById(R.id.rvBelumMengumpulkan)
        tvEmpty = findViewById(R.id.tvEmpty)
        etSearch = findViewById(R.id.etSearch)
    }

    private fun setupRecyclerViews() {
        rvSudahMengumpulkan.layoutManager = LinearLayoutManager(this)
        sudahAdapter = PengajarSudahMengumpulkanAdapter { item ->
            Toast.makeText(this, "Pengumpulan: ${item.namaPelajar} (TODO)", Toast.LENGTH_SHORT).show()
        }
        rvSudahMengumpulkan.adapter = sudahAdapter

        rvBelumMengumpulkan.layoutManager = LinearLayoutManager(this)
        belumAdapter = PengajarBelumMengumpulkanAdapter { pelajar ->
            Toast.makeText(this, "Pelajar: ${pelajar.nama} (TODO)", Toast.LENGTH_SHORT).show()
        }
        rvBelumMengumpulkan.adapter = belumAdapter
    }

    private fun setupListeners() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        chipSemua.setOnClickListener {
            viewModel.setFilterStatus("SEMUA")
            updateChipSelection("SEMUA")
        }
        chipSudah.setOnClickListener {
            viewModel.setFilterStatus("SUDAH")
            updateChipSelection("SUDAH")
        }
        chipBelum.setOnClickListener {
            viewModel.setFilterStatus("BELUM")
            updateChipSelection("BELUM")
        }
        updateChipSelection("SEMUA")
    }

    private fun updateChipSelection(filterVal: String) {
        val activeBg = UiKit.rounded(this, Color.parseColor("#1D5EFF"), 10)
        val inactiveBg = UiKit.rounded(this, Color.parseColor("#E8F0FF"), 10)

        val chips = listOf(Pair(chipSemua, "SEMUA"), Pair(chipSudah, "SUDAH"), Pair(chipBelum, "BELUM"))
        for ((chip, value) in chips) {
            if (value == filterVal) {
                chip.background = activeBg
                chip.setTextColor(Color.WHITE)
                chip.setTypeface(null, Typeface.BOLD)
            } else {
                chip.background = inactiveBg
                chip.setTextColor(Color.parseColor("#7186A5"))
                chip.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pengumpulanState.collectLatest { state ->
                    when (state) {
                        is PengajarUiState.Success<*> -> {
                            val data = state.data as DaftarPengumpulan
                            tvStatSudah.text = data.totalSudah.toString()
                            tvStatBelum.text = data.totalBelum.toString()
                            chipSemua.text = "Semua (${data.totalSudah + data.totalBelum})"
                            chipSudah.text = "Sudah mengumpulkan (${data.totalSudah})"
                            chipBelum.text = "Belum (${data.totalBelum})"

                            if (data.sudahMengumpulkan.isEmpty() && data.belumMengumpulkan.isEmpty()) {
                                tvEmpty.visibility = View.VISIBLE
                                rvSudahMengumpulkan.visibility = View.GONE
                                rvBelumMengumpulkan.visibility = View.GONE
                                tvHeaderSudah.visibility = View.GONE
                                tvHeaderBelum.visibility = View.GONE
                            } else {
                                tvEmpty.visibility = View.GONE

                                if (data.sudahMengumpulkan.isNotEmpty()) {
                                    tvHeaderSudah.visibility = View.VISIBLE
                                    rvSudahMengumpulkan.visibility = View.VISIBLE
                                    tvHeaderSudah.text = "Sudah Mengumpulkan (${data.sudahMengumpulkan.size})"
                                    sudahAdapter.submitList(data.sudahMengumpulkan)
                                } else {
                                    tvHeaderSudah.visibility = View.GONE
                                    rvSudahMengumpulkan.visibility = View.GONE
                                }

                                if (data.belumMengumpulkan.isNotEmpty()) {
                                    tvHeaderBelum.visibility = View.VISIBLE
                                    rvBelumMengumpulkan.visibility = View.VISIBLE
                                    tvHeaderBelum.text = "Belum Mengumpulkan (${data.belumMengumpulkan.size})"
                                    belumAdapter.submitList(data.belumMengumpulkan)
                                } else {
                                    tvHeaderBelum.visibility = View.GONE
                                    rvBelumMengumpulkan.visibility = View.GONE
                                }
                            }
                        }
                        is PengajarUiState.Empty -> {
                            tvStatSudah.text = "0"
                            tvStatBelum.text = "0"
                            chipSemua.text = "Semua (0)"
                            chipSudah.text = "Sudah mengumpulkan (0)"
                            chipBelum.text = "Belum (0)"
                            tvEmpty.visibility = View.VISIBLE
                            rvSudahMengumpulkan.visibility = View.GONE
                            rvBelumMengumpulkan.visibility = View.GONE
                            tvHeaderSudah.visibility = View.GONE
                            tvHeaderBelum.visibility = View.GONE
                        }
                        else -> {
                            tvEmpty.visibility = View.VISIBLE
                            rvSudahMengumpulkan.visibility = View.GONE
                            rvBelumMengumpulkan.visibility = View.GONE
                            tvHeaderSudah.visibility = View.GONE
                            tvHeaderBelum.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}
