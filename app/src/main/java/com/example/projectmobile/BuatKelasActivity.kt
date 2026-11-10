package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Form "Buat Kelas" (pengajar, mode mandiri). Dipakai juga untuk mengedit kelas. */
class BuatKelasActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var editing: TeacherClass? = null

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var ddTopic: UiKit.DropdownHandle
    private lateinit var ddCategory: UiKit.DropdownHandle
    private lateinit var ddLevel: UiKit.DropdownHandle
    private lateinit var tvCapacity: TextView
    private var capacity = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        editing = intent.getStringExtra(EXTRA_CODE)?.let { TeacherClassStore.byCode(this, it) }
        capacity = editing?.capacity ?: 30
        setContentView(build())
    }

    private fun build(): View {
        val ed = editing
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, if (ed == null) "Buat Kelas" else "Edit Kelas") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(
            UiKit.tv(this, "Informasi kelas", 18f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8)
        )
        content.addView(
            UiKit.tv(this, "Lengkapi detail kelas untuk mulai belajar bersama.", 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 16)
        )

        val card = UiKit.card(this).apply { setPadding(px(16), px(4), px(16), px(16)) }

        // Nama kelas
        card.addView(UiKit.fieldLabel(this, "Nama Kelas"))
        etName = UiKit.input(
            this, "Masukkan nama kelas", ed?.name ?: "",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS, R.drawable.ic_book
        )
        card.addView(etName)

        // Deskripsi
        card.addView(UiKit.fieldLabel(this, "Deskripsi Kelas"))
        val descBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = UiKit.rounded(this@BuatKelasActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), px(12), px(16), px(10))
        }
        etDesc = EditText(this).apply {
            hint = "Tuliskan deskripsi singkat kelas"
            setText(ed?.description ?: "")
            textSize = 15f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = null
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            minLines = 3
            maxLines = 6
            gravity = Gravity.TOP
            setPadding(0, 0, 0, 0)
            filters = arrayOf(InputFilter.LengthFilter(TeacherClassStore.MAX_DESC))
        }
        val tvCount = UiKit.tv(
            this, "${etDesc.text.length}/${TeacherClassStore.MAX_DESC}", 12f, UiKit.MUTED
        ).apply { gravity = Gravity.END }
        etDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                tvCount.text = "${s?.length ?: 0}/${TeacherClassStore.MAX_DESC}"
            }
        })
        descBox.addView(etDesc, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        descBox.addView(tvCount, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6))
        card.addView(descBox, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))

        // Topik, kategori, tingkat
        card.addView(UiKit.fieldLabel(this, "Mata Pelajaran / Topik"))
        ddTopic = UiKit.dropdown(this, "Pilih topik kelas", ed?.topic ?: "", TeacherClassStore.TOPICS, R.drawable.ic_folder)
        card.addView(ddTopic.view)

        card.addView(UiKit.fieldLabel(this, "Kategori"))
        ddCategory = UiKit.dropdown(this, "Pilih kategori", ed?.category ?: "", TeacherClassStore.CATEGORIES, R.drawable.ic_flag)
        card.addView(ddCategory.view)

        card.addView(UiKit.fieldLabel(this, "Tingkat"))
        ddLevel = UiKit.dropdown(this, "Pilih tingkat belajar", ed?.level ?: "", TeacherClassStore.LEVELS, R.drawable.ic_description)
        card.addView(ddLevel.view)

        // Kapasitas peserta
        card.addView(UiKit.fieldLabel(this, "Kapasitas Peserta"))
        val stepper = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(this@BuatKelasActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), 0, px(8), 0)
        }
        stepper.addView(UiKit.icon(this, R.drawable.ic_people, 22, UiKit.MUTED), LinearLayout.LayoutParams(px(22), px(22)))
        tvCapacity = UiKit.tv(this, "$capacity", 15f, UiKit.NAVY)
        stepper.addView(tvCapacity, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        stepper.addView(stepButton("−") { changeCapacity(-5) }, UiKit.params(this, px(36), px(36), end = 6))
        stepper.addView(stepButton("+") { changeCapacity(5) }, UiKit.params(this, px(36), px(36)))
        card.addView(stepper, LinearLayout.LayoutParams(UiKit.MATCH, px(52)))

        content.addView(card)
        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(
            UiKit.outlineButton(this, "Batal") { finish() },
            UiKit.params(this, 0, px(56), weight = 1f)
        )
        row.addView(
            UiKit.primaryButton(this, if (ed == null) "+ Buat Kelas" else "Simpan Perubahan") { submit() },
            UiKit.params(this, 0, px(56), start = 12, weight = 2f)
        )
        root.addView(UiKit.bottomBar(this, row))
        root.addView(UiKit.bottomNav(this, "Kelas", role, mode))
        return root
    }

    private fun stepButton(label: String, onClick: () -> Unit): TextView =
        UiKit.tv(this, label, 20f, UiKit.PRIMARY, true).apply {
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@BuatKelasActivity, UiKit.SOFT, 10)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

    private fun changeCapacity(delta: Int) {
        capacity = (capacity + delta).coerceIn(5, 200)
        tvCapacity.text = "$capacity"
    }

    private fun submit() {
        etName.error = null
        val name = etName.text.toString().trim()
        if (name.isEmpty()) {
            etName.error = "Nama kelas wajib diisi"
            etName.requestFocus()
            return
        }
        if (ddTopic.selected.isBlank() || ddCategory.selected.isBlank() || ddLevel.selected.isBlank()) {
            UiKit.toast(this, "Lengkapi topik, kategori, dan tingkat belajar")
            return
        }
        val desc = etDesc.text.toString().trim()
        val ed = editing
        if (ed == null) {
            val c = TeacherClassStore.create(
                this, name, desc, ddTopic.selected, ddCategory.selected, ddLevel.selected, capacity
            )
            val next = UiKit.withProfile(Intent(this, KelasBerhasilActivity::class.java), role, mode)
            next.putExtra(KelasBerhasilActivity.EXTRA_CODE, c.code)
            next.putExtra(KelasBerhasilActivity.EXTRA_CREATED, true)
            startActivity(next)
        } else {
            TeacherClassStore.update(
                this,
                ed.copy(
                    name = name,
                    description = desc,
                    topic = ddTopic.selected,
                    category = ddCategory.selected,
                    level = ddLevel.selected,
                    capacity = capacity
                )
            )
            UiKit.toast(this, "Perubahan disimpan")
        }
        finish()
    }

    companion object {
        const val EXTRA_CODE = "extra_buat_kelas_code"
    }
}
