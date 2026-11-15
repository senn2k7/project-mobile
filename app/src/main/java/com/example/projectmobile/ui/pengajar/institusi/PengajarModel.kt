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

data class Kelas(
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
    val arsip: Boolean = false,
    val deskripsi: String = ""
)

data class Materi(
    val id: String = "",
    val kelasId: String = "",
    val pertemuanKe: Int = 0,
    val judul: String = "",
    val deskripsi: String = "",
    val namaFile: String = "",
    val tipeFile: String = "",
    val ukuranByte: Long = 0L,
    val url: String = "",
    val status: String = "dipublikasikan",
    val createdAt: Long = 0L
)

data class Tugas(
    val id: String = "",
    val kelasId: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val pertemuanKe: Int = 0,
    val ikon: String = "ic_assignment_check",
    val tipe: String = "individu", // "individu" atau "kelompok"
    val tenggat: Long = 0L,
    val totalPelajar: Int = 0,
    val jumlahMengumpulkan: Int = 0,
    val status: String = "dipublikasikan", // "draf" atau "dipublikasikan"
    val createdAt: Long = 0L
)

data class LampiranTugas(
    val id: String = "",
    val tugasId: String = "",
    val nama: String = "",
    val tipeFile: String = "",
    val ukuranByte: Long = 0L,
    val url: String = ""
)

data class PelajarKelas(
    val uid: String = "",
    val nama: String = "",
    val nim: String = "",
    val fotoUrl: String = ""
)

data class Pengumpulan(
    val id: String = "",
    val tugasId: String = "",
    val pelajarId: String = "",
    val namaPelajar: String = "",
    val fotoUrl: String = "",
    val waktuKumpul: Long = 0L,
    val namaFile: String = "",
    val tipeFile: String = "",
    val ukuranByte: Long = 0L,
    val url: String = ""
)

data class DaftarPengumpulan(
    val sudahMengumpulkan: List<Pengumpulan> = emptyList(),
    val belumMengumpulkan: List<PelajarKelas> = emptyList(),
    val totalSudah: Int = 0,
    val totalBelum: Int = 0
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
