package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.ChatAiRepository
import com.example.projectmobile.ui.pengajar.institusi.KelasRepository
import com.example.projectmobile.ui.pengajar.institusi.PengajarRepositoryProvider
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KelolaAiAsistenPelajarActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_KELAS_ID = "extra_kelas_id"
    }

    private val chatAiRepo: ChatAiRepository = PengajarRepositoryProvider.provideChatAiRepo()
    private val kelasRepo: KelasRepository = PengajarRepositoryProvider.provideKelasRepo()

    private lateinit var tvKonteksBelajar: TextView
    private lateinit var rvChat: RecyclerView
    private lateinit var etPesan: EditText
    private lateinit var btnKirim: ImageView
    private lateinit var btnAttach: ImageView
    private lateinit var cardAttachmentPopup: CardView
    private lateinit var adapter: PelajarChatAiAdapter

    private var kelasId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        kelasId = intent.getStringExtra(EXTRA_KELAS_ID) ?: ""

        setContentView(R.layout.activity_kelola_ai_asisten_pelajar)

        bindViews()
        setupRecyclerView()
        setupListeners()
        loadKonteksDanChat()
    }

    private fun bindViews() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnNewChat).setOnClickListener {
            Toast.makeText(this, "Membuat sesi chat baru (TODO)", Toast.LENGTH_SHORT).show()
        }

        tvKonteksBelajar = findViewById(R.id.tvKonteksBelajar)
        rvChat = findViewById(R.id.rvChat)
        etPesan = findViewById(R.id.etPesan)
        btnKirim = findViewById(R.id.btnKirim)
        btnAttach = findViewById(R.id.btnAttach)
        cardAttachmentPopup = findViewById(R.id.cardAttachmentPopup)
    }

    private fun setupRecyclerView() {
        rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        adapter = PelajarChatAiAdapter()
        rvChat.adapter = adapter
    }

    private fun setupListeners() {
        btnAttach.setOnClickListener {
            if (cardAttachmentPopup.visibility == View.VISIBLE) {
                cardAttachmentPopup.visibility = View.GONE
            } else {
                cardAttachmentPopup.visibility = View.VISIBLE
            }
        }

        findViewById<View>(R.id.btnAttachFoto).setOnClickListener {
            cardAttachmentPopup.visibility = View.GONE
            Toast.makeText(this, "Lampirkan Foto (TODO)", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.btnAttachVideo).setOnClickListener {
            cardAttachmentPopup.visibility = View.GONE
            Toast.makeText(this, "Lampirkan Video (TODO)", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.btnAttachDokumen).setOnClickListener {
            cardAttachmentPopup.visibility = View.GONE
            Toast.makeText(this, "Lampirkan Dokumen (TODO)", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.btnAttachKamera).setOnClickListener {
            cardAttachmentPopup.visibility = View.GONE
            Toast.makeText(this, "Ambil Foto (TODO)", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.btnPilihPerangkat).setOnClickListener {
            cardAttachmentPopup.visibility = View.GONE
            Toast.makeText(this, "Pilih dari perangkat (TODO)", Toast.LENGTH_SHORT).show()
        }

        val saranList = listOf(
            findViewById<TextView>(R.id.btnSaran1),
            findViewById<TextView>(R.id.btnSaran2),
            findViewById<TextView>(R.id.btnSaran3),
            findViewById<TextView>(R.id.btnSaran4)
        )

        for (saran in saranList) {
            saran.setOnClickListener {
                etPesan.setText(saran.text)
                etPesan.setSelection(etPesan.text.length)
            }
        }

        btnKirim.setOnClickListener {
            val teks = etPesan.text.toString().trim()
            if (teks.isBlank()) {
                Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cardAttachmentPopup.visibility = View.GONE
            lifecycleScope.launch {
                val result = chatAiRepo.kirimPesanAi(teks)
                if (result.isSuccess) {
                    etPesan.setText("")
                } else {
                    Toast.makeText(this@KelolaAiAsistenPelajarActivity, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadKonteksDanChat() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Load Context
                if (kelasId.isNotBlank()) {
                    launch {
                        kelasRepo.kelasById(kelasId).collectLatest { kelas ->
                            if (kelas != null) {
                                tvKonteksBelajar.text = "${kelas.namaMataKuliah} — Pertemuan ${kelas.pertemuanKe}"
                            } else {
                                tvKonteksBelajar.text = "Belum memilih materi"
                            }
                        }
                    }
                } else {
                    tvKonteksBelajar.text = "Belum memilih materi"
                }

                // 2. Observe chat messages reactively
                launch {
                    chatAiRepo.pesanSesi().collectLatest { pesanList ->
                        adapter.submitList(pesanList) {
                            if (pesanList.isNotEmpty()) {
                                rvChat.scrollToPosition(pesanList.size - 1)
                            }
                        }
                    }
                }
            }
        }
    }
}
