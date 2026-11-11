package com.example.projectmobile.ui.pengajar.institusi

data class PengajarProfilGuru(
    val uid: String = "",
    val nama: String = "",
    val gelar: String = "",
    val fotoUrl: String = "",
    val institusiId: String = ""
)

data class PengajarKelas(
    val id: String = "",
    val namaMataKuliah: String = "",
    val namaKelas: String = "",
    val kodeKelas: String = "",
    val jenisInstitusi: String = "",
    val semester: String = "",
    val ikon: String = "ic_book",
    val warna: String = "#1D5EFF",
    val jumlahPelajar: Int = 0,
    val guruId: String = "",
    val arsip: Boolean = false
)

data class PengajarStatistikBeranda(
    val kelasDiampu: Int = 0,
    val tugasAktif: Int = 0,
    val perluDinilai: Int = 0,
    val totalPelajar: Int = 0
)

data class PengajarNotifikasi(
    val id: String = "",
    val userId: String = "",
    val judul: String = "",
    val isi: String = "",
    val dibaca: Boolean = false,
    val createdAt: Long = 0L
)

sealed class PengajarUiState<out T> {
    object Loading : PengajarUiState<Nothing>()
    data class Success<T>(val data: T) : PengajarUiState<T>()
    object Empty : PengajarUiState<Nothing>()
    data class Error(val message: String) : PengajarUiState<Nothing>()
}
