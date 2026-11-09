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

class LengkapiProfilPengajarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val mode = intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_MANDIRI
        val role = intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PENGAJAR

        setContentView(createFormLayout(mode, role))
    }

    private fun createFormLayout(mode: String, role: String): View {
        val bgColor = Color.rgb(245, 250, 255)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
            clipToPadding = false
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(32))
            setBackgroundColor(bgColor)
        }

        // Top bar with back button
        val backButton = TextView(this).apply {
            text = "←"
            textSize = 28f
            setTextColor(Color.rgb(25, 54, 95))
            includeFontPadding = false
            setOnClickListener { finish() }
        }
        root.addView(backButton, LinearLayout.LayoutParams(dp(40), dp(44)))

        // Title
        val titleTv = TextView(this).apply {
            text = "Lengkapi Profil Pengajar"
            textSize = 24f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val titleParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }
        root.addView(titleTv, titleParams)

        // Subtitle
        val subTv = TextView(this).apply {
            text = "Isi data diri dan informasi pengajaran untuk memulai di Temanstudi."
            textSize = 14f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val subParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(6)
        }
        root.addView(subTv, subParams)

        // Form Fields
        addInputField(root, "Nama Lengkap", "Nama lengkap sesuai identitas", "👤")
        addInputField(root, "Email", "nama@email.com", "✉️")
        addInputField(root, "Nomor Telepon (Opsional)", "0812 3456 7890", "📞")
        addInputField(root, "Bidang Keahlian", "Pilih bidang keahlian", "📚", hasDropdown = true)
        addInputField(root, "Nama Instansi (Opsional)", "Nama sekolah, kampus, atau lembaga", "🏛️")
        addInputField(root, "Pengalaman Mengajar (Opsional)", "Pilih pengalaman mengajar", "💼", hasDropdown = true)

        // Foto Profil (Opsional)
        val fotoLabel = TextView(this).apply {
            text = "Foto Profil (Opsional)"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val fotoLabelParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(18)
        }
        root.addView(fotoLabel, fotoLabelParams)

        val uploadBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(24), dp(16), dp(24))
            background = dashedBackground(Color.WHITE, 12, Color.rgb(117, 139, 169))
        }

        val uploadIcon = TextView(this).apply {
            text = "👤➕"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        uploadBox.addView(uploadIcon)

        val uploadTitle = TextView(this).apply {
            text = "Pilih atau unggah foto profil"
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val upTitleParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        uploadBox.addView(uploadTitle, upTitleParams)

        val uploadSub = TextView(this).apply {
            text = "Format: JPG, PNG. Maksimal 2 MB."
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        val upSubParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        }
        uploadBox.addView(uploadSub, upSubParams)

        val uploadParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(8)
        }
        root.addView(uploadBox, uploadParams)

        // Info box
        val infoBox = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = roundedBackground(Color.rgb(232, 242, 255), 12)
        }
        val infoIcon = TextView(this).apply {
            text = "ⓘ"
            textSize = 16f
            setTextColor(Color.rgb(33, 96, 245))
        }
        infoBox.addView(infoIcon)

        val infoText = TextView(this).apply {
            text = "Pastikan informasi pengajaran yang kamu masukkan benar."
            textSize = 13f
            setTextColor(Color.rgb(33, 96, 245))
            setPadding(dp(10), 0, 0, 0)
        }
        infoBox.addView(infoText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val infoParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(20)
        }
        root.addView(infoBox, infoParams)

        // Submit Button (Simpan dan Lanjutkan)
        val btnSimpan = TextView(this).apply {
            text = "Simpan dan Lanjutkan"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(29, 94, 255), 18)
            setPadding(dp(16), dp(16), dp(16), dp(16))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val intent = Intent(this@LengkapiProfilPengajarActivity, HomeActivity::class.java).apply {
                    putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, mode)
                    putExtra(PilihPeranActivity.EXTRA_ROLE, role)
                }
                startActivity(intent)
                finish()
            }
        }
        val btnParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(24)
        }
        root.addView(btnSimpan, btnParams)

        scrollView.addView(root)
        return scrollView
    }

    private fun addInputField(parent: LinearLayout, label: String, hint: String, icon: String, hasDropdown: Boolean = false) {
        val labelTv = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        val labelParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }
        parent.addView(labelTv, labelParams)

        val inputContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(4), dp(12), dp(4))
            background = roundedBackground(Color.WHITE, 12, Color.rgb(220, 231, 244), 1)
        }

        val iconTv = TextView(this).apply {
            text = icon
            textSize = 16f
        }
        inputContainer.addView(iconTv)

        val editText = EditText(this).apply {
            this.hint = hint
            textSize = 14f
            setTextColor(Color.rgb(25, 54, 95))
            setHintTextColor(Color.rgb(117, 139, 169))
            background = null
            setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        inputContainer.addView(editText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        if (hasDropdown) {
            val arrowTv = TextView(this).apply {
                text = "⌄"
                textSize = 18f
                setTextColor(Color.rgb(117, 139, 169))
            }
            inputContainer.addView(arrowTv)
        }

        val containerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(52)).apply {
            topMargin = dp(8)
        }
        parent.addView(inputContainer, containerParams)
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

    private fun dashedBackground(color: Int, radiusDp: Int, strokeColor: Int): GradientDrawable {
        val density = resources.displayMetrics.density
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radiusDp).toFloat()
            setStroke(dp(1), strokeColor, 8f * density, 4f * density)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toFloat().toInt()
}
// Note: no fields are strictly required to block navigation now so user can proceed seamlessly to dashboard as requested.
