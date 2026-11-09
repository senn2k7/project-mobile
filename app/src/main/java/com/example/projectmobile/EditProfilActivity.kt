package com.example.snapchatsphlasscreen

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/** Halaman "Edit Profil" untuk pelajar dan pengajar. */
class EditProfilActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var avatar: UiKit.AvatarHandle? = null

    // Setiap kolom mengembalikan nilainya lewat fungsi ini.
    private val getters = LinkedHashMap<String, () -> String>()
    private val labels = LinkedHashMap<String, String>()
    private val required = HashSet<String>()
    private val editors = HashMap<String, EditText>()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Foto hanya dipratinjau; unggah ke server dihubungkan nanti.
        if (uri != null) avatar?.setPhoto(uri)
    }

    private val prodiOptions = listOf(
        "Teknik Informatika", "Sistem Informasi", "Teknik Elektro", "Teknik Industri",
        "Manajemen", "Akuntansi", "Ilmu Hukum", "Pendidikan Matematika"
    )
    private val institusiOptions = listOf(
        "Universitas Trunojoyo Madura", "Universitas Teknologi Indonesia",
        "Universitas Negeri Surabaya", "Universitas Airlangga", "Institut Teknologi Sepuluh Nopember"
    )
    private val jabatanOptions = listOf("Dosen / Pengajar", "Guru", "Asisten Dosen", "Instruktur")
    private val fakultasOptions = listOf(
        "Fakultas Ilmu Komputer", "Fakultas Teknik", "Fakultas Ekonomi dan Bisnis",
        "Fakultas Hukum", "Fakultas Keguruan dan Ilmu Pendidikan"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        setContentView(build())
    }

    private fun value(field: String) = ProfileStore.get(this, role, field)

    private fun build(): View {
        val teacher = ProfileStore.isTeacher(role)
        val institusi = mode == ModePembelajaranActivity.MODE_INSTITUSI

        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Edit Profil") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        // Foto profil
        val photo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        val handle = UiKit.avatar(this, 96, withCamera = true) { pickImage.launch("image/*") }
        avatar = handle
        photo.addView(handle.view)
        if (teacher) {
            photo.addView(
                UiKit.tv(this, "Ubah Foto Profil", 14f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 10)
            )
            photo.addView(
                UiKit.tv(this, "Format: JPG, PNG. Maksimal 2 MB.", 12f, UiKit.MUTED),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
            )
        }
        content.addView(photo, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4, bottom = 8))

        // Kolom-kolom sesuai peran dan mode
        addText(content, "Nama Lengkap", ProfileStore.NAMA, true,
            if (teacher) R.drawable.ic_person else null)
        addEmail(content, teacher)

        if (teacher) {
            addText(content, "Nomor Telepon", ProfileStore.PHONE, true, R.drawable.ic_phone,
                InputType.TYPE_CLASS_PHONE)
            if (institusi) {
                addDropdown(content, "Jabatan", ProfileStore.JABATAN, true, jabatanOptions, R.drawable.ic_briefcase)
                addDropdown(content, "Fakultas", ProfileStore.FAKULTAS, true, fakultasOptions, R.drawable.ic_institution)
                addDropdown(content, "Program Studi", ProfileStore.PRODI, true, prodiOptions, R.drawable.ic_book)
                content.addView(UiKit.fieldLabel(this, "Institusi"))
                content.addView(
                    UiKit.readOnlyBox(this, value(ProfileStore.INSTITUSI),
                        startIcon = R.drawable.ic_institution, endIcon = R.drawable.ic_lock)
                )
                content.addView(
                    UiKit.infoBox(this, "Data institusi dikelola administrator."),
                    UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8)
                )
            }
        } else if (institusi) {
            addText(content, "NIM", ProfileStore.NIM, false, null, InputType.TYPE_CLASS_NUMBER)
            addDropdown(content, "Program Studi", ProfileStore.PRODI, true, prodiOptions)
            addDropdown(content, "Institusi", ProfileStore.INSTITUSI, true, institusiOptions)
        } else {
            addText(content, "Nomor Telepon", ProfileStore.PHONE, false, null, InputType.TYPE_CLASS_PHONE)
        }

        root.addView(scroll, UiKit.scrollParams())
        root.addView(UiKit.bottomBar(this, UiKit.primaryButton(this, "Simpan Perubahan") { save() }))
        return root
    }

    private fun addText(
        parent: LinearLayout,
        label: String,
        field: String,
        isRequired: Boolean,
        iconRes: Int?,
        type: Int = InputType.TYPE_CLASS_TEXT
    ) {
        parent.addView(UiKit.fieldLabel(this, label, isRequired))
        val et = UiKit.input(this, label, value(field), type, iconRes)
        parent.addView(et)
        getters[field] = { et.text.toString().trim() }
        labels[field] = label
        editors[field] = et
        if (isRequired) required.add(field)
    }

    private fun addDropdown(
        parent: LinearLayout,
        label: String,
        field: String,
        isRequired: Boolean,
        options: List<String>,
        iconRes: Int? = null
    ) {
        parent.addView(UiKit.fieldLabel(this, label, isRequired))
        val dd = UiKit.dropdown(this, "Pilih ${label.lowercase()}", value(field), options, iconRes)
        parent.addView(dd.view)
        getters[field] = { dd.selected }
        labels[field] = label
        if (isRequired) required.add(field)
    }

    private fun addEmail(parent: LinearLayout, teacher: Boolean) {
        parent.addView(UiKit.fieldLabel(this, "Email", true))
        parent.addView(
            UiKit.readOnlyBox(
                this, value(ProfileStore.EMAIL),
                startIcon = if (teacher) R.drawable.ic_email else null,
                endIcon = if (teacher) R.drawable.ic_check_circle else null,
                endTint = UiKit.PRIMARY
            )
        )
        if (teacher) {
            parent.addView(
                UiKit.infoBox(this, "Email terverifikasi. Hubungi administrator untuk mengubah email institusi."),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8)
            )
        }
    }

    private fun save() {
        // Validasi kolom wajib
        for (field in required) {
            val v = getters[field]?.invoke().orEmpty()
            if (v.isBlank()) {
                val name = labels[field] ?: field
                val et = editors[field]
                if (et != null) {
                    et.error = "$name harus diisi"
                    et.requestFocus()
                } else {
                    UiKit.toast(this, "$name harus dipilih")
                }
                return
            }
        }
        for ((field, getter) in getters) {
            ProfileStore.put(this, role, field, getter())
        }
        UiKit.toast(this, "Profil berhasil diperbarui")
        finish()
    }
}
