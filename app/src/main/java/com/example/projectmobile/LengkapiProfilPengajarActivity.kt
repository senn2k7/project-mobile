package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import java.io.ByteArrayOutputStream
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.app.AlertDialog
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts

class LengkapiProfilPengajarActivity : AppCompatActivity() {

    private lateinit var inputNama: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputTelepon: EditText
    private lateinit var inputKeahlian: EditText
    private lateinit var inputInstansi: EditText
    private lateinit var inputPengalaman: EditText
    private lateinit var btnSimpan: TextView


    private var sedangMenyimpan = false
    private var fotoProfilUri: Uri? = null
    private var fotoProfilBytes: ByteArray? = null
    private lateinit var fotoPreview: ImageView

    private val pilihFotoGaleri = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            fotoProfilUri = uri
            fotoProfilBytes = null
            fotoPreview.setImageURI(uri)
            fotoPreview.visibility = View.VISIBLE
        }
    }

    private val ambilFotoKamera = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val output = ByteArrayOutputStream()
            bitmap.compress(CompressFormat.JPEG, 90, output)
            fotoProfilBytes = output.toByteArray()
            fotoProfilUri = null
            fotoPreview.setImageBitmap(bitmap)
            fotoPreview.visibility = View.VISIBLE
        }
    }
    private var uidAktif: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val mode = intent.getStringExtra(
            ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN
        ) ?: ModePembelajaranActivity.MODE_MANDIRI

        val role = intent.getStringExtra(
            PilihPeranActivity.EXTRA_ROLE
        ) ?: PilihPeranActivity.ROLE_PENGAJAR

        val user = FirebaseAuthManager.currentUser

        if (user == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        uidAktif = user.uid
        setContentView(createFormLayout(mode, role, user.email.orEmpty()))
    }

    private fun createFormLayout(mode: String, role: String, emailFirebase: String): View {
        val bgColor = Color.rgb(245, 250, 255)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
            clipToPadding = false
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(32))
            setBackgroundColor(bgColor)
        }

        val backButton = TextView(this).apply {
            text = "←"
            textSize = 28f
            setTextColor(Color.rgb(25, 54, 95))
            includeFontPadding = false
            setOnClickListener { finish() }
        }
        root.addView(backButton, LinearLayout.LayoutParams(dp(40), dp(44)))

        val titleTv = TextView(this).apply {
            text = "Lengkapi Profil Pengajar"
            textSize = 24f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        root.addView(titleTv, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(12) })

        val subTv = TextView(this).apply {
            text = "Isi data diri dan informasi pengajaran untuk memulai di Temanstudi."
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
        }
        root.addView(subTv, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(6) })

        inputNama = addInputField(root, "Nama Lengkap", "Nama lengkap sesuai identitas", "👤")

        inputEmail = addInputField(root, "Email", "Pilih email akun", "✉️").apply {
            setText(emailFirebase)
            isFocusable = false
            isFocusableInTouchMode = false
            isClickable = true
            isLongClickable = false
            alpha = 0.95f

            setOnClickListener {
                tampilkanPilihanEmail(emailFirebase)
            }
        }

        inputTelepon = addInputField(root, "Nomor Telepon (Opsional)", "081234567890", "📞").apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            keyListener = android.text.method.DigitsKeyListener.getInstance("0123456789")
            filters = arrayOf(android.text.InputFilter.LengthFilter(13))
        }

        inputKeahlian = addInputField(root, "Bidang Keahlian", "Contoh: Matematika, Informatika", "📚")

        inputInstansi = addInputField(root, "Nama Instansi (Opsional)", "Nama sekolah, kampus, atau lembaga", "🏛️")

        inputPengalaman = addInputField(root, "Pengalaman Mengajar (Opsional)", "Contoh: 2 tahun atau pemula", "💼")

        val fotoLabel = TextView(this).apply {
            text = "Foto Profil (Opsional)"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        root.addView(fotoLabel, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(18) })

        val uploadBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(24), dp(16), dp(24))
            background = roundedBackground(Color.WHITE, 12)
        }

        fotoPreview = ImageView(this).apply {
            visibility = View.GONE
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = roundedBackground(Color.rgb(232, 242, 255), 12)
        }
        uploadBox.addView(
            fotoPreview,
            LinearLayout.LayoutParams(dp(120), dp(120))
        )

        val uploadIcon = TextView(this).apply {
            text = "👤➕"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        uploadBox.addView(uploadIcon)

        val uploadTitle = TextView(this).apply {
            text = "Pilih atau unggah foto profil"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        uploadBox.addView(uploadTitle, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(8) })

        val uploadSub = TextView(this).apply {
            text = "Format: JPG, PNG. Maksimal 2 MB."
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        uploadBox.addView(uploadSub, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(2) })

        root.addView(uploadBox, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(8) })

        uploadBox.isClickable = true
        uploadBox.isFocusable = true
        uploadBox.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Pilih Foto Profil")
                .setItems(arrayOf("Pilih dari Galeri", "Ambil Foto dari Kamera")) { _, pilihan ->
                    when (pilihan) {
                        0 -> pilihFotoGaleri.launch("image/*")
                        1 -> ambilFotoKamera.launch(null)
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Pulihkan draft yang pernah disimpan
        ProfilDraftManager.restore(this, uidAktif)?.let { draft ->
            inputNama.setText(draft["nama"])
            inputTelepon.setText(draft["telepon"])
            inputKeahlian.setText(draft["keahlian"])
            inputInstansi.setText(draft["instansi"])
            inputPengalaman.setText(draft["pengalaman"])
        }

        pasangAutosaveDraft()


        val infoBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(Color.rgb(232, 242, 255), 12)
        }

        val infoIcon = TextView(this).apply {
            text = "ⓘ"
            textSize = 16f
            setTextColor(Color.rgb(33, 96, 245))
        }
        infoBox.addView(infoIcon)

        val infoText = TextView(this).apply {
            text = "Pastikan informasi pengajaran yang kamu masukkan benar."
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setPadding(dp(10), 0, 0, 0)
        }
        infoBox.addView(infoText, LinearLayout.LayoutParams(
            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        ))

        root.addView(infoBox, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(20) })

        btnSimpan = TextView(this).apply {
            text = "Simpan dan Lanjutkan"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 18)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            isClickable = true
            isFocusable = true

            setOnClickListener {
                simpanProfil(mode, role)
            }
        }

        root.addView(btnSimpan, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(24) })

        scrollView.addView(root)
        return scrollView
    }

    private fun pasangAutosaveDraft() {
        val inputs = listOf(
            inputNama,
            inputTelepon,
            inputKeahlian,
            inputInstansi,
            inputPengalaman
        )

        inputs.forEach { input ->
            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) {
                    ProfilDraftManager.save(
                        this@LengkapiProfilPengajarActivity,
                        uidAktif,
                        inputNama.text.toString(),
                        inputTelepon.text.toString(),
                        inputKeahlian.text.toString(),
                        inputInstansi.text.toString(),
                        inputPengalaman.text.toString()
                    )
                }

                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
    }

    private fun tampilkanPilihanEmail(emailAktif: String) {
        val emails = LoginEmailHistory.getEmails(this)
            .toMutableList()

        val emailAktifBersih = emailAktif.trim().lowercase()

        if (emailAktifBersih.isNotBlank() &&
            !emails.contains(emailAktifBersih)
        ) {
            emails.add(0, emailAktifBersih)
        }

        if (emails.isEmpty()) {
            Toast.makeText(
                this,
                "Belum ada riwayat email login.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val pilihan = emails.map { email ->
            if (email.equals(emailAktifBersih, ignoreCase = true)) {
                "$email  •  AKUN AKTIF"
            } else {
                email
            }
        }.toTypedArray()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Pilih akun email")
            .setItems(pilihan) { dialog, which ->
                val emailDipilih = emails[which]

                if (emailDipilih.equals(emailAktifBersih, ignoreCase = true)) {
                    inputEmail.setText(emailAktif)
                } else {
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Ganti akun?")
                        .setMessage(
                            "Email $emailDipilih pernah digunakan untuk login. " +
                            "Untuk menggunakan akun ini, Anda harus login ulang " +
                            "agar profil terhubung dengan UID Firebase yang benar."
                        )
                        .setPositiveButton("Mengerti", null)
                        .show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun simpanProfil(mode: String, role: String) {
        if (sedangMenyimpan) return

        val user = FirebaseAuthManager.currentUser
        if (user == null || user.uid != uidAktif) {
            Toast.makeText(this, "Sesi login tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }

        val nama = inputNama.text.toString().trim()
        val email = user.email.orEmpty().trim()
        val telepon = inputTelepon.text.toString().trim()
        val keahlian = inputKeahlian.text.toString().trim()
        val instansi = inputInstansi.text.toString().trim()
        val pengalaman = inputPengalaman.text.toString().trim()

        if (nama.isEmpty()) {
            inputNama.error = "Nama lengkap wajib diisi"
            inputNama.requestFocus()
            return
        }
        if (telepon.isNotEmpty() && !telepon.matches(Regex("^08[0-9]{8,11}$"))) {
            inputTelepon.error = "Nomor harus diawali 08 dan terdiri dari 10–13 digit"
            inputTelepon.requestFocus()
            return
        }

        if (keahlian.isEmpty()) {
            inputKeahlian.error = "Bidang keahlian wajib diisi"
            inputKeahlian.requestFocus()
            return
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Email akun tidak ditemukan.", Toast.LENGTH_SHORT).show()
            return
        }
        if (role != PilihPeranActivity.ROLE_PENGAJAR) {
            Toast.makeText(this, "Peran tidak valid.", Toast.LENGTH_SHORT).show()
            return
        }

        sedangMenyimpan = true
        btnSimpan.isEnabled = false
        btnSimpan.text = "Menyimpan profil..."
        btnSimpan.alpha = 0.7f

        val success = {
            sedangMenyimpan = false
            ProfilDraftManager.clear(this@LengkapiProfilPengajarActivity, user.uid)

            Toast.makeText(
                this@LengkapiProfilPengajarActivity,
                "Profil berhasil disimpan!",
                Toast.LENGTH_SHORT
            ).show()

            val next = UiKit.getBerandaIntent(this@LengkapiProfilPengajarActivity, role, mode)
            if (mode == ModePembelajaranActivity.MODE_INSTITUSI) {
                next.setClass(
                    this@LengkapiProfilPengajarActivity,
                    com.example.projectmobile.ui.pengajar.institusi.PengajarBerandaActivity::class.java
                )
            }
            startActivity(next)
            finish()
        }

        val failure: (String) -> Unit = { message ->
            sedangMenyimpan = false
            btnSimpan.isEnabled = true
            btnSimpan.text = "Simpan dan Lanjutkan"
            btnSimpan.alpha = 1f
            Toast.makeText(
                this,
                "Gagal menyimpan: $message",
                Toast.LENGTH_LONG
            ).show()
        }

        val simpanKeFirebase: (String?) -> Unit = { fotoUrl ->
            if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
                FirebaseManager.savePengajarMandiriProfile(
                    uid = user.uid,
                    nama = nama,
                    email = email,
                    nomorTelepon = telepon,
                    bidangKeahlian = keahlian,
                    namaInstansi = instansi,
                    pengalamanMengajar = pengalaman,
                    fotoProfilUrl = fotoUrl,
                    onSuccess = success,
                    onFailure = failure
                )
            } else if (mode == ModePembelajaranActivity.MODE_INSTITUSI) {
                val data = mutableMapOf<String, Any>(
                    "nama" to nama,
                    "email" to email,
                    "nomor_telepon" to telepon,
                    "bidang_keahlian" to keahlian,
                    "nama_instansi" to instansi,
                    "pengalaman_mengajar" to pengalaman
                )

                if (!fotoUrl.isNullOrBlank()) {
                    data["foto_profil_url"] = fotoUrl
                }

                FirebaseManager.saveRoleModeProfile(
                    uid = user.uid,
                    collection = "pengajar_institusi",
                    role = role,
                    mode = mode,
                    data = data,
                    onSuccess = success,
                    onFailure = failure
                )
            } else {
                failure("Mode pembelajaran tidak valid.")
            }
        }

        when {
            fotoProfilUri != null -> {
                btnSimpan.text = "Mengunggah foto..."

                CloudinaryUploader.upload(
                    this,
                    fotoProfilUri!!,
                    onSuccess = { url ->
                        simpanKeFirebase(url)
                    },
                    onFailure = { message ->
                        failure("Gagal mengunggah foto: $message")
                    }
                )
            }

            fotoProfilBytes != null -> {
                btnSimpan.text = "Mengunggah foto..."

                CloudinaryUploader.uploadBytes(
                    fotoProfilBytes!!,
                    "image/jpeg",
                    onSuccess = { url ->
                        simpanKeFirebase(url)
                    },
                    onFailure = { message ->
                        failure("Gagal mengunggah foto: $message")
                    }
                )
            }

            else -> {
                simpanKeFirebase(null)
            }
        }
    }

    private fun addInputField(
        parent: LinearLayout,
        label: String,
        hint: String,
        icon: String
    ): EditText {
        val labelTv = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }

        parent.addView(labelTv, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(16) })

        val inputContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(4), dp(12), dp(4))
            background = roundedBackground(
                Color.WHITE,
                12,
                Color.rgb(220, 231, 244),
                1
            )
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 16f
        }
        inputContainer.addView(iconTv)

        val editText = EditText(this).apply {
            this.hint = hint
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = null
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }

        inputContainer.addView(editText, LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ))

        parent.addView(inputContainer, LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        dp(52)
        ).apply { topMargin = dp(8) })

        return editText
    }

    private fun roundedBackground(
        color: Int,
        radiusDp: Int,
        strokeColor: Int? = null,
        strokeWidthDp: Int = 0
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()

            if (strokeColor != null) {
                setStroke(dp(strokeWidthDp), strokeColor)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
