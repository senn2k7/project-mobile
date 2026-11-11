package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
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
import com.example.projectmobile.NotifikasiActivity
import com.example.projectmobile.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarBerandaActivity : AppCompatActivity() {

    private val viewModel: PengajarBerandaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_pengajar_beranda)

        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottomNavContainer)
        bottomNavContainer.addView(PengajarBottomNavHelper.create(this, "Beranda"))

        val rvKelas = findViewById<RecyclerView>(R.id.rvKelasBeranda)
        rvKelas.layoutManager = LinearLayoutManager(this)
        val adapter = PengajarKelasAdapter(
            isBeranda = true,
            onItemClick = { kelas ->
                val intent = Intent(this, DetailKelasActivity::class.java).apply {
                    putExtra(DetailKelasActivity.EXTRA_CLASS_NAME, kelas.namaMataKuliah)
                    putExtra(DetailKelasActivity.EXTRA_CLASS_CODE, "${kelas.jenisInstitusi} • ${kelas.namaKelas}")
                }
                startActivity(intent)
            },
            onMenuClick = { kelas, _ ->
                Toast.makeText(this, "Menu: ${kelas.namaMataKuliah}", Toast.LENGTH_SHORT).show()
            }
        )
        rvKelas.adapter = adapter

        findViewById<TextView>(R.id.tvLihatSemua).setOnClickListener {
            val intent = Intent(this, PengajarKelasSayaActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.btnNotification).setOnClickListener {
            startActivity(Intent(this, NotifikasiActivity::class.java))
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profilState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            val profil = state.data as PengajarProfilGuru
                            val greeting = findViewById<TextView>(R.id.tvGreeting)
                            if (profil.nama.isNotBlank()) {
                                greeting.text = "Halo, ${profil.nama}! 👋"
                            }
                        }
                    }
                }

                launch {
                    viewModel.statistikState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            val stat = state.data as PengajarStatistikBeranda
                            findViewById<TextView>(R.id.tvStatKelasDiampu).text = stat.kelasDiampu.toString()
                            findViewById<TextView>(R.id.tvStatTugasAktif).text = stat.tugasAktif.toString()
                            findViewById<TextView>(R.id.tvStatPerluDinilai).text = stat.perluDinilai.toString()
                            findViewById<TextView>(R.id.tvStatPelajar).text = stat.totalPelajar.toString()
                        }
                    }
                }

                launch {
                    viewModel.kelasState.collectLatest { state ->
                        val emptyLayout = findViewById<View>(R.id.layoutEmptyBeranda)
                        if (state is PengajarUiState.Success<*>) {
                            val list = state.data as List<PengajarKelas>
                            adapter.submitList(list)
                            rvKelas.visibility = View.VISIBLE
                            emptyLayout.visibility = View.GONE
                        } else if (state is PengajarUiState.Empty) {
                            adapter.submitList(emptyList())
                            rvKelas.visibility = View.GONE
                            emptyLayout.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    viewModel.unreadCount.collectLatest { count ->
                        val badge = findViewById<View>(R.id.notifBadge)
                        badge.visibility = if (count > 0) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }
}
