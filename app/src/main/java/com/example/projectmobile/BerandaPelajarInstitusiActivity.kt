package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.ui.pengajar.institusi.FormatTanggal
import com.example.projectmobile.ui.pengajar.institusi.KelasRepository
import com.example.projectmobile.ui.pengajar.institusi.KuisRepository
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import com.example.projectmobile.ui.pengajar.institusi.TugasRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class AgendaUiModel(
    val title: String,
    val dateText: String,
    val timestamp: Long,
    val isKuis: Boolean
)

class BerandaPelajarInstitusiActivity : AppCompatActivity() {

    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo()
    private val tugasRepo: TugasRepository = PengajarRepositoryProvider.provideTugasRepo()
    private val kuisRepo: KuisRepository = PengajarRepositoryProvider.provideKuisRepo()

    private lateinit var tvStatKelas: TextView
    private lateinit var tvStatTugas: TextView
    private lateinit var tvStatKuis: TextView
    private lateinit var containerKelasPreview: LinearLayout
    private lateinit var containerAgendaPreview: LinearLayout
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
        containerAgendaPreview = findViewById(R.id.containerAgendaPreview)
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
                // 1. Load Kelas
                launch {
                    kelasRepo.getKelasDiikuti("", "pelajar_me").collectLatest { list ->
                        tvStatKelas.text = list.size.toString()

                        containerKelasPreview.removeAllViews()
                        if (list.isEmpty()) {
                            val emptyTv = TextView(this@BerandaPelajarInstitusiActivity).apply {
                                text = "Belum ada kelas yang diikuti"
                                textSize = 14f
                                setTextColor(Color.parseColor("#7186A5"))
                                gravity = Gravity.CENTER
                                setPadding(0, dp(16), 0, dp(16))
                            }
                            containerKelasPreview.addView(emptyTv)
                        } else {
                            val previewList = list.take(3)
                            for (kelas in previewList) {
                                val card = layoutInflater.inflate(R.layout.item_kelas_pelajar, containerKelasPreview, false)
                                card.findViewById<TextView>(R.id.tvNamaMataKuliah).text = kelas.namaMataKuliah
                                card.findViewById<TextView>(R.id.tvDosenProdi).text = "${kelas.namaKelas} • ${kelas.semester}"
                                card.findViewById<TextView>(R.id.tvPeserta).text = "${kelas.jumlahPelajar} peserta"
                                card.setOnClickListener {
                                    val intent = Intent(this@BerandaPelajarInstitusiActivity, DetailKelasPelajarInstitusiActivity::class.java).apply {
                                        putExtra(DetailKelasPelajarInstitusiActivity.EXTRA_KELAS_ID, kelas.id)
                                        putExtra(PilihPeranActivity.EXTRA_ROLE, PilihPeranActivity.ROLE_PELAJAR)
                                        putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, ModePembelajaranActivity.MODE_INSTITUSI)
                                    }
                                    startActivity(intent)
                                }
                                containerKelasPreview.addView(card)
                            }
                        }
                    }
                }

                // 2. Load Tugas & Kuis for Stats & Agenda
                launch {
                    combine(
                        tugasRepo.tugasMendatang("pelajar_me"),
                        kuisRepo.kuisMendatang("pelajar_me")
                    ) { tugasList, kuisList ->
                        tvStatTugas.text = tugasList.size.toString()
                        tvStatKuis.text = kuisList.size.toString()

                        val agendas = mutableListOf<AgendaUiModel>()
                        for (t in tugasList) {
                            agendas.add(AgendaUiModel("Tugas: ${t.judul}", FormatTanggal.formatTanpaTahun(t.tenggat), t.tenggat, false))
                        }
                        for (k in kuisList) {
                            agendas.add(AgendaUiModel("Kuis: ${k.judul}", FormatTanggal.formatTanpaTahun(k.createdAt + 86400000L * 2), k.createdAt, true))
                        }
                        agendas.sortedBy { it.timestamp }
                    }.collectLatest { agendaList ->
                        containerAgendaPreview.removeAllViews()
                        if (agendaList.isEmpty()) {
                            val emptyAgendaTv = TextView(this@BerandaPelajarInstitusiActivity).apply {
                                text = "Belum ada agenda mendatang"
                                textSize = 14f
                                setTextColor(Color.parseColor("#7186A5"))
                                gravity = Gravity.CENTER
                                setPadding(0, dp(16), 0, dp(16))
                            }
                            containerAgendaPreview.addView(emptyAgendaTv)
                        } else {
                            for (agenda in agendaList.take(3)) {
                                val card = createAgendaCard(agenda.title, agenda.dateText, agenda.isKuis)
                                containerAgendaPreview.addView(card)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createAgendaCard(title: String, date: String, isKuis: Boolean): View {
        val card = androidx.cardview.widget.CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = dp(8)
            }
            radius = dp(16).toFloat()
            cardElevation = dp(1).toFloat()
            setCardBackgroundColor(Color.WHITE)
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }
        val icon = ImageView(this).apply {
            setImageResource(if (isKuis) R.drawable.ic_code else R.drawable.ic_description)
            setColorFilter(if (isKuis) Color.parseColor("#8A5CF5") else Color.parseColor("#FB8C00"))
        }
        layout.addView(icon, LinearLayout.LayoutParams(dp(24), dp(24)))

        val titleTv = TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(Color.parseColor("#19365F"))
            setTypeface(null, Typeface.BOLD)
        }
        layout.addView(titleTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(12) })

        val dateTv = TextView(this).apply {
            text = date
            textSize = 12f
            setTextColor(Color.parseColor("#7186A5"))
        }
        layout.addView(dateTv)

        card.addView(layout)
        return card
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
