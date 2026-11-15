# ATURAN_AI.md — Aturan proyek TemanStudi (pengajar, mode institusi)

## 1. LINGKUP
- Hanya UI PENGAJAR pada MODE INSTITUSI. Presensi dan tab Diskusi tidak dikerjakan (placeholder tanpa aksi).
- Jangan mengubah login, register, forgot password, onboarding, pilih mode pembelajaran.
- Jangan menyambungkan UI pengajar ke Firebase sekarang. Jangan mengubah konfigurasi Firebase yang sudah ada (google-services.json, plugin, dependency).

## 2. KEPEMILIKAN FILE (diisi di Tahap 0, disimpan di CATATAN_TAHAP.md)
Ada tiga daftar:
- DAFTAR_PENGAJAR: file yang hanya dipakai alur pengajar-institusi. BOLEH diubah atau ditulis ulang.
- DAFTAR_PELAJAR_DILARANG: file milik pelajar atau mode lain. DILARANG diubah, termasuk isinya.
- DAFTAR_BERSAMA: file yang dipakai kedua alur (mis. TaskStore). Jangan mengosongkan, menghapus, atau mengubah perilakunya untuk pelajar. Alur pengajar berhenti memakainya.
File yang belum ada di daftar: berhenti dan tanyakan sebelum mengubahnya.
Pembagian ditentukan dengan Find Usages dan membaca alur navigasi, bukan dari nama file.

## 3. PENGGANTIAN LAYAR LAMA (penyebab error sebelumnya)
Untuk setiap layar pengajar yang sudah ada dan menampilkan data dummy:
- Jika Activity lamanya ada di DAFTAR_PENGAJAR: TULIS ULANG DI TEMPAT (nama class sama) agar semua titik masuk yang ada otomatis memakai layar baru.
- Jika Activity lamanya ada di DAFTAR_BERSAMA: buat Activity baru berawalan "Pengajar" dan UBAH SEMUA titik masuk pengajar (bottom navigation pengajar, kartu kelas, tombol) agar membuka yang baru. Activity lama tetap untuk pelajar.
- Jangan pernah membiarkan layar lama dan layar baru hidup berdampingan dengan titik masuk pengajar yang masih mengarah ke yang lama.

## 4. TEKNIS
- Kotlin, XML Views (bukan Compose). ViewBinding jika proyek sudah memakainya.
- MVVM: Activity/Fragment -> ViewModel (StateFlow) -> Repository (interface) -> sumber data.
- UI dan ViewModel TIDAK boleh import Firebase.
- Jangan menambah library/dependency baru. Ikuti pola proyek (DI yang ada, atau ViewModelFactory manual).
- Antar layar kirim hanya ID lewat Intent extra/arguments.

## 5. DATA
- Model: data class biasa, semua properti punya default value, ada id.
- Setiap repository = interface + Empty + Fake. Pemilihan di SATU tempat (RepositoryProvider) lewat satu flag useFakeData = false. Default HARUS Empty dan TIDAK BOLEH bergantung pada BuildConfig.DEBUG atau build type.
- Semua Fake hanya ada di file PengajarFakeRepositories.kt (dan file Fake lain yang namanya berakhiran Fake). Tidak ada data contoh di file lain.
- Empty: baca = data kosong; tulis = Result.failure("Belum terhubung ke database"), tampil sebagai pesan error, bukan sukses palsu.
- Fake: tanggal dibuat relatif terhadap waktu sekarang. Pada Fake, operasi tulis boleh sukses.
- Baca lewat Flow. UiState: Loading, Success, Empty, Error. Pencarian memakai debounce.
- Statistik dan hitungan dihitung di ViewModel. Status berlangsung/selesai dihitung dari tenggat; field status di model hanya "draf"/"dipublikasikan".

## 6. TAMPILAN
- Warna, ukuran, radius, spacing, font diambil dari gambar desain dan dicocokkan dengan resource yang ada. Resource baru diberi nama jelas. Jangan mengubah resource lama yang dipakai layar pelajar.
- Ikon dari res/drawable. Tambah ic_*.xml hanya jika sudah ada. Gunakan ikon vektor sesuai desain, bukan emoji.
- Semua daftar = RecyclerView + ListAdapter (DiffUtil). Jangan menulis item berulang di XML.
- DILARANG: teks data (nama, judul, tanggal, angka, status) di android:text layout, strings.xml, Adapter, atau kode di luar Fake. tools:text hanya untuk preview. Teks tetap (judul, label, hint, empty state) di strings.xml.
- Angka/teks contoh di Figma (20 menit, Pertemuan 6, dll.) jangan diisi otomatis. Field mulai kosong.
- Saat data kosong: top bar, tab, tombol aksi, kolom pencarian, chip filter, kartu statistik (angka 0) TETAP tampil. Hanya area data diganti empty state selaras tema.
- Loading: placeholder berbentuk kartu berukuran sama dengan aslinya, tanpa library.

## 7. DEFINISI SELESAI (WAJIB untuk setiap tahap)
Sebuah tahap BELUM selesai sampai semua ini dibuktikan di laporan akhir:
a) Build lancar.
b) JALUR NAVIGASI: tulis jalur dari layar utama aplikasi sampai tiap layar tahap ini, dengan nama class di tiap langkah (mis. HomeActivity -> PengajarBottomNavHelper "Tugas" -> TugasActivity). Setiap layar harus bisa dicapai dari aplikasi asli tanpa mengubah kode.
c) BUKTI PENCARIAN: hasil pencarian teks dummy yang relevan ("Membuat UI Login Android", "Pemrograman Mobile", "Sistem Cerdas", "Basis Data", "Budi", "Nadia", "pelajar", angka statistik tertulis). Hasilnya hanya boleh ada di file Fake dan DAFTAR_PELAJAR_DILARANG.
d) BUKTI SUMBER DATA: untuk tiap layar, tulis satu baris "Layar X membaca dari Repository Y lewat ViewModel Z".
e) Default repository = Empty (tampilkan baris kode flag-nya).
f) Layar lama pengganti: nyatakan "ditulis ulang di tempat" atau "tidak lagi dipakai oleh titik masuk pengajar mana pun".
g) Perbarui CATATAN_TAHAP.md (file diubah, komponen reusable, keputusan, TODO).
Lalu BERHENTI.

## 8. CARA KERJA
- Awal tahap: baca ATURAN_AI.md dan CATATAN_TAHAP.md. Berikan rencana file maksimal 10 baris, lalu minta persetujuan.
- Kerjakan HANYA tahap yang diminta.
- Kuota/konteks hampir habis: berhenti di titik build hijau dan catat progres di CATATAN_TAHAP.md.
