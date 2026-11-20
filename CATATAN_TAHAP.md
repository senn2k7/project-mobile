# CATATAN_TAHAP.md — Beranda & Kelas Saya Pelajar Mode Institusi (Selesai)

## 1. Status Selesai
- **Beranda Pelajar Institusi** (`BerandaPelajarInstitusiActivity.kt`) dan **Kelas Saya Pelajar** (`KelasPelajarInstitusiActivity.kt`) telah **SELESAI** diimplementasikan sesuai acuan visual Figma (`01-beranda-pelajar.png` & `02-kelas-saya.png`).
- **Agenda Mendatang & Statistik**:
  - `tugasMendatang(pelajarId)` dan `kuisMendatang(pelajarId)` kini melakukan query dinamis berbasis kelas yang diikuti (`getKelasDiikuti(pelajarId)`) dan filter waktu (`tenggat > now`), menggantikan data statis/hardcode sebelumnya.
  - Kartu statistik dan agenda mendatang kini 100% konsisten dengan data repository.
- **Progress Bar Kelas**:
  - Ditampilkan dengan nilai default `0%` (TODO untuk implementasi sistem tracking pembacaan materi/tugas per pelajar di sesi terpisah).
- **PelajarBottomNavHelper** (`PelajarBottomNavHelper.kt`) telah aktif dengan 5 tab (Beranda, Kelas, AI, Aktivitas, Profil).
- **Repository Configuration**:
  - `PengajarRepoConfig.useFakeData` dikembalikan ke `false` (Default Empty Repository).

## 2. Definisi Selesai (Checklist BUG_DIKENAL.md)
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pelajar) -> `ModePembelajaranActivity` (Institusi) -> `BerandaPelajarInstitusiActivity` -> Tab Kelas / "Lihat Semua" -> `KelasPelajarInstitusiActivity`.
c) **Bukti Pencarian**: String data dummy steril dari file produksi non-Fake.
d) **Default Repository = Empty**: `PengajarRepoConfig.useFakeData = false`.
e) **CATATAN_TAHAP.md diperbarui**.
