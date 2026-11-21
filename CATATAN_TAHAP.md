# CATATAN_TAHAP.md — Detail Kelas Pelajar Mode Institusi (Selesai)

## 1. Status Selesai (Sesi 2)
- **Detail Kelas Pelajar Institusi** (`DetailKelasPelajarInstitusiActivity.kt`) telah ditulis ulang dari prosedural `createScreen()` menjadi layout XML resmi (`activity_detail_kelas_pelajar_institusi.xml`), lengkap dengan `PelajarFileMateriAdapter` (`ListAdapter` + `DiffUtil`) serta item layout `item_file_materi_pelajar.xml`.
- **Model `Kelas`**:
  - Ditambahkan field pelengkap untuk tampilan header detail kelas: `dosenPengampu`, `dosenFotoUrl`, `tanggalPertemuan`, dan `pertemuanKe` — semuanya tanpa nilai default senyap (default: `""` atau `0L` atau `0`), diisi secara eksplisit pada data contoh di `FakeKelasRepository`.
- **Single Source of Truth**:
  - Beranda (`BerandaPelajarInstitusiActivity`) dan Kelas Saya (`KelasPelajarInstitusiActivity`) mengirimkan `EXTRA_KELAS_ID` ke `DetailKelasPelajarInstitusiActivity`, yang kemudian menggunakan `kelasRepo.kelasById(kelasId)` sebagai sumber kebenaran tunggal untuk merender judul materi, pertemuan, nama dosen, tanggal, dan deskripsi pendahuluan secara dinamis.
- **Jembatan & TODO**:
  - Tombol `"Tanyakan Materi ke AI"` disiapkan dengan intent ke `KelolaAiAsistenPelajarActivity` disertai extra konteks (nama kelas + pertemuan) dan Toast TODO (`startActivity` di-comment sesuai pola Bug 5).
  - Tombol `"Tandai Selesai"` menampilkan Toast TODO (`"Materi ditandai selesai (TODO - belum tersimpan)"`).
  - Tab Tugas, Kuis, Diskusi tersambung dengan `UiKit.withProfile()`. Tab Presensi menampilkan Toast TODO placeholder.
  - Repository Configuration: `PengajarRepoConfig.useFakeData = false` (Default Empty Repository).

## 2. Definisi Selesai (Checklist BUG_DIKENAL.md)
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pelajar) -> `ModePembelajaranActivity` (Institusi) -> `BerandaPelajarInstitusiActivity` -> Klik Kartu Kelas -> `DetailKelasPelajarInstitusiActivity`.
c) **Bukti Pencarian**: String data dummy steril dari file produksi non-Fake.
d) **Default Repository = Empty**: `PengajarRepoConfig.useFakeData = false`.
e) **CATATAN_TAHAP.md diperbarui**.
