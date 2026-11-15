package com.example.projectmobile.ui.pengajar.institusi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarFormViewModel(
    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo(),
    private val materiRepo: MateriRepository = PengajarRepositoryProvider.provideMateriRepo(),
    private val tugasRepo: TugasRepository = PengajarRepositoryProvider.provideTugasRepo()
) : ViewModel() {

    private val _classList = MutableStateFlow<List<PengajarKelas>>(emptyList())
    val classList: StateFlow<List<PengajarKelas>> = _classList.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Materi, 1: Tugas
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedKelasId = MutableStateFlow("")
    val selectedKelasId: StateFlow<String> = _selectedKelasId.asStateFlow()

    private val _pertemuanKe = MutableStateFlow(1)
    val pertemuanKe: StateFlow<Int> = _pertemuanKe.asStateFlow()

    private val _judul = MutableStateFlow("")
    val judul: StateFlow<String> = _judul.asStateFlow()

    private val _deskripsi = MutableStateFlow("")
    val deskripsi: StateFlow<String> = _deskripsi.asStateFlow()

    private val _fileLampiran = MutableStateFlow<FileLampiranInput?>(null)
    val fileLampiran: StateFlow<FileLampiranInput?> = _fileLampiran.asStateFlow()

    private val _lampiranList = MutableStateFlow<List<FileLampiranInput>>(emptyList())
    val lampiranList: StateFlow<List<FileLampiranInput>> = _lampiranList.asStateFlow()

    private val _tenggat = MutableStateFlow(System.currentTimeMillis() + 86400000L * 3)
    val tenggat: StateFlow<Long> = _tenggat.asStateFlow()

    private val _tipeTugas = MutableStateFlow("individu")
    val tipeTugas: StateFlow<String> = _tipeTugas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successResult = MutableStateFlow<Pair<String, String>?>(null) // Pair(type, id)
    val successResult: StateFlow<Pair<String, String>?> = _successResult.asStateFlow()

    init {
        loadKelas()
    }

    fun loadKelas() {
        viewModelScope.launch {
            kelasRepo.getKelasList().collectLatest { list ->
                _classList.value = list
                if (list.isNotEmpty() && _selectedKelasId.value.isBlank()) {
                    _selectedKelasId.value = list[0].id
                }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
        _errorMessage.value = null
    }

    fun setSelectedKelas(id: String) {
        _selectedKelasId.value = id
    }

    fun setPertemuan(p: Int) {
        _pertemuanKe.value = p
    }

    fun setJudul(j: String) {
        _judul.value = j
    }

    fun setDeskripsi(d: String) {
        if (d.length <= 500) {
            _deskripsi.value = d
        }
    }

    fun setFileLampiran(file: FileLampiranInput?) {
        if (file != null) {
            val ext = file.nama.substringAfterLast('.', "").lowercase()
            val allowed = listOf("pdf", "docx", "pptx", "zip")
            if (!allowed.contains(ext)) {
                _errorMessage.value = "Format file tidak diizinkan. Gunakan PDF, DOCX, PPTX, atau ZIP."
                return
            }
            if (file.ukuranByte > 10 * 1024 * 1024L) { // 10 MB
                _errorMessage.value = "Ukuran file melebihi batas maksimal 10 MB."
                return
            }
            _errorMessage.value = null
        }
        _fileLampiran.value = file
    }

    fun addLampiran(file: FileLampiranInput) {
        val ext = file.nama.substringAfterLast('.', "").lowercase()
        val allowed = listOf("pdf", "docx", "pptx", "zip")
        if (!allowed.contains(ext)) {
            _errorMessage.value = "Format file tidak diizinkan."
            return
        }
        if (file.ukuranByte > 10 * 1024 * 1024L) {
            _errorMessage.value = "Ukuran file melebihi 10 MB."
            return
        }
        _errorMessage.value = null
        _lampiranList.value = _lampiranList.value + file
    }

    fun removeLampiran(index: Int) {
        val list = _lampiranList.value.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _lampiranList.value = list
        }
    }

    fun setTenggat(t: Long) {
        _tenggat.value = t
    }

    fun setTipeTugas(tipe: String) {
        _tipeTugas.value = tipe
    }

    fun unggah() {
        if (_selectedKelasId.value.isBlank()) {
            _errorMessage.value = "Silakan pilih kelas terlebih dahulu."
            return
        }
        if (_judul.value.isBlank()) {
            _errorMessage.value = "Judul tidak boleh kosong."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            if (_selectedTab.value == 0) {
                // Materi
                val input = InputMateri(
                    kelasId = _selectedKelasId.value,
                    pertemuanKe = _pertemuanKe.value,
                    judul = _judul.value,
                    deskripsi = _deskripsi.value,
                    file = _fileLampiran.value
                )
                val result = materiRepo.buatMateri(input)
                _isLoading.value = false
                if (result.isSuccess) {
                    _successResult.value = Pair("materi", result.getOrDefault("m1"))
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengunggah materi"
                }
            } else {
                // Tugas
                val input = InputTugas(
                    kelasId = _selectedKelasId.value,
                    pertemuanKe = _pertemuanKe.value,
                    judul = _judul.value,
                    deskripsi = _deskripsi.value,
                    tenggat = _tenggat.value,
                    tipe = _tipeTugas.value,
                    lampiranList = _lampiranList.value
                )
                val result = tugasRepo.buatTugas(input)
                _isLoading.value = false
                if (result.isSuccess) {
                    _successResult.value = Pair("tugas", result.getOrDefault("t1"))
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengunggah tugas"
                }
            }
        }
    }
}
