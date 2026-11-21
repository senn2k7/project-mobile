package com.example.projectmobile.ui.pengajar.institusi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

typealias KelasRepository = PengajarKelasRepository

interface PengajarProfilRepository {
    fun getProfil(): Flow<PengajarProfilGuru?>
}

interface PengajarKelasRepository {
    fun getKelasList(searchQuery: String = "", filterInstitusi: Boolean = false, semester: String = "", guruId: String = ""): Flow<List<PengajarKelas>>
    fun getKelasDiikuti(searchQuery: String = "", pelajarId: String = ""): Flow<List<PengajarKelas>>
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
    fun tugasMendatang(pelajarId: String = ""): Flow<List<Tugas>>
    fun tugasUntukPelajar(pelajarId: String = ""): Flow<List<Tugas>>
}

interface KuisRepository {
    fun kuisPerKelas(kelasId: String): Flow<List<Kuis>>
    fun kuisById(id: String): Flow<Kuis?>
    suspend fun simpanKuis(input: InputKuis, status: String): Result<String>
    fun kuisMendatang(pelajarId: String = ""): Flow<List<Kuis>>
}

interface HasilKuisRepository {
    fun hasilPerKuis(kuisId: String, query: String = ""): Flow<List<HasilKuis>>
    fun hasilById(id: String): Flow<HasilKuis?>
    fun soalPerKuis(kuisId: String): Flow<List<Soal>>
}

interface PengumpulanRepository {
    fun statusPengumpulan(tugasId: String, query: String = "", filterStatus: String = "SEMUA"): Flow<DaftarPengumpulan>
    suspend fun kumpulkanTugas(tugasId: String, pelajarId: String, jawabanText: String, lampiran: FileLampiranInput?): Result<String>
    fun getPengumpulanPelajar(pelajarId: String = ""): Flow<List<Pengumpulan>>
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
    override fun getKelasDiikuti(searchQuery: String, pelajarId: String): Flow<List<PengajarKelas>> = flowOf(emptyList())
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
    override fun tugasMendatang(pelajarId: String): Flow<List<Tugas>> = flowOf(emptyList())
    override fun tugasUntukPelajar(pelajarId: String): Flow<List<Tugas>> = flowOf(emptyList())
}

class EmptyKuisRepository : KuisRepository {
    override fun kuisPerKelas(kelasId: String): Flow<List<Kuis>> = flowOf(emptyList())
    override fun kuisById(id: String): Flow<Kuis?> = flowOf(null)
    override suspend fun simpanKuis(input: InputKuis, status: String): Result<String> = Result.failure(Exception("Belum terhubung ke database"))
    override fun kuisMendatang(pelajarId: String): Flow<List<Kuis>> = flowOf(emptyList())
}

class EmptyHasilKuisRepository : HasilKuisRepository {
    override fun hasilPerKuis(kuisId: String, query: String): Flow<List<HasilKuis>> = flowOf(emptyList())
    override fun hasilById(id: String): Flow<HasilKuis?> = flowOf(null)
    override fun soalPerKuis(kuisId: String): Flow<List<Soal>> = flowOf(emptyList())
}

class EmptyPengumpulanRepository : PengumpulanRepository {
    override fun statusPengumpulan(tugasId: String, query: String, filterStatus: String): Flow<DaftarPengumpulan> = flowOf(DaftarPengumpulan())
    override suspend fun kumpulkanTugas(tugasId: String, pelajarId: String, jawabanText: String, lampiran: FileLampiranInput?): Result<String> = Result.failure(Exception("Belum terhubung ke database"))
    override fun getPengumpulanPelajar(pelajarId: String): Flow<List<Pengumpulan>> = flowOf(emptyList())
}

class EmptyPengajarStatistikRepository : PengajarStatistikRepository {
    override fun getStatistik(): Flow<PengajarStatistikBeranda> = flowOf(PengajarStatistikBeranda())
}

class EmptyPengajarNotifikasiRepository : PengajarNotifikasiRepository {
    override fun getUnreadCount(): Flow<Int> = flowOf(0)
}

object PengajarRepositoryProvider {
    private val fakeProfilRepo by lazy { FakePengajarProfilRepository() }
    private val fakeKelasRepo by lazy { FakePengajarKelasRepository() }
    private val fakeMateriRepo by lazy { FakeMateriRepository() }
    private val fakeTugasRepo by lazy { FakeTugasRepository(fakeKelasRepo) }
    private val fakeKuisRepo by lazy { FakeKuisRepository(fakeKelasRepo) }
    private val fakeHasilKuisRepo by lazy { FakeHasilKuisRepository() }
    private val fakePengumpulanRepo by lazy { FakePengumpulanRepository() }
    private val fakeStatistikRepo by lazy { FakePengajarStatistikRepository() }
    private val fakeNotifikasiRepo by lazy { FakePengajarNotifikasiRepository() }

    fun provideProfilRepo(): PengajarProfilRepository = fakeProfilRepo

    fun provideKelasRepo(): KelasRepository =
        if (PengajarRepoConfig.useFakeData) fakeKelasRepo else EmptyPengajarKelasRepository()

    fun provideMateriRepo(): MateriRepository =
        if (PengajarRepoConfig.useFakeData) fakeMateriRepo else EmptyMateriRepository()

    fun provideTugasRepo(): TugasRepository =
        if (PengajarRepoConfig.useFakeData) fakeTugasRepo else EmptyTugasRepository()

    fun provideKuisRepo(): KuisRepository =
        if (PengajarRepoConfig.useFakeData) fakeKuisRepo else EmptyKuisRepository()

    fun provideHasilKuisRepo(): HasilKuisRepository =
        if (PengajarRepoConfig.useFakeData) fakeHasilKuisRepo else EmptyHasilKuisRepository()

    fun providePengumpulanRepo(): PengumpulanRepository =
        if (PengajarRepoConfig.useFakeData) fakePengumpulanRepo else EmptyPengumpulanRepository()

    fun provideStatistikRepo(): PengajarStatistikRepository = fakeStatistikRepo

    fun provideNotifikasiRepo(): PengajarNotifikasiRepository = fakeNotifikasiRepo
}
