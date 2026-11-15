package com.example.projectmobile

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** AI Asisten untuk mode mandiri (pelajar dan pengajar), memakai konteks kelas dan pertemuan. */
class AiAsistenMandiriActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private lateinit var contexts: List<AiContext>
    private var selected = 0

    private lateinit var scroll: ScrollView
    private lateinit var list: LinearLayout
    private lateinit var etMessage: EditText
    private lateinit var tvContext: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var pending: Runnable? = null

    class Msg(val fromUser: Boolean, val text: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = ModePembelajaranActivity.MODE_MANDIRI
        contexts = buildContexts()
        val lastKey = savedInstanceState?.getString(STATE_KEY) ?: lastKeyByRole[role]
        selected = contexts.indexOfFirst { it.key == lastKey }.coerceAtLeast(0)
        setContentView(build())
        renderMessages()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_KEY, current().key)
    }

    override fun onDestroy() {
        pending?.let { handler.removeCallbacks(it) }
        super.onDestroy()
    }

    private fun current(): AiContext = contexts[selected]

    private fun memoryKey(): String = "$role|${current().key}"

    private fun messages(): MutableList<Msg> = memory.getOrPut(memoryKey()) {
        mutableListOf(
            Msg(
                false,
                if (role == PilihPeranActivity.ROLE_PENGAJAR) "Halo! Aku TemanStudi. Ada yang bisa kubantu untuk kelasmu hari ini?"
                else "Halo! Aku TemanStudi. Materi apa yang ingin kamu pelajari hari ini?"
            )
        )
    }

    // ------------------------------------------------------------------ Konteks

    private fun buildContexts(): List<AiContext> {
        val out = mutableListOf<AiContext>()
        if (role == PilihPeranActivity.ROLE_PENGAJAR) {
            for (c in TeacherClassStore.all(this)) {
                out.add(AiContext("${c.code}|0", "${c.name}  •  Umum", c.code, c.name, c.topic, 0, null, "", ""))
                TeacherClassStore.materials(this, c.code).forEachIndexed { i, m ->
                    out.add(AiContext("${c.code}|${i + 1}", "${c.name}  •  Pertemuan ${i + 1}", c.code, c.name, c.topic, i + 1, m.title, m.description, m.fileName))
                }
            }
        } else {
            for (c in ClassStore.joined(this)) {
                out.add(AiContext("${c.code}|0", "${c.name}  •  Umum", c.code, c.name, c.field, 0, null, "", ""))
                for (m in ClassStore.meetings(this, c)) {
                    out.add(AiContext("${c.code}|${m.number}", "${c.name}  •  Pertemuan ${m.number}", c.code, c.name, c.field, m.number, m.title, m.description, m.fileName))
                }
            }
        }
        if (out.isEmpty()) out.add(AiContext("umum", "Umum (belum ada kelas)", null, "", "", 0, null, "", ""))
        return out
    }

    // ------------------------------------------------------------------ Tampilan

    private fun build(): View {
        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "AI Asisten") { finish() })

        // Header asisten
        val header = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(14), px(16), px(14))
        }
        header.addView(
            UiKit.tv(this, "🤖", 26f, Color.WHITE).apply {
                gravity = Gravity.CENTER
                background = UiKit.oval(UiKit.SOFT)
            },
            LinearLayout.LayoutParams(px(52), px(52))
        )
        val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(this, "TemanStudi AI Assistant", 16f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(this, "Asisten belajar pribadi kamu", 12.5f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        header.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        header.addView(
            UiKit.tv(this, "+", 28f, UiKit.PRIMARY, true).apply {
                gravity = Gravity.CENTER
                setPadding(px(8), 0, px(4), 0)
                isClickable = true
                isFocusable = true
                setOnClickListener { newChat() }
            }
        )
        root.addView(header, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, start = 20, end = 20, top = 4))

        // Konteks belajar
        val ctxCard = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(12), px(16), px(12))
            isClickable = true
            isFocusable = true
            setOnClickListener { pickContext() }
        }
        val ctxCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        ctxCol.addView(UiKit.tv(this, "Konteks Belajar", 12f, UiKit.MUTED))
        tvContext = UiKit.tv(this, current().label, 14f, UiKit.NAVY, true).apply {
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }
        ctxCol.addView(tvContext, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 2))
        ctxCard.addView(ctxCol, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        ctxCard.addView(UiKit.tv(this, "▾", 18f, UiKit.MUTED))
        root.addView(ctxCard, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, start = 20, end = 20, top = 10))

        // Catatan mode demo
        root.addView(
            UiKit.tv(
                this,
                "Mode demo: jawaban disusun dari materi dan data kelasmu, bukan dari model AI online.",
                11.5f, UiKit.MUTED
            ),
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, start = 24, end = 24, top = 8)
        )

        // Percakapan
        scroll = ScrollView(this).apply { isFillViewport = true }
        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(px(16), px(8), px(16), px(8))
        }
        scroll.addView(list, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        root.addView(scroll, LinearLayout.LayoutParams(UiKit.MATCH, 0, 1f))

        // Saran cepat
        val chips = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(px(16), px(4), px(16), px(8))
        }
        for (s in AiConfig.suggestions(role)) {
            val chip = UiKit.tv(this, s, 13f, UiKit.PRIMARY).apply {
                gravity = Gravity.CENTER
                background = UiKit.rounded(this@AiAsistenMandiriActivity, Color.WHITE, 18, UiKit.PRIMARY)
                setPadding(px(14), px(8), px(14), px(8))
                isClickable = true
                isFocusable = true
                setOnClickListener { ask(s) }
            }
            chips.addView(chip, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, end = 8))
        }
        val hs = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            addView(chips)
        }
        root.addView(hs, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))

        // Kolom pesan
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(px(16), px(10), px(16), px(6))
        }
        etMessage = EditText(this).apply {
            hint = "Tanyakan sesuatu..."
            textSize = 15f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = UiKit.rounded(this@AiAsistenMandiriActivity, UiKit.SOFT, 24)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            maxLines = 3
            setPadding(px(18), px(12), px(18), px(12))
        }
        bar.addView(etMessage, UiKit.params(this, 0, UiKit.WRAP, weight = 1f))
        bar.addView(
            UiKit.tv(this, "➤", 18f, Color.WHITE, true).apply {
                gravity = Gravity.CENTER
                background = UiKit.oval(UiKit.PRIMARY)
                isClickable = true
                isFocusable = true
                setOnClickListener { sendFromField() }
            },
            UiKit.params(this, px(48), px(48), start = 10)
        )
        val wrap = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        wrap.addView(bar, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        wrap.addView(
            UiKit.tv(this, "Periksa kembali jawaban AI untuk memastikan keakuratannya.", 11f, UiKit.MUTED)
                .apply { gravity = Gravity.CENTER; setBackgroundColor(Color.WHITE); setPadding(px(16), 0, px(16), px(10)) },
            LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP)
        )
        root.addView(wrap, LinearLayout.LayoutParams(UiKit.MATCH, UiKit.WRAP))
        return root
    }

    private fun bubble(m: Msg): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = if (m.fromUser) Gravity.END else Gravity.START
        }
        if (m.fromUser) {
            row.addView(
                UiKit.tv(this, m.text, 14.5f, Color.WHITE).apply {
                    maxWidth = px(270)
                    background = UiKit.rounded(this@AiAsistenMandiriActivity, UiKit.PRIMARY, 16)
                    setPadding(px(14), px(10), px(14), px(10))
                }
            )
        } else {
            row.addView(
                UiKit.tv(this, "🤖", 16f, Color.WHITE).apply {
                    gravity = Gravity.CENTER
                    background = UiKit.oval(UiKit.SOFT)
                },
                LinearLayout.LayoutParams(px(36), px(36))
            )
            row.addView(
                UiKit.tv(this, m.text, 14.5f, UiKit.NAVY).apply {
                    maxWidth = px(280)
                    background = UiKit.rounded(this@AiAsistenMandiriActivity, Color.WHITE, 16, UiKit.BORDER)
                    setPadding(px(14), px(10), px(14), px(10))
                },
                UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8)
            )
        }
        return row
    }

    private fun renderMessages(typing: Boolean = false) {
        list.removeAllViews()
        for (m in messages()) list.addView(bubble(m), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        if (typing) list.addView(bubble(Msg(false, "Sedang menyusun jawaban...")), UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
    }

    // ------------------------------------------------------------------ Aksi

    private fun pickContext() {
        AlertDialog.Builder(this)
            .setTitle("Konteks Belajar")
            .setItems(contexts.map { it.label }.toTypedArray()) { _, i ->
                selected = i
                lastKeyByRole[role] = current().key
                tvContext.text = current().label
                renderMessages()
            }
            .show()
    }

    private fun newChat() {
        pending?.let { handler.removeCallbacks(it) }
        memory.remove(memoryKey())
        renderMessages()
        UiKit.toast(this, "Percakapan baru dimulai")
    }

    private fun sendFromField() {
        val text = etMessage.text.toString().trim()
        if (text.isEmpty()) return
        etMessage.setText("")
        ask(text)
    }

    private fun ask(text: String) {
        if (pending != null) return
        val key = memoryKey()
        val c = current()
        messages().add(Msg(true, text))
        renderMessages(typing = true)
        val task = Runnable {
            pending = null
            val answer = AiConfig.engine.reply(this, c, role, text)
            memory.getOrPut(key) { mutableListOf() }.add(Msg(false, answer))
            if (!isFinishing && memoryKey() == key) renderMessages()
        }
        pending = task
        handler.postDelayed(task, 600L)
    }

    companion object {
        private const val STATE_KEY = "state_ai_context"
        private val memory = mutableMapOf<String, MutableList<Msg>>()
        private val lastKeyByRole = mutableMapOf<String, String>()
    }
}
