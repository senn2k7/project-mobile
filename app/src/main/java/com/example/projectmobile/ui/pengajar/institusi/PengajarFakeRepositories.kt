package com.example.projectmobile.ui.pengajar.institusi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

class FakeProfilPelajarRepository : ProfilPelajarRepository {
    private val profil = ProfilPelajar(
        uid = "pelajar_me",
        nama = "Andi Pratama",
        nim = "10522001",
        fotoUrl = ""
    )
    override fun getProfilPelajar(): Flow<ProfilPelajar?> = flowOf(profil)
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
            guruId = "teacher_budi",
            deskripsi = "Mata kuliah pengembangan aplikasi Android modern menggunakan Kotlin dan Jetpack.",
            dosenPengampu = "Dr. Budi Santoso",
            tanggalPertemuan = 1747008000000L,
            pertemuanKe = 1
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
            guruId = "teacher_budi",
            deskripsi = "Studi tentang kecerdasan buatan, machine learning, dan sistem pakar.",
            dosenPengampu = "Dr. Rina Wati",
            tanggalPertemuan = 1747008000000L,
            pertemuanKe = 2
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
            guruId = "teacher_budi",
            deskripsi = "Perancangan database relasional, normalisasi, dan SQL tingkat lanjut.",
            dosenPengampu = "Dr. Ahmad Fauzi",
            tanggalPertemuan = 1747008000000L,
            pertemuanKe = 3
        )
    )

    override fun getKelasList(searchQuery: String, filterInstitusi: Boolean, semester: String, guruId: String): Flow<List<PengajarKelas>> {
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
        if (guruId.isNotBlank()) {
            filtered = filtered.filter { it.guruId == guruId }
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

    override fun getKelasDiikuti(searchQuery: String, pelajarId: String): Flow<List<PengajarKelas>> {
        if (pelajarId.isBlank()) {
            return flowOf(emptyList())
        }
        var filtered = allKelas.map { it ->
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

class FakeTugasRepository(
    private val kelasRepo: KelasRepository = FakePengajarKelasRepository()
) : TugasRepository {
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

    override fun tugasMendatang(pelajarId: String): Flow<List<Tugas>> {
        if (pelajarId.isBlank()) {
            return flowOf(emptyList())
        }
        return kelasRepo.getKelasDiikuti("", pelajarId).map { kelasList ->
            val followedKelasIds = kelasList.map { it.id }
            val now = System.currentTimeMillis()
            tugasList.filter { 
                it.tenggat > now && followedKelasIds.contains(it.kelasId)
            }.sortedBy { it.tenggat }
        }
    }

    override fun tugasUntukPelajar(pelajarId: String): Flow<List<Tugas>> {
        if (pelajarId.isBlank()) {
            return flowOf(emptyList())
        }
        return kelasRepo.getKelasDiikuti("", pelajarId).map { kelasList ->
            val followedKelasIds = kelasList.map { it.id }
            tugasList.filter { followedKelasIds.contains(it.kelasId) || it.kelasId == "1" }
        }
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
    private val _pengumpulanFlow = MutableStateFlow(
        mutableListOf(
            Pengumpulan(
                id = "peng_t3",
                tugasId = "t3",
                pelajarId = "pelajar_me",
                namaPelajar = "Andi Pratama",
                waktuKumpul = System.currentTimeMillis() - 86400000L * 2,
                namaFile = "Laporan_Basis_Data.pdf",
                tipeFile = "application/pdf",
                ukuranByte = 2400000L,
                url = "",
                jawabanText = "Berikut laporan basis data.",
                nilai = 85
            )
        )
    )

    override suspend fun kumpulkanTugas(tugasId: String, pelajarId: String, jawabanText: String, lampiran: FileLampiranInput?): Result<String> {
        if (pelajarId.isBlank()) {
            return Result.failure(Exception("Pelajar tidak teridentifikasi"))
        }
        val id = "peng_${System.currentTimeMillis()}"
        val p = Pengumpulan(
            id = id,
            tugasId = tugasId,
            pelajarId = pelajarId,
            namaPelajar = "Andi Pratama",
            waktuKumpul = System.currentTimeMillis(),
            namaFile = lampiran?.nama ?: "",
            tipeFile = lampiran?.tipeFile ?: "",
            ukuranByte = lampiran?.ukuranByte ?: 0L,
            url = lampiran?.uriString ?: "",
            jawabanText = jawabanText
        )
        val currentList = _pengumpulanFlow.value.toMutableList()
        currentList.add(p)
        _pengumpulanFlow.value = currentList
        return Result.success(id)
    }

    override fun getPengumpulanPelajar(pelajarId: String): Flow<List<Pengumpulan>> {
        if (pelajarId.isBlank()) {
            return flowOf(emptyList())
        }
        return _pengumpulanFlow.map { list -> list.filter { it.pelajarId == pelajarId } }
    }

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

class FakeKuisRepository(
    private val kelasRepo: KelasRepository = FakePengajarKelasRepository()
) : KuisRepository {
    private val currentTime = System.currentTimeMillis()
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
            createdAt = currentTime - 86400000L * 2,
            tenggat = currentTime + 86400000L * 3
        )
    )

    override fun kuisPerKelas(kelasId: String): Flow<List<Kuis>> = flowOf(kuisList.filter { it.kelasId == kelasId || kelasId == "1" })
    override fun kuisById(id: String): Flow<Kuis?> = flowOf(kuisList.find { it.id == id })
    override fun kuisMendatang(pelajarId: String): Flow<List<Kuis>> {
        if (pelajarId.isBlank()) {
            return flowOf(emptyList())
        }
        return kelasRepo.getKelasDiikuti("", pelajarId).map { kelasList ->
            val followedKelasIds = kelasList.map { it.id }
            val now = System.currentTimeMillis()
            kuisList.filter { 
                it.status == "dipublikasikan" && 
                it.tenggat > now && 
                followedKelasIds.contains(it.kelasId)
            }.sortedBy { it.tenggat }
        }
    }
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
    private val hasilList = mutableListOf<HasilKuis>()
    private val soalMap = mutableMapOf<String, List<Soal>>(
        "k1" to listOf(
            Soal("s1", "k1", 1, "Manakah yang merupakan Layout dasar di Android?", listOf(OpsiSoal("A", "LinearLayout"), OpsiSoal("B", "CardView"), OpsiSoal("C", "TextView"), OpsiSoal("D", "ImageView")), "A"),
            Soal("s2", "k1", 2, "Komponen utama untuk menampilkan list reaktif adalah?", listOf(OpsiSoal("A", "ListView"), OpsiSoal("B", "RecyclerView"), OpsiSoal("C", "ScrollView"), OpsiSoal("D", "GridView")), "B"),
            Soal("s3", "k1", 3, "Arsitektur yang direkomendasikan Google untuk aplikasi Android adalah?", listOf(OpsiSoal("A", "MVC"), OpsiSoal("B", "MVP"), OpsiSoal("C", "MVVM"), OpsiSoal("D", "MVI")), "C"),
            Soal("s4", "k1", 4, "Bahasa pemrograman utama yang didukung Google untuk Android adalah?", listOf(OpsiSoal("A", "Java"), OpsiSoal("B", "Kotlin"), OpsiSoal("C", "Python"), OpsiSoal("D", "C++")), "B"),
            Soal("s5", "k1", 5, "Fungsi untuk memulai Activity baru di Android adalah?", listOf(OpsiSoal("A", "startService"), OpsiSoal("B", "startActivity"), OpsiSoal("C", "runActivity"), OpsiSoal("D", "initActivity")), "B")
        )
    )

    override fun hasilPerKuis(kuisId: String, query: String): Flow<List<HasilKuis>> = flowOf(hasilList.filter { it.kuisId == kuisId })
    override fun hasilById(id: String): Flow<HasilKuis?> = flowOf(hasilList.find { it.id == id })
    override fun soalPerKuis(kuisId: String): Flow<List<Soal>> {
        val list = soalMap[kuisId] ?: soalMap.values.firstOrNull() ?: emptyList()
        return flowOf(list.sortedBy { it.nomor })
    }

    override suspend fun simpanHasilKuis(hasil: HasilKuis): Result<String> {
        if (hasil.pelajarId.isBlank()) {
            return Result.failure(Exception("Pelajar tidak teridentifikasi"))
        }
        val id = "hasil_${System.currentTimeMillis()}"
        val finalHasil = hasil.copy(id = id)
        hasilList.add(finalHasil)
        return Result.success(id)
    }
}

class FakeDiskusiRepository : DiskusiRepository {
    private val _pesanFlow = MutableStateFlow(
        mutableListOf(
            PesanDiskusi("p1", "1", "teacher_budi", "Budi Santoso", "Bagaimana cara kerja algoritma BFS? Saya masih bingung dengan cara kerjanya.", System.currentTimeMillis() - 600000L, false),
            PesanDiskusi("p2", "1", "pelajar_me", "Andi Pratama", "Algoritma BFS (Breadth First Search) bekerja dengan menjelajahi simpul secara level atau per tingkat.", System.currentTimeMillis() - 400000L, false),
            PesanDiskusi("p3", "1", "user_siti", "Siti Aminah", "Apakah bisa diberikan contoh kode sederhana?", System.currentTimeMillis() - 200000L, false),
            PesanDiskusi("p4", "1", "teacher_budi", "Budi Santoso", "Tentu! Berikut contoh kode sederhana dalam Python:\n\nfrom collections import deque\ndef bfs(graph, start):\n    visited = set()\n    queue = deque([start])", System.currentTimeMillis() - 100000L, true)
        )
    )

    override fun pesanPerKelas(kelasId: String): Flow<List<PesanDiskusi>> {
        return _pesanFlow.map { list -> list.filter { it.kelasId == kelasId || it.kelasId == "1" } }
    }

    override suspend fun kirimPesan(kelasId: String, pengirimId: String, namaPengirim: String, teks: String): Result<String> {
        if (teks.trim().isBlank()) {
            return Result.failure(Exception("Pesan tidak boleh kosong"))
        }
        val id = "pesan_${System.currentTimeMillis()}"
        val pesan = PesanDiskusi(
            id = id,
            kelasId = if (kelasId.isBlank()) "1" else kelasId,
            pengirimId = if (pengirimId.isBlank()) "pelajar_me" else pengirimId,
            namaPengirim = if (namaPengirim.isBlank()) "Andi Pratama" else namaPengirim,
            teks = teks,
            waktu = System.currentTimeMillis(),
            isKode = false
        )
        val current = _pesanFlow.value.toMutableList()
        current.add(pesan)
        _pesanFlow.value = current
        return Result.success(id)
    }
}
