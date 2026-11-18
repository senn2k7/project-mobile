package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.ui.pengajar.institusi.KelasRepository
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BerandaPelajarInstitusiActivity : AppCompatActivity() {

    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo()

    private lateinit var tvStatKelas: TextView
    private lateinit var tvStatTugas: TextView
    private lateinit var tvStatKuis: TextView
    private lateinit var containerKelasPreview: LinearLayout
    private lateinit var tvLihatSemuaKelas: TextView
    private lateinit var btnMulaiBertanya: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(R.layout.activity_beranda_pelajar_institusi)

        bindViews()
        setupBottomNav()
        setupListeners()
        loadStatsAndClasses()
    }

    private fun bindViews() {
        tvStatKelas = findViewById(R.id.tvStatKelas)
        tvStatTugas = findViewById(R.id.tvStatTugas)
        tvStatKuis = findViewById(R.id.tvStatKuis)
        containerKelasPreview = findViewById(R.id.containerKelasPreview)
        tvLihatSemuaKelas = findViewById(R.id.tvLihatSemuaKelas)
        btnMulaiBertanya = findViewById(R.id.btnMulaiBertanya)
    }

    private fun setupBottomNav() {
        val bottomNavContainer = findViewById<FrameLayout>(R.id.bottomNavContainer)
        bottomNavContainer.addView(PelajarBottomNavHelper.create(this, "Beranda"))
    }

    private fun setupListeners() {
        tvLihatSemuaKelas.setOnClickListener {
            val intent = Intent(this, KelasPelajarInstitusiActivity::class.java).apply {
                putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PELAJAR)
                putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
            }
            startActivity(intent)
        }

        btnMulaiBertanya.setOnClickListener {
            Toast.makeText(this, "Kelola AI Asisten (TODO)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadStatsAndClasses() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                kelasRepo.getKelasDiikuti("", "pelajar_me").collectLatest { list ->
                    tvStatKelas.text = list.size.toString()
                    tvStatTugas.text = "2" // Pending tasks mock count
                    tvStatKuis.text = "1"  // Upcoming quiz mock count

                    containerKelasPreview.removeAllViews()
                    val previewList = list.take(3)
                    for (kelas in previewList) {
                        val card = layoutInflater.inflate(R.layout.item_kelas_pelajar, containerKelasPreview, false)
                        card.findViewById<TextView>(R.id.tvNamaMataKuliah).text = kelas.namaMataKuliah
                        card.findViewById<TextView>(R.id.tvDosenProdi).text = "${kelas.namaKelas} • ${kelas.semester}"
                        card.findViewById<TextView>(R.id.tvPeserta).text = "${kelas.jumlahPelajar} peserta"
                        card.setOnClickListener {
                            val intent = Intent(this@BerandaPelajarInstitusiActivity, DetailKelasPelajarInstitusiActivity::class.java).apply {
                                putExtra(DetailKelasPelajarInstitusiActivity.EXTRA_CLASS_NAME, kelas.namaMataKuliah)
                            }
                            startActivity(intent)
                        }
                        containerKelasPreview.addView(card)
                    }
                }
            }
        }
    }
}
