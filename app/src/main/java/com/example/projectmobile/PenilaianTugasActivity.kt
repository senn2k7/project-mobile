package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/** Pengajar melihat pengumpulan sebuah tugas lalu memberi nilai dan catatan. */
class PenilaianTugasActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var classCode = ""
    private var taskId = ""
    private var etScore: EditText? = null
    private var etFeedback: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PENGAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        classCode = intent.getStringExtra(EXTRA_CLASS_CODE) ?: ""
        taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: ""
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val cls = TeacherClassStore.byCode(this, classCode)
        val task = TeacherClassStore.tasks(this, classCode).firstOrNull { it.id == taskId }
        if (cls == null || task == null) {
            finish()
            return
        }
        setContentView(build(cls, task))
    }

    private fun initials(name: String): String =
        name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }.ifEmpty { "?" }

    private fun build(cls: TeacherClass, task: TeacherTask): View {
        val sub = TaskStore.submission(this, task.id)
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Detail Tugas") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        // Info tugas
        val info = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        info.addView(UiKit.tv(this, task.title, 18f, UiKit.NAVY, true))
        info.addView(ClassUi.chip(this, cls.name), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 8))
        val due = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        due.addView(UiKit.icon(this, R.drawable.ic_calendar, 16, UiKit.MUTED), LinearLayout.LayoutParams(px(16), px(16)))
        due.addView(
            UiKit.tv(this, "Tenggat: ${TaskStore.format(task.deadlineMillis)}", 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 6)
        )
        info.addView(due, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        if (task.instructions.isNotBlank()) {
            info.addView(UiKit.tv(this, "Instruksi", 12.5f, UiKit.MUTED, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14))
            info.addView(UiKit.tv(this, task.instructions, 13.5f, UiKit.NAVY), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4))
        }
        content.addView(info, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6))

        content.addView(
            UiKit.tv(this, "Pengumpulan Pelajar", 16f, UiKit.NAVY, true),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22)
        )

        if (sub == null) {
            val empty = UiKit.card(this).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(px(24), px(28), px(24), px(28))
            }
            empty.addView(UiKit.badge(this, R.drawable.ic_assignment_check, UiKit.PRIMARY, UiKit.SOFT, 56, 28, 26))
            empty.addView(
                UiKit.tv(this, "Belum ada pengumpulan", 15f, UiKit.NAVY, true),
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14)
            )
            empty.addView(
                UiKit.tv(this, "Pengumpulan pelajar akan tampil di sini.", 12.5f, UiKit.MUTED).apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 6)
            )
            content.addView(empty, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
            root.addView(scroll, UiKit.scrollParams())
            return root
        }

        // Kartu pengumpulan
        val name = ProfileStore.get(this, PilihPeranActivity.ROLE_PELAJAR, ProfileStore.NAMA).ifBlank { "Pelajar" }
        val card = UiKit.card(this).apply { setPadding(px(16), px(16), px(16), px(16)) }
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(
            UiKit.tv(this, initials(name), 14f, UiKit.PRIMARY, true).apply {
                gravity = Gravity.CENTER
                background = UiKit.oval(UiKit.SOFT)
            },
            LinearLayout.LayoutParams(px(44), px(44))
        )
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, name, 15f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "Dikumpulkan ${TaskStore.format(sub.submittedAt)}", 12f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        head.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
        head.addView(
            if (sub.score != null) ClassUi.chip(this, "Dinilai (${sub.score})", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)
            else ClassUi.chip(this, "Menunggu Penilaian")
        )
        card.addView(head)

        card.addView(UiKit.tv(this, "Jawaban", 12.5f, UiKit.MUTED, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 16))
        val answerBox = UiKit.tv(
            this,
            sub.answer.ifBlank { "Pelajar tidak menulis jawaban teks." },
            14f, if (sub.answer.isBlank()) UiKit.MUTED else UiKit.NAVY
        ).apply {
            background = UiKit.rounded(this@PenilaianTugasActivity, UiKit.SOFT, 12)
            setPadding(px(14), px(12), px(14), px(12))
        }
        card.addView(answerBox, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 6))

        if (sub.files.isNotEmpty()) {
            card.addView(UiKit.tv(this, "Lampiran", 12.5f, UiKit.MUTED, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 14))
            for (f in sub.files) {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }
                row.addView(UiKit.icon(this, R.drawable.ic_description, 18, UiKit.PRIMARY), LinearLayout.LayoutParams(px(18), px(18)))
                row.addView(UiKit.tv(this, f, 13.5f, UiKit.NAVY), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8))
                card.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
            }
        }
        content.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))

        // Form penilaian
        val grade = UiKit.card(this).apply { setPadding(px(16), px(4), px(16), px(16)) }
        grade.addView(UiKit.fieldLabel(this, "Nilai (0–100)"))
        val scoreField = UiKit.input(
            this, "Contoh: 85", sub.score?.toString() ?: "",
            InputType.TYPE_CLASS_NUMBER, R.drawable.ic_assignment_check
        )
        etScore = scoreField
        grade.addView(scoreField)
        grade.addView(UiKit.fieldLabel(this, "Catatan untuk pelajar (opsional)"))
        val feedbackField = EditText(this).apply {
            hint = "Tuliskan masukan untuk pelajar"
            setText(sub.feedback)
            textSize = 15f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = UiKit.rounded(this@PenilaianTugasActivity, Color.WHITE, 12, UiKit.BORDER)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            minLines = 3
            maxLines = 6
            gravity = Gravity.TOP
            setPadding(px(16), px(12), px(16), px(12))
        }
        etFeedback = feedbackField
        grade.addView(feedbackField, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        content.addView(grade, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 14))

        root.addView(scroll, UiKit.scrollParams())
        root.addView(
            UiKit.bottomBar(this, UiKit.primaryButton(this, if (sub.score == null) "Simpan Nilai" else "Perbarui Nilai") { save() })
        )
        return root
    }

    private fun save() {
        val score = etScore?.text?.toString()?.trim()?.toIntOrNull()
        if (score == null || score !in 0..100) {
            etScore?.error = "Nilai harus berupa angka 0–100"
            etScore?.requestFocus()
            return
        }
        val ok = TaskStore.grade(this, taskId, score, etFeedback?.text?.toString()?.trim() ?: "")
        UiKit.toast(this, if (ok) "Nilai disimpan" else "Pengumpulan tidak ditemukan")
        render()
    }

    companion object {
        const val EXTRA_CLASS_CODE = "extra_penilaian_class"
        const val EXTRA_TASK_ID = "extra_penilaian_task"
    }
}
