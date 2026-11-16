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

class DetailKelasViewModel(
    private val kelasId: String,
    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo(),
    private val materiRepo: MateriRepository = PengajarRepositoryProvider.provideMateriRepo(),
    private val tugasRepo: TugasRepository = PengajarRepositoryProvider.provideTugasRepo()
) : ViewModel() {

    private val _kelasState = MutableStateFlow<PengajarUiState<Kelas>>(PengajarUiState.Loading)
    val kelasState: StateFlow<PengajarUiState<Kelas>> = _kelasState.asStateFlow()

    private val _materiState = MutableStateFlow<PengajarUiState<List<Materi>>>(PengajarUiState.Loading)
    val materiState: StateFlow<PengajarUiState<List<Materi>>> = _materiState.asStateFlow()

    private val _tugasState = MutableStateFlow<PengajarUiState<List<Tugas>>>(PengajarUiState.Loading)
    val tugasState: StateFlow<PengajarUiState<List<Tugas>>> = _tugasState.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            kelasRepo.kelasById(kelasId).collectLatest { kelas ->
                if (kelas != null) {
                    _kelasState.value = PengajarUiState.Success(kelas)
                } else {
                    _kelasState.value = PengajarUiState.Empty
                }
            }
        }
        viewModelScope.launch {
            materiRepo.materiPerKelas(kelasId).collectLatest { list ->
                if (list.isEmpty()) {
                    _materiState.value = PengajarUiState.Empty
                } else {
                    _materiState.value = PengajarUiState.Success(list)
                }
            }
        }
        viewModelScope.launch {
            tugasRepo.tugasPerKelas(kelasId, "SEMUA").collectLatest { list ->
                if (list.isEmpty()) {
                    _tugasState.value = PengajarUiState.Empty
                } else {
                    _tugasState.value = PengajarUiState.Success(list)
                }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }
}

class TugasViewModel(
    private val rawKelasId: String,
    private val profilRepo: PengajarProfilRepository = PengajarRepositoryProvider.provideProfilRepo(),
    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo(),
    private val tugasRepo: TugasRepository = PengajarRepositoryProvider.provideTugasRepo()
) : ViewModel() {

    private val _filterState = MutableStateFlow("SEMUA")
    val filterState: StateFlow<String> = _filterState.asStateFlow()

    private val _tugasState = MutableStateFlow<PengajarUiState<List<Tugas>>>(PengajarUiState.Loading)
    val tugasState: StateFlow<PengajarUiState<List<Tugas>>> = _tugasState.asStateFlow()

    private val _kelasInfo = MutableStateFlow<Kelas?>(null)
    val kelasInfo: StateFlow<Kelas?> = _kelasInfo.asStateFlow()

    init {
        loadData("SEMUA")
    }

    fun setFilter(filter: String) {
        _filterState.value = filter
        loadData(filter)
    }

    private fun loadData(filter: String) {
        viewModelScope.launch {
            _tugasState.value = PengajarUiState.Loading

            profilRepo.getProfil().collectLatest { profil ->
                if (profil == null) {
                    _tugasState.value = PengajarUiState.Error("Sesi pengajar tidak ditemukan")
                    return@collectLatest
                }
                val teacherId = profil.uid

                if (rawKelasId.isBlank()) {
                    kelasRepo.getKelasList(guruId = teacherId).collectLatest { kelasList ->
                        if (kelasList.isEmpty()) {
                            _tugasState.value = PengajarUiState.CustomEmpty("Belum ada kelas")
                        } else {
                            val firstKelas = kelasList.sortedBy { it.namaMataKuliah }.first()
                            kelasRepo.kelasById(firstKelas.id).collectLatest { fullKelas ->
                                if (fullKelas == null) {
                                    _tugasState.value = PengajarUiState.Error("Kelas tidak ditemukan")
                                } else {
                                    _kelasInfo.value = fullKelas
                                    tugasRepo.tugasPerKelas(fullKelas.id, filter).collectLatest { tasks ->
                                        if (tasks.isEmpty()) {
                                            _tugasState.value = PengajarUiState.CustomEmpty("Belum ada tugas")
                                        } else {
                                            _tugasState.value = PengajarUiState.Success(tasks)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    kelasRepo.kelasById(rawKelasId).collectLatest { kelas ->
                        if (kelas == null) {
                            _tugasState.value = PengajarUiState.Error("Kelas tidak ditemukan")
                        } else {
                            _kelasInfo.value = kelas
                            tugasRepo.tugasPerKelas(rawKelasId, filter).collectLatest { tasks ->
                                if (tasks.isEmpty()) {
                                    _tugasState.value = PengajarUiState.CustomEmpty("Belum ada tugas")
                                } else {
                                    _tugasState.value = PengajarUiState.Success(tasks)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class DetailTugasViewModel(
    private val tugasId: String,
    private val tugasRepo: TugasRepository = PengajarRepositoryProvider.provideTugasRepo()
) : ViewModel() {

    private val _tugasState = MutableStateFlow<PengajarUiState<Tugas>>(PengajarUiState.Loading)
    val tugasState: StateFlow<PengajarUiState<Tugas>> = _tugasState.asStateFlow()

    private val _lampiranState = MutableStateFlow<PengajarUiState<List<LampiranTugas>>>(PengajarUiState.Loading)
    val lampiranState: StateFlow<PengajarUiState<List<LampiranTugas>>> = _lampiranState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            if (tugasId.isBlank()) {
                _tugasState.value = PengajarUiState.Error("Tugas tidak ditemukan")
                _lampiranState.value = PengajarUiState.Empty
                return@launch
            }
            tugasRepo.tugasById(tugasId).collectLatest { tugas ->
                if (tugas != null) {
                    _tugasState.value = PengajarUiState.Success(tugas)
                } else {
                    _tugasState.value = PengajarUiState.Error("Tugas tidak ditemukan")
                }
            }
        }
        viewModelScope.launch {
            if (tugasId.isBlank()) return@launch
            tugasRepo.lampiran(tugasId).collectLatest { list ->
                if (list.isEmpty()) {
                    _lampiranState.value = PengajarUiState.Empty
                } else {
                    _lampiranState.value = PengajarUiState.Success(list)
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
class PengumpulanViewModel(
    private val tugasId: String,
    private val pengumpulanRepo: PengumpulanRepository = PengajarRepositoryProvider.providePengumpulanRepo()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterStatus = MutableStateFlow("SEMUA")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    private val _pengumpulanState = MutableStateFlow<PengajarUiState<DaftarPengumpulan>>(PengajarUiState.Loading)
    val pengumpulanState: StateFlow<PengajarUiState<DaftarPengumpulan>> = _pengumpulanState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchData(query, _filterStatus.value)
                }
        }
        fetchData("", "SEMUA")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
        fetchData(_searchQuery.value, status)
    }

    private fun fetchData(query: String, status: String) {
        viewModelScope.launch {
            _pengumpulanState.value = PengajarUiState.Loading
            pengumpulanRepo.statusPengumpulan(tugasId, query, status).collectLatest { data ->
                if (data.sudahMengumpulkan.isEmpty() && data.belumMengumpulkan.isEmpty()) {
                    _pengumpulanState.value = PengajarUiState.Empty
                } else {
                    _pengumpulanState.value = PengajarUiState.Success(data)
                }
            }
        }
    }
}

class DetailKelasViewModelFactory(private val kelasId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailKelasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailKelasViewModel(kelasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TugasViewModelFactory(private val kelasId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TugasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TugasViewModel(kelasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DetailTugasViewModelFactory(private val tugasId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailTugasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailTugasViewModel(tugasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PengumpulanViewModelFactory(private val tugasId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PengumpulanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PengumpulanViewModel(tugasId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
