package com.example.projectmobile.ui.pengajar.institusi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarKuisViewModel(
    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo(),
    private val kuisRepo: KuisRepository = PengajarRepositoryProvider.provideKuisRepo()
) : ViewModel() {

    private val _classList = MutableStateFlow<List<PengajarKelas>>(emptyList())
    val classList: StateFlow<List<PengajarKelas>> = _classList.asStateFlow()

    private val _selectedKelasId = MutableStateFlow("")
    val selectedKelasId: StateFlow<String> = _selectedKelasId.asStateFlow()

    private val _pertemuanKe = MutableStateFlow(1)
    val pertemuanKe: StateFlow<Int> = _pertemuanKe.asStateFlow()

    private val _judul = MutableStateFlow("")
    val judul: StateFlow<String> = _judul.asStateFlow()

    private val _instruksi = MutableStateFlow("")
    val instruksi: StateFlow<String> = _instruksi.asStateFlow()

    private val _durasiMenit = MutableStateFlow(20)
    val durasiMenit: StateFlow<Int> = _durasiMenit.asStateFlow()

    private val _nilaiMinimal = MutableStateFlow(0)
    val nilaiMinimal: StateFlow<Int> = _nilaiMinimal.asStateFlow()

    private val _jumlahSoal = MutableStateFlow(0)
    val jumlahSoal: StateFlow<Int> = _jumlahSoal.asStateFlow()

    private val _fileSoal = MutableStateFlow<FileLampiranInput?>(null)
    val fileSoal: StateFlow<FileLampiranInput?> = _fileSoal.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successResult = MutableStateFlow<Pair<String, String>?>(null)
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

    fun setSelectedKelas(id: String) {
        _selectedKelasId.value = id
    }

    fun setPertemuan(p: Int) {
        _pertemuanKe.value = p
    }

    fun setJudul(j: String) {
        _judul.value = j
    }

    fun setInstruksi(i: String) {
        if (i.length <= 500) {
            _instruksi.value = i
        }
    }

    fun setDurasi(d: Int) {
        _durasiMenit.value = d
    }

    fun setNilaiMinimal(n: Int) {
        _nilaiMinimal.value = n
    }

    fun setJumlahSoal(s: Int) {
        _jumlahSoal.value = s
    }

    fun setFileSoal(file: FileLampiranInput?) {
        if (file != null) {
            val ext = file.nama.substringAfterLast('.', "").lowercase()
            val allowed = listOf("pdf", "docx", "zip")
            if (!allowed.contains(ext)) {
                _errorMessage.value = "Format file soal tidak diizinkan. Gunakan PDF, DOCX, atau ZIP."
                return
            }
            _errorMessage.value = null
        }
        _fileSoal.value = file
    }

    fun simpan(status: String) {
        if (_selectedKelasId.value.isBlank()) {
            _errorMessage.value = "Silakan pilih kelas terlebih dahulu."
            return
        }
        if (_judul.value.isBlank()) {
            _errorMessage.value = "Judul kuis wajib diisi."
            return
        }
        if (_nilaiMinimal.value !in 1..100) {
            _errorMessage.value = "Nilai minimal harus antara 1 - 100."
            return
        }
        if (_jumlahSoal.value < 1) {
            _errorMessage.value = "Jumlah soal minimal 1."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val input = InputKuis(
                kelasId = _selectedKelasId.value,
                pertemuanKe = _pertemuanKe.value,
                judul = _judul.value,
                instruksi = _instruksi.value,
                durasiMenit = _durasiMenit.value,
                nilaiMinimal = _nilaiMinimal.value,
                jumlahSoal = _jumlahSoal.value,
                fileSoal = _fileSoal.value,
                status = status
            )

            val result = kuisRepo.simpanKuis(input, status)
            _isLoading.value = false

            if (result.isSuccess) {
                _successResult.value = Pair("kuis", result.getOrDefault("k1"))
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal menyimpan kuis"
            }
        }
    }
}
