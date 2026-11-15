package com.example.projectmobile.ui.pengajar.institusi

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.projectmobile.R
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.UiKit
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarBuatKuisActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS_ID = "extra_kelas_id"
    }

    private val viewModel: PengajarKuisViewModel by viewModels()

    private lateinit var spinnerKelas: Spinner
    private lateinit var spinnerPertemuan: Spinner
    private lateinit var etJudul: EditText
    private lateinit var etInstruksi: EditText
    private lateinit var tvCharCount: TextView
    private lateinit var spinnerDurasi: Spinner
    private lateinit var etNilaiMin: EditText
    private lateinit var etJumlahSoal: EditText
    private lateinit var tvFileInfo: TextView
    private lateinit var btnUpload: TextView
    private lateinit var btnSimpanDraf: TextView
    private lateinit var tvError: TextView

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val fileInfo = getFileInfo(uri)
            val input = FileLampiranInput(
                uriString = uri.toString(),
                nama = fileInfo.first,
                tipeFile = contentResolver.getType(uri) ?: "application/octet-stream",
                ukuranByte = fileInfo.second
            )
            viewModel.setFileSoal(input)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val initialKelasId = intent.getStringExtra(EXTRA_KELAS_ID)
        if (initialKelasId != null) {
            viewModel.setSelectedKelas(initialKelasId)
        }

        setContentView(createScreen())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.classList.collectLatest { list ->
                        if (list.isNotEmpty()) {
                            val adapter = ArrayAdapter(
                                this@PengajarBuatKuisActivity,
                                android.R.layout.simple_spinner_item,
                                list.map { "${it.namaMataKuliah} (${it.namaKelas})" }
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerKelas.adapter = adapter
                            val targetId = viewModel.selectedKelasId.value
                            val index = list.indexOfFirst { it.id == targetId }
                            if (index >= 0) spinnerKelas.setSelection(index)
                        } else {
                            val adapter = ArrayAdapter(
                                this@PengajarBuatKuisActivity,
                                android.R.layout.simple_spinner_item,
                                listOf("Belum ada kelas")
                            )
                            spinnerKelas.adapter = adapter
                            btnUpload.isEnabled = false
                            btnSimpanDraf.isEnabled = false
                        }
                    }
                }

                launch {
                    viewModel.errorMessage.collectLatest { err ->
                        if (err != null) {
                            tvError.text = err
                            tvError.visibility = View.VISIBLE
                        } else {
                            tvError.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collectLatest { loading ->
                        if (loading) {
                            btnUpload.isEnabled = false
                            btnSimpanDraf.isEnabled = false
                            btnUpload.alpha = 0.6f
                            btnSimpanDraf.alpha = 0.6f
                        } else {
                            btnUpload.isEnabled = true
                            btnSimpanDraf.isEnabled = true
                            btnUpload.alpha = 1.0f
                            btnSimpanDraf.alpha = 1.0f
                        }
                    }
                }

                launch {
                    viewModel.successResult.collectLatest { res ->
                        if (res != null) {
                            val (type, id) = res
                            if (type == "kuis_draf") {
                                Toast.makeText(this@PengajarBuatKuisActivity, "Draf tersimpan", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                val intent = Intent(this@PengajarBuatKuisActivity, PengajarSuksesActivity::class.java).apply {
                                    putExtra(PengajarSuksesActivity.EXTRA_TYPE, type)
                                    putExtra(PengajarSuksesActivity.EXTRA_ID, id)
                                }
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                }

                launch {
                    viewModel.fileSoal.collectLatest { file ->
                        if (file != null) {
                            tvFileInfo.text = "📄 ${file.nama} (${FormatUkuran.format(file.ukuranByte)}) [Hapus]"
                            tvFileInfo.visibility = View.VISIBLE
                        } else {
                            tvFileInfo.visibility = View.GONE
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
            text = "Buat Kuis"
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

        tvError = TextView(this).apply {
            textSize = 12f
            setTextColor(Color.parseColor("#C62828"))
            setPadding(dp(16), dp(8), dp(16), 0)
            visibility = View.GONE
        }
        rootLayout.addView(tvError)

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        // Pilih Kelas
        content.addView(createLabel("Pilih Kelas"))
        spinnerKelas = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4); bottomMargin = dp(16) }
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val list = viewModel.classList.value
                    if (position in list.indices) {
                        viewModel.setSelectedKelas(list[position].id)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
        content.addView(spinnerKelas)

        // Pertemuan ke-
        content.addView(createLabel("Pertemuan ke-"))
        spinnerPertemuan = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4); bottomMargin = dp(16) }
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
        }
        val pAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, (1..16).map { "Pertemuan $it" })
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPertemuan.adapter = pAdapter
        spinnerPertemuan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setPertemuan(position + 1)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        content.addView(spinnerPertemuan)

        // Judul Kuis
        content.addView(createLabel("Judul Kuis"))
        etJudul = EditText(this).apply {
            hint = "Contoh: Kuis Struktur Data"
            textSize = 14f
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(16) }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.setJudul(s?.toString().orEmpty())
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        content.addView(etJudul)

        // Instruksi
        content.addView(createLabel("Instruksi"))
        etInstruksi = EditText(this).apply {
            hint = "Tulis instruksi pengerjaan kuis..."
            textSize = 14f
            gravity = Gravity.TOP
            minLines = 3
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(100)).apply { topMargin = dp(4); bottomMargin = dp(4) }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = s?.toString().orEmpty()
                    viewModel.setInstruksi(text)
                    tvCharCount.text = "${text.length}/500"
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        content.addView(etInstruksi)

        tvCharCount = TextView(this).apply {
            text = "0/500"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(16) }
        }
        content.addView(tvCharCount)

        // 3 Columns Row: Durasi Pengerjaan, Nilai Minimal, Jumlah Soal
        val row3Col = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 3f
        }

        // 1. Durasi
        val colDurasi = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { rightMargin = dp(4) }
        }
        colDurasi.addView(createLabel("Durasi"))
        spinnerDurasi = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4) }
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
        }
        val durasiList = listOf(10, 15, 20, 30, 45, 60)
        val dAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durasiList.map { "$it Menit" })
        dAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDurasi.adapter = dAdapter
        spinnerDurasi.setSelection(2) // 20 Menit default index
        spinnerDurasi.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.setDurasi(durasiList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        colDurasi.addView(spinnerDurasi)
        row3Col.addView(colDurasi)

        // 2. Nilai Minimal
        val colNilai = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(2); rightMargin = dp(2) }
        }
        colNilai.addView(createLabel("Nilai Min"))
        etNilaiMin = EditText(this).apply {
            hint = "70"
            textSize = 14f
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(dp(8), dp(12), dp(8), dp(12))
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4) }
            setText("70")
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val v = s?.toString()?.toIntOrNull() ?: 70
                    viewModel.setNilaiMinimal(v)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        colNilai.addView(etNilaiMin)
        row3Col.addView(colNilai)

        // 3. Jumlah Soal
        val colSoal = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(4) }
        }
        colSoal.addView(createLabel("Jml Soal"))
        etJumlahSoal = EditText(this).apply {
            hint = "10"
            textSize = 14f
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(dp(8), dp(12), dp(8), dp(12))
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4) }
            setText("10")
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val v = s?.toString()?.toIntOrNull() ?: 10
                    viewModel.setJumlahSoal(v)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
        colSoal.addView(etJumlahSoal)
        row3Col.addView(colSoal)

        content.addView(row3Col, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(16) })

        // Tambah Soal (Upload file soal opsional)
        content.addView(createLabel("Tambah Soal"))
        val boxUpload = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(20), dp(16), dp(20))
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.parseColor("#F8FAFC"), 12, Color.parseColor("#1D5EFF"), 2)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(8) }
            setOnClickListener { filePickerLauncher.launch("*/*") }
        }
        val upIcon = ImageView(this).apply {
            setImageResource(R.drawable.ic_folder)
            setColorFilter(Color.parseColor("#1D5EFF"))
        }
        boxUpload.addView(upIcon, LinearLayout.LayoutParams(dp(28), dp(28)))
        val upText = TextView(this).apply {
            text = "Upload file soal (opsional)"
            textSize = 13f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }
        boxUpload.addView(upText, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(6) })
        val upSub = TextView(this).apply {
            text = "Format: PDF, DOCX, ZIP"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        boxUpload.addView(upSub, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })
        content.addView(boxUpload)

        tvFileInfo = TextView(this).apply {
            textSize = 12f
            setTextColor(Color.parseColor("#1D5EFF"))
            setPadding(0, dp(4), 0, dp(16))
            visibility = View.GONE
            setOnClickListener { viewModel.setFileSoal(null) }
        }
        content.addView(tvFileInfo)

        scrollView.addView(content)
        rootLayout.addView(scrollView)

        // Bottom Buttons Bar (Upload Kuis & Simpan Draf)
        val bottomBar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        btnUpload = TextView(this).apply {
            text = "Upload Kuis"
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.parseColor("#1D5EFF"), 12)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            setOnClickListener { viewModel.simpan("dipublikasikan") }
        }
        bottomBar.addView(btnUpload, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        btnSimpanDraf = TextView(this).apply {
            text = "Simpan Draf"
            textSize = 14f
            setTextColor(Color.parseColor("#1D5EFF"))
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarBuatKuisActivity, Color.WHITE, 12, Color.parseColor("#1D5EFF"), 1)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            setTypeface(null, Typeface.BOLD)
            setOnClickListener { viewModel.simpan("draf") }
        }
        bottomBar.addView(btnSimpanDraf, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })

        rootLayout.addView(bottomBar)

        return rootLayout
    }

    private fun createLabel(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
    }

    private fun getFileInfo(uri: Uri): Pair<String, Long> {
        var name = "soal.pdf"
        var size = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) name = cursor.getString(nameIndex)
                if (sizeIndex >= 0) size = cursor.getLong(sizeIndex)
            }
        }
        return Pair(name, size)
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
