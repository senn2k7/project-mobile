package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Pengajar membuat kuis pilihan ganda untuk kelas mandiri. */
class BuatKuisMandiriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var code = ""

    private lateinit var etTitle: EditText
    private lateinit var questionsBox: LinearLayout
    private var duration = 20
    private var passScore = 70

    private class Block(
        val root: LinearLayout,
        val heading: TextView,
        val etQuestion: EditText,
        val etOptions: List<EditText>,
        val letters: List<TextView>,
        var correct: Int = -1
    )

    private val blocks = mutableListOf<Block>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        code = intent.getStringExtra(EXTRA_CODE) ?: ""
        if (TeacherClassStore.byCode(this, code) == null) {
            finish()
            return
        }
        setContentView(build())
        addBlock()
    }

    private fun field(hint: String, multiline: Boolean): EditText = EditText(this).apply {
        this.hint = hint
        textSize = 15f
        setTextColor(UiKit.NAVY)
        setHintTextColor(Color.parseColor("#9AABC4"))
        background = UiKit.rounded(this@BuatKuisMandiriActivity, Color.WHITE, 12, UiKit.BORDER)
        setPadding(px(14), px(10), px(14), px(10))
        if (multiline) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            minLines = 2
            maxLines = 5
            gravity = Gravity.TOP
        } else {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            maxLines = 2
        }
    }

    private fun stepButton(label: String, onClick: () -> Unit): TextView =
        UiKit.tv(this, label, 20f, UiKit.PRIMARY, true).apply {
            gravity = Gravity.CENTER
            background = UiKit.rounded(this@BuatKuisMandiriActivity, UiKit.SOFT, 10)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

    private fun stepper(initial: Int, step: Int, min: Int, max: Int, suffix: String, onChange: (Int) -> Unit): View {
        var value = initial
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = UiKit.rounded(this@BuatKuisMandiriActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), 0, px(8), 0)
        }
        val tv = UiKit.tv(this, "$value $suffix", 15f, UiKit.NAVY)
        fun update(delta: Int) {
            value = (value + delta).coerceIn(min, max)
            tv.text = "$value $suffix"
            onChange(value)
        }
        box.addView(tv, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        box.addView(stepButton("−") { update(-step) }, UiKit.params(this, px(36), px(36), end = 6))
        box.addView(stepButton("+") { update(step) }, UiKit.params(this, px(36), px(36)))
        box.layoutParams = LinearLayout.LayoutParams(UiKit.MATCH, px(52))
        return box
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Buat Kuis") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        val info = UiKit.card(this).apply { setPadding(px(16), px(4), px(16), px(16)) }
        info.addView(UiKit.fieldLabel(this, "Judul Kuis"))
        etTitle = UiKit.input(
            this, "Contoh: Kuis Pengenalan Android", "",
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES, R.drawable.ic_assignment_check
        )
        info.addView(etTitle)
        info.addView(UiKit.fieldLabel(this, "Durasi"))
        info.addView(stepper(duration, 5, 5, 120, "menit") { duration = it })
        info.addView(UiKit.fieldLabel(this, "Nilai Minimal Lulus"))
        info.addView(stepper(passScore, 5, 10, 100, "") { passScore = it })
        content.addView(info, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))

        content.addView(
            UiKit.tv(this, "Daftar Soal", 17f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22)
        )
        content.addView(
            UiKit.tv(this, "Ketuk huruf A–D untuk menandai jawaban yang benar.", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4, bottom = 4)
        )
        questionsBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(questionsBox, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        content.addView(
            UiKit.outlineButton(this, "+ Tambah Soal") { addBlock() },
            UiKit.params(this, UiKit.MATCH, px(52), top = 14)
        )

        root.addView(scroll, UiKit.scrollParams())
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        row.addView(UiKit.outlineButton(this, "Batal") { finish() }, UiKit.params(this, 0, px(56), weight = 1f))
        row.addView(UiKit.primaryButton(this, "Simpan Kuis") { submit() }, UiKit.params(this, 0, px(56), start = 12, weight = 2f))
        root.addView(UiKit.bottomBar(this, row))
        return root
    }

    private fun addBlock() {
        val card = UiKit.card(this).apply { setPadding(px(14), px(12), px(14), px(14)) }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val heading = UiKit.tv(this, "Soal ${blocks.size + 1}", 14f, UiKit.NAVY, true)
        head.addView(heading, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        card.addView(head)

        val etQ = field("Tuliskan pertanyaan", true)
        card.addView(etQ, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10))

        val etOpts = mutableListOf<EditText>()
        val letters = mutableListOf<TextView>()
        val block = Block(card, heading, etQ, etOpts, letters)

        for (i in 0 until 4) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val letter = UiKit.tv(this, "ABCD"[i].toString(), 14f, UiKit.MUTED, true).apply {
                gravity = Gravity.CENTER
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    block.correct = i
                    refreshLetters(block)
                }
            }
            val et = field("Pilihan ${"ABCD"[i]}", false)
            letters.add(letter)
            etOpts.add(et)
            row.addView(letter, UiKit.params(this, px(36), px(36), end = 10))
            row.addView(et, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
            card.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        }
        refreshLetters(block)

        val remove = UiKit.tv(this, "Hapus soal", 12f, UiKit.DANGER, true).apply {
            setPadding(0, px(8), px(4), px(4))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (blocks.size <= 1) {
                    UiKit.toast(this@BuatKuisMandiriActivity, "Kuis minimal memiliki satu soal")
                } else {
                    questionsBox.removeView(card)
                    blocks.remove(block)
                    blocks.forEachIndexed { n, b -> b.heading.text = "Soal ${n + 1}" }
                }
            }
        }
        card.addView(remove, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6))

        blocks.add(block)
        questionsBox.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
    }

    private fun refreshLetters(b: Block) {
        b.letters.forEachIndexed { i, tv ->
            val on = i == b.correct
            tv.setTextColor(if (on) Color.WHITE else UiKit.MUTED)
            tv.background = UiKit.rounded(this, if (on) UiKit.SUCCESS else UiKit.SOFT, 18)
        }
    }

    private fun submit() {
        etTitle.error = null
        val title = etTitle.text.toString().trim()
        if (title.isEmpty()) {
            etTitle.error = "Judul kuis wajib diisi"
            etTitle.requestFocus()
            return
        }
        val questions = mutableListOf<QuizQuestion>()
        for ((i, b) in blocks.withIndex()) {
            val q = b.etQuestion.text.toString().trim()
            val opts = b.etOptions.map { it.text.toString().trim() }
            if (q.isEmpty() || opts.any { it.isEmpty() }) {
                UiKit.toast(this, "Soal ${i + 1}: lengkapi pertanyaan dan keempat pilihan")
                return
            }
            if (b.correct < 0) {
                UiKit.toast(this, "Soal ${i + 1}: tandai jawaban yang benar")
                return
            }
            questions.add(QuizQuestion(q, opts, b.correct))
        }
        QuizStore.add(this, code, title, duration, passScore, questions)
        UiKit.toast(this, "Kuis disimpan")
        finish()
    }

    companion object {
        const val EXTRA_CODE = "extra_buat_kuis_mandiri_code"
    }
}
