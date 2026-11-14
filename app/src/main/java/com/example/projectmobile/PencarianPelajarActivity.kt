package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Pencarian pelajar mandiri: kelas, materi, tugas, dan kuis dari kelas yang diikuti. */
class PencarianPelajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var query = ""
    private var filter = T_SEMUA

    private lateinit var etSearch: EditText
    private lateinit var chips: LinearLayout
    private lateinit var results: LinearLayout

    private class Hit(val type: String, val title: String, val subtitle: String, val open: () -> Unit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = PilihPeranActivity.ROLE_PELAJAR
        mode = ModePembelajaranActivity.MODE_MANDIRI
        setContentView(build())
        renderChips()
        search()
    }

    override fun onResume() {
        super.onResume()
        if (::results.isInitialized) search()
    }

    private fun build(): View {
        val root = UiKit.root(this)

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(20), px(16), px(20), px(8))
        }
        top.addView(
            ImageView(this).apply {
                setImageResource(R.drawable.ic_back)
                contentDescription = "Kembali"
                isClickable = true
                isFocusable = true
                setOnClickListener { finish() }
            }
        )
        etSearch = EditText(this).apply {
            hint = "Cari kelas, materi, tugas, atau kuis..."
            textSize = 15f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = UiKit.rounded(this@PencarianPelajarActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), 0, px(16), 0)
            inputType = InputType.TYPE_CLASS_TEXT
            maxLines = 1
            setSingleLine(true)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    query = s?.toString()?.trim() ?: ""
                    search()
                }
            })
        }
        top.addView(etSearch, UiKit.params(this, 0, px(48), start = 12, weight = 1f))
        root.addView(top)

        chips = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(px(20), px(4), px(20), px(8))
        }
        val hs = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(chips)
        }
        root.addView(hs, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))

        val (scroll, content) = UiKit.scroll(this)
        results = content
        root.addView(scroll, UiKit.scrollParams())
        return root
    }

    private fun renderChips() {
        chips.removeAllViews()
        for (label in listOf(T_SEMUA, T_KELAS, T_MATERI, T_TUGAS, T_KUIS)) {
            val on = label == filter
            val chip = UiKit.tv(this, label, 13f, if (on) Color.WHITE else UiKit.MUTED, true).apply {
                gravity = Gravity.CENTER
                background = if (on) UiKit.rounded(this@PencarianPelajarActivity, UiKit.PRIMARY, 18)
                else UiKit.rounded(this@PencarianPelajarActivity, Color.WHITE, 18, UiKit.BORDER)
                setPadding(px(16), 0, px(16), 0)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    filter = label
                    renderChips()
                    search()
                }
            }
            chips.addView(chip, UiKit.params(this, UiKit.WRAP, px(36), end = 8))
        }
    }

    private fun profileIntent(target: Class<*>): Intent = UiKit.withProfile(Intent(this, target), role, mode)

    private fun collect(q: String): List<Hit> {
        val hits = mutableListOf<Hit>()
        fun match(vararg texts: String): Boolean = texts.any { it.contains(q, ignoreCase = true) }

        for (c in ClassStore.joined(this)) {
            if (match(c.name, c.teacher, c.field)) {
                hits.add(Hit(T_KELAS, c.name, "${c.teacher}  •  ${c.field}") {
                    startActivity(profileIntent(DetailKelasPelajarActivity::class.java).putExtra(DetailKelasPelajarActivity.EXTRA_CODE, c.code))
                })
            }
            for (m in ClassStore.meetings(this, c)) {
                if (match(m.title)) {
                    hits.add(Hit(T_MATERI, m.title, "${c.name}  •  Pertemuan ${m.number}") {
                        startActivity(
                            profileIntent(DetailMateriActivity::class.java)
                                .putExtra(DetailMateriActivity.EXTRA_CODE, c.code)
                                .putExtra(DetailMateriActivity.EXTRA_NUMBER, m.number)
                        )
                    })
                }
            }
            for (z in QuizStore.quizzes(this, c.code)) {
                if (match(z.title)) {
                    hits.add(Hit(T_KUIS, z.title, c.name) {
                        startActivity(profileIntent(DetailKuisMandiriActivity::class.java).putExtra(DetailKuisMandiriActivity.EXTRA_QUIZ_ID, z.id))
                    })
                }
            }
        }
        for (t in TaskStore.tasks(this)) {
            if (match(t.title)) {
                hits.add(Hit(T_TUGAS, t.title, t.className) {
                    startActivity(profileIntent(DetailTugasPelajarActivity::class.java).putExtra(DetailTugasPelajarActivity.EXTRA_TASK_ID, t.id))
                })
            }
        }
        return hits
    }

    private fun iconFor(type: String): Int = when (type) {
        T_KELAS -> R.drawable.ic_book
        T_MATERI -> R.drawable.ic_description
        T_TUGAS -> R.drawable.ic_folder
        else -> R.drawable.ic_assignment_check
    }

    private fun message(text: String): View =
        UiKit.tv(this, text, 13.5f, UiKit.MUTED).apply { gravity = Gravity.CENTER }

    private fun search() {
        results.removeAllViews()
        if (query.isEmpty()) {
            results.addView(
                message("Ketik untuk mencari kelas, materi, tugas, atau kuis dari kelas yang kamu ikuti."),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 48)
            )
            return
        }
        val hits = collect(query).filter { filter == T_SEMUA || it.type == filter }
        if (hits.isEmpty()) {
            results.addView(
                message("Tidak ada hasil untuk \"$query\"."),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 48)
            )
            return
        }
        results.addView(
            UiKit.tv(this, "${hits.size} hasil", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, bottom = 4)
        )
        for (h in hits) {
            val card = UiKit.card(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(px(14), px(14), px(14), px(14))
                isClickable = true
                isFocusable = true
                setOnClickListener { h.open() }
            }
            card.addView(UiKit.badge(this, iconFor(h.type), Color.WHITE, UiKit.PRIMARY, 44, 12, 22))
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(UiKit.tv(this, h.title, 14.5f, UiKit.NAVY, true))
            col.addView(UiKit.tv(this, h.subtitle, 12f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3))
            card.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
            card.addView(ClassUi.chip(this, h.type))
            results.addView(card, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10))
        }
    }

    companion object {
        private const val T_SEMUA = "Semua"
        private const val T_KELAS = "Kelas"
        private const val T_MATERI = "Materi"
        private const val T_TUGAS = "Tugas"
        private const val T_KUIS = "Kuis"
    }
}
