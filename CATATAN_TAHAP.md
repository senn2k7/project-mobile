# CATATAN_TAHAP.md — Sesi 3: Tugas Pelajar Mode Institusi (Selesai)

## 1. Status Selesai (Sesi 3)
- **Tugas Pelajar Institusi** (`TugasPelajarInstitusiActivity.kt`), **Detail Tugas Pelajar** (`DetailTugasPelajarInstitusiActivity.kt`), dan **Tugas Berhasil Dikumpulkan** (`TugasBerhasilPelajarActivity.kt`) telah selesai diimplementasikan menggunakan layout XML resmi dan komponen RecyclerView + ListAdapter.
- **Model Pengumpulan**:
  - Diperluas dengan field `jawabanText` dan `nilai` (nullable `Int? = null` agar nilai 0 tetap valid).
- **Arsitektur Repository & Singleton**:
  - `PengajarRepositoryProvider` diperbarui dengan pola Singleton (`by lazy`), memastikan sinkronisasi reaktif antara `DetailTugasPelajarInstitusiActivity` (saat submit) dan `TugasPelajarInstitusiActivity` (saat membaca list status).
- **Zero Default Senyap**:
  - Pengecekan `pelajarId.isBlank()` pada `kumpulkanTugas()` dan `getPengumpulanPelajar()` mengembalikan `Result.failure` dan `flowOf(emptyList())`.
- **Repository Configuration**:
  - `PengajarRepoConfig.useFakeData = false` (Default Empty Repository).

## 2. Definisi Selesai (Checklist BUG_DIKENAL.md)
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pelajar) -> `ModePembelajaranActivity` (Institusi) -> `BerandaPelajarInstitusiActivity` -> Tab Aktivitas / Menu Tugas -> `TugasPelajarInstitusiActivity` -> Klik Tugas -> `DetailTugasPelajarInstitusiActivity` -> Submit -> `TugasBerhasilPelajarActivity`.
c) **Bukti Pencarian**: String data dummy steril dari file produksi non-Fake.
d) **Default Repository = Empty**: `PengajarRepoConfig.useFakeData = false`.
e) **CATATAN_TAHAP.md & BUG_DIKENAL.md diperbarui**.
