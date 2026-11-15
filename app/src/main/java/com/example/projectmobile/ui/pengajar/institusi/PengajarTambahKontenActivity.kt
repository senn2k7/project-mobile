package com.example.projectmobile.ui.pengajar.institusi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.util.Calendar

class PengajarTambahKontenActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TAB_INDEX = "extra_tab_index" // 0: Materi, 1: Tugas
        const val EXTRA_KELAS_ID = "extra_kelas_id"
    }

    private val viewModel: PengajarFormViewModel by viewModels()

    private lateinit var tabMateriBtn: TextView
    private lateinit var tabTugasBtn: TextView
    private lateinit var containerForm: LinearLayout
    private lateinit var spinnerKelas: Spinner
    private lateinit var spinnerPertemuan: Spinner
    private lateinit var etJudul: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var tvCharCount: TextView
    private lateinit var boxUpload: LinearLayout
    private lateinit var tvFileInfo: TextView
    private lateinit var btnUnggah: TextView
    private lateinit var tvError: TextView

    // Tugas specific views
    private lateinit var tvBatasVal: TextView
    private lateinit var radioIndividu: TextView
    private lateinit var radioKelompok: TextView
    private lateinit var lampiranTugasContainer: LinearLayout

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val fileInfo = getFileInfo(uri)
            val input = FileLampiranInput(
                uriString = uri.toString(),
                nama = fileInfo.first,
                tipeFile = contentResolver.getType(uri) ?: "application/octet-stream",
                ukuranByte = fileInfo.second
            )
            if (viewModel.selectedTab.value == 0) {
                viewModel.setFileLampiran(input)
            } else {
                viewModel.addLampiran(input)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val initialTab = intent.getIntExtra(EXTRA_TAB_INDEX, 0)
        val initialKelasId = intent.getStringExtra(EXTRA_KELAS_ID)

        if (initialKelasId != null) {
            viewModel.setSelectedKelas(initialKelasId)
        }
        viewModel.setSelectedTab(initialTab)

        setContentView(createScreen())

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedTab.collectLatest { tab ->
                        updateTabStyles(tab)
                        renderForm(tab)
                    }
                }

                launch {
                    viewModel.classList.collectLatest { list ->
                        if (list.isNotEmpty()) {
                            val adapter = ArrayAdapter(
                                this@PengajarTambahKontenActivity,
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
                                this@PengajarTambahKontenActivity,
                                android.R.layout.simple_spinner_item,
                                listOf("Belum ada kelas")
                            )
                            spinnerKelas.adapter = adapter
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
                            btnUnggah.text = "Mengunggah..."
                            btnUnggah.isEnabled = false
                            btnUnggah.alpha = 0.6f
                        } else {
                            btnUnggah.text = "Unggah"
                            btnUnggah.isEnabled = true
                            btnUnggah.alpha = 1.0f
                        }
                    }
                }

                launch {
                    viewModel.successResult.collectLatest { res ->
                        if (res != null) {
                            val (type, id) = res
                            val intent = Intent(this@PengajarTambahKontenActivity, PengajarSuksesActivity::class.java).apply {
                                putExtra(PengajarSuksesActivity.EXTRA_TYPE, type)
                                putExtra(PengajarSuksesActivity.EXTRA_ID, id)
                            }
                            startActivity(intent)
                            finish()
                        }
                    }
                }

                launch {
                    viewModel.fileLampiran.collectLatest { file ->
                        if (viewModel.selectedTab.value == 0) {
                            if (file != null) {
                                tvFileInfo.text = "📄 ${file.nama} (${FormatUkuran.format(file.ukuranByte)}) [Hapus]"
                                tvFileInfo.visibility = View.VISIBLE
                            } else {
                                tvFileInfo.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.lampiranList.collectLatest { list ->
                        if (viewModel.selectedTab.value == 1) {
                            renderLampiranTugasList(list)
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
            text = "Tambah Materi / Tugas"
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

        // Segmented Tabs (Materi | Tugas)
        val tabsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.parseColor("#E8F0FF"), 12)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
                leftMargin = dp(16); rightMargin = dp(16); topMargin = dp(8)
            }
        }

        tabMateriBtn = TextView(this).apply {
            text = "Materi"
            textSize = 14f
            gravity = Gravity.CENTER
            setOnClickListener { viewModel.setSelectedTab(0) }
        }
        tabsLayout.addView(tabMateriBtn, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabTugasBtn = TextView(this).apply {
            text = "Tugas"
            textSize = 14f
            gravity = Gravity.CENTER
            setOnClickListener { viewModel.setSelectedTab(1) }
        }
        tabsLayout.addView(tabTugasBtn, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))
        rootLayout.addView(tabsLayout)

        // Error message textview
        tvError = TextView(this).apply {
            textSize = 12f
            setTextColor(Color.parseColor("#C62828"))
            setPadding(dp(16), dp(8), dp(16), 0)
            visibility = View.GONE
        }
        rootLayout.addView(tvError)

        // Scrollable form container
        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        }

        containerForm = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        scrollView.addView(containerForm)
        rootLayout.addView(scrollView)

        // Bottom Unggah Button pinned
        val bottomBar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(16))
            background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }
        btnUnggah = TextView(this).apply {
            text = "Unggah"
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.parseColor("#1D5EFF"), 14)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setTypeface(null, Typeface.BOLD)
            setOnClickListener { viewModel.unggah() }
        }
        bottomBar.addView(btnUnggah, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        rootLayout.addView(bottomBar)

        return rootLayout
    }

    private fun updateTabStyles(tab: Int) {
        val activeBg = UiKit.rounded(this, Color.parseColor("#1D5EFF"), 10)
        val inactiveBg = UiKit.rounded(this, Color.TRANSPARENT, 10)

        if (tab == 0) {
            tabMateriBtn.background = activeBg
            tabMateriBtn.setTextColor(Color.WHITE)
            tabMateriBtn.setTypeface(null, Typeface.BOLD)

            tabTugasBtn.background = inactiveBg
            tabTugasBtn.setTextColor(Color.parseColor("#7186A5"))
            tabTugasBtn.setTypeface(null, Typeface.NORMAL)
        } else {
            tabTugasBtn.background = activeBg
            tabTugasBtn.setTextColor(Color.WHITE)
            tabTugasBtn.setTypeface(null, Typeface.BOLD)

            tabMateriBtn.background = inactiveBg
            tabMateriBtn.setTextColor(Color.parseColor("#7186A5"))
            tabMateriBtn.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun renderForm(tab: Int) {
        containerForm.removeAllViews()

        // 1. Pilih Kelas
        containerForm.addView(createLabel("Pilih Kelas"))
        spinnerKelas = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4); bottomMargin = dp(16) }
            background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
        }
        containerForm.addView(spinnerKelas)

        // 2. Pertemuan ke-
        containerForm.addView(createLabel("Pertemuan ke-"))
        spinnerPertemuan = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply { topMargin = dp(4); bottomMargin = dp(16) }
            background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
        }
        val pertemuanAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, (1..16).map { "Pertemuan $it" })
        pertemuanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPertemuan.adapter = pertemuanAdapter
        containerForm.addView(spinnerPertemuan)

        if (tab == 0) {
            // === TAB MATERI ===
            containerForm.addView(createLabel("Judul Materi"))
            etJudul = EditText(this).apply {
                hint = "Contoh: Pengenalan Android Studio"
                textSize = 14f
                setPadding(dp(12), dp(12), dp(12), dp(12))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(16) }
                setText(viewModel.judul.value)
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        viewModel.setJudul(s?.toString().orEmpty())
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
            containerForm.addView(etJudul)

            containerForm.addView(createLabel("Deskripsi"))
            etDeskripsi = EditText(this).apply {
                hint = "Tulis deskripsi materi di sini..."
                textSize = 14f
                gravity = Gravity.TOP
                minLines = 3
                setPadding(dp(12), dp(12), dp(12), dp(12))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(100)).apply { topMargin = dp(4); bottomMargin = dp(4) }
                setText(viewModel.deskripsi.value)
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val text = s?.toString().orEmpty()
                        viewModel.setDeskripsi(text)
                        tvCharCount.text = "${text.length}/500"
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
            containerForm.addView(etDeskripsi)

            tvCharCount = TextView(this).apply {
                text = "${viewModel.deskripsi.value.length}/500"
                textSize = 11f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.END
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(16) }
            }
            containerForm.addView(tvCharCount)

            // Upload File Box
            containerForm.addView(createLabel("Upload File"))
            boxUpload = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(16), dp(24), dp(16), dp(24))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.parseColor("#F8FAFC"), 12, Color.parseColor("#1D5EFF"), 2)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(8) }
                setOnClickListener { filePickerLauncher.launch("*/*") }
            }
            val upIcon = ImageView(this).apply {
                setImageResource(R.drawable.ic_folder)
                setColorFilter(Color.parseColor("#1D5EFF"))
            }
            boxUpload.addView(upIcon, LinearLayout.LayoutParams(dp(32), dp(32)))
            val upText = TextView(this).apply {
                text = "Pilih file atau seret ke sini"
                textSize = 14f
                setTextColor(Color.rgb(25, 54, 95))
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
            }
            boxUpload.addView(upText, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(8) })
            val upSub = TextView(this).apply {
                text = "Format: PDF, DOCX, PPTX, ZIP (Maks. 10 MB)"
                textSize = 12f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.CENTER
            }
            boxUpload.addView(upSub, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(2) })

            containerForm.addView(boxUpload)

            tvFileInfo = TextView(this).apply {
                textSize = 13f
                setTextColor(Color.parseColor("#1D5EFF"))
                setPadding(0, dp(4), 0, dp(16))
                visibility = View.GONE
                setOnClickListener { viewModel.setFileLampiran(null) }
            }
            containerForm.addView(tvFileInfo)

        } else {
            // === TAB TUGAS (Sementara sesuai spesifikasi) ===
            containerForm.addView(createLabel("Judul Tugas"))
            etJudul = EditText(this).apply {
                hint = "Contoh: Membuat UI Login Android"
                textSize = 14f
                setPadding(dp(12), dp(12), dp(12), dp(12))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(16) }
                setText(viewModel.judul.value)
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        viewModel.setJudul(s?.toString().orEmpty())
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
            containerForm.addView(etJudul)

            containerForm.addView(createLabel("Deskripsi Tugas"))
            etDeskripsi = EditText(this).apply {
                hint = "Tulis deskripsi tugas di sini..."
                textSize = 14f
                gravity = Gravity.TOP
                minLines = 3
                setPadding(dp(12), dp(12), dp(12), dp(12))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(100)).apply { topMargin = dp(4); bottomMargin = dp(16) }
                setText(viewModel.deskripsi.value)
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        viewModel.setDeskripsi(s?.toString().orEmpty())
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
            }
            containerForm.addView(etDeskripsi)

            // Batas Pengumpulan Picker
            containerForm.addView(createLabel("Batas Pengumpulan"))
            tvBatasVal = TextView(this).apply {
                text = FormatTanggal.formatLengkap(viewModel.tenggat.value)
                textSize = 14f
                setTextColor(Color.rgb(25, 54, 95))
                setPadding(dp(12), dp(12), dp(12), dp(12))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 12, Color.parseColor("#DCE7F4"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(16) }
                setOnClickListener { showDateTimePicker() }
            }
            containerForm.addView(tvBatasVal)

            // Tipe Tugas Radio (Individu / Kelompok)
            containerForm.addView(createLabel("Tipe Tugas"))
            val tipeRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(16) }
            }
            radioIndividu = TextView(this).apply {
                text = "Individu"
                textSize = 13f
                gravity = Gravity.CENTER
                setPadding(dp(16), dp(10), dp(16), dp(10))
                setOnClickListener { viewModel.setTipeTugas("individu") }
            }
            radioKelompok = TextView(this).apply {
                text = "Kelompok"
                textSize = 13f
                gravity = Gravity.CENTER
                setPadding(dp(16), dp(10), dp(16), dp(10))
                setOnClickListener { viewModel.setTipeTugas("kelompok") }
            }
            tipeRow.addView(radioIndividu, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { rightMargin = dp(4) })
            tipeRow.addView(radioKelompok, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = dp(4) })
            containerForm.addView(tipeRow)
            updateTipeStyles(viewModel.tipeTugas.value)

            // Lampiran Tugas Section
            containerForm.addView(createLabel("Lampiran Tugas"))
            lampiranTugasContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(4); bottomMargin = dp(8) }
            }
            containerForm.addView(lampiranTugasContainer)

            val btnAddLampiran = TextView(this).apply {
                text = "+ Tambah Lampiran File"
                textSize = 13f
                setTextColor(Color.parseColor("#1D5EFF"))
                setTypeface(null, Typeface.BOLD)
                setPadding(0, dp(4), 0, dp(16))
                setOnClickListener { filePickerLauncher.launch("*/*") }
            }
            containerForm.addView(btnAddLampiran)
        }
    }

    private fun updateTipeStyles(tipe: String) {
        val activeBg = UiKit.rounded(this, Color.parseColor("#1D5EFF"), 10)
        val inactiveBg = UiKit.rounded(this, Color.WHITE, 10, Color.parseColor("#DCE7F4"), 1)
        if (tipe == "individu") {
            radioIndividu.background = activeBg; radioIndividu.setTextColor(Color.WHITE); radioIndividu.setTypeface(null, Typeface.BOLD)
            radioKelompok.background = inactiveBg; radioKelompok.setTextColor(Color.parseColor("#7186A5")); radioKelompok.setTypeface(null, Typeface.NORMAL)
        } else {
            radioKelompok.background = activeBg; radioKelompok.setTextColor(Color.WHITE); radioKelompok.setTypeface(null, Typeface.BOLD)
            radioIndividu.background = inactiveBg; radioIndividu.setTextColor(Color.parseColor("#7186A5")); radioIndividu.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun renderLampiranTugasList(list: List<FileLampiranInput>) {
        lampiranTugasContainer.removeAllViews()
        list.forEachIndexed { index, file ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(10), dp(8), dp(10), dp(8))
                background = UiKit.rounded(this@PengajarTambahKontenActivity, Color.WHITE, 10, Color.parseColor("#DCE7F4"), 1)
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(6) }
            }
            val fIcon = TextView(this).apply { text = "📄"; textSize = 14f }
            row.addView(fIcon)
            val fName = TextView(this).apply {
                text = "${file.nama} (${FormatUkuran.format(file.ukuranByte)})"
                textSize = 12f
                setTextColor(Color.rgb(25, 54, 95))
                setPadding(dp(8), 0, 0, 0)
            }
            row.addView(fName, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            val fDel = TextView(this).apply {
                text = "Hapus"
                textSize = 12f
                setTextColor(Color.parseColor("#C62828"))
                setOnClickListener { viewModel.removeLampiran(index) }
            }
            row.addView(fDel)
            lampiranTugasContainer.addView(row)
        }
    }

    private fun showDateTimePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            TimePickerDialog(this, { _, hourOfDay, minute ->
                cal.set(year, month, dayOfMonth, hourOfDay, minute)
                viewModel.setTenggat(cal.timeInMillis)
                tvBatasVal.text = FormatTanggal.formatLengkap(cal.timeInMillis)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
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
        var name = "file.pdf"
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
