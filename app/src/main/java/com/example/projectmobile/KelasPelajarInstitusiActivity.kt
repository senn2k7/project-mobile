package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.KelasRepository
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KelasPelajarInstitusiActivity : AppCompatActivity() {

    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo()

    private lateinit var rvKelasPelajar: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var etSearchKelas: EditText
    private lateinit var adapter: PelajarKelasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_kelas_pelajar_institusi)

        bindViews()
        setupRecyclerView()
        setupBottomNav()
        observeKelas("")
    }

    private fun bindViews() {
        rvKelasPelajar = findViewById(R.id.rvKelasPelajar)
        tvEmpty = findViewById(R.id.tvEmpty)
        etSearchKelas = findViewById(R.id.etSearchKelas)

        etSearchKelas.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                observeKelas(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupRecyclerView() {
        rvKelasPelajar.layoutManager = LinearLayoutManager(this)
        adapter = PelajarKelasAdapter { kelas ->
            val intent = Intent(this, DetailKelasPelajarInstitusiActivity::class.java).apply {
                putExtra(DetailKelasPelajarInstitusiActivity.EXTRA_KELAS_ID, kelas.id)
                putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PELAJAR)
                putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
            }
            startActivity(intent)
        }
        rvKelasPelajar.adapter = adapter
    }

    private fun setupBottomNav() {
        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottomNavContainer)
        bottomNavContainer.addView(PelajarBottomNavHelper.create(this, "Kelas"))
    }

    private fun observeKelas(query: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                kelasRepo.getKelasDiikuti(query, "pelajar_me").collectLatest { list ->
                    if (list.isEmpty()) {
                        rvKelasPelajar.visibility = View.GONE
                        tvEmpty.visibility = View.VISIBLE
                    } else {
                        rvKelasPelajar.visibility = View.VISIBLE
                        tvEmpty.visibility = View.GONE
                        adapter.submitList(list)
                    }
                }
            }
        }
    }
}
