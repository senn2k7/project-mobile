package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.UiKit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarDetailTugasActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TUGAS_ID = "extra_tugas_id"
    }

    private val viewModel: DetailTugasViewModel by viewModels {
        val tugasId = intent.getStringExtra(EXTRA_TUGAS_ID) ?: "t1"
        DetailTugasViewModelFactory(tugasId)
    }

    private lateinit var tvJudul: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvBadge: TextView
    private lateinit var tvDeskripsi: TextView
    private lateinit var tvBatas: TextView
    private lateinit var tvTipe: TextView
    private lateinit var lampiranContainer: LinearLayout
    private lateinit var tvSudahKumpul: TextView
    private lateinit var tvBelumKumpul: TextView
    private lateinit var btnLihatPengumpulan: TextView

    private var currentTugasId: String = "t1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        currentTugasId = intent.getStringExtra(EXTRA_TUGAS_ID) ?: "t1"

        setContentView(createScreen())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.tugasState.collectLatest { state ->
                        if (state is PengajarUiState.Success<*>) {
                            val tugas = state.data as Tugas
                            tvJudul.text = tugas.judul
                            tvSubtitle.text = "Pemrograman Mobile • Pertemuan ${tugas.pertemuanKe}"
                            tvDeskripsi.text = if (tugas.deskripsi.isNotBlank()) tugas.deskripsi else "Belum ada deskripsi"
                            tvBatas.text = FormatTanggal.formatLengkap(tugas.tenggat)
                            tvTipe.text = tugas.tipe.replaceFirstChar { it.uppercase() }

                            val isBerlangsung = TaskStatusUtils.hitungStatus(tugas.tenggat) == "Berlangsung"
                            tvBadge.text = if (isBerlangsung) "Tugas Berlangsung" else "Tugas Selesai"
                            tvBadge.setTextColor(if (isBerlangsung) Color.parseColor("#1D5EFF") else Color.parseColor("#2E7D32"))
                            tvBadge.background = UiKit.rounded(this@PengajarDetailTugasActivity, if (isBerlangsung) Color.parseColor("#E8F2FF") else Color.parseColor("#E8F5E9"), 8)

                            tvSudahKumpul.text = "${tugas.jumlahMengumpulkan} Pelajar"
                            tvBelumKumpul.text = "${tugas.totalPelajar - tugas.jumlahMengumpulkan} Pelajar"
                        }
                    }
                }

                launch {
                    viewModel.lampiranState.collectLatest { state ->
                        lampiranContainer.removeAllViews()
                        if (state is PengajarUiState.Success<*>) {
                            @Suppress("UNCHECKED_CAST")
                            val lampiranList = state.data as List<LampiranTugas>
                            if (lampiranList.isEmpty()) {
                                lampiranContainer.addView(createEmptyLampiran())
                            } else {
                                for (lampiran in lampiranList) {
                                    lampiranContainer.addView(createLampiranCard(lampiran))
                                }
                            }
                        } else {
                            lampiranContainer.addView(createEmptyLampiran())
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
        val btnBack = TextView(this).apply {
            text = "‹"
            textSize = 32f
            setTextColor(Color.rgb(25, 54, 95))
            setOnClickListener { finish() }
        }
        topBar.addView(btnBack, LinearLayout.LayoutParams(dp(32), dp(36)))

        val titleTv = TextView(this).apply {
            text = "Detail Tugas"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        topBar.addView(titleTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val menuTv = TextView(this).apply {
            text = "⋮"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
        }
        topBar.addView(menuTv, LinearLayout.LayoutParams(dp(36), dp(36)))
        rootLayout.addView(topBar)

        // Scroll
        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(24))
        }

        // Header Card
        val headerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val iconBox = TextView(this).apply {
            text = "💻"
            textSize = 20f
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.parseColor("#1D5EFF"), 12)
        }
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(44), dp(44)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        tvJudul = TextView(this).apply {
            text = ""
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(tvJudul)

        tvSubtitle = TextView(this).apply {
            text = "Pemrograman Mobile • Pertemuan 2"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(tvSubtitle, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        headerCard.addView(topRow)

        tvBadge = TextView(this).apply {
            text = "Tugas Berlangsung"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#1D5EFF"))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.parseColor("#E8F2FF"), 8)
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }
        headerCard.addView(tvBadge, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })

        content.addView(headerCard)

        // Deskripsi Card
        val descCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
        }
        val dTitle = TextView(this).apply {
            text = "Deskripsi Tugas"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        descCard.addView(dTitle)

        tvDeskripsi = TextView(this).apply {
            text = "Buat antarmuka login menggunakan ConstraintLayout dan Material Components."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        descCard.addView(tvDeskripsi, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) })

        // Batas & Tipe Row
        val infoRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val infoParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(14) }

        val lBatas = TextView(this).apply {
            text = "🗓️ Batas: "
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        tvBatas = TextView(this).apply {
            text = "-"
            textSize = 12f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val batasCol = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(lBatas)
            addView(tvBatas)
        }
        infoRow.addView(batasCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val lTipe = TextView(this).apply {
            text = "👥 Tipe: "
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        tvTipe = TextView(this).apply {
            text = "Individu"
            textSize = 12f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val tipeCol = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            addView(lTipe)
            addView(tvTipe)
        }
        infoRow.addView(tipeCol)

        descCard.addView(infoRow, infoParams)
        content.addView(descCard, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })

        // Lampiran Tugas Section
        val lampiranCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
        }
        val lTitle = TextView(this).apply {
            text = "Lampiran Tugas"
            textSize = 15f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        lampiranCard.addView(lTitle)

        lampiranContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) }
        }
        lampiranCard.addView(lampiranContainer)
        content.addView(lampiranCard, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })

        // Status Pengumpulan Section (2 stat cards: Hijau & Pink)
        val statRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }
        val statParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }

        // Card Hijau (Sudah)
        val cardHijau = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.parseColor("#E8F5E9"), 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { rightMargin = dp(6) }
        }
        val shTitle = TextView(this).apply {
            text = "Mengumpulkan"
            textSize = 12f
            setTextColor(Color.parseColor("#2E7D32"))
            setTypeface(null, Typeface.BOLD)
        }
        cardHijau.addView(shTitle)
        tvSudahKumpul = TextView(this).apply {
            text = "0 Pelajar"
            textSize = 18f
            setTextColor(Color.parseColor("#2E7D32"))
            setTypeface(null, Typeface.BOLD)
        }
        cardHijau.addView(tvSudahKumpul, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })
        statRow.addView(cardHijau)

        // Card Pink/Merah Muda (Belum)
        val cardPink = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.parseColor("#FFEBEE"), 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(6) }
        }
        val sbTitle = TextView(this).apply {
            text = "Belum Mengumpulkan"
            textSize = 12f
            setTextColor(Color.parseColor("#C62828"))
            setTypeface(null, Typeface.BOLD)
        }
        cardPink.addView(sbTitle)
        tvBelumKumpul = TextView(this).apply {
            text = "0 Pelajar"
            textSize = 18f
            setTextColor(Color.parseColor("#C62828"))
            setTypeface(null, Typeface.BOLD)
        }
        cardPink.addView(tvBelumKumpul, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })
        statRow.addView(cardPink)

        content.addView(statRow, statParams)

        // Action Buttons
        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }
        val actParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) }

        btnLihatPengumpulan = TextView(this).apply {
            text = "Lihat Pengumpulan"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.parseColor("#1D5EFF"), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { rightMargin = dp(6) }
            setOnClickListener {
                val intent = Intent(this@PengajarDetailTugasActivity, PengajarPengumpulanTugasActivity::class.java).apply {
                    putExtra(PengajarPengumpulanTugasActivity.EXTRA_TUGAS_ID, currentTugasId)
                }
                startActivity(intent)
            }
        }
        actionRow.addView(btnLihatPengumpulan)

        val btnEditTugas = TextView(this).apply {
            text = "Edit Tugas"
            textSize = 14f
            setTextColor(Color.parseColor("#1D5EFF"))
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.WHITE, 12, Color.parseColor("#1D5EFF"), 1)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(6) }
        }
        actionRow.addView(btnEditTugas)

        content.addView(actionRow, actParams)

        scrollView.addView(content)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    private fun createEmptyLampiran(): View {
        return TextView(this).apply {
            text = "Tidak ada lampiran"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(0, dp(8), 0, dp(8))
        }
    }

    private fun createLampiranCard(lampiran: LampiranTugas): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(12), dp(10))
            background = UiKit.rounded(this@PengajarDetailTugasActivity, Color.parseColor("#F8FAFC"), 12, Color.parseColor("#E2E8F0"), 1)
        }
        val icon = TextView(this).apply {
            text = "📄"
            textSize = 16f
        }
        card.addView(icon)

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        val name = TextView(this).apply {
            text = lampiran.nama
            textSize = 13f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(name)
        val size = TextView(this).apply {
            text = FormatUkuran.format(lampiran.ukuranByte)
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(size)
        card.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return card
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
