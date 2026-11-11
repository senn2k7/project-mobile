package com.example.projectmobile.ui.pengajar.institusi

import androidx.lifecycle.ViewModel
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
class PengajarKelasSayaViewModel(
    private val kelasRepo: PengajarKelasRepository = PengajarRepositoryProvider.provideKelasRepo()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterInstitusi = MutableStateFlow(false)
    val filterInstitusi: StateFlow<Boolean> = _filterInstitusi.asStateFlow()

    private val _selectedSemester = MutableStateFlow("")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    private val _kelasState = MutableStateFlow<PengajarUiState<List<PengajarKelas>>>(PengajarUiState.Loading)
    val kelasState: StateFlow<PengajarUiState<List<PengajarKelas>>> = _kelasState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    fetchKelas(query, _filterInstitusi.value, _selectedSemester.value)
                }
        }
        fetchKelas("", false, "")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilterInstitusi(isInstitusi: Boolean) {
        _filterInstitusi.value = isInstitusi
        fetchKelas(_searchQuery.value, isInstitusi, _selectedSemester.value)
    }

    fun setSemester(semester: String) {
        _selectedSemester.value = semester
        fetchKelas(_searchQuery.value, _filterInstitusi.value, semester)
    }

    private fun fetchKelas(query: String, institusi: Boolean, semester: String) {
        viewModelScope.launch {
            _kelasState.value = PengajarUiState.Loading
            kelasRepo.getKelasList(query, institusi, semester).collectLatest { list ->
                if (list.isEmpty()) {
                    _kelasState.value = PengajarUiState.Empty
                } else {
                    _kelasState.value = PengajarUiState.Success(list)
                }
            }
        }
    }
}
