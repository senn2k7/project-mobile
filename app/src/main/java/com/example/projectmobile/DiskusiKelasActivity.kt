package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity

/** Diskusi kelas mandiri, dipakai pelajar dan pengajar. Pesan sendiri tampil di kanan. */
class DiskusiKelasActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var code = ""
    private var className = ""

    private lateinit var scroll: ScrollView
    private lateinit var list: LinearLayout
    private lateinit var etMessage: EditText
    private lateinit var tvSubtitle: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        code = intent.getStringExtra(EXTRA_CODE) ?: ""
        className = ClassStore.byCode(this, code)?.name ?: ""
        if (className.isEmpty()) {
            finish()
            return
        }
        setContentView(build())
        reload()
    }

    override fun onResume() {
        super.onResume()
        if (className.isNotEmpty()) reload()
    }

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Diskusi Kelas") { finish() })

        tvSubtitle = UiKit.tv(this, className, 13f, UiKit.MUTED)
        root.addView(tvSubtitle, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, start = 24, end = 24, bottom = 8))

        scroll = ScrollView(this).apply { isFillViewport = true }
        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(16), px(8), px(16), px(16))
        }
        scroll.addView(list, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        root.addView(scroll, LinearLayout.LayoutParams(UiKit.MATCH, 0, 1f))

        // Kolom pesan
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(px(16), px(10), px(16), px(10))
        }
        etMessage = EditText(this).apply {
            hint = "Tulis pesan..."
            textSize = 15f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = UiKit.rounded(this@DiskusiKelasActivity, UiKit.SOFT, 24)
            inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            maxLines = 4
            filters = arrayOf(InputFilter.LengthFilter(DiscussionStore.MAX_LENGTH))
            setPadding(px(18), px(12), px(18), px(12))
        }
        bar.addView(etMessage, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        val send = UiKit.tv(this, "➤", 18f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            background = UiKit.oval(UiKit.PRIMARY)
            isClickable = true
            isFocusable = true
            setOnClickListener { send() }
        }
        bar.addView(send, UiKit.params(this, px(48), px(48), start = 10))
        root.addView(bar, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        return root
    }

    private fun reload() {
        val messages = DiscussionStore.messages(this, code)
        tvSubtitle.text = if (messages.isEmpty()) className else "$className  •  ${messages.size} pesan"
        list.removeAllViews()
        if (messages.isEmpty()) {
            list.addView(
                UiKit.tv(this, "Belum ada pesan. Mulai diskusi dengan menulis pesan di bawah.", 13.5f, UiKit.MUTED)
                    .apply { gravity = Gravity.CENTER },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 48)
            )
        }
        for (m in messages) {
            list.addView(bubble(m), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        }
        scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
    }

    private fun initials(name: String): String =
        name.trim().split(" ").filter { it.isNotEmpty() }.take(2)
            .joinToString("") { it.first().uppercase() }.ifEmpty { "?" }

    private fun bubble(m: DiscussionMessage): View {
        val mine = m.role == role
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (mine) Gravity.END else Gravity.START
        }
        if (mine) {
            val box = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = UiKit.rounded(this@DiskusiKelasActivity, UiKit.PRIMARY, 16)
                setPadding(px(14), px(10), px(14), px(8))
            }
            box.addView(UiKit.tv(this, m.text, 14.5f, Color.WHITE).apply { maxWidth = px(260) })
            box.addView(
                UiKit.tv(this, AttendanceStore.formatTime(m.at), 11f, Color.parseColor("#D6E4FF")).apply { gravity = Gravity.END },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4)
            )
            row.addView(box)
        } else {
            val avatar = UiKit.tv(this, initials(m.sender), 12f, UiKit.PRIMARY, true).apply {
                gravity = Gravity.CENTER
                background = UiKit.oval(UiKit.SOFT)
            }
            row.addView(avatar, LinearLayout.LayoutParams(px(36), px(36)))
            val box = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = UiKit.rounded(this@DiskusiKelasActivity, Color.WHITE, 16, UiKit.BORDER)
                setPadding(px(14), px(10), px(14), px(8))
            }
            val who = if (m.role == PilihPeranActivity.ROLE_PENGAJAR) "${m.sender}  •  Pengajar" else m.sender
            box.addView(UiKit.tv(this, who, 12.5f, UiKit.NAVY, true))
            box.addView(
                UiKit.tv(this, m.text, 14.5f, UiKit.NAVY).apply { maxWidth = px(240) },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 4)
            )
            box.addView(
                UiKit.tv(this, AttendanceStore.formatTime(m.at), 11f, UiKit.MUTED).apply { gravity = Gravity.END },
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4)
            )
            row.addView(box, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8))
        }
        return row
    }

    private fun send() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty()) return
        DiscussionStore.send(this, code, role, text)
        etMessage.setText("")
        reload()
    }

    companion object {
        const val EXTRA_CODE = "extra_diskusi_kelas_code"
    }
}
