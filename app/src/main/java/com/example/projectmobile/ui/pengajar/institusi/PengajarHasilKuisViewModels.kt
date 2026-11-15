package com.example.projectmobile.ui.pengajar.institusi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class NilaiHasilKuisViewModel(
    private val kelasId: String,
    private val kuisRepo: KuisRepository = PengajarRepositoryProvider.provideKuisRepo(),
    private val hasilRepo: HasilKuisRepository = PengajarRepositoryProvider.provideHasilKuisRepo()
) : ViewModel() {

    private val _kuisList = MutableStateFlow<List<Kuis>>(emptyList())
    val kuisList: StateFlow<List<Kuis>> = _kuisList.asStateFlow()

    private val _selectedKuis = MutableStateFlow<Kuis?>(null)
    val selectedKuis: StateFlow<Kuis?> = _selectedKuis.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _hasilState = MutableStateFlow<PengajarUiState<List<HasilKuis>>>(PengajarUiState.Loading)
    val hasilState: StateFlow<PengajarUiState<List<HasilKuis>>> = _hasilState.asStateFlow()

    private val _statistikState = MutableStateFlow(Triple(0, 0, "-")) // (selesai, belumSelesai, rataRata)
    val statistikState: StateFlow<Triple<Int, Int, String>> = _statistikState.asStateFlow()

    init {
        loadKuis()
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    val kuis = _selectedKuis.value
                    if (kuis != null) {
                        loadHasil(kuis.id, query)
                    }
                }
        }
    }

    private fun loadKuis() {
        viewModelScope.launch {
            kuisRepo.kuisPerKelas(kelasId).collectLatest { list ->
                _kuisList.value = list
                if (list.isNotEmpty() && _selectedKuis.value == null) {
                    _selectedKuis.value = list[0]
                    loadHasil(list[0].id, "")
                } else if (list.isEmpty()) {
                    _hasilState.value = PengajarUiState.Empty
                }
            }
        }
    }

    fun setSelectedKuis(kuis: Kuis) {
        _selectedKuis.value = kuis
        loadHasil(kuis.id, _searchQuery.value)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadHasil(kuisId: String, query: String) {
        viewModelScope.launch {
            _hasilState.value = PengajarUiState.Loading
            hasilRepo.hasilPerKuis(kuisId, query).collectLatest { list ->
                if (list.isEmpty()) {
                    _hasilState.value = PengajarUiState.Empty
                    _statistikState.value = Triple(0, 0, "-")
                } else {
                    _hasilState.value = PengajarUiState.Success(list)
                    val selesai = list.count { it.status == "selesai" }
                    val belum = list.count { it.status == "belum_selesai" }
                    val nilaiList = list.mapNotNull { it.nilai }
                    val rata = if (nilaiList.isNotEmpty()) {
                        String.format(java.util.Locale.getDefault(), "%.1f", nilaiList.average())
                    } else {
                        "-"
                    }
                    _statistikState.value = Triple(selesai, belum, rata)
                }
            }
        }
    }
}

class DetailHasilKuisViewModel(
    private val hasilId: String,
    private val hasilRepo: HasilKuisRepository = PengajarRepositoryProvider.provideHasilKuisRepo()
) : ViewModel() {

    private val _hasilState = MutableStateFlow<PengajarUiState<HasilKuis>>(PengajarUiState.Loading)
    val hasilState: StateFlow<PengajarUiState<HasilKuis>> = _hasilState.asStateFlow()

    private val _soalState = MutableStateFlow<PengajarUiState<List<Soal>>>(PengajarUiState.Loading)
    val soalState: StateFlow<PengajarUiState<List<Soal>>> = _soalState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            hasilRepo.hasilById(hasilId).collectLatest { hasil ->
                if (hasil != null) {
                    _hasilState.value = PengajarUiState.Success(hasil)
                    hasilRepo.soalPerKuis(hasil.kuisId).collectLatest { soalList ->
                        if (soalList.isEmpty()) {
                            _soalState.value = PengajarUiState.Empty
                        } else {
                            _soalState.value = PengajarUiState.Success(soalList)
                        }
                    }
                } else {
                    _hasilState.value = PengajarUiState.Empty
                }
            }
        }
    }
}

class NilaiHasilKuisViewModelFactory(private val kelasId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NilaiHasilKuisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NilaiHasilKuisViewModel(kelasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DetailHasilKuisViewModelFactory(private val hasilId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailHasilKuisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailHasilKuisViewModel(hasilId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
