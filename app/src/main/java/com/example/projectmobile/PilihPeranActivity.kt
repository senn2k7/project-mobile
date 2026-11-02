package com.example.snapchatsphlasscreen

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.drawable.GradientDrawable

class PilihPeranActivity : AppCompatActivity() {

    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val modePembelajaran = intent.getStringExtra(
            ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN
        ) ?: ModePembelajaranActivity.MODE_INSTITUSI

        setContentView(createScreen(modePembelajaran))
    }

    private fun createScreen(modePembelajaran: String): View {
        val bgColor = Color.rgb(245, 250, 255)

        val scrollView = ScrollView(this).apply {
            setBackgroundColor(bgColor)
            isFillViewport = true
            clipToPadding = false
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(28))
            setBackgroundColor(bgColor)
        }

        // Header: tombol kembali dan judul halaman
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val backButton = TextView(this).apply {
            text = "‹"
            textSize = 43f
            setTextColor(Color.rgb(25, 54, 95))
            gravity = Gravity.CENTER
            includeFontPadding = false
            contentDescription = "Kembali"
            setOnClickListener { finish() }
        }

        header.addView(
            backButton,
            LinearLayout.LayoutParams(dp(34), dp(48))
        )

        val headerTitle = TextView(this).apply {
            text = "Pilih Peran"
            textSize = 18f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), 0, 0, 0)
        }

        header.addView(
            headerTitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(48)
            )
        )

        root.addView(header)

        // Judul dan deskripsi utama
        val heading = TextView(this).apply {
            text = "Pilih peran Anda"
            textSize = 26f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, android.graphics.Typeface.BOLD)
            includeFontPadding = false
        }

        val headingParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(55)
        }
        root.addView(heading, headingParams)

        val description = TextView(this).apply {
            text = "Pilih peran yang sesuai untuk memulai pengalaman pembelajaran Anda."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setLineSpacing(dp(3).toFloat(), 1f)
        }

        val descriptionParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(12)
        }
        root.addView(description, descriptionParams)

        // Kartu Pengajar
        val teacherCard = createRoleCard(
            title = "Pengajar",
            description = "Kelola kelas, bagikan materi, buat tugas dan kuis, serta pantau hasil belajar pelajar.",
            role = ROLE_PENGAJAR,
            illustrationType = RoleIllustrationView.TYPE_TEACHER
        )

        val teacherParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(142)
        ).apply {
            topMargin = dp(32)
        }
        root.addView(teacherCard, teacherParams)

        // Kartu Pelajar
        val studentCard = createRoleCard(
            title = "Pelajar",
            description = "Gabung ke kelas, pelajari materi, kerjakan tugas dan kuis, serta diskusikan pelajaran.",
            role = ROLE_PELAJAR,
            illustrationType = RoleIllustrationView.TYPE_STUDENT
        )

        val studentParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(142)
        ).apply {
            topMargin = dp(12)
        }
        root.addView(studentCard, studentParams)

        // Informasi pilihan peran
        val infoContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(
                Color.rgb(232, 242, 255),
                12
            )
        }

        val infoIcon = TextView(this).apply {
            text = "ⓘ"
            textSize = 23f
            setTextColor(Color.rgb(33, 96, 245))
            gravity = Gravity.CENTER
        }

        infoContainer.addView(
            infoIcon,
            LinearLayout.LayoutParams(dp(24), dp(32))
        )

        val infoText = TextView(this).apply {
            text = "Pilihan peran digunakan untuk menyesuaikan fitur dan beranda aplikasi."
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setLineSpacing(dp(3).toFloat(), 1f)
            setPadding(dp(12), 0, 0, 0)
        }

        infoContainer.addView(
            infoText,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val infoParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(70)
        ).apply {
            topMargin = dp(24)
        }
        root.addView(infoContainer, infoParams)

        scrollView.addView(root)
        return scrollView
    }

    private fun createRoleCard(
        title: String,
        description: String,
        role: String,
        illustrationType: Int
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(11), dp(12), dp(12), dp(12))
            background = roundedBackground(
                Color.WHITE,
                11,
                Color.rgb(220, 231, 244),
                1
            )
            isClickable = true
            isFocusable = true
            elevation = dp(1).toFloat()
            contentDescription = "Pilih $title"
        }

        val illustration = RoleIllustrationView(this, illustrationType)

        card.addView(
            illustration,
            LinearLayout.LayoutParams(dp(80), dp(84))
        )

        val textColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), 0, dp(4), 0)
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 17f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, android.graphics.Typeface.BOLD)
            includeFontPadding = false
        }

        textColumn.addView(titleView)

        val descriptionView = TextView(this).apply {
            text = description
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
            setLineSpacing(dp(3).toFloat(), 1f)
        }

        val descriptionParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(8)
        }

        textColumn.addView(descriptionView, descriptionParams)

        card.addView(
            textColumn,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
        )

        val arrow = TextView(this).apply {
            text = "›"
            textSize = 36f
            setTextColor(Color.rgb(33, 96, 245))
            gravity = Gravity.CENTER
            includeFontPadding = false
        }

        card.addView(
            arrow,
            LinearLayout.LayoutParams(dp(20), dp(48))
        )

        card.setOnClickListener {
            selectedRole = role
            val mode = intent.getStringExtra(
                ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN
            ) ?: ModePembelajaranActivity.MODE_INSTITUSI

            if (mode == ModePembelajaranActivity.MODE_MANDIRI) {
                val targetActivity = if (role == ROLE_PENGAJAR) {
                    LengkapiProfilPengajarActivity::class.java
                } else {
                    LengkapiProfilPelajarActivity::class.java
                }
                val next = Intent(this, targetActivity).apply {
                    putExtra(
                        ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN,
                        mode
                    )
                    putExtra(EXTRA_ROLE, role)
                }
                startActivity(next)
                finish()
            } else {
                openHome(role)
            }
        }

        return card
    }

    private fun openHome(role: String) {
        val mode = intent.getStringExtra(
            ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN
        ) ?: ModePembelajaranActivity.MODE_INSTITUSI

        val next = Intent(this, HomeActivity::class.java).apply {
            putExtra(
                ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN,
                mode
            )
            putExtra(EXTRA_ROLE, role)
        }

        startActivity(next)
        finish()
    }

    private fun roundedBackground(
        color: Int,
        radiusDp: Int,
        strokeColor: Int? = null,
        strokeWidthDp: Int = 0
    ): GradientDrawable {
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

    companion object {
        const val EXTRA_ROLE = "extra_user_role"
        const val ROLE_PENGAJAR = "pengajar"
        const val ROLE_PELAJAR = "pelajar"
    }
}

/**
 * Ilustrasi sederhana yang digambar langsung menggunakan Canvas.
 * Tidak memerlukan file gambar tambahan.
 */
private class RoleIllustrationView(
    context: android.content.Context,
    private val illustrationType: Int
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        // Latar ilustrasi
        paint.color = Color.rgb(232, 242, 255)
        canvas.drawRoundRect(
            RectF(0f, 0f, w, h),
            15f,
            15f,
            paint
        )

        val sx = w / 100f
        val sy = h / 100f

        canvas.save()
        canvas.scale(sx, sy)

        if (illustrationType == TYPE_TEACHER) {
            drawTeacher(canvas)
        } else {
            drawStudent(canvas)
        }

        canvas.restore()
    }

    private fun drawTeacher(canvas: Canvas) {
        // Papan tulis
        paint.color = Color.rgb(22, 140, 245)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        canvas.drawRoundRect(RectF(53f, 19f, 88f, 73f), 4f, 4f, paint)
        canvas.drawLine(59f, 30f, 80f, 30f, paint)
        canvas.drawLine(59f, 40f, 75f, 40f, paint)
        canvas.drawLine(59f, 50f, 82f, 50f, paint)
        paint.style = Paint.Style.FILL

        // Rambut
        paint.color = Color.rgb(25, 65, 120)
        canvas.drawCircle(43f, 31f, 18f, paint)
        canvas.drawCircle(31f, 28f, 11f, paint)
        canvas.drawCircle(53f, 27f, 12f, paint)

        // Telinga dan wajah
        paint.color = Color.rgb(255, 190, 115)
        canvas.drawCircle(27f, 49f, 5f, paint)
        canvas.drawCircle(59f, 49f, 5f, paint)
        canvas.drawOval(RectF(28f, 27f, 59f, 67f), paint)

        // Rambut bagian atas
        paint.color = Color.rgb(25, 65, 120)
        canvas.drawCircle(33f, 25f, 10f, paint)
        canvas.drawCircle(47f, 23f, 12f, paint)

        // Mata
        paint.color = Color.rgb(25, 54, 95)
        canvas.drawCircle(37f, 46f, 1.7f, paint)
        canvas.drawCircle(51f, 46f, 1.7f, paint)

        // Senyum
        paint.color = Color.rgb(25, 54, 95)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawArc(RectF(38f, 48f, 51f, 59f), 10f, 155f, false, paint)
        paint.style = Paint.Style.FILL

        // Badan
        paint.color = Color.rgb(22, 140, 245)
        val body = Path().apply {
            moveTo(31f, 66f)
            lineTo(19f, 76f)
            lineTo(16f, 100f)
            lineTo(73f, 100f)
            lineTo(70f, 76f)
            lineTo(57f, 66f)
            close()
        }
        canvas.drawPath(body, paint)

        // Kerah kemeja
        paint.color = Color.WHITE
        val collar = Path().apply {
            moveTo(34f, 67f)
            lineTo(44f, 75f)
            lineTo(39f, 83f)
            lineTo(29f, 70f)
            close()
        }
        canvas.drawPath(collar, paint)

        val collarRight = Path().apply {
            moveTo(54f, 67f)
            lineTo(44f, 75f)
            lineTo(50f, 83f)
            lineTo(60f, 70f)
            close()
        }
        canvas.drawPath(collarRight, paint)

        // Dasi
        paint.color = Color.rgb(33, 96, 245)
        val tie = Path().apply {
            moveTo(42f, 76f)
            lineTo(47f, 76f)
            lineTo(49f, 91f)
            lineTo(44f, 96f)
            lineTo(40f, 91f)
            close()
        }
        canvas.drawPath(tie, paint)

        // Lengan menunjuk
        paint.color = Color.rgb(255, 190, 115)
        paint.strokeWidth = 9f
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(65f, 78f, 83f, 63f, paint)
        canvas.drawLine(83f, 63f, 91f, 45f, paint)
        canvas.drawCircle(92f, 44f, 5f, paint)
    }

    private fun drawStudent(canvas: Canvas) {
        // Rambut belakang
        paint.color = Color.rgb(25, 65, 120)
        canvas.drawCircle(48f, 31f, 19f, paint)

        // Wajah
        paint.color = Color.rgb(255, 190, 115)
        canvas.drawCircle(29f, 49f, 5f, paint)
        canvas.drawCircle(67f, 49f, 5f, paint)
        canvas.drawOval(RectF(29f, 28f, 67f, 70f), paint)

        // Rambut atas
        paint.color = Color.rgb(25, 65, 120)
        canvas.drawCircle(37f, 25f, 12f, paint)
        canvas.drawCircle(49f, 22f, 13f, paint)
        canvas.drawCircle(61f, 26f, 11f, paint)

        // Mata
        paint.color = Color.rgb(25, 54, 95)
        canvas.drawCircle(40f, 48f, 1.8f, paint)
        canvas.drawCircle(56f, 48f, 1.8f, paint)

        // Senyum
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        canvas.drawArc(RectF(41f, 50f, 56f, 62f), 10f, 155f, false, paint)
        paint.style = Paint.Style.FILL

        // Leher
        paint.color = Color.rgb(255, 190, 115)
        canvas.drawRect(42f, 66f, 54f, 76f, paint)

        // Badan
        paint.color = Color.rgb(22, 140, 245)
        canvas.drawRoundRect(RectF(22f, 72f, 77f, 100f), 8f, 8f, paint)

        // Buku terbuka
        paint.color = Color.WHITE
        val leftPage = Path().apply {
            moveTo(22f, 70f)
            quadTo(39f, 66f, 49f, 77f)
            lineTo(49f, 96f)
            quadTo(35f, 87f, 22f, 91f)
            close()
        }
        canvas.drawPath(leftPage, paint)

        val rightPage = Path().apply {
            moveTo(49f, 77f)
            quadTo(62f, 66f, 78f, 70f)
            lineTo(78f, 91f)
            quadTo(63f, 87f, 49f, 96f)
            close()
        }
        canvas.drawPath(rightPage, paint)

        // Garis halaman buku
        paint.color = Color.rgb(33, 96, 245)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.8f
        canvas.drawLine(49f, 78f, 49f, 96f, paint)
        canvas.drawLine(28f, 77f, 42f, 80f, paint)
        canvas.drawLine(56f, 80f, 72f, 76f, paint)
        canvas.drawLine(28f, 85f, 42f, 87f, paint)
        canvas.drawLine(56f, 87f, 72f, 84f, paint)
        paint.style = Paint.Style.FILL

        // Tangan memegang buku
        paint.color = Color.rgb(255, 190, 115)
        canvas.drawRoundRect(RectF(17f, 76f, 25f, 92f), 4f, 4f, paint)
        canvas.drawRoundRect(RectF(75f, 76f, 83f, 92f), 4f, 4f, paint)
    }

    companion object {
        const val TYPE_TEACHER = 1
        const val TYPE_STUDENT = 2
    }
}
