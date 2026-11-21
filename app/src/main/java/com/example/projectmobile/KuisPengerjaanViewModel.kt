package com.example.projectmobile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projectmobile.ui.pengajar.institusi.HasilKuisRepository
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import com.example.projectmobile.ui.pengajar.institusi.PengajarUiState
import com.example.projectmobile.ui.pengajar.institusi.Soal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KuisPengerjaanViewModel(
    private val kuisId: String,
    private val hasilRepo: HasilKuisRepository = PengajarRepositoryProvider.provideHasilKuisRepo()
) : ViewModel() {

    var soalList = listOf<Soal>()
    var currentIndex = 0
    val jawabanMap = mutableMapOf<Int, String>() // soalIndex -> opsiKode ("A", "B", dll)
    var startTime = System.currentTimeMillis()
    var durasiMs = 20 * 60 * 1000L

    private val _uiState = MutableStateFlow<PengajarUiState<List<Soal>>>(PengajarUiState.Loading)
    val uiState: StateFlow<PengajarUiState<List<Soal>>> = _uiState

    init {
        loadSoal()
    }

    fun loadSoal() {
        viewModelScope.launch {
            hasilRepo.soalPerKuis(kuisId).collectLatest { list ->
                soalList = list.sortedBy { it.nomor }
                if (soalList.isEmpty()) {
                    _uiState.value = PengajarUiState.Empty
                } else {
                    _uiState.value = PengajarUiState.Success(soalList)
                }
            }
        }
    }
}

class KuisPengerjaanViewModelFactory(private val kuisId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KuisPengerjaanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return KuisPengerjaanViewModel(kuisId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
