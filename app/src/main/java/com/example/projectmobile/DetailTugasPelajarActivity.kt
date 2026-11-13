package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.OpenableColumns
import android.net.Uri
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/** Detail tugas untuk pelajar: instruksi, lampiran, jawaban, dan tombol kumpulkan. */
class DetailTugasPelajarActivity : AppCompatActivity() {

    private lateinit var role: String
    private lateinit var mode: String
    private var task: TaskInfo? = null

    private var etAnswer: EditText? = null
    private val pickedFiles = mutableListOf<String>()
    private var filesContainer: LinearLayout? = null

    private val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Hanya nama file yang dicatat; unggah ke server dihubungkan nanti.
        if (uri != null) {
            pickedFiles.add(displayName(uri))
            renderPickedFiles()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = UiKit.roleOf(intent)
        mode = UiKit.modeOf(intent)
        task = intent.getStringExtra(EXTRA_TASK_ID)?.let { TaskStore.byId(this, it) }
        if (task == null) {
            UiKit.toast(this, "Tugas tidak ditemukan")
            finish()
            return
        }
        setContentView(build(task!!))
    }

    private fun displayName(uri: Uri): String {
        var name: String? = null
        try {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) name = c.getString(0)
            }
        } catch (e: Exception) {
            // abaikan, pakai nama cadangan
        }
        return name ?: (uri.lastPathSegment ?: "Lampiran")
    }

    private fun build(t: TaskInfo): View {
        val sub = TaskStore.submission(this, t.id)
        val submitted = sub != null

        val root = UiKit.root(this)

        // Bar atas
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(20), px(16), px(20), px(8))
        }
        bar.addView(
            UiKit.icon(this, R.drawable.ic_back, 24, UiKit.NAVY).apply {
                setColorFilter(null)
                isClickable = true
                isFocusable = true
                setOnClickListener { finish() }
            },
            LinearLayout.LayoutParams(px(32), px(32))
        )
        bar.addView(UiKit.tv(this, "Detail Tugas", 20f, UiKit.NAVY, true), UiKit.params(this, 0, UiKit.WRAP, start = 16, weight = 1f))
        val more = UiKit.icon(this, R.drawable.ic_more_vert, 24, UiKit.NAVY).apply {
            isClickable = true
            isFocusable = true
        }
        more.setOnClickListener { showMenu(it, t) }
        bar.addView(more, LinearLayout.LayoutParams(px(24), px(24)))
        root.addView(bar)

        val (scroll, content) = UiKit.scroll(this)

        // Judul
        val head = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val tBox = FrameLayout(this).apply { background = UiKit.rounded(this@DetailTugasPelajarActivity, UiKit.PRIMARY, 14) }
        tBox.addView(
            UiKit.tv(this, "T", 26f, Color.WHITE).apply { gravity = Gravity.CENTER },
            FrameLayout.LayoutParams(UiKit.MATCH, UiKit.MATCH, Gravity.CENTER)
        )
        head.addView(tBox, LinearLayout.LayoutParams(px(56), px(56)))
        val titleCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titleCol.addView(UiKit.tv(this, t.title, 19f, UiKit.NAVY, true))
        titleCol.addView(UiKit.tv(this, t.className, 13f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3))
        head.addView(titleCol, UiKit.params(this, 0, UiKit.WRAP, start = 14, weight = 1f))
        content.addView(head, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4))

        // Batas pengumpulan
        val dl = UiKit.card(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(px(16), px(14), px(16), px(14))
        }
        dl.addView(UiKit.icon(this, R.drawable.ic_calendar, 26, UiKit.MUTED), LinearLayout.LayoutParams(px(26), px(26)))
        val dlCol = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        dlCol.addView(UiKit.tv(this, "Batas Pengumpulan", 12.5f, UiKit.MUTED))
        dlCol.addView(UiKit.tv(this, TaskStore.format(t.deadlineMillis), 14.5f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 3))
        dl.addView(dlCol, UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 14))
        content.addView(dl, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 16))

        // Status bila sudah dikumpulkan
        if (sub != null) {
            val banner = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(px(14), px(12), px(14), px(12))
                background = UiKit.rounded(this@DetailTugasPelajarActivity, UiKit.SUCCESS_SOFT, 12)
            }
            banner.addView(UiKit.icon(this, R.drawable.ic_check_circle, 20, UiKit.SUCCESS), LinearLayout.LayoutParams(px(20), px(20)))
            val msg = if (sub.score != null) "Dinilai: ${sub.score}" + (if (sub.feedback.isNotBlank()) "\nCatatan pengajar: ${sub.feedback}" else "") else "Dikumpulkan ${TaskStore.format(sub.submittedAt)} · menunggu penilaian"
            banner.addView(UiKit.tv(this, msg, 13f, UiKit.SUCCESS, true), UiKit.params(this, 0, UiKit.WRAP, start = 10, weight = 1f))
            content.addView(banner, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 12))
        }

        // Instruksi
        content.addView(UiKit.tv(this, "Instruksi", 17f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22))
        val steps = t.instructions.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n")
        content.addView(
            UiKit.tv(this, steps, 13.5f, UiKit.MUTED).apply { setLineSpacing(px(4).toFloat(), 1f) },
            UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8)
        )

        // Lampiran dari pengajar
        val lampHead = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        lampHead.addView(UiKit.icon(this, R.drawable.ic_attach, 18, UiKit.MUTED), LinearLayout.LayoutParams(px(18), px(18)))
        lampHead.addView(UiKit.tv(this, "Lampiran", 15f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8))
        content.addView(lampHead, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 20, bottom = 4))
        for (a in t.attachments) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = UiKit.rounded(this@DetailTugasPelajarActivity, Color.WHITE, 12, UiKit.BORDER)
                setPadding(px(10), px(10), px(14), px(10))
                isClickable = true
                isFocusable = true
                setOnClickListener { UiKit.toast(this@DetailTugasPelajarActivity, "Mengunduh ${a.name}...") }
            }
            row.addView(UiKit.badge(this, R.drawable.ic_description, Color.WHITE, a.color, 42, 10, 22))
            val col = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            col.addView(UiKit.tv(this, a.name, 13f, UiKit.NAVY, true))
            col.addView(UiKit.tv(this, a.size, 11.5f, UiKit.MUTED), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 2))
            row.addView(col, UiKit.params(this, 0, UiKit.WRAP, start = 12, weight = 1f))
            row.addView(UiKit.tv(this, a.size, 12.5f, UiKit.PRIMARY, true))
            content.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        }

        // Jawaban
        content.addView(UiKit.tv(this, "Jawaban Anda", 17f, UiKit.NAVY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, top = 22))
        val et = EditText(this).apply {
            hint = "Tulis jawaban atau penjelasan..."
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 5
            gravity = Gravity.TOP
            textSize = 14f
            setTextColor(UiKit.NAVY)
            setHintTextColor(Color.parseColor("#9AABC4"))
            background = UiKit.rounded(this@DetailTugasPelajarActivity, Color.WHITE, 12, UiKit.BORDER)
            setPadding(px(16), px(14), px(16), px(14))
            setText(sub?.answer ?: "")
            isEnabled = !submitted
        }
        etAnswer = et
        content.addView(et, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 10))

        // File jawaban
        val fc = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        filesContainer = fc
        if (sub != null) {
            pickedFiles.clear()
            pickedFiles.addAll(sub.files)
        }
        content.addView(fc, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 4))
        renderPickedFiles(editable = !submitted)

        if (!submitted) {
            val addBtn = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                background = UiKit.rounded(this@DetailTugasPelajarActivity, Color.WHITE, 12, UiKit.PRIMARY)
                isClickable = true
                isFocusable = true
                setOnClickListener { pickFile.launch("*/*") }
            }
            addBtn.addView(UiKit.icon(this, R.drawable.ic_add, 20, UiKit.PRIMARY), LinearLayout.LayoutParams(px(20), px(20)))
            addBtn.addView(UiKit.tv(this, "Lampirkan File", 15f, UiKit.PRIMARY, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 8))
            content.addView(addBtn, UiKit.params(this, UiKit.MATCH, px(52), top = 12))
        }

        root.addView(scroll, UiKit.scrollParams())

        // Tombol bawah
        val bottom = if (!submitted) {
            val btn = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                background = UiKit.rounded(this@DetailTugasPelajarActivity, UiKit.PRIMARY, 14)
                isClickable = true
                isFocusable = true
                setOnClickListener { submit(t) }
            }
            btn.addView(UiKit.icon(this, R.drawable.ic_upload, 22, Color.WHITE), LinearLayout.LayoutParams(px(22), px(22)))
            btn.addView(UiKit.tv(this, "Kumpulkan Tugas", 16f, Color.WHITE, true), UiKit.params(this, UiKit.WRAP, UiKit.WRAP, start = 10))
            btn.layoutParams = LinearLayout.LayoutParams(UiKit.MATCH, px(56))
            btn
        } else if (sub?.score == null) {
            UiKit.tv(this, "Batalkan Pengumpulan", 16f, UiKit.DANGER, true).apply {
                gravity = Gravity.CENTER
                background = UiKit.rounded(this@DetailTugasPelajarActivity, Color.WHITE, 14, UiKit.DANGER)
                isClickable = true
                isFocusable = true
                setOnClickListener { confirmCancel(t) }
                layoutParams = LinearLayout.LayoutParams(UiKit.MATCH, px(56))
            }
        } else null
        if (bottom != null) root.addView(UiKit.bottomBar(this, bottom))
        return root
    }

    private fun renderPickedFiles(editable: Boolean = true) {
        val fc = filesContainer ?: return
        fc.removeAllViews()
        pickedFiles.forEachIndexed { index, name ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = UiKit.rounded(this@DetailTugasPelajarActivity, UiKit.SOFT, 10)
                setPadding(px(12), px(10), px(12), px(10))
            }
            row.addView(UiKit.icon(this, R.drawable.ic_attach, 18, UiKit.PRIMARY), LinearLayout.LayoutParams(px(18), px(18)))
            row.addView(UiKit.tv(this, name, 13f, UiKit.NAVY), UiKit.params(this, 0, UiKit.WRAP, start = 10, weight = 1f))
            if (editable) {
                row.addView(
                    UiKit.icon(this, R.drawable.ic_close, 18, UiKit.MUTED).apply {
                        isClickable = true
                        isFocusable = true
                        setOnClickListener {
                            pickedFiles.removeAt(index)
                            renderPickedFiles()
                        }
                    },
                    LinearLayout.LayoutParams(px(18), px(18))
                )
            }
            fc.addView(row, UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8))
        }
    }

    private fun submit(t: TaskInfo) {
        val answer = etAnswer?.text?.toString()?.trim().orEmpty()
        if (answer.isEmpty() && pickedFiles.isEmpty()) {
            etAnswer?.error = "Tulis jawaban atau lampirkan file"
            etAnswer?.requestFocus()
            return
        }
        val at = TaskStore.submit(this, t.id, answer, pickedFiles.toList())
        val next = UiKit.withProfile(Intent(this, TugasBerhasilActivity::class.java), role, mode).apply {
            putExtra(TugasBerhasilActivity.EXTRA_TITLE, t.title)
            putExtra(TugasBerhasilActivity.EXTRA_TIME, at)
        }
        startActivity(next)
        finish()
    }

    private fun confirmCancel(t: TaskInfo) {
        AlertDialog.Builder(this)
            .setTitle("Batalkan pengumpulan?")
            .setMessage("Jawaban yang sudah dikumpulkan akan dibuka kembali dan kamu bisa mengumpulkannya lagi.")
            .setNegativeButton("Tidak", null)
            .setPositiveButton("Batalkan") { _, _ ->
                TaskStore.cancel(this, t.id)
                pickedFiles.clear()
                UiKit.toast(this, "Pengumpulan dibatalkan")
                setContentView(build(t))
            }
            .show()
    }

    private fun showMenu(anchor: View, t: TaskInfo) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 1, 0, "Buka kelas")
        popup.setOnMenuItemClickListener {
            val detail = UiKit.withProfile(Intent(this, DetailKelasPelajarActivity::class.java), role, mode)
            detail.putExtra(DetailKelasPelajarActivity.EXTRA_CODE, t.classCode)
            startActivity(detail)
            true
        }
        popup.show()
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_tugas_task_id"
    }
}
