# BUG_DIKENAL.md — Catatan Bug & Isu Arsitektural Teratasi

## 1. Bug Instansiasi Repository Tanpa Pola Singleton (Baru)
- **Deskripsi Masalah**: `PengajarRepositoryProvider` sebelumnya memanggil fungsi instansiasi `Fake` repository (seperti `FakePengumpulanRepository()`, `FakeTugasRepository()`, dll) secara langsung (sebagai objek baru `new`) pada setiap pemanggilan *provide*-nya. Akibatnya, setiap Activity mendapatkan *instance* repository terpisah dengan *StateFlow* yang terisolasi, sehingga data yang ditulis di satu Activity (misal submit tugas) tidak terbaca oleh Activity lain yang membaca repository yang sama.
- **Solusi**: Menerapkan pola *Singleton* menggunakan penang caching `by lazy` di dalam object `PengajarRepositoryProvider` untuk memastikan satu instansiasi repository dibagikan ke seluruh aplikasi.

## 2. Default Senyap (Silent Defaults)
- **Prinsip**: Dilarang keras menggunakan fallback nilai diam-diam (seperti string fiktif atau ID default) pada parameter wajib seperti `pelajarId`, `tugasId`, atau `kelasId`.
- **Solusi**: Jika parameter wajib kosong/blank, repository harus mengembalikan `Result.failure` atau `flowOf(emptyList())` secara eksplisit, dan Activity menangani state error/kosong secara transparan.
