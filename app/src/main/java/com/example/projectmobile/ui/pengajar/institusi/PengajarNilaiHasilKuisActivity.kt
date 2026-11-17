package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.UiKit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarNilaiHasilKuisActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS_ID = "extra_kelas_id"
        const val EXTRA_KUIS_ID = "extra_kuis_id"
    }

    private val viewModel: NilaiHasilKuisViewModel by viewModels {
        val kelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: "1"
        NilaiHasilKuisViewModelFactory(kelasId)
    }

    private lateinit var tvKuisTitle: TextView
    private lateinit var tvKuisSub: TextView
    private lateinit var tvStatSelesai: TextView
    private lateinit var tvStatBelum: TextView
    private lateinit var tvStatRata: TextView
    private lateinit var containerList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(createScreen())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedKuis.collectLatest { kuis ->
                        if (kuis != null) {
                            tvKuisTitle.text = kuis.judul
                            tvKuisSub.text = "Pertemuan ${kuis.pertemuanKe} • ${FormatTanggal.formatTanpaTahun(kuis.createdAt)}"
                        } else {
                            tvKuisTitle.text = "Belum ada kuis"
                            tvKuisSub.text = "-"
                        }
                    }
                }

                launch {
                    viewModel.statistikState.collectLatest { (selesai, belum, rata) ->
                        tvStatSelesai.text = selesai.toString()
                        tvStatBelum.text = belum.toString()
                        tvStatRata.text = rata
                    }
                }

                launch {
                    viewModel.hasilState.collectLatest { state ->
                        when (state) {
                            is PengajarUiState.Success<*> -> {
                                @Suppress("UNCHECKED_CAST")
                                val list = state.data as List<HasilKuis>
                                renderHasilList(list)
                            }
                            is PengajarUiState.Empty -> {
                                renderHasilList(emptyList())
                            }
                            else -> {
                                renderHasilList(emptyList())
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
        val btnBack = TextView(this).apply {
            text = "‹"
            textSize = 32f
            setTextColor(Color.rgb(25, 54, 95))
            setOnClickListener { finish() }
        }
        topBar.addView(btnBack, LinearLayout.LayoutParams(dp(32), dp(36)))

        val titleTv = TextView(this).apply {
            text = "Nilai dan Hasil Kuis"
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

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(24))
        }

        // Header Card (Building icon + Course & Quiz Selector Card)
        val headerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.WHITE, 16)
            elevation = dp(1).toFloat()
        }

        val courseRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val bIcon = TextView(this).apply { text = "🏛️"; textSize = 18f }
        courseRow.addView(bIcon)
        val tvCourse = TextView(this).apply {
            text = "Pemrograman Mobile – Kelas A"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        courseRow.addView(tvCourse)
        headerCard.addView(courseRow)

        // Quiz Selector Card (Light blue)
        val quizCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.parseColor("#E8F2FF"), 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }
        }
        val qIcon = TextView(this).apply { text = "📋"; textSize = 20f }
        quizCard.addView(qIcon)

        val qTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), 0, 0, 0)
        }
        tvKuisTitle = TextView(this).apply {
            text = "Kuis Struktur Data"
            textSize = 14f
            setTextColor(Color.parseColor("#1D5EFF"))
            setTypeface(null, Typeface.BOLD)
        }
        qTextCol.addView(tvKuisTitle)
        tvKuisSub = TextView(this).apply {
            text = "Pertemuan 2 • 12 Jun 2025"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        qTextCol.addView(tvKuisSub, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        quizCard.addView(qTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val qArrow = TextView(this).apply { text = "▾"; textSize = 16f; setTextColor(Color.parseColor("#1D5EFF")) }
        quizCard.addView(qArrow)

        headerCard.addView(quizCard)
        content.addView(headerCard)

        // 3 Stat Cards Row
        val statRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 3f
        }
        val statParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) }

        // 1. Selesai (Hijau)
        val c1 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.parseColor("#E8F5E9"), 14)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { rightMargin = dp(4) }
        }
        c1.addView(TextView(this).apply { text = "Selesai"; textSize = 11f; setTextColor(Color.parseColor("#2E7D32")); setTypeface(null, Typeface.BOLD) })
        tvStatSelesai = TextView(this).apply { text = "0"; textSize = 18f; setTextColor(Color.parseColor("#2E7D32")); setTypeface(null, Typeface.BOLD) }
        c1.addView(tvStatSelesai, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        statRow.addView(c1)

        // 2. Belum Selesai (Pink)
        val c2 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.parseColor("#FFEBEE"), 14)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(2); rightMargin = dp(2) }
        }
        c2.addView(TextView(this).apply { text = "Belum Selesai"; textSize = 11f; setTextColor(Color.parseColor("#C62828")); setTypeface(null, Typeface.BOLD) })
        tvStatBelum = TextView(this).apply { text = "0"; textSize = 18f; setTextColor(Color.parseColor("#C62828")); setTypeface(null, Typeface.BOLD) }
        c2.addView(tvStatBelum, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        statRow.addView(c2)

        // 3. Rata-rata Nilai (Biru)
        val c3 = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.parseColor("#E8F2FF"), 14)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(4) }
        }
        c3.addView(TextView(this).apply { text = "Rata-rata"; textSize = 11f; setTextColor(Color.parseColor("#1D5EFF")); setTypeface(null, Typeface.BOLD) })
        tvStatRata = TextView(this).apply { text = "-"; textSize = 18f; setTextColor(Color.parseColor("#1D5EFF")); setTypeface(null, Typeface.BOLD) }
        c3.addView(tvStatRata, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        statRow.addView(c3)

        content.addView(statRow, statParams)

        // Search Bar
        val searchBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), 0, dp(12), 0)
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
        }
        val sIcon = TextView(this).apply { text = "🔍"; textSize = 16f }
        searchBox.addView(sIcon)
        val etSearch = EditText(this).apply {
            hint = "Cari nama pelajar..."
            textSize = 14f
            background = null
            setPadding(dp(8), dp(12), dp(8), dp(12))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.setSearchQuery(s?.toString().orEmpty())
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        searchBox.addView(etSearch, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        content.addView(searchBox, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })

        // List Header ("Nama Pelajar" vs "Nilai & Status")
        val listHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(8), dp(4), dp(4))
        }
        val hLeft = TextView(this).apply { text = "Nama Pelajar"; textSize = 12f; setTextColor(Color.rgb(117, 139, 169)); setTypeface(null, Typeface.BOLD) }
        listHeader.addView(hLeft, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        val hRight = TextView(this).apply { text = "Nilai & Status"; textSize = 12f; setTextColor(Color.rgb(117, 139, 169)); setTypeface(null, Typeface.BOLD) }
        listHeader.addView(hRight)
        content.addView(listHeader, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })

        // Container List
        containerList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(containerList, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })

        scrollView.addView(content)
        rootLayout.addView(scrollView)

        return rootLayout
    }

    private fun renderHasilList(list: List<HasilKuis>) {
        containerList.removeAllViews()

        if (list.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "Belum ada hasil kuis"
                textSize = 14f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.CENTER
                setPadding(0, dp(32), 0, dp(32))
            }
            containerList.addView(emptyTv)
            return
        }

        for (item in list) {
            containerList.addView(createHasilItemCard(item))
        }
    }

    private fun createHasilItemCard(item: HasilKuis): View {
        val isSelesai = item.status == "selesai"

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.WHITE, 16, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(8) }
        }

        val avatar = TextView(this).apply {
            text = NameUtils.getInisial(item.namaPelajar)
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, NameUtils.getWarnaAvatar(item.namaPelajar), 20)
        }
        card.addView(avatar, LinearLayout.LayoutParams(dp(36), dp(36)))

        val nameCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        val nameTv = TextView(this).apply {
            text = item.namaPelajar
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        nameCol.addView(nameTv)
        val nimTv = TextView(this).apply {
            text = item.nim
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        nameCol.addView(nimTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        card.addView(nameCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val rightCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.END
        }

        if (isSelesai) {
            val scoreTv = TextView(this).apply {
                text = "${item.nilai ?: 0}"
                textSize = 16f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.END
            }
            rightCol.addView(scoreTv)

            val badge = TextView(this).apply {
                text = "Selesai"
                textSize = 11f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#2E7D32"))
                background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.parseColor("#E8F5E9"), 8)
                setPadding(dp(8), dp(4), dp(8), dp(4))
                gravity = Gravity.CENTER
            }
            rightCol.addView(badge, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })

            val btnDetail = TextView(this).apply {
                text = "Lihat Jawaban"
                textSize = 11f
                setTextColor(Color.parseColor("#1D5EFF"))
                background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.WHITE, 8, Color.parseColor("#1D5EFF"), 1)
                setPadding(dp(8), dp(4), dp(8), dp(4))
                gravity = Gravity.CENTER
                setOnClickListener {
                    val intent = Intent(this@PengajarNilaiHasilKuisActivity, PengajarDetailHasilKuisActivity::class.java).apply {
                        putExtra(PengajarDetailHasilKuisActivity.EXTRA_HASIL_ID, item.id)
                    }
                    startActivity(intent)
                }
            }
            rightCol.addView(btnDetail, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) })

        } else {
            val dashTv = TextView(this).apply {
                text = "-"
                textSize = 16f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.END
            }
            rightCol.addView(dashTv)

            val badge = TextView(this).apply {
                text = "Belum Selesai"
                textSize = 11f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#C62828"))
                background = UiKit.rounded(this@PengajarNilaiHasilKuisActivity, Color.parseColor("#FFEBEE"), 8)
                setPadding(dp(8), dp(4), dp(8), dp(4))
                gravity = Gravity.CENTER
            }
            rightCol.addView(badge, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })
        }

        card.addView(rightCol)
        return card
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
