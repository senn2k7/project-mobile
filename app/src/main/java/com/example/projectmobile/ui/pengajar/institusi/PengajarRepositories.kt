package com.example.projectmobile.ui.pengajar.institusi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

typealias KelasRepository = PengajarKelasRepository

interface PengajarProfilRepository {
    fun getProfil(): Flow<PengajarProfilGuru?>
}

interface PengajarKelasRepository {
    fun getKelasList(searchQuery: String = "", filterInstitusi: Boolean = false, semester: String = "", guruId: String = ""): Flow<List<PengajarKelas>>
    fun kelasById(id: String): Flow<Kelas?>
}

interface MateriRepository {
    fun materiPerKelas(kelasId: String): Flow<List<Materi>>
    suspend fun buatMateri(input: InputMateri): Result<String>
}

interface TugasRepository {
    fun tugasPerKelas(kelasId: String, filter: String = "SEMUA"): Flow<List<Tugas>>
    fun tugasById(id: String): Flow<Tugas?>
    fun lampiran(tugasId: String): Flow<List<LampiranTugas>>
    suspend fun buatTugas(input: InputTugas): Result<String>
}

interface KuisRepository {
    fun kuisPerKelas(kelasId: String): Flow<List<Kuis>>
    fun kuisById(id: String): Flow<Kuis?>
    suspend fun simpanKuis(input: InputKuis, status: String): Result<String>
}

interface HasilKuisRepository {
    fun hasilPerKuis(kuisId: String, query: String = ""): Flow<List<HasilKuis>>
    fun hasilById(id: String): Flow<HasilKuis?>
    fun soalPerKuis(kuisId: String): Flow<List<Soal>>
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
    override fun getKelasList(searchQuery: String, filterInstitusi: Boolean, semester: String, guruId: String): Flow<List<PengajarKelas>> = flowOf(emptyList())
    override fun kelasById(id: String): Flow<Kelas?> = flowOf(null)
}

class EmptyMateriRepository : MateriRepository {
    override fun materiPerKelas(kelasId: String): Flow<List<Materi>> = flowOf(emptyList())
    override suspend fun buatMateri(input: InputMateri): Result<String> = Result.failure(Exception("Belum terhubung ke database"))
}

class EmptyTugasRepository : TugasRepository {
    override fun tugasPerKelas(kelasId: String, filter: String): Flow<List<Tugas>> = flowOf(emptyList())
    override fun tugasById(id: String): Flow<Tugas?> = flowOf(null)
    override fun lampiran(tugasId: String): Flow<List<LampiranTugas>> = flowOf(emptyList())
    override suspend fun buatTugas(input: InputTugas): Result<String> = Result.failure(Exception("Belum terhubung ke database"))
}

class EmptyKuisRepository : KuisRepository {
    override fun kuisPerKelas(kelasId: String): Flow<List<Kuis>> = flowOf(emptyList())
    override fun kuisById(id: String): Flow<Kuis?> = flowOf(null)
    override suspend fun simpanKuis(input: InputKuis, status: String): Result<String> = Result.failure(Exception("Belum terhubung ke database"))
}

class EmptyHasilKuisRepository : HasilKuisRepository {
    override fun hasilPerKuis(kuisId: String, query: String): Flow<List<HasilKuis>> = flowOf(emptyList())
    override fun hasilById(id: String): Flow<HasilKuis?> = flowOf(null)
    override fun soalPerKuis(kuisId: String): Flow<List<Soal>> = flowOf(emptyList())
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

object PengajarRepositoryProvider {
    fun provideProfilRepo(): PengajarProfilRepository = EmptyPengajarProfilRepository()

    fun provideKelasRepo(): KelasRepository =
        if (PengajarRepoConfig.useFakeData) FakePengajarKelasRepository() else EmptyPengajarKelasRepository()

    fun provideMateriRepo(): MateriRepository =
        if (PengajarRepoConfig.useFakeData) FakeMateriRepository() else EmptyMateriRepository()

    fun provideTugasRepo(): TugasRepository =
        if (PengajarRepoConfig.useFakeData) FakeTugasRepository() else EmptyTugasRepository()

    fun provideKuisRepo(): KuisRepository =
        if (PengajarRepoConfig.useFakeData) FakeKuisRepository() else EmptyKuisRepository()

    fun provideHasilKuisRepo(): HasilKuisRepository =
        if (PengajarRepoConfig.useFakeData) FakeHasilKuisRepository() else EmptyHasilKuisRepository()

    fun providePengumpulanRepo(): PengumpulanRepository =
        if (PengajarRepoConfig.useFakeData) FakePengumpulanRepository() else EmptyPengumpulanRepository()

    fun provideStatistikRepo(): PengajarStatistikRepository = EmptyPengajarStatistikRepository()

    fun provideNotifikasiRepo(): PengajarNotifikasiRepository = EmptyPengajarNotifikasiRepository()
}
