package com.example.projectmobile.ui.pengajar.institusi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface PengajarProfilRepository {
    fun getProfil(): Flow<PengajarProfilGuru?>
}

interface PengajarKelasRepository {
    fun getKelasList(searchQuery: String = "", filterInstitusi: Boolean = false, semester: String = ""): Flow<List<PengajarKelas>>
}

interface PengajarStatistikRepository {
    fun getStatistik(): Flow<PengajarStatistikBeranda>
}

interface PengajarNotifikasiRepository {
    fun getUnreadCount(): Flow<Int>
}

class EmptyPengajarProfilRepository : PengajarProfilRepository {
    override fun getProfil(): Flow<PengajarProfilGuru?> = flowOf(null)
}

class EmptyPengajarKelasRepository : PengajarKelasRepository {
    override fun getKelasList(searchQuery: String, filterInstitusi: Boolean, semester: String): Flow<List<PengajarKelas>> = flowOf(emptyList())
}

class EmptyPengajarStatistikRepository : PengajarStatistikRepository {
    override fun getStatistik(): Flow<PengajarStatistikBeranda> = flowOf(PengajarStatistikBeranda())
}

class EmptyPengajarNotifikasiRepository : PengajarNotifikasiRepository {
    override fun getUnreadCount(): Flow<Int> = flowOf(0)
}

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
        PengajarKelas(
            id = "1",
            namaMataKuliah = "Pemrograman Mobile",
            namaKelas = "Kelas A",
            kodeKelas = "PM2025-A",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_code",
            warna = "#1D5EFF",
            jumlahPelajar = 28
        ),
        PengajarKelas(
            id = "2",
            namaMataKuliah = "Sistem Cerdas",
            namaKelas = "Kelas B",
            kodeKelas = "SC2025-B",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_database",
            warna = "#1D5EFF",
            jumlahPelajar = 32
        ),
        PengajarKelas(
            id = "3",
            namaMataKuliah = "Basis Data",
            namaKelas = "Kelas C",
            kodeKelas = "BD2025-C",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_database",
            warna = "#1FAF6E",
            jumlahPelajar = 26
        ),
        PengajarKelas(
            id = "4",
            namaMataKuliah = "Pemrograman Mobile",
            namaKelas = "Kelas B",
            kodeKelas = "PM2025-B",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_code",
            warna = "#1D5EFF",
            jumlahPelajar = 24
        ),
        PengajarKelas(
            id = "5",
            namaMataKuliah = "Sistem Cerdas",
            namaKelas = "Kelas A",
            kodeKelas = "SC2025-A",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_database",
            warna = "#1D5EFF",
            jumlahPelajar = 27
        ),
        PengajarKelas(
            id = "6",
            namaMataKuliah = "Basis Data",
            namaKelas = "Kelas B",
            kodeKelas = "BD2025-B",
            jenisInstitusi = "Universitas",
            semester = "Semester 1",
            ikon = "ic_database",
            warna = "#1D5EFF",
            jumlahPelajar = 20
        )
    )

    override fun getKelasList(searchQuery: String, filterInstitusi: Boolean, semester: String): Flow<List<PengajarKelas>> {
        var filtered = allKelas
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

    fun provideKelasRepo(): PengajarKelasRepository =
        if (PengajarRepoConfig.useFakeData) FakePengajarKelasRepository() else EmptyPengajarKelasRepository()

    fun provideStatistikRepo(): PengajarStatistikRepository = EmptyPengajarStatistikRepository()

    fun provideNotifikasiRepo(): PengajarNotifikasiRepository = EmptyPengajarNotifikasiRepository()
}
