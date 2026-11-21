package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.HasilKuis
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import com.example.projectmobile.ui.pengajar.institusi.PengajarUiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class MulaiKuisPelajarActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KUIS_ID = "extra_kuis_id"
        const val EXTRA_QUIZ_TITLE = "extra_quiz_title"
    }

    private val viewModel: KuisPengerjaanViewModel by viewModels {
        val kuisId = intent.getStringExtra(EXTRA_KUIS_ID) ?: "k1"
        KuisPengerjaanViewModelFactory(kuisId)
    }

    private val hasilRepo = PengajarRepositoryProvider.provideHasilKuisRepo()
    private val profilRepo = PengajarRepositoryProvider.provideProfilRepo()

    private lateinit var tvProgressSoal: TextView
    private lateinit var tvTimer: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPertanyaan: TextView
    private lateinit var rvOpsi: RecyclerView
    private lateinit var btnSebelumnya: TextView
    private lateinit var btnSelanjutnya: TextView

    private var countDownTimer: CountDownTimer? = null
    private var quizTitle: String = "Kuis Sistem Cerdas"
    private var kuisId: String = "k1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        kuisId = intent.getStringExtra(EXTRA_KUIS_ID) ?: "k1"
        quizTitle = intent.getStringExtra(EXTRA_QUIZ_TITLE) ?: "Kuis Sistem Cerdas"

        if (kuisId.isBlank()) {
            Toast.makeText(this, "Kuis tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_mulai_kuis_pelajar)

        bindViews()
        setupListeners()
        observeViewModel()
        startTimer()
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        tvProgressSoal = findViewById(R.id.tvProgressSoal)
        tvTimer = findViewById(R.id.tvTimer)
        progressBar = findViewById(R.id.progressBar)
        tvPertanyaan = findViewById(R.id.tvPertanyaan)
        rvOpsi = findViewById(R.id.rvOpsi)
        rvOpsi.layoutManager = LinearLayoutManager(this)
        btnSebelumnya = findViewById(R.id.btnSebelumnya)
        btnSelanjutnya = findViewById(R.id.btnSelanjutnya)
    }

    private fun setupListeners() {
        btnSebelumnya.setOnClickListener {
            if (viewModel.currentIndex > 0) {
                viewModel.currentIndex--
                displayCurrentSoal()
            } else {
                Toast.makeText(this, "Ini soal pertama", Toast.LENGTH_SHORT).show()
            }
        }

        btnSelanjutnya.setOnClickListener {
            val total = viewModel.soalList.size
            if (viewModel.currentIndex < total - 1) {
                viewModel.currentIndex++
                displayCurrentSoal()
            } else {
                submitKuis()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (state is PengajarUiState.Success<*>) {
                        displayCurrentSoal()
                    }
                }
            }
        }
    }

    private fun displayCurrentSoal() {
        val list = viewModel.soalList
        if (list.isEmpty()) return

        val index = viewModel.currentIndex
        val total = list.size
        val soal = list[index]

        tvProgressSoal.text = "Soal ${index + 1} dari $total"
        progressBar.progress = ((index + 1) * 100) / total
        tvPertanyaan.text = "${index + 1}. ${soal.pertanyaan}"

        btnSebelumnya.visibility = if (index > 0) View.VISIBLE else View.INVISIBLE
        btnSelanjutnya.text = if (index == total - 1) "Kirim" else "Selanjutnya"

        val selectedKode = viewModel.jawabanMap[index]
        rvOpsi.adapter = PelajarOpsiAdapter(soal.opsi, selectedKode) { opsi ->
            viewModel.jawabanMap[index] = opsi.kode
            rvOpsi.adapter?.notifyDataSetChanged()
        }
    }

    private fun startTimer() {
        val elapsed = System.currentTimeMillis() - viewModel.startTime
        val remaining = viewModel.durasiMs - elapsed

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                tvTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                Toast.makeText(this@MulaiKuisPelajarActivity, "Waktu habis! Kuis disubmit otomatis.", Toast.LENGTH_LONG).show()
                submitKuis()
            }
        }.start()
    }

    private fun submitKuis() {
        countDownTimer?.cancel()

        lifecycleScope.launch {
            var pelajarId = ""
            profilRepo.getProfil().collectLatest { profil ->
                if (profil != null) {
                    pelajarId = profil.uid
                }
            }

            if (pelajarId.isBlank()) {
                Toast.makeText(this@MulaiKuisPelajarActivity, "Pelajar tidak teridentifikasi", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val soalList = viewModel.soalList
            var benarCount = 0
            for ((index, soal) in soalList.withIndex()) {
                val selected = viewModel.jawabanMap[index]
                if (selected != null && selected.equals(soal.kunciJawaban, ignoreCase = true)) {
                    benarCount++
                }
            }

            val total = if (soalList.isNotEmpty()) soalList.size else 5
            val nilai = (benarCount * 100) / total
            val lulus = nilai >= 70

            val hasil = HasilKuis(
                kuisId = kuisId,
                pelajarId = pelajarId,
                namaPelajar = "Andi Pratama",
                waktuMulai = viewModel.startTime,
                waktuSelesai = System.currentTimeMillis(),
                nilai = nilai,
                status = "selesai",
                lulus = lulus
            )

            val result = hasilRepo.simpanHasilKuis(hasil)
            if (result.isSuccess) {
                val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
                val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI
                val intent = UiKit.withProfile(Intent(this@MulaiKuisPelajarActivity, HasilKuisPelajarActivity::class.java), role, mode).apply {
                    putExtra(HasilKuisPelajarActivity.EXTRA_QUIZ_TITLE, quizTitle)
                    putExtra(HasilKuisPelajarActivity.EXTRA_NILAI, nilai)
                    putExtra(HasilKuisPelajarActivity.EXTRA_BENAR, benarCount)
                    putExtra(HasilKuisPelajarActivity.EXTRA_TOTAL, total)
                    putExtra(HasilKuisPelajarActivity.EXTRA_LULUS, lulus)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@MulaiKuisPelajarActivity, "Gagal menyimpan hasil kuis", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
