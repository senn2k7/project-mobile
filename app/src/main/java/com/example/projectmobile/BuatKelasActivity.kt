package com.example.projectmobile

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
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
    private lateinit var ddTopic: InlineDropdown
    private lateinit var ddCategory: InlineDropdown
    private lateinit var ddLevel: InlineDropdown
    private val dropdowns = mutableListOf<InlineDropdown>()
    private lateinit var tvCapacity: TextView
    private lateinit var stepperBox: LinearLayout
    private lateinit var btnMinus: TextView
    private lateinit var btnPlus: TextView
    private var capacity = 30
    private var unlimited = false
    private var isSubmitting = false
    private var submitButton: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        editing = intent.getStringExtra(EXTRA_CODE)?.let { TeacherClassStore.byCode(this, it) }
        val savedCapacity = editing?.capacity
        unlimited = savedCapacity != null && TeacherClassStore.isUnlimited(savedCapacity)
        capacity = if (savedCapacity == null || unlimited) 30 else savedCapacity
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
        card.addView(label("Nama Kelas", required = true))
        etName = UiKit.input(
            this, "Masukkan nama kelas", ed?.name ?: "",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS, R.drawable.ic_book
        )
        card.addView(etName)

        // Deskripsi
        card.addView(label("Deskripsi Kelas", required = false))
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
        card.addView(label("Mata Pelajaran / Topik", required = true))
        ddTopic = InlineDropdown("Pilih topik kelas", ed?.topic ?: "", TeacherClassStore.TOPICS, R.drawable.ic_folder)
        card.addView(ddTopic.view)

        card.addView(label("Kategori", required = true))
        ddCategory = InlineDropdown("Pilih kategori", ed?.category ?: "", TeacherClassStore.CATEGORIES, R.drawable.ic_flag)
        card.addView(ddCategory.view)

        card.addView(label("Tingkat", required = true))
        ddLevel = InlineDropdown("Pilih tingkat belajar", ed?.level ?: "", TeacherClassStore.LEVELS, R.drawable.ic_description)
        card.addView(ddLevel.view)

        // Kapasitas peserta
        card.addView(label("Kapasitas Peserta", required = false))
        val stepper = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(this@BuatKelasActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), 0, px(8), 0)
        }
        stepper.addView(UiKit.icon(this, R.drawable.ic_people, 22, UiKit.MUTED), LinearLayout.LayoutParams(px(22), px(22)))
        tvCapacity = UiKit.tv(this, "$capacity", 15f, UiKit.NAVY)
        stepper.addView(tvCapacity, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        btnMinus = stepButton("−") { changeCapacity(-5) }
        btnPlus = stepButton("+") { changeCapacity(5) }
        stepper.addView(btnMinus, UiKit.params(this, px(36), px(36), end = 6))
        stepper.addView(btnPlus, UiKit.params(this, px(36), px(36)))
        stepperBox = stepper
        card.addView(stepper, LinearLayout.LayoutParams(UiKit.MATCH, px(52)))

        val cbUnlimited = CheckBox(this).apply {
            text = "Tanpa Batas (Unlimited)"
            textSize = 14f
            setTextColor(UiKit.NAVY)
            buttonTintList = ColorStateList.valueOf(UiKit.PRIMARY)
            isChecked = unlimited
            setOnCheckedChangeListener { _, checked ->
                unlimited = checked
                applyCapacityState()
            }
        }
        card.addView(cbUnlimited, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8))
        applyCapacityState()

        content.addView(card)
        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(
            UiKit.outlineButton(this, "Batal") { finish() },
            UiKit.params(this, 0, px(52), weight = 1f)
        )
        val button = UiKit.primaryButton(
            this,
            if (ed == null) "+ Buat Kelas" else "Simpan Perubahan"
        ) { submit() }
        submitButton = button
        row.addView(
            button,
            UiKit.params(this, 0, px(52), start = 12, weight = 2f)
        )
        root.addView(UiKit.bottomBar(this, row))
        root.addView(UiKit.bottomNav(this, "Kelas", role, mode))
        return root
    }

    private fun hideKeyboard() {
        currentFocus?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }
    }

    /**
     * Dropdown yang terbuka di tempat (di dalam scroll form), bukan pop-up.
     * Mengetuk field membuka daftar pilihan tepat di bawahnya; memilih satu pilihan
     * menutup daftar dan pengguna langsung lanjut mengisi field berikutnya.
     */
    private inner class InlineDropdown(
        private val placeholder: String,
        current: String,
        private val options: List<String>,
        iconRes: Int
    ) {
        private val ctx = this@BuatKelasActivity
        private val valueView = UiKit.tv(ctx, "", 15f, UiKit.NAVY)
        private val chevron = UiKit.icon(ctx, R.drawable.ic_chevron_down, 22, UiKit.MUTED)
        private var expanded = false

        private val optionList = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
            background = UiKit.rounded(ctx, Color.WHITE, 12, UiKit.BORDER)
            setPadding(0, px(6), 0, px(6))
        }

        private val header = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(ctx, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), 0, px(16), 0)
            isClickable = true
            isFocusable = true
            addView(UiKit.icon(ctx, iconRes, 22, UiKit.MUTED), LinearLayout.LayoutParams(px(22), px(22)))
            addView(valueView, UiKit.params(ctx, 0, UiKit.WRAP, start = 12, weight = 1f))
            addView(chevron, LinearLayout.LayoutParams(px(22), px(22)))
            setOnClickListener { toggle() }
        }

        val view = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP)
            addView(header, LinearLayout.LayoutParams(UiKit.MATCH, px(52)))
            addView(optionList, UiKit.params(ctx, UiKit.MATCH, UiKit.WRAP, top = 6))
        }

        var selected: String = ""
            set(value) {
                field = value
                valueView.text = if (value.isBlank()) placeholder else value
                valueView.setTextColor(if (value.isBlank()) UiKit.MUTED else UiKit.NAVY)
                renderOptions()
            }

        init {
            selected = current
            dropdowns.add(this)
        }

        fun collapse() {
            expanded = false
            optionList.visibility = View.GONE
            chevron.rotation = 0f
        }

        private fun toggle() {
            if (expanded) {
                collapse()
                return
            }
            hideKeyboard()
            dropdowns.filter { it !== this }.forEach { it.collapse() }
            expanded = true
            optionList.visibility = View.VISIBLE
            chevron.rotation = 180f
            view.post { view.requestRectangleOnScreen(Rect(0, 0, view.width, view.height), false) }
        }

        private fun renderOptions() {
            optionList.removeAllViews()
            options.forEach { option ->
                val isSelected = option == selected
                val row = UiKit.tv(
                    ctx, option, 15f,
                    if (isSelected) UiKit.PRIMARY else UiKit.NAVY, isSelected
                ).apply {
                    setPadding(px(14), px(13), px(14), px(13))
                    if (isSelected) background = UiKit.rounded(ctx, UiKit.SOFT, 8)
                    isClickable = true
                    isFocusable = true
                    setOnClickListener {
                        selected = option
                        collapse()
                    }
                }
                optionList.addView(row, UiKit.params(ctx, UiKit.MATCH, UiKit.WRAP, start = 6, end = 6))
            }
        }
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
        if (unlimited) return
        capacity = (capacity + delta).coerceIn(5, 200)
        tvCapacity.text = "$capacity"
    }

    /** Menampilkan kapasitas sesuai pilihan "Tanpa Batas"; stepper dinonaktifkan saat tanpa batas. */
    private fun applyCapacityState() {
        tvCapacity.text = if (unlimited) "Tanpa Batas" else "$capacity"
        stepperBox.alpha = if (unlimited) 0.45f else 1f
        btnMinus.isEnabled = !unlimited
        btnPlus.isEnabled = !unlimited
    }

    /** Label field: "(wajib)" untuk field yang harus diisi, "(Opsional)" untuk sisanya. */
    private fun label(text: String, required: Boolean): TextView {
        val view = UiKit.fieldLabel(this, text)
        val sb = SpannableStringBuilder(text)
        if (required) {
            val starStart = sb.length
            sb.append(" *")
            sb.setSpan(ForegroundColorSpan(UiKit.DANGER), starStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val noteStart = sb.length
        sb.append(if (required) "(wajib)" else " (Opsional)")
        sb.setSpan(ForegroundColorSpan(UiKit.MUTED), noteStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(StyleSpan(Typeface.NORMAL), noteStart, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.text = sb
        return view
    }

    private fun submit() {
        if (isSubmitting) return

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
        val finalCapacity = if (unlimited) TeacherClassStore.CAPACITY_UNLIMITED else capacity

        if (ed == null) {
            val uid = FirebaseAuthManager.currentUser?.uid
            if (uid.isNullOrBlank()) {
                UiKit.toast(this, "Silakan login kembali untuk membuat kelas.")
                return
            }

            val pending = TeacherClassStore.createPending(
                this,
                name,
                desc,
                ddTopic.selected,
                ddCategory.selected,
                ddLevel.selected,
                finalCapacity,
                uid
            )

            isSubmitting = true
            submitButton?.isEnabled = false

            FirebaseManager.saveTeacherClass(
                pending,
                teacherName = ProfileStore.get(
                    this,
                    PilihPeranActivity.ROLE_PENGAJAR,
                    ModePembelajaranActivity.MODE_MANDIRI,
                    ProfileStore.NAMA
                ),
                onSuccess = {
                    if (isFinishing || isDestroyed) return@saveTeacherClass

                    TeacherClassStore.cacheCreated(this, pending)

                    val next = UiKit.withProfile(
                        Intent(this, KelasBerhasilActivity::class.java),
                        role,
                        mode
                    )
                    next.putExtra(KelasBerhasilActivity.EXTRA_CODE, pending.code)
                    next.putExtra(KelasBerhasilActivity.EXTRA_CREATED, true)
                    startActivity(next)
                    finish()
                },
                onFailure = { message ->
                    if (isFinishing || isDestroyed) return@saveTeacherClass

                    isSubmitting = false
                    submitButton?.isEnabled = true
                    UiKit.toast(this, message)
                }
            )
            return
        }

        // Alur edit kelas tetap menggunakan penyimpanan lokal seperti sebelumnya.
        TeacherClassStore.update(
            this,
            ed.copy(
                name = name,
                description = desc,
                topic = ddTopic.selected,
                category = ddCategory.selected,
                level = ddLevel.selected,
                capacity = finalCapacity
            )
        )
        UiKit.toast(this, "Perubahan disimpan")
        finish()
    }

    companion object {
        const val EXTRA_CODE = "extra_buat_kelas_code"
    }
}
