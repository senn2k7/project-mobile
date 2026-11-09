package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AiAssistantActivity : AppCompatActivity() {

    private var isAttachmentPopupVisible = false
    private lateinit var attachmentPopupContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        setContentView(createScreen())
    }

    private fun createScreen(): View {
        val bgColor = Color.rgb(245, 250, 255)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // 1. Header Top (Back button, Title "Kelola AI Asisten")
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(bgColor, 0)
            elevation = dp(2).toFloat()
        }

        val backButton = TextView(this).apply {
            text = "‹"
            textSize = 36f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
            includeFontPadding = false
            setOnClickListener { finish() }
        }
        headerLayout.addView(backButton, LinearLayout.LayoutParams(dp(32), dp(44)))

        val headerTitle = TextView(this).apply {
            text = "Kelola AI Asisten"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(12), 0, 0, 0)
        }
        headerLayout.addView(headerTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        rootLayout.addView(headerLayout)

        // 2. Main Scrollable Chat & Banner Area
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
            clipToPadding = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(24))
            setBackgroundColor(bgColor)
        }

        // Assistant Banner Card
        val bannerCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val botIconBox = TextView(this).apply {
            text = "🤖"
            textSize = 24f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(232, 242, 255), 24)
        }
        bannerCard.addView(botIconBox, LinearLayout.LayoutParams(dp(52), dp(52)))

        val bannerTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }
        val bannerTitle = TextView(this).apply {
            text = "TemanStudi AI Assistant"
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        bannerTextCol.addView(bannerTitle)

        val bannerSub = TextView(this).apply {
            text = "Asisten belajar pribadi kamu"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val bsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        bannerTextCol.addView(bannerSub, bsParams)

        bannerCard.addView(bannerTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val plusBtn = TextView(this).apply {
            text = "+"
            textSize = 20f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        bannerCard.addView(plusBtn, LinearLayout.LayoutParams(dp(32), dp(32)))

        contentLayout.addView(bannerCard)

        // Context Selector Card ("Konteks Belajar")
        val contextCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val ccParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val docIcon = TextView(this).apply {
            text = "📄"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(33, 96, 245))
        }
        contextCard.addView(docIcon)

        val contextTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }
        val contextLabel = TextView(this).apply {
            text = "Konteks Belajar"
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
        }
        contextTextCol.addView(contextLabel)

        val contextVal = TextView(this).apply {
            text = "Pemrograman Mobile — Pertemuan 1"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val cvParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        contextTextCol.addView(contextVal, cvParams)

        contextCard.addView(contextTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val arrowDown = TextView(this).apply {
            text = "▾"
            textSize = 16f
            setTextColor(Color.rgb(33, 96, 245))
            gravity = Gravity.CENTER
        }
        contextCard.addView(arrowDown, LinearLayout.LayoutParams(dp(24), dp(24)))

        contentLayout.addView(contextCard, ccParams)

        // Chat Message Container & Bot Greeting
        val chatContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
        }
        val chatParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }

        val botAvatar = TextView(this).apply {
            text = "🤖"
            textSize = 18f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 20)
        }
        chatContainer.addView(botAvatar, LinearLayout.LayoutParams(dp(40), dp(40)))

        val bubble = TextView(this).apply {
            text = "Halo! Aku TemanStudi. Materi apa yang ingin kamu pelajari hari ini?"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setLineSpacing(dp(3).toFloat(), 1f)
        }
        val bubbleParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(12)
        }
        chatContainer.addView(bubble, bubbleParams)

        contentLayout.addView(chatContainer, chatParams)

        // Suggestion Chips Container
        val chipsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(52), dp(12), 0, 0) // indented to align with message bubble
        }

        chipsContainer.addView(createSuggestionChip("Jelaskan materi dengan sederhana"))
        chipsContainer.addView(createSuggestionChip("Buatkan rangkuman"))
        chipsContainer.addView(createSuggestionChip("Berikan contoh soal"))
        chipsContainer.addView(createSuggestionChip("Bantu aku memahami kode program"))

        contentLayout.addView(chipsContainer)

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)

        // 3. Attachment Popup Menu Container (Above input bar)
        attachmentPopupContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 20, Color.rgb(220, 231, 244), 1)
            elevation = dp(6).toFloat()
            visibility = View.GONE
        }

        // Options row (Foto, Video, Dokumen, Ambil Foto)
        val popupOptionsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        popupOptionsRow.addView(createPopupOption("Foto", "🖼️", Color.rgb(232, 242, 255), Color.rgb(33, 96, 245)), createPopupOptionParams())
        popupOptionsRow.addView(createPopupOption("Video", "📹", Color.rgb(243, 229, 245), Color.rgb(156, 39, 176)), createPopupOptionParams())
        popupOptionsRow.addView(createPopupOption("Dokumen", "📄", Color.rgb(232, 245, 233), Color.rgb(46, 125, 50)), createPopupOptionParams())
        popupOptionsRow.addView(createPopupOption("Ambil Foto", "📷", Color.rgb(255, 243, 224), Color.rgb(255, 152, 0)), createPopupOptionParams())

        attachmentPopupContainer.addView(popupOptionsRow)

        // Divider
        val div = View(this).apply {
            background = roundedBackground(Color.rgb(230, 238, 248), 1)
        }
        val divParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(1)).apply {
            topMargin = dp(14)
            bottomMargin = dp(10)
        }
        attachmentPopupContainer.addView(div, divParams)

        // "Pilih dari perangkat" row
        val deviceRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@AiAssistantActivity, "Memilih dari perangkat...", Toast.LENGTH_SHORT).show()
                toggleAttachmentPopup()
            }
        }
        val folderIcon = TextView(this).apply {
            text = "📁"
            textSize = 20f
        }
        deviceRow.addView(folderIcon)

        val deviceText = TextView(this).apply {
            text = "Pilih dari perangkat"
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
            setPadding(dp(12), 0, 0, 0)
        }
        deviceRow.addView(deviceText)
        attachmentPopupContainer.addView(deviceRow)

        val popupWrapperParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            marginStart = dp(16)
            marginEnd = dp(16)
            bottomMargin = dp(8)
        }
        rootLayout.addView(attachmentPopupContainer, popupWrapperParams)

        // 4. Bottom Message Input Bar & Footer
        val bottomBarContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(12))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        val inputBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = roundedBackground(Color.WHITE, 24, Color.rgb(220, 231, 244), 1)
        }

        val attachIcon = TextView(this).apply {
            text = "📎"
            textSize = 20f
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setOnClickListener {
                toggleAttachmentPopup()
            }
        }
        inputBar.addView(attachIcon, LinearLayout.LayoutParams(dp(40), dp(40)))

        val etMessage = EditText(this).apply {
            hint = "Tanyakan sesuatu..."
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = null
            setPadding(dp(8), 0, dp(8), 0)
            maxLines = 3
        }
        val etParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dp(8)
            marginEnd = dp(8)
        }
        inputBar.addView(etMessage, etParams)

        val sendButton = TextView(this).apply {
            text = "↗"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            background = roundedBackground(Color.rgb(29, 94, 255), 22)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@AiAssistantActivity, "Mengirim pesan ke AI...", Toast.LENGTH_SHORT).show()
            }
        }
        inputBar.addView(sendButton, LinearLayout.LayoutParams(dp(40), dp(40)))

        bottomBarContainer.addView(inputBar)

        val footerTv = TextView(this).apply {
            text = "Periksa kembali jawaban AI untuk memastikan keakuratannya."
            textSize = 11f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        val fParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        bottomBarContainer.addView(footerTv, fParams)

        rootLayout.addView(bottomBarContainer)

        return rootLayout
    }

    private fun toggleAttachmentPopup() {
        isAttachmentPopupVisible = !isAttachmentPopupVisible
        attachmentPopupContainer.visibility = if (isAttachmentPopupVisible) View.VISIBLE else View.GONE
    }

    private fun createSuggestionChip(chipText: String): View {
        val chip = TextView(this).apply {
            text = chipText
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            background = roundedBackground(Color.WHITE, 18, Color.rgb(179, 211, 255), 1)
            setPadding(dp(16), dp(10), dp(16), dp(10))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@AiAssistantActivity, "Memilih: $chipText", Toast.LENGTH_SHORT).show()
            }
        }
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        return chip.apply { layoutParams = params }
    }

    private fun createPopupOption(label: String, emoji: String, bgColor: Int, textColor: Int): View {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Toast.makeText(this@AiAssistantActivity, "Memilih: $label", Toast.LENGTH_SHORT).show()
                toggleAttachmentPopup()
            }
        }

        val circle = TextView(this).apply {
            text = emoji
            textSize = 22f
            gravity = Gravity.CENTER
            background = roundedBackground(bgColor, 28)
        }
        col.addView(circle, LinearLayout.LayoutParams(dp(56), dp(56)))

        val tv = TextView(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
        }
        val tParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        col.addView(tv, tParams)

        return col
    }

    private fun createPopupOptionParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }

    private fun roundedBackground(color: Int, radiusDp: Int, strokeColor: Int? = null, strokeWidthDp: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
            if (strokeColor != null) {
                setStroke(dp(strokeWidthDp), strokeColor)
            }
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
