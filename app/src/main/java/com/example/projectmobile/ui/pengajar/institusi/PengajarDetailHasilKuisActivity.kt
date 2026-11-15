package com.example.projectmobile.ui.pengajar.institusi

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.R
import com.example.projectmobile.UiKit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarDetailHasilKuisActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_HASIL_ID = "extra_hasil_id"
    }

    private val viewModel: DetailHasilKuisViewModel by viewModels {
        val hasilId = intent.getStringExtra(EXTRA_HASIL_ID) ?: "hk_0"
        DetailHasilKuisViewModelFactory(hasilId)
    }

    private lateinit var tvAvatar: TextView
    private lateinit var tvNama: TextView
    private lateinit var tvNim: TextView
    private lateinit var tvNilaiBesar: TextView
    private lateinit var tvWaktu: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var containerJawaban: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(createScreen())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.hasilState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            val hasil = state.data as HasilKuis
                            tvAvatar.text = NameUtils.getInisial(hasil.namaPelajar)
                            tvAvatar.background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, NameUtils.getWarnaAvatar(hasil.namaPelajar), 24)
                            tvNama.text = hasil.namaPelajar
                            tvNim.text = hasil.nim
                            tvNilaiBesar.text = "${hasil.nilai ?: 0}"
                            tvWaktu.text = "${hasil.durasiPengerjaanMenit} menit"
                            tvTanggal.text = FormatTanggal.formatLengkap(hasil.waktuSelesai)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val hasilFlow = viewModel.hasilState
                val soalFlow = viewModel.soalState

                launch {
                    hasilFlow.collectLatest { hState ->
                        soalFlow.collectLatest { sState ->
                            if (hState is PengajarUiState.Success<*> && sState is PengajarUiState.Success<*>) {
                                @Suppress("UNCHECKED_CAST")
                                val hasil = hState.data as HasilKuis
                                @Suppress("UNCHECKED_CAST")
                                val soalList = sState.data as List<Soal>
                                renderJawabanSiswa(soalList, hasil.jawaban)
                            } else {
                                containerJawaban.removeAllViews()
                                containerJawaban.addView(createEmptyView())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createScreen(): View {
        val bgColor = Color.rgb(245, 250, 255)
        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }

        // Top bar
        val topBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(12))
        }
        val btnBack = ImageView(this).apply {
            setImageResource(R.drawable.ic_back)
            setColorFilter(Color.rgb(25, 54, 95))
            setOnClickListener { finish() }
        }
        topBar.addView(btnBack, LinearLayout.LayoutParams(dp(28), dp(28)))

        val titleTv = TextView(this).apply {
            text = "Detail Hasil Kuis"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(12), 0, 0, 0)
        }
        topBar.addView(titleTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val menuView = ImageView(this).apply {
            setImageResource(R.drawable.ic_more_vert)
            setColorFilter(Color.rgb(25, 54, 95))
        }
        topBar.addView(menuView, LinearLayout.LayoutParams(dp(28), dp(28)))
        rootLayout.addView(topBar)

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(24))
        }

        // Profile Card with Score Box
        val profileCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
        }

        val pRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        tvAvatar = TextView(this).apply {
            text = "NP"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, Color.parseColor("#1D5EFF"), 24)
        }
        pRow.addView(tvAvatar, LinearLayout.LayoutParams(dp(48), dp(48)))

        val pTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        tvNama = TextView(this).apply {
            text = "Nadia Putri"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        pTextCol.addView(tvNama)
        tvNim = TextView(this).apply {
            text = "10522001"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        pTextCol.addView(tvNim, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        pRow.addView(pTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        profileCard.addView(pRow)

        // Score Box (Light Green)
        val scoreBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, Color.parseColor("#E8F5E9"), 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) }
        }
        tvNilaiBesar = TextView(this).apply {
            text = "85"
            textSize = 32f
            setTextColor(Color.parseColor("#2E7D32"))
            setTypeface(null, Typeface.BOLD)
        }
        scoreBox.addView(tvNilaiBesar)
        val tvDari = TextView(this).apply {
            text = "Dari 100"
            textSize = 12f
            setTextColor(Color.parseColor("#2E7D32"))
        }
        scoreBox.addView(tvDari, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        profileCard.addView(scoreBox)

        content.addView(profileCard)

        // Informasi Pengerjaan Card
        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }
        }
        val iTitle = TextView(this).apply {
            text = "Informasi Pengerjaan"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        infoCard.addView(iTitle)

        val iRow1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(10) }
        }
        iRow1.addView(TextView(this).apply { text = "Waktu Pengerjaan"; textSize = 13f; setTextColor(Color.rgb(117, 139, 169)) }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        tvWaktu = TextView(this).apply { text = "18 menit"; textSize = 13f; setTextColor(Color.rgb(25, 54, 95)); setTypeface(null, Typeface.BOLD) }
        iRow1.addView(tvWaktu)
        infoCard.addView(iRow1)

        val iRow2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) }
        }
        iRow2.addView(TextView(this).apply { text = "Tanggal"; textSize = 13f; setTextColor(Color.rgb(117, 139, 169)) }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        tvTanggal = TextView(this).apply { text = "12 Jun 2025, 14.30"; textSize = 13f; setTextColor(Color.rgb(25, 54, 95)); setTypeface(null, Typeface.BOLD) }
        iRow2.addView(tvTanggal)
        infoCard.addView(iRow2)

        content.addView(infoCard)

        // Jawaban Siswa Section Header
        val jsHeader = TextView(this).apply {
            text = "Jawaban Siswa"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(16), 0, dp(8))
        }
        content.addView(jsHeader)

        containerJawaban = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(containerJawaban)

        scrollView.addView(content)
        rootLayout.addView(scrollView)

        // Bottom Button Pinned
        val bottomBar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }
        val btnKembali = TextView(this).apply {
            text = "Kembali ke Daftar Nilai"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, Color.parseColor("#1D5EFF"), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            setOnClickListener { finish() }
        }
        bottomBar.addView(btnKembali, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        rootLayout.addView(bottomBar)

        return rootLayout
    }

    private fun renderJawabanSiswa(soalList: List<Soal>, jawabanList: List<JawabanSoal>) {
        containerJawaban.removeAllViews()
        val jawabanMap = jawabanList.associateBy { it.soalId }

        for (soal in soalList) {
            val jawab = jawabanMap[soal.id]
            val benar = jawab?.benar == true
            val kodeDipilih = jawab?.kodeDipilih ?: "-"

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(14), dp(14), dp(14), dp(14))
                background = UiKit.rounded(this@PengajarDetailHasilKuisActivity, if (benar) Color.parseColor("#E8F5E9") else Color.parseColor("#FFEBEE"), 16, if (benar) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(12) }
            }

            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val qTv = TextView(this).apply {
                text = "${soal.nomor}. ${soal.pertanyaan}"
                textSize = 14f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
            }
            topRow.addView(qTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

            val iconStatus = ImageView(this).apply {
                setImageResource(if (benar) R.drawable.ic_check_circle else R.drawable.ic_close)
                setColorFilter(if (benar) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"))
            }
            topRow.addView(iconStatus, LinearLayout.LayoutParams(dp(24), dp(24)).apply { leftMargin = dp(8) })
            card.addView(topRow)

            // Selected Answer Row
            val opsiDipilih = soal.opsi.find { it.kode == kodeDipilih }
            val teksOpsi = opsiDipilih?.teks ?: "-"

            val ansRow = TextView(this).apply {
                text = "Jawaban siswa: $kodeDipilih. $teksOpsi"
                textSize = 12f
                setTextColor(if (benar) Color.parseColor("#2E7D32") else Color.parseColor("#C62828"))
                setTypeface(null, Typeface.BOLD)
            }
            card.addView(ansRow, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })

            containerJawaban.addView(card)
        }
    }

    private fun createEmptyView(): View {
        return TextView(this).apply {
            text = "Belum ada jawaban"
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
            setPadding(0, dp(32), 0, dp(32))
        }
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
