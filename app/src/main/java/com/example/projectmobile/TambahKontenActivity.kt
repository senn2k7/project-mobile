package com.example.projectmobile

import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

/** Form tambah materi atau tugas untuk kelas mandiri milik pengajar. */
class TambahKontenActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var code = ""
    private var isTask = false

    private lateinit var etTitle: EditText
    private lateinit var etBody: EditText
    private var tvFile: TextView? = null
    private var tvDeadline: TextView? = null
    private var fileName = ""
    private var deadline = 0L

    private val picker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            fileName = queryName(uri)
            tvFile?.text = fileName
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        code = intent.getStringExtra(EXTRA_CODE) ?: ""
        isTask = intent.getStringExtra(EXTRA_TYPE) == TYPE_TUGAS
        if (TeacherClassStore.byCode(this, code) == null) {
            finish()
            return
        }
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 7)
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        deadline = cal.timeInMillis
        setContentView(build())
    }

    private fun queryName(uri: Uri): String {
        var name = "Lampiran"
        try {
            contentResolver.query(uri, null, null, null, null)?.use { cur ->
                val i = cur.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (i >= 0 && cur.moveToFirst()) name = cur.getString(i) ?: name
            }
        } catch (e: Exception) {
            // pakai nama bawaan
        }
        return name
    }

    private fun pickerBox(iconRes: Int, text: String, onClick: () -> Unit): Pair<LinearLayout, TextView> {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(this@TambahKontenActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), 0, px(16), 0)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }
        box.addView(UiKit.icon(this, iconRes, 22, UiKit.MUTED), LinearLayout.LayoutParams(px(22), px(22)))
        val tv = UiKit.tv(this, text, 15f, UiKit.NAVY)
        box.addView(tv, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        return Pair(box, tv)
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, if (isTask) "Tambah Tugas" else "Tambah Materi") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        val card = UiKit.card(this).apply { setPadding(px(16), px(4), px(16), px(16)) }

        card.addView(UiKit.fieldLabel(this, if (isTask) "Judul Tugas" else "Judul Materi"))
        etTitle = UiKit.input(
            this, "Masukkan judul", "",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES, R.drawable.ic_description
        )
        card.addView(etTitle)

        card.addView(UiKit.fieldLabel(this, if (isTask) "Instruksi Tugas" else "Deskripsi Materi"))
        etBody = EditText(this).apply {
            hint = if (isTask) "Tuliskan instruksi pengerjaan (satu langkah per baris)" else "Tuliskan ringkasan materi"
            textSize = 15f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = UiKit.rounded(this@TambahKontenActivity, Color.WHITE, 12, UiKit.BORDER)
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            minLines = 4
            maxLines = 8
            gravity = Gravity.TOP
            setPadding(px(16), px(12), px(16), px(12))
        }
        card.addView(etBody, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))

        if (isTask) {
            card.addView(UiKit.fieldLabel(this, "Tenggat Pengumpulan"))
            val (box, tv) = pickerBox(R.drawable.ic_calendar, TaskStore.format(deadline)) { pickDate() }
            tvDeadline = tv
            card.addView(box, LinearLayout.LayoutParams(UiKit.MATCH, px(52)))
        } else {
            card.addView(UiKit.fieldLabel(this, "Lampiran (opsional)"))
            val (box, tv) = pickerBox(R.drawable.ic_upload, "Pilih file materi") {
                picker.launch(arrayOf("*/*"))
            }
            tvFile = tv
            card.addView(box, LinearLayout.LayoutParams(UiKit.MATCH, px(52)))
        }

        content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        root.addView(scroll, UiKit.scrollParams())

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(UiKit.outlineButton(this, "Batal") { finish() }, UiKit.params(this, 0, px(56), weight = 1f))
        row.addView(
            UiKit.primaryButton(this, if (isTask) "Simpan Tugas" else "Simpan Materi") { submit() },
            UiKit.params(this, 0, px(56), start = 12, weight = 2f)
        )
        root.addView(UiKit.bottomBar(this, row))
        return root
    }

    private fun pickDate() {
        val cal = Calendar.getInstance().apply { timeInMillis = deadline }
        DatePickerDialog(
            this,
            { _, y, mo, d ->
                val picked = Calendar.getInstance()
                picked.set(y, mo, d, 23, 59, 0)
                picked.set(Calendar.MILLISECOND, 0)
                deadline = picked.timeInMillis
                tvDeadline?.text = TaskStore.format(deadline)
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun submit() {
        etTitle.error = null
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            etTitle.error = "Judul wajib diisi"
            etTitle.requestFocus()
            return
        }
        val body = etBody.text.toString().trim()
        if (isTask) {
            TeacherClassStore.addTask(this, code, title, body, deadline)
            UiKit.toast(this, "Tugas ditambahkan")
        } else {
            TeacherClassStore.addMaterial(this, code, title, body, fileName)
            UiKit.toast(this, "Materi ditambahkan")
        }
        finish()
    }

    companion object {
        const val EXTRA_CODE = "extra_tambah_konten_code"
        const val EXTRA_TYPE = "extra_tambah_konten_type"
        const val TYPE_MATERI = "materi"
        const val TYPE_TUGAS = "tugas"
    }
}
