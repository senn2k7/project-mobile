package com.example.snapchatsphlasscreen

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Halaman Bantuan & Dukungan. */
class BantuanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Bantuan & Dukungan") { finish() })
        val (scroll, content) = UiKit.scroll(this, padTop = 16)

        helpRow(content, R.drawable.ic_help, "Pusat Bantuan", "Temukan jawaban pertanyaan umum") { showGuide() }
        helpRow(content, R.drawable.ic_help, "FAQ", "Pertanyaan yang sering diajukan") { showFaq() }
        helpRow(content, R.drawable.ic_forum, "Hubungi Kami", "Tim kami siap membantu") { contactUs() }
        helpRow(content, R.drawable.ic_flag, "Laporkan Masalah", "Beri tahu kendala yang dialami") {
            showFeedback("Laporkan Masalah", "Ceritakan kendala yang kamu alami", "Laporan terkirim. Terima kasih!")
        }
        helpRow(content, R.drawable.ic_thumb_up, "Kirim Masukan", "Bantu kami menjadi lebih baik") {
            showFeedback("Kirim Masukan", "Tulis saran atau masukanmu", "Masukan terkirim. Terima kasih!")
        }

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun helpRow(parent: LinearLayout, iconRes: Int, title: String, subtitle: String, onClick: () -> Unit) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, px(14), 0, px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }
        row.addView(UiKit.badge(this, iconRes, Color.WHITE, UiKit.PRIMARY, 48, 12, 26))
        val texts = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        texts.addView(UiKit.tv(this, title, 15f, UiKit.NAVY, true))
        texts.addView(
            UiKit.tv(this, subtitle, 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        row.addView(texts, UiKit.params(this, 0, UiKit.WRAP, start = 16, weight = 1f))
        row.addView(
            UiKit.icon(this, R.drawable.ic_chevron_right, 20, UiKit.MUTED),
            LinearLayout.LayoutParams(px(20), px(20))
        )
        parent.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, bottom = 6))
    }

    private fun showGuide() {
        // TODO: ganti dengan isi pusat bantuan yang sebenarnya.
        AlertDialog.Builder(this)
            .setTitle("Pusat Bantuan")
            .setMessage(
                "• Bergabung ke kelas: buka tab Kelas lalu pilih Gabung Kelas.\n\n" +
                "• Mengumpulkan tugas: buka Tugas, pilih tugas, lalu unggah dokumen.\n\n" +
                "• Presensi: pindai kode QR yang ditampilkan pengajar.\n\n" +
                "• Lupa kata sandi: gunakan menu Lupa Password di halaman masuk."
            )
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun showFaq() {
        // TODO: ganti dengan daftar FAQ yang sebenarnya.
        AlertDialog.Builder(this)
            .setTitle("FAQ")
            .setMessage(
                "Bagaimana cara mengganti mode pembelajaran?\n" +
                "Buka Profil lalu pilih Mode Pembelajaran.\n\n" +
                "Apakah data saya aman?\n" +
                "Ya. Lihat menu Kebijakan Privasi untuk penjelasan lengkap.\n\n" +
                "Bagaimana mengubah email institusi?\n" +
                "Hubungi administrator institusi kamu."
            )
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun contactUs() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$SUPPORT_EMAIL")
            putExtra(Intent.EXTRA_SUBJECT, "Bantuan TemanStudi")
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            UiKit.toast(this, "Tidak ada aplikasi email. Hubungi: $SUPPORT_EMAIL")
        }
    }

    private fun showFeedback(title: String, hint: String, thanks: String) {
        val box = EditText(this).apply {
            this.hint = hint
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 4
            gravity = Gravity.TOP
            setTextColor(UiKit.NAVY)
        }
        val wrap = LinearLayout(this).apply {
            setPadding(px(24), px(8), px(24), 0)
            addView(box, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        }
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(wrap)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Kirim") { _, _ ->
                if (box.text.toString().isBlank()) {
                    UiKit.toast(this, "Isi pesan terlebih dahulu")
                } else {
                    // TODO: kirim ke server / Firestore.
                    UiKit.toast(this, thanks)
                }
            }
            .show()
    }

    companion object {
        // Ganti dengan alamat email dukungan yang sebenarnya.
        private const val SUPPORT_EMAIL = "support@temanstudi.id"
    }
}
