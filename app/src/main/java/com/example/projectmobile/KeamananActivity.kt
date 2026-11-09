package com.example.projectmobile

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider

/** Halaman Keamanan: ubah kata sandi, autentikasi dua faktor, dan perangkat aktif. */
class KeamananActivity : AppCompatActivity() {

    private val passwordType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

    private lateinit var etCurrent: EditText
    private lateinit var etNew: EditText
    private lateinit var etConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Keamanan") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(UiKit.sectionBand(this, "Kata Sandi"))

        etCurrent = UiKit.input(this, "Kata sandi saat ini", "", passwordType)
        etNew = UiKit.input(this, "Kata sandi baru", "", passwordType)
        etConfirm = UiKit.input(this, "Konfirmasi kata sandi", "", passwordType)

        content.addView(UiKit.fieldLabel(this, "Kata sandi saat ini"))
        content.addView(etCurrent)
        content.addView(UiKit.fieldLabel(this, "Kata sandi baru"))
        content.addView(etNew)
        content.addView(UiKit.fieldLabel(this, "Konfirmasi kata sandi"))
        content.addView(etConfirm)

        content.addView(
            UiKit.primaryButton(this, "Ubah Kata Sandi") { changePassword() },
            UiKit.params(this, UiKit.MATCH, px(56), top = 20)
        )

        // Keamanan tambahan
        content.addView(
            UiKit.tv(this, "Keamanan Tambahan", 17f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 28)
        )
        content.addView(
            UiKit.tv(this, "Lindungi akun Anda dengan verifikasi tambahan.", 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
        )

        val prefs = getSharedPreferences("security_prefs", MODE_PRIVATE)
        val twoFa = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            addView(
                UiKit.tv(this@KeamananActivity, "Autentikasi dua faktor", 15f, UiKit.NAVY),
                UiKit.params(this@KeamananActivity, 0, UiKit.WRAP, weight = 1f)
            )
            val sw = UiKit.styledSwitch(this@KeamananActivity, prefs.getBoolean("two_factor", false))
            sw.setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("two_factor", checked).apply()
                UiKit.toast(
                    this@KeamananActivity,
                    if (checked) "Autentikasi dua faktor diaktifkan" else "Autentikasi dua faktor dimatikan"
                )
            }
            addView(sw)
        }
        content.addView(twoFa, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Perangkat
        content.addView(UiKit.sectionBand(this, "Perangkat"), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 24))
        content.addView(
            UiKit.menuRow(
                this, R.drawable.ic_phone_android, "Perangkat aktif",
                "Kelola perangkat yang terhubung"
            ) { showDevices() }
        )

        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun showDevices() {
        val device = "${Build.MANUFACTURER} ${Build.MODEL}".trim()
        AlertDialog.Builder(this)
            .setTitle("Perangkat aktif")
            .setMessage("$device\nPerangkat ini · sedang digunakan")
            .setPositiveButton("Tutup", null)
            .show()
    }

    private fun changePassword() {
        val current = etCurrent.text.toString()
        val newPass = etNew.text.toString()
        val confirm = etConfirm.text.toString()

        etCurrent.error = null
        etNew.error = null
        etConfirm.error = null

        if (current.isEmpty()) {
            etCurrent.error = "Masukkan kata sandi saat ini"
            return
        }
        if (newPass.length < 6) {
            etNew.error = "Kata sandi baru minimal 6 karakter"
            return
        }
        if (newPass == current) {
            etNew.error = "Kata sandi baru harus berbeda"
            return
        }
        if (confirm != newPass) {
            etConfirm.error = "Konfirmasi kata sandi tidak sama"
            return
        }

        val user = FirebaseAuthManager.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            UiKit.toast(this, "Sesi tidak ditemukan. Silakan masuk kembali.")
            return
        }

        val credential = EmailAuthProvider.getCredential(email, current)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPass)
                    .addOnSuccessListener {
                        UiKit.toast(this, "Kata sandi berhasil diubah")
                        etCurrent.text.clear()
                        etNew.text.clear()
                        etConfirm.text.clear()
                    }
                    .addOnFailureListener { e ->
                        UiKit.toast(this, "Gagal mengubah kata sandi: ${e.message}")
                    }
            }
            .addOnFailureListener {
                etCurrent.error = "Kata sandi saat ini salah (atau akun masuk lewat Google)"
            }
    }
}
