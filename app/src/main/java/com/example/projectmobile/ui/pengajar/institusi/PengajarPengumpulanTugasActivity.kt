package com.example.projectmobile.ui.pengajar.institusi

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
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
    private lateinit var containerList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(createScreen())

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
                            renderPengumpulanList(data)
                        }
                        is PengajarUiState.Empty -> {
                            tvStatSudah.text = "0"
                            tvStatBelum.text = "0"
                            chipSemua.text = "Semua (0)"
                            chipSudah.text = "Sudah mengumpulkan (0)"
                            chipBelum.text = "Belum (0)"
                            renderPengumpulanList(DaftarPengumpulan())
                        }
                        else -> {
                            renderPengumpulanList(DaftarPengumpulan())
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

        // Top bar (No three-dots menu as per design note)
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
            text = "Pengumpulan Tugas"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(12), 0, 0, 0)
        }
        topBar.addView(titleTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
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

        // 2 Stat cards (Hijau / Pink)
        val statRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
        }

        val cardHijau = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.parseColor("#E8F5E9"), 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { rightMargin = dp(6) }
        }
        val shTitle = TextView(this).apply {
            text = "Mengumpulkan"
            textSize = 12f
            setTextColor(Color.parseColor("#2E7D32"))
            setTypeface(null, Typeface.BOLD)
        }
        cardHijau.addView(shTitle)
        tvStatSudah = TextView(this).apply {
            text = "0"
            textSize = 22f
            setTextColor(Color.parseColor("#2E7D32"))
            setTypeface(null, Typeface.BOLD)
        }
        cardHijau.addView(tvStatSudah, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })
        statRow.addView(cardHijau)

        val cardPink = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.parseColor("#FFEBEE"), 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(6) }
        }
        val sbTitle = TextView(this).apply {
            text = "Belum Mengumpulkan"
            textSize = 12f
            setTextColor(Color.parseColor("#C62828"))
            setTypeface(null, Typeface.BOLD)
        }
        cardPink.addView(sbTitle)
        tvStatBelum = TextView(this).apply {
            text = "0"
            textSize = 22f
            setTextColor(Color.parseColor("#C62828"))
            setTypeface(null, Typeface.BOLD)
        }
        cardPink.addView(tvStatBelum, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4) })
        statRow.addView(cardPink)

        content.addView(statRow)

        // Search Bar
        val searchBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), 0, dp(12), 0)
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
        }
        val sIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_search)
            setColorFilter(Color.rgb(117, 139, 169))
        }
        searchBox.addView(sIcon, LinearLayout.LayoutParams(dp(20), dp(20)))

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

        // Chips Row (Semua, Sudah, Belum)
        val chipScroll = ScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val chipLayout = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }

        chipSemua = createChip("Semua (0)", "SEMUA")
        chipSudah = createChip("Sudah mengumpulkan (0)", "SUDAH")
        chipBelum = createChip("Belum (0)", "BELUM")

        chipLayout.addView(chipSemua)
        chipLayout.addView(chipSudah, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { leftMargin = dp(8) })
        chipLayout.addView(chipBelum, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { leftMargin = dp(8) })
        chipScroll.addView(chipLayout)

        content.addView(chipScroll, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(12) })
        updateChipSelection("SEMUA")

        // List Container
        containerList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        content.addView(containerList, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })

        scrollView.addView(content)
        rootLayout.addView(scrollView)
        return rootLayout
    }

    private fun createChip(label: String, filterVal: String): TextView {
        return TextView(this).apply {
            text = label
            textSize = 12f
            setPadding(dp(14), dp(8), dp(14), dp(8))
            setOnClickListener {
                viewModel.setFilterStatus(filterVal)
                updateChipSelection(filterVal)
            }
        }
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

    private fun renderPengumpulanList(data: DaftarPengumpulan) {
        containerList.removeAllViews()

        if (data.sudahMengumpulkan.isEmpty() && data.belumMengumpulkan.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "Pelajar tidak ditemukan"
                textSize = 14f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.CENTER
                setPadding(0, dp(32), 0, dp(32))
            }
            containerList.addView(emptyTv)
            return
        }

        // Section Sudah Mengumpulkan
        if (data.sudahMengumpulkan.isNotEmpty()) {
            val headerSudah = TextView(this).apply {
                text = "Sudah Mengumpulkan (${data.sudahMengumpulkan.size})"
                textSize = 15f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                setPadding(0, dp(4), 0, dp(8))
            }
            containerList.addView(headerSudah)

            for (item in data.sudahMengumpulkan) {
                containerList.addView(createSudahItemCard(item))
            }
        }

        // Section Belum Mengumpulkan
        if (data.belumMengumpulkan.isNotEmpty()) {
            val headerBelum = TextView(this).apply {
                text = "Belum Mengumpulkan (${data.belumMengumpulkan.size})"
                textSize = 15f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                setPadding(0, dp(16), 0, dp(8))
            }
            containerList.addView(headerBelum)

            for (pelajar in data.belumMengumpulkan) {
                containerList.addView(createBelumItemCard(pelajar))
            }
        }
    }

    private fun createSudahItemCard(item: Pengumpulan): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.WHITE, 16, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(8) }
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val avatar = TextView(this).apply {
            text = NameUtils.getInisial(item.namaPelajar)
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, NameUtils.getWarnaAvatar(item.namaPelajar), 20)
        }
        topRow.addView(avatar, LinearLayout.LayoutParams(dp(36), dp(36)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }
        val nameTv = TextView(this).apply {
            text = item.namaPelajar
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(nameTv)

        val timeTv = TextView(this).apply {
            text = "Dikumpulkan ${FormatTanggal.formatTanpaTahun(item.waktuKumpul)}"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(timeTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val chipTerkumpul = TextView(this).apply {
            text = "Terkumpul"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#2E7D32"))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.parseColor("#E8F5E9"), 8)
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }
        topRow.addView(chipTerkumpul)

        val arrow = ImageView(this).apply {
            setImageResource(R.drawable.ic_chevron_right)
            setColorFilter(Color.rgb(117, 139, 169))
        }
        topRow.addView(arrow, LinearLayout.LayoutParams(dp(20), dp(20)).apply { leftMargin = dp(8) })

        card.addView(topRow)

        // File Row
        val fileRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(8), dp(10), dp(8))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.parseColor("#F8FAFC"), 10, Color.parseColor("#E2E8F0"), 1)
        }
        val fIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_description)
            setColorFilter(Color.parseColor("#E53935"))
        }
        fileRow.addView(fIcon, LinearLayout.LayoutParams(dp(18), dp(18)))

        val fName = TextView(this).apply {
            text = item.namaFile
            textSize = 12f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        fileRow.addView(fName, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        card.addView(fileRow, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(10) })

        return card
    }

    private fun createBelumItemCard(pelajar: PelajarKelas): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.WHITE, 16, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(8) }
        }

        val avatar = TextView(this).apply {
            text = NameUtils.getInisial(pelajar.nama)
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, NameUtils.getWarnaAvatar(pelajar.nama), 20)
        }
        card.addView(avatar, LinearLayout.LayoutParams(dp(36), dp(36)))

        val nameTv = TextView(this).apply {
            text = pelajar.nama
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(10), 0, 0, 0)
        }
        card.addView(nameTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val chipBelum = TextView(this).apply {
            text = "Belum mengumpulkan"
            textSize = 11f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#C62828"))
            background = UiKit.rounded(this@PengajarPengumpulanTugasActivity, Color.parseColor("#FFEBEE"), 8)
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }
        card.addView(chipBelum)

        val arrow = ImageView(this).apply {
            setImageResource(R.drawable.ic_chevron_right)
            setColorFilter(Color.rgb(117, 139, 169))
        }
        card.addView(arrow, LinearLayout.LayoutParams(dp(20), dp(20)).apply { leftMargin = dp(8) })

        return card
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
