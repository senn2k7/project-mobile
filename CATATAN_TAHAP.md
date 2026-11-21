# CATATAN_TAHAP.md — Sesi 4: Kuis Pelajar Mode Institusi (Selesai)

## 1. Status Selesai (Sesi 4)
- **Detail Kuis Pelajar** (`DetailKuisPelajarInstitusiActivity.kt`), **Mulai Kuis Pelajar** (`MulaiKuisPelajarActivity.kt`), dan **Hasil Kuis Pelajar** (`HasilKuisPelajarActivity.kt`) telah selesai diimplementasikan menggunakan layout XML resmi dan komponen RecyclerView (`PelajarOpsiAdapter`).
- **Model HasilKuis**:
  - Diperluas dengan field baru `lulus: Boolean` (terpisah dari `status` `"selesai"` untuk menghindari konflik kontrak dengan sisi pengajar).
- **Arsitektur ViewModel & Timer**:
  - `KuisPengerjaanViewModel` mengelola state soal, indeks aktif, dan peta jawaban.
  - Timer berbasis timestamp (`startTime` + durasi) sehingga tetap akurat dan tidak mereset saat rotasi layar (*config change*) maupun saat aplikasi diminimize/resume.
- **Orientasi Layar**:
  - Semua Activity pelajar institusi dikunci ke `android:screenOrientation="portrait"` di `AndroidManifest.xml` agar konsisten dengan seluruh acuan mockup Figma.
- **Jembatan & TODO**:
  - Tombol `"Lihat Pembahasan"` menampilkan Toast TODO (`"Membuka pembahasan kuis (TODO)"`).
  - Repository Configuration: `PengajarRepoConfig.useFakeData = false` (Default Empty Repository).

## 2. Definisi Selesai (Checklist BUG_DIKENAL.md)
a) **Build lancar**: Build sukses (`app:assembleDebug` passed).
b) **Jalur Navigasi**:
   - `MainActivity` -> `LoginActivity` -> `PilihPeranActivity` (Pelajar) -> `ModePembelajaranActivity` (Institusi) -> `BerandaPelajarInstitusiActivity` -> Tab Aktivitas / Menu Tugas/Kuis -> `DetailKuisPelajarInstitusiActivity` -> `MulaiKuisPelajarActivity` -> Submit -> `HasilKuisPelajarActivity`.
c) **Bukti Pencarian**: String data dummy steril dari file produksi non-Fake.
d) **Default Repository = Empty**: `PengajarRepoConfig.useFakeData = false`.
e) **CATATAN_TAHAP.md & BUG_DIKENAL.md diperbarui**.
