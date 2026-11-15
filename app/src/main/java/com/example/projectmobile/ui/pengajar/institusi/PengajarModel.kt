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
    val pertemuanKe: Int = 1,
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
    val pertemuanKe: Int = 1,
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

data class Kuis(
    val id: String = "",
    val kelasId: String = "",
    val pertemuanKe: Int = 1,
    val judul: String = "",
    val instruksi: String = "",
    val durasiMenit: Int = 20,
    val nilaiMinimal: Int = 70,
    val jumlahSoal: Int = 5,
    val namaFileSoal: String = "",
    val tipeFileSoal: String = "",
    val ukuranByteFileSoal: Long = 0L,
    val urlFileSoal: String = "",
    val status: String = "dipublikasikan", // "draf" atau "dipublikasikan"
    val createdAt: Long = 0L,
    val publishedAt: Long = 0L
)

data class OpsiSoal(
    val kode: String = "",
    val teks: String = ""
)

data class Soal(
    val id: String = "",
    val kuisId: String = "",
    val nomor: Int = 1,
    val pertanyaan: String = "",
    val opsi: List<OpsiSoal> = emptyList(),
    val kunciJawaban: String = "A"
)

data class JawabanSoal(
    val soalId: String = "",
    val kodeDipilih: String = "",
    val benar: Boolean = false
)

data class HasilKuis(
    val id: String = "",
    val kuisId: String = "",
    val kelasId: String = "",
    val pelajarId: String = "",
    val namaPelajar: String = "",
    val nim: String = "",
    val fotoUrl: String = "",
    val nilai: Int? = null, // null jika belum selesai
    val nilaiMaks: Int = 100,
    val status: String = "selesai", // "selesai" atau "belum_selesai"
    val durasiPengerjaanMenit: Int = 0,
    val waktuSelesai: Long = 0L,
    val jawaban: List<JawabanSoal> = emptyList()
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

data class FileLampiranInput(
    val uriString: String = "",
    val nama: String = "",
    val tipeFile: String = "",
    val ukuranByte: Long = 0L
)

data class InputMateri(
    val kelasId: String = "",
    val pertemuanKe: Int = 1,
    val judul: String = "",
    val deskripsi: String = "",
    val file: FileLampiranInput? = null
)

data class InputTugas(
    val kelasId: String = "",
    val pertemuanKe: Int = 1,
    val judul: String = "",
    val deskripsi: String = "",
    val tenggat: Long = 0L,
    val tipe: String = "individu",
    val lampiranList: List<FileLampiranInput> = emptyList()
)

data class InputKuis(
    val kelasId: String = "",
    val pertemuanKe: Int = 1,
    val judul: String = "",
    val instruksi: String = "",
    val durasiMenit: Int = 20,
    val nilaiMinimal: Int = 70,
    val jumlahSoal: Int = 10,
    val fileSoal: FileLampiranInput? = null,
    val status: String = "dipublikasikan"
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
