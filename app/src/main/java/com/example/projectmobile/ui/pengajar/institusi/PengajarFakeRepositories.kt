package com.example.projectmobile.ui.pengajar.institusi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale

class FakePengajarProfilRepository : PengajarProfilRepository {
    override fun getProfil(): Flow<PengajarProfilGuru?> = flowOf(
        PengajarProfilGuru(
            uid = "teacher_budi",
            nama = "Pak Budi",
            gelar = "S.T., M.T.",
            fotoUrl = "",
            institusiId = "inst_01"
        )
    )
}

class FakePengajarKelasRepository : PengajarKelasRepository {
    private val allKelas = listOf(
        Kelas(
            id = "1",
            namaMataKuliah = "Pemrograman Mobile",
            namaKelas = "Kelas A",
            kodeKelas = "PM2025-A",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_code",
            warna = "#1D5EFF",
            jumlahPelajar = 28,
            deskripsi = "Mata kuliah pengembangan aplikasi Android modern menggunakan Kotlin dan Jetpack."
        ),
        Kelas(
            id = "2",
            namaMataKuliah = "Sistem Cerdas",
            namaKelas = "Kelas B",
            kodeKelas = "SC2025-B",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_database",
            warna = "#1D5EFF",
            jumlahPelajar = 32,
            deskripsi = "Studi tentang kecerdasan buatan, machine learning, dan sistem pakar."
        ),
        Kelas(
            id = "3",
            namaMataKuliah = "Basis Data",
            namaKelas = "Kelas C",
            kodeKelas = "BD2025-C",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_database",
            warna = "#1FAF6E",
            jumlahPelajar = 26,
            deskripsi = "Perancangan database relasional, normalisasi, dan SQL tingkat lanjut."
        )
    )

    override fun getKelasList(searchQuery: String, filterInstitusi: Boolean, semester: String): Flow<List<PengajarKelas>> {
        var filtered: List<PengajarKelas> = allKelas.map { it ->
            PengajarKelas(
                id = it.id,
                namaMataKuliah = it.namaMataKuliah,
                namaKelas = it.namaKelas,
                kodeKelas = it.kodeKelas,
                jenisInstitusi = it.jenisInstitusi,
                semester = it.semester,
                ikon = it.ikon,
                warna = it.warna,
                jumlahPelajar = it.jumlahPelajar,
                guruId = it.guruId,
                arsip = it.arsip
            )
        }
        if (filterInstitusi) {
            filtered = filtered.filter { it.jenisInstitusi.isNotBlank() }
        }
        if (semester.isNotBlank() && semester != "Semester ▾") {
            filtered = filtered.filter { it.semester.equals(semester, ignoreCase = true) }
        }
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.namaMataKuliah.contains(searchQuery, ignoreCase = true) ||
                        it.namaKelas.contains(searchQuery, ignoreCase = true) ||
                        it.kodeKelas.contains(searchQuery, ignoreCase = true)
            }
        }
        return flowOf(filtered)
    }

    override fun kelasById(id: String): Flow<Kelas?> {
        val found = allKelas.find { it.id == id }
        return flowOf(found)
    }
}

class FakeMateriRepository : MateriRepository {
    private val materiList = listOf(
        Materi(
            id = "m1",
            kelasId = "1",
            pertemuanKe = 1,
            judul = "Pengenalan Android Studio",
            deskripsi = "Materi dasar pengenalan lingkungan pengembangan Android Studio.",
            namaFile = "Pengenalan Android Studio.pdf",
            tipeFile = "application/pdf",
            ukuranByte = 2450000L,
            url = "",
            createdAt = System.currentTimeMillis() - 86400000L * 5
        ),
        Materi(
            id = "m2",
            kelasId = "1",
            pertemuanKe = 2,
            judul = "Struktur Project",
            deskripsi = "Penjelasan mengenai struktur folder dan file gradle pada project Android.",
            namaFile = "Struktur Project.zip",
            tipeFile = "application/zip",
            ukuranByte = 5200000L,
            url = "",
            createdAt = System.currentTimeMillis() - 86400000L * 3
        ),
        Materi(
            id = "m3",
            kelasId = "1",
            pertemuanKe = 3,
            judul = "Modul Pertemuan 1",
            deskripsi = "Modul praktikum sesi pertama.",
            namaFile = "Modul Pertemuan 1.pdf",
            tipeFile = "application/pdf",
            ukuranByte = 1800000L,
            url = "",
            createdAt = System.currentTimeMillis() - 86400000L * 1
        )
    )

    override fun materiPerKelas(kelasId: String): Flow<List<Materi>> {
        return flowOf(materiList.filter { it.kelasId == kelasId || kelasId == "1" })
    }

    override suspend fun buatMateri(input: InputMateri): Result<String> {
        return Result.success("materi_sukses_${System.currentTimeMillis()}")
    }
}

class FakeTugasRepository : TugasRepository {
    private val currentTime = System.currentTimeMillis()
    private val tugasList = listOf(
        Tugas(
            id = "t1",
            kelasId = "1",
            judul = "Membuat UI Login Android",
            deskripsi = "Buat antarmuka login menggunakan ConstraintLayout dan Material Components.",
            pertemuanKe = 2,
            ikon = "ic_assignment_check",
            tipe = "individu",
            tenggat = currentTime + 86400000L * 2, // Berlangsung (2 hari lagi)
            totalPelajar = 25,
            jumlahMengumpulkan = 18,
            status = "dipublikasikan",
            createdAt = currentTime - 86400000L * 3
        ),
        Tugas(
            id = "t2",
            kelasId = "1",
            judul = "Analisis Sistem",
            deskripsi = "Analisis kebutuhan sistem aplikasi mobile untuk studi kasus.",
            pertemuanKe = 3,
            ikon = "ic_assignment_check",
            tipe = "kelompok",
            tenggat = currentTime - 86400000L * 1, // Selesai (kemarin)
            totalPelajar = 25,
            jumlahMengumpulkan = 25,
            status = "dipublikasikan",
            createdAt = currentTime - 86400000L * 7
        ),
        Tugas(
            id = "t3",
            kelasId = "1",
            judul = "Laporan Basis Data",
            deskripsi = "Penyusunan ERD dan normalisasi tabel.",
            pertemuanKe = 4,
            ikon = "ic_assignment_check",
            tipe = "individu",
            tenggat = currentTime + 86400000L * 5,
            totalPelajar = 25,
            jumlahMengumpulkan = 5,
            status = "dipublikasikan",
            createdAt = currentTime - 86400000L * 1
        )
    )

    private val lampiranList = listOf(
        LampiranTugas(
            id = "l1",
            tugasId = "t1",
            nama = "Panduan_UI_Login.pdf",
            tipeFile = "application/pdf",
            ukuranByte = 1200000L,
            url = ""
        )
    )

    override fun tugasPerKelas(kelasId: String, filter: String): Flow<List<Tugas>> {
        var list = tugasList.filter { it.kelasId == kelasId || kelasId == "1" }
        if (filter.equals("BERLANGSUNG", ignoreCase = true)) {
            list = list.filter { TaskStatusUtils.hitungStatus(it.tenggat) == "Berlangsung" }
        } else if (filter.equals("SELESAI", ignoreCase = true)) {
            list = list.filter { TaskStatusUtils.hitungStatus(it.tenggat) == "Selesai" }
        }
        return flowOf(list)
    }

    override fun tugasById(id: String): Flow<Tugas?> {
        return flowOf(tugasList.find { it.id == id } ?: tugasList.first())
    }

    override fun lampiran(tugasId: String): Flow<List<LampiranTugas>> {
        return flowOf(lampiranList.filter { it.tugasId == tugasId })
    }

    override suspend fun buatTugas(input: InputTugas): Result<String> {
        return Result.success("tugas_sukses_${System.currentTimeMillis()}")
    }
}

class FakePengumpulanRepository : PengumpulanRepository {
    private val pelajarNames = listOf(
        "Nadia Putri", "Rizky Pratama", "Siti Aulia", "Andi Saputra",
        "Dewi Lestari", "Ahmad Fauzi", "Rina Marlina", "Budi Santoso",
        "Citra Kirana", "Dedi Kurniawan", "Eka Putri", "Fajar Hidayat",
        "Gita Gutawa", "Hadi Sucipto", "Indah Permata", "Joko Widodo",
        "Kartika Sari", "Lukman Hakim", "Maya Estianty", "Novan Pratama",
        "Olivia Zalianty", "Putri Titian", "Qomaruddin", "Rian D'Masiv", "Sinta Bella"
    )

    override fun statusPengumpulan(tugasId: String, query: String, filterStatus: String): Flow<DaftarPengumpulan> {
        val allPelajar = pelajarNames.mapIndexed { index, name ->
            PelajarKelas(
                uid = "p_$index",
                nama = name,
                nim = "105220${String.format(Locale.getDefault(), "%02d", index + 1)}",
                fotoUrl = ""
            )
        }

        // 18 sudah mengumpulkan, 7 belum
        val sudahList = allPelajar.take(18).mapIndexed { index, p ->
            Pengumpulan(
                id = "peng_$index",
                tugasId = tugasId,
                pelajarId = p.uid,
                namaPelajar = p.nama,
                fotoUrl = p.fotoUrl,
                waktuKumpul = System.currentTimeMillis() - index * 3600000L,
                namaFile = "Tugas_${p.nama.replace(" ", "_")}.pdf",
                tipeFile = "application/pdf",
                ukuranByte = 2100000L,
                url = ""
            )
        }

        val sudahIds = sudahList.map { it.pelajarId }.toSet()
        val belumFiltered = allPelajar.filter { !sudahIds.contains(it.uid) }

        var filteredSudah = sudahList
        var filteredBelum = belumFiltered

        if (query.isNotBlank()) {
            filteredSudah = filteredSudah.filter { it.namaPelajar.contains(query, ignoreCase = true) }
            filteredBelum = filteredBelum.filter { it.nama.contains(query, ignoreCase = true) }
        }

        val finalSudah = if (filterStatus.equals("BELUM", ignoreCase = true)) emptyList() else filteredSudah
        val finalBelum = if (filterStatus.equals("SUDAH", ignoreCase = true)) emptyList() else filteredBelum

        return flowOf(
            DaftarPengumpulan(
                sudahMengumpulkan = finalSudah,
                belumMengumpulkan = finalBelum,
                totalSudah = sudahList.size,
                totalBelum = belumFiltered.size
            )
        )
    }
}

class FakePengajarStatistikRepository : PengajarStatistikRepository {
    override fun getStatistik(): Flow<PengajarStatistikBeranda> = flowOf(
        PengajarStatistikBeranda(
            kelasDiampu = 4,
            tugasAktif = 12,
            perluDinilai = 3,
            totalPelajar = 86
        )
    )
}

class FakePengajarNotifikasiRepository : PengajarNotifikasiRepository {
    override fun getUnreadCount(): Flow<Int> = flowOf(2)
}

class FakeKuisRepository : KuisRepository {
    private val kuisList = mutableListOf(
        Kuis(
            id = "k1",
            kelasId = "1",
            pertemuanKe = 2,
            judul = "Kuis Struktur Data",
            instruksi = "Kerjakan soal pilihan ganda berikut dengan teliti.",
            durasiMenit = 20,
            nilaiMinimal = 70,
            jumlahSoal = 10,
            status = "dipublikasikan",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        )
    )

    override fun kuisPerKelas(kelasId: String): Flow<List<Kuis>> = flowOf(kuisList.filter { it.kelasId == kelasId || kelasId == "1" })
    override fun kuisById(id: String): Flow<Kuis?> = flowOf(kuisList.find { it.id == id })
    override suspend fun simpanKuis(input: InputKuis, status: String): Result<String> {
        val id = "kuis_${System.currentTimeMillis()}"
        kuisList.add(
            Kuis(
                id = id,
                kelasId = input.kelasId,
                pertemuanKe = input.pertemuanKe,
                judul = input.judul,
                instruksi = input.instruksi,
                durasiMenit = input.durasiMenit,
                nilaiMinimal = input.nilaiMinimal,
                jumlahSoal = input.jumlahSoal,
                status = status,
                createdAt = System.currentTimeMillis()
            )
        )
        return Result.success(id)
    }
}

class FakeHasilKuisRepository : HasilKuisRepository {
    private val soalList = listOf(
        Soal(
            id = "s1",
            kuisId = "k1",
            nomor = 1,
            pertanyaan = "Manakah struktur data yang menerapkan prinsip LIFO (Last In First Out)?",
            opsi = listOf(
                OpsiSoal("A", "Queue"),
                OpsiSoal("B", "Stack"),
                OpsiSoal("C", "Array"),
                OpsiSoal("D", "Tree")
            ),
            kunciJawaban = "B"
        ),
        Soal(
            id = "s2",
            kuisId = "k1",
            nomor = 2,
            pertanyaan = "Kompleksitas waktu pencarian biner (Binary Search) pada array terurut adalah...",
            opsi = listOf(
                OpsiSoal("A", "O(n)"),
                OpsiSoal("B", "O(n log n)"),
                OpsiSoal("C", "O(log n)"),
                OpsiSoal("D", "O(1)")
            ),
            kunciJawaban = "C"
        ),
        Soal(
            id = "s3",
            kuisId = "k1",
            nomor = 3,
            pertanyaan = "Struktur data linier di mana penambahan dan penghapusan elemen dilakukan pada ujung yang berbeda disebut...",
            opsi = listOf(
                OpsiSoal("A", "Stack"),
                OpsiSoal("B", "Queue"),
                OpsiSoal("C", "Graph"),
                OpsiSoal("D", "Tree")
            ),
            kunciJawaban = "B"
        )
    )

    private val pelajarNames = listOf(
        "Nadia Putri", "Rizky Pratama", "Siti Aulia", "Andi Saputra",
        "Dewi Lestari", "Ahmad Fauzi", "Rina Marlina", "Budi Santoso",
        "Citra Kirana", "Dedi Kurniawan", "Eka Putri", "Fajar Hidayat",
        "Gita Gutawa", "Hadi Sucipto", "Indah Permata", "Joko Widodo",
        "Kartika Sari", "Lukman Hakim", "Maya Estianty", "Novan Pratama",
        "Olivia Zalianty", "Putri Titian", "Qomaruddin", "Rian D'Masiv", "Sinta Bella"
    )

    override fun hasilPerKuis(kuisId: String, query: String): Flow<List<HasilKuis>> {
        val list = pelajarNames.mapIndexed { index, name ->
            val isSelesai = index < 20 // 20 selesai, 5 belum
            val nilai = if (isSelesai) 70 + (index * 3) % 31 else null
            val jawaban = soalList.map { soal ->
                val benar = isSelesai && (index % 2 == 0 || soal.id != "s2")
                val kode = if (benar) soal.kunciJawaban else if (soal.kunciJawaban == "A") "B" else "A"
                JawabanSoal(soalId = soal.id, kodeDipilih = kode, benar = benar)
            }
            HasilKuis(
                id = "hk_$index",
                kuisId = kuisId,
                kelasId = "1",
                pelajarId = "p_$index",
                namaPelajar = name,
                nim = "105220${String.format(Locale.getDefault(), "%02d", index + 1)}",
                fotoUrl = "",
                nilai = if (isSelesai) (nilai?.coerceAtMost(100)) else null,
                nilaiMaks = 100,
                status = if (isSelesai) "selesai" else "belum_selesai",
                durasiPengerjaanMenit = 15 + (index % 6),
                waktuSelesai = System.currentTimeMillis() - index * 1800000L,
                jawaban = jawaban
            )
        }

        var filtered = list
        if (query.isNotBlank()) {
            filtered = filtered.filter { it.namaPelajar.contains(query, ignoreCase = true) }
        }
        return flowOf(filtered)
    }

    override fun hasilById(id: String): Flow<HasilKuis?> {
        val all = pelajarNames.mapIndexed { index, name ->
            val isSelesai = index < 20
            val jawaban = soalList.map { soal ->
                JawabanSoal(soalId = soal.id, kodeDipilih = soal.kunciJawaban, benar = true)
            }
            HasilKuis(
                id = "hk_$index",
                kuisId = "k1",
                kelasId = "1",
                pelajarId = "p_$index",
                namaPelajar = name,
                nim = "105220${String.format(Locale.getDefault(), "%02d", index + 1)}",
                fotoUrl = "",
                nilai = 85,
                nilaiMaks = 100,
                status = if (isSelesai) "selesai" else "belum_selesai",
                durasiPengerjaanMenit = 18,
                waktuSelesai = System.currentTimeMillis(),
                jawaban = jawaban
            )
        }
        return flowOf(all.find { it.id == id } ?: all.first())
    }

    override fun soalPerKuis(kuisId: String): Flow<List<Soal>> {
        return flowOf(soalList)
    }
}
