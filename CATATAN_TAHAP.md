# CATATAN_TAHAP.md — Tulis Ulang Layar Detail Kelas Pengajar Institusi

## 1. Status Selesai
- **Detail Kelas Pengajar** (`PengajarDetailKelasActivity.kt`) telah ditulis ulang dari kode Kotlin prosedural (*createScreen*) menjadi layout XML resmi (`activity_pengajar_detail_kelas.xml`) dengan menggunakan RecyclerView + `PengajarMateriAdapter` (`ListAdapter` + `DiffUtil`) serta item layout `item_materi_pengajar.xml`.
- Tampilan disesuaikan persis dengan gambar acuan `03-detail-kelas.png`:
  - Top bar dengan tombol kembali, judul `"Detail Kelas"`, dan menu vertikal 3 titik (`ic_more_vert`).
  - Kartu header kelas dengan ikon vektor kode (`ic_code`) di dalam kotak biru, nama mata kuliah, institusi, kode kelas, dan info pengajar.
  - Kartu deskripsi kelas dengan tautan edit.
  - Tab horizontal scrollable (Materi, Tugas, Lihat Hasil Kuis, Buat Kuis, Diskusi) dan presensi wide pill bar.
  - Daftar materi dengan ikon vektor berkas, nama file, ukuran, dan menu vertikal.
  - Tombol aksi utama (*"+ Tambah Materi"* / *"+ Buat Tugas"*) dipin (*pinned*) di bagian bawah layar.

## 2. Definisi Selesai
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pengajar) -> `ModePembelajaranActivity` (Institusi) -> `PengajarBerandaActivity` -> Klik Kartu Kelas -> `PengajarDetailKelasActivity`.
c) **Bukti Pencarian**: String data dummy hanya ada di `PengajarFakeRepositories.kt` dan kode pelajar (`DAFTAR_PELAJAR_DILARANG`).
d) **Bukti Sumber Data**:
   - `PengajarDetailKelasActivity` membaca data kelas dan materi dari `KelasRepository` dan `MateriRepository` lewat `DetailKelasViewModel`.
e) **Default Repository = Empty**:
   - `PengajarRepoConfig.useFakeData = false`.
f) **Layar Lama Pengganti**:
   - `PengajarDetailKelasActivity` menggunakan layout XML dan tidak lagi membangun tampilan via kode Kotlin prosedural.
g) **CATATAN_TAHAP.md diperbarui**.
