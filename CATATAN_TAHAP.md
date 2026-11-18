# CATATAN_TAHAP.md — Beranda & Kelas Saya Pelajar Mode Institusi

## 1. Status Selesai
- **Beranda Pelajar Institusi** (`BerandaPelajarInstitusiActivity.kt`) telah diisi desain lengkap sesuai acuan visual `01-beranda-pelajar.png`:
  - Header sapaan, search bar, 3 kartu statistik (Kelas Aktif, Tugas Pending, Kuis Mendatang) mengambil data nyata dari `KelasRepository.getKelasDiikuti()`.
  - Section "Kelas Saya" (preview card + "Lihat Semua" ke `KelasPelajarInstitusiActivity`), Agenda Mendatang, dan banner AI dengan jembatan Toast TODO ("Kelola AI Asisten (TODO)").
- **Kelas Saya Pelajar** (`KelasPelajarInstitusiActivity.kt`) telah ditulis ulang dari prosedural `createScreen()` menjadi layout XML resmi (`activity_kelas_pelajar_institusi.xml`) dengan RecyclerView + `PelajarKelasAdapter` (`ListAdapter` + `DiffUtil`) serta item layout `item_kelas_pelajar.xml`. Progress bar diset 0% default (TODO pencatatan tracking di masa mendatang).
- **PelajarBottomNavHelper** (`PelajarBottomNavHelper.kt`) telah dibuat terpusat dengan 5 tab (Beranda, Kelas, AI, Aktivitas, Profil) dan dipasang di kedua layar pelajar institusi.
- **Repository**:
  - Penambahan fungsi `getKelasDiikuti(searchQuery, pelajarId)` pada `PengajarKelasRepository` (dan implementasinya di `FakePengajarKelasRepository` & `EmptyPengajarKelasRepository`).
- **Manifest**:
  - `BerandaPelajarInstitusiActivity`, `KelasPelajarInstitusiActivity`, dan `KelolaAiAsistenPelajarActivity` telah didaftarkan di `AndroidManifest.xml`.

## 2. Definisi Selesai
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pelajar) -> `ModePembelajaranActivity` (Institusi) -> `BerandaPelajarInstitusiActivity` -> Tab Kelas / "Lihat Semua" -> `KelasPelajarInstitusiActivity`.
c) **Bukti Pencarian**: String data dummy steril dari file produksi non-Fake.
d) **Default Repository = Empty**: `PengajarRepoConfig.useFakeData = false`.
e) **CATATAN_TAHAP.md diperbarui**.
