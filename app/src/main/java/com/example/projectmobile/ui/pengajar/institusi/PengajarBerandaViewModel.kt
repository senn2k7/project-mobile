package com.example.projectmobile.ui.pengajar.institusi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PengajarBerandaViewModel(
    private val profilRepo: PengajarProfilRepository = PengajarRepositoryProvider.provideProfilRepo(),
    private val statistikRepo: PengajarStatistikRepository = PengajarRepositoryProvider.provideStatistikRepo(),
    private val kelasRepo: PengajarKelasRepository = PengajarRepositoryProvider.provideKelasRepo(),
    private val notifRepo: PengajarNotifikasiRepository = PengajarRepositoryProvider.provideNotifikasiRepo()
) : ViewModel() {

    private val _profilState = MutableStateFlow<PengajarUiState<PengajarProfilGuru>>(PengajarUiState.Loading)
    val profilState: StateFlow<PengajarUiState<PengajarProfilGuru>> = _profilState.asStateFlow()

    private val _statistikState = MutableStateFlow<PengajarUiState<PengajarStatistikBeranda>>(PengajarUiState.Loading)
    val statistikState: StateFlow<PengajarUiState<PengajarStatistikBeranda>> = _statistikState.asStateFlow()

    private val _kelasState = MutableStateFlow<PengajarUiState<List<PengajarKelas>>>(PengajarUiState.Loading)
    val kelasState: StateFlow<PengajarUiState<List<PengajarKelas>>> = _kelasState.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            profilRepo.getProfil().collectLatest { profil ->
                if (profil != null) {
                    _profilState.value = PengajarUiState.Success(profil)
                } else {
                    _profilState.value = PengajarUiState.Empty
                }
            }
        }
        viewModelScope.launch {
            statistikRepo.getStatistik().collectLatest { stat ->
                _statistikState.value = PengajarUiState.Success(stat)
            }
        }
        viewModelScope.launch {
            kelasRepo.getKelasList().collectLatest { list ->
                val top3 = list.take(3)
                if (top3.isEmpty()) {
                    _kelasState.value = PengajarUiState.Empty
                } else {
                    _kelasState.value = PengajarUiState.Success(top3)
                }
            }
        }
        viewModelScope.launch {
            notifRepo.getUnreadCount().collectLatest { count ->
                _unreadCount.value = count
            }
        }
    }
}
