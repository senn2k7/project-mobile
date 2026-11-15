package com.example.projectmobile.ui.pengajar.institusi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Locale

typealias KelasRepository = PengajarKelasRepository

interface PengajarProfilRepository {
    fun getProfil(): Flow<PengajarProfilGuru?>
}

interface PengajarKelasRepository {
    fun getKelasList(searchQuery: String = "", filterInstitusi: Boolean = false, semester: String = ""): Flow<List<PengajarKelas>>
    fun kelasById(id: String): Flow<Kelas?>
}

interface MateriRepository {
    fun materiPerKelas(kelasId: String): Flow<List<Materi>>
}

interface TugasRepository {
    fun tugasPerKelas(kelasId: String, filter: String = "SEMUA"): Flow<List<Tugas>>
    fun tugasById(id: String): Flow<Tugas?>
    fun lampiran(tugasId: String): Flow<List<LampiranTugas>>
}

interface PengumpulanRepository {
    fun statusPengumpulan(tugasId: String, query: String = "", filterStatus: String = "SEMUA"): Flow<DaftarPengumpulan>
}

interface PengajarStatistikRepository {
    fun getStatistik(): Flow<PengajarStatistikBeranda>
}

interface PengajarNotifikasiRepository {
    fun getUnreadCount(): Flow<Int>
}

// EMPTY IMPLEMENTATIONS
class EmptyPengajarProfilRepository : PengajarProfilRepository {
    override fun getProfil(): Flow<PengajarProfilGuru?> = flowOf(null)
}

class EmptyPengajarKelasRepository : PengajarKelasRepository {
    override fun getKelasList(searchQuery: String, filterInstitusi: Boolean, semester: String): Flow<List<PengajarKelas>> = flowOf(emptyList())
    override fun kelasById(id: String): Flow<Kelas?> = flowOf(null)
}

class EmptyMateriRepository : MateriRepository {
    override fun materiPerKelas(kelasId: String): Flow<List<Materi>> = flowOf(emptyList())
}

class EmptyTugasRepository : TugasRepository {
    override fun tugasPerKelas(kelasId: String, filter: String): Flow<List<Tugas>> = flowOf(emptyList())
    override fun tugasById(id: String): Flow<Tugas?> = flowOf(null)
    override fun lampiran(tugasId: String): Flow<List<LampiranTugas>> = flowOf(emptyList())
}

class EmptyPengumpulanRepository : PengumpulanRepository {
    override fun statusPengumpulan(tugasId: String, query: String, filterStatus: String): Flow<DaftarPengumpulan> = flowOf(DaftarPengumpulan())
}

class EmptyPengajarStatistikRepository : PengajarStatistikRepository {
    override fun getStatistik(): Flow<PengajarStatistikBeranda> = flowOf(PengajarStatistikBeranda())
}

class EmptyPengajarNotifikasiRepository : PengajarNotifikasiRepository {
    override fun getUnreadCount(): Flow<Int> = flowOf(0)
}

// FAKE IMPLEMENTATIONS
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

object PengajarRepositoryProvider {
    fun provideProfilRepo(): PengajarProfilRepository = EmptyPengajarProfilRepository()

    fun provideKelasRepo(): KelasRepository =
        if (PengajarRepoConfig.useFakeData) FakePengajarKelasRepository() else EmptyPengajarKelasRepository()

    fun provideMateriRepo(): MateriRepository =
        if (PengajarRepoConfig.useFakeData) FakeMateriRepository() else EmptyMateriRepository()

    fun provideTugasRepo(): TugasRepository =
        if (PengajarRepoConfig.useFakeData) FakeTugasRepository() else EmptyTugasRepository()

    fun providePengumpulanRepo(): PengumpulanRepository =
        if (PengajarRepoConfig.useFakeData) FakePengumpulanRepository() else EmptyPengumpulanRepository()

    fun provideStatistikRepo(): PengajarStatistikRepository = EmptyPengajarStatistikRepository()

    fun provideNotifikasiRepo(): PengajarNotifikasiRepository = EmptyPengajarNotifikasiRepository()
}
