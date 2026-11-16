# CATATAN_TAHAP.md — Tulis Ulang Layar Detail Tugas Pengajar Institusi

## 1. Status Selesai
- **Detail Tugas Pengajar** (`PengajarDetailTugasActivity.kt`) telah ditulis ulang dari kode Kotlin prosedural (*createScreen*) menjadi layout XML resmi (`activity_pengajar_detail_tugas.xml`) dengan menggunakan RecyclerView + `PengajarLampiranAdapter` (`ListAdapter` + `DiffUtil`) serta item layout `item_lampiran_tugas.xml`.
- Tampilan disesuaikan persis dengan gambar acuan `05-detail-tugas.png`:
  - Top bar dengan tombol kembali, judul `"Detail Tugas"`, dan menu vertikal 3 titik (`ic_more_vert`).
  - Kartu header tugas dengan ikon vektor kode (`ic_code`), judul, subjudul pertemuan, dan chip status `"Tugas Berlangsung"`.
  - Kartu deskripsi tugas dengan teks multiline dan informasi batas pengumpulan serta tipe tugas.
  - Kartu lampiran tugas dengan RecyclerView untuk berkas lampiran.
  - Kartu status pengumpulan dengan 2 kartu statistik (Mengumpulkan & Belum Mengumpulkan).
  - Pinned bottom action bar dengan tombol `"Lihat Pengumpulan"` dan `"Edit Tugas"`.

## 2. Definisi Selesai
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pengajar) -> `ModePembelajaranActivity` (Institusi) -> `PengajarBerandaActivity` -> Tab Tugas (`TugasActivity`) -> Kartu Tugas -> `PengajarDetailTugasActivity`.
c) **Bukti Pencarian**: String data dummy hanya ada di `PengajarFakeRepositories.kt` dan kode pelajar (`DAFTAR_PELAJAR_DILARANG`).
d) **Bukti Sumber Data**:
   - `PengajarDetailTugasActivity` membaca data tugas dan lampiran dari `TugasRepository` lewat `DetailTugasViewModel`.
e) **Default Repository = Empty**:
   - `PengajarRepoConfig.useFakeData = false`.
f) **Layar Lama Pengganti**:
   - `PengajarDetailTugasActivity` menggunakan layout XML dan tidak lagi membangun tampilan via kode Kotlin prosedural.
g) **CATATAN_TAHAP.md diperbarui**.
