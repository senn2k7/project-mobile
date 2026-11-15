# Catatan Tahap - TemanStudi (Pengajar Mode Institusi)

## Status Saat Ini
- Selesai membangun:
  1. **Layar Beranda Pengajar** (`PengajarBerandaActivity.kt`, `activity_pengajar_beranda.xml`)
  2. **Layar Kelas Saya Pengajar** (`PengajarKelasSayaActivity.kt`, `activity_pengajar_kelas_saya.xml`)
  3. **Tahap A: Fondasi Data Detail Kelas, Tugas, Detail Tugas, & Pengumpulan Tugas** (Models, Repositories [Empty & Fake], Utils, ViewModels + UiState, ViewModelFactories).

## File Dibuat / Diubah
- `ui/pengajar/institusi/PengajarModel.kt` (ditambah Kelas.deskripsi, Materi, Tugas, LampiranTugas, PelajarKelas, Pengumpulan, DaftarPengumpulan)
- `ui/pengajar/institusi/PengajarRepositories.kt` (ditambah interface, Empty, & Fake untuk MateriRepository, TugasRepository, PengumpulanRepository, dan kelasById di KelasRepository)
- `ui/pengajar/institusi/PengajarUtils.kt` (FileIconMapper, FormatUkuran, FormatTanggal, NameUtils, TaskStatusUtils)
- `ui/pengajar/institusi/PengajarViewModels.kt` (DetailKelasViewModel, TugasViewModel, DetailTugasViewModel, PengumpulanViewModel + ViewModelFactories)
- Layouts & Drawables sebelumnya.

## Komponen Reusable
- **`PengajarKelasAdapter`**: Adapter RecyclerView berbasis `ListAdapter` dan `DiffUtil` untuk kartu kelas.
- **`PengajarBottomNavHelper`**: Komponen helper navigasi bawah standar.
- **`FileIconMapper` & `FormatUkuran` & `FormatTanggal` & `NameUtils` & `TaskStatusUtils`**: Utilitas pemetaan ikon file, format ukuran byte, format tanggal Indonesia, inisial & warna avatar, serta kalkulator status tenggat tugas.

## Keputusan & Catatan
- Semua data default bernilai kosong / zero melalui `Empty...Repository` saat `useFakeData = false`.
- Fake repository diisi data lengkap sesuai spesifikasi Tahap A (Pemrograman Mobile, Sistem Cerdas, Basis Data, materi, tugas, dan 25 pelajar dengan 18 sudah mengumpulkan & 7 belum).
- Tidak ada dependency atau import Firebase di UI / ViewModel.
- Belum ada layout atau Activity baru yang dibuat pada Tahap A (sesuai aturan).

## Daftar TODO Selanjutnya
- Menunggu instruksi tahap berikutnya untuk implementasi UI Detail Kelas, Tugas, Detail Tugas, dan Pengumpulan Tugas.
