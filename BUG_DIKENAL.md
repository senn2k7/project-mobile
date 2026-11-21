# BUG_DIKENAL.md — Catatan Bug & Isu Arsitektural Teratasi

## 1. Bug Instansiasi Repository Tanpa Pola Singleton (Teratasi)
- **Deskripsi Masalah**: `PengajarRepositoryProvider` sebelumnya memanggil fungsi instansiasi `Fake` repository secara langsung (`new`) pada setiap pemanggilan *provide*-nya, menyebabkan state terisolasi antar Activity.
- **Solusi**: Menerapkan pola *Singleton* menggunakan penang caching `by lazy` di dalam object `PengajarRepositoryProvider`.

## 2. Bug CardView Item Tidak Responsif Klik (Teratasi)
- **Deskripsi Masalah**: `CardView` di dalam `RecyclerView` item (seperti opsi jawaban kuis) tidak merespons sentuhan / klik jika tidak diberi atribut interaktif pada XML root-nya.
- **Solusi**: Menambahkan `android:clickable="true"`, `android:focusable="true"`, dan `android:foreground="?attr/selectableItemBackground"` pada root `CardView` (misal pada `item_opsi_jawaban_pelajar.xml`).

## 3. Default Senyap (Silent Defaults)
- **Prinsip**: Dilarang keras menggunakan fallback nilai diam-diam pada parameter wajib (`pelajarId`, `kuisId`, dll).
- **Solusi**: Mengembalikan `Result.failure` atau `flowOf(emptyList())` secara eksplisit.
