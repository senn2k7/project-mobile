package com.example.projectmobile

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectmobile.ui.pengajar.institusi.FormatTanggal
import com.example.projectmobile.ui.pengajar.institusi.Tugas
import com.example.projectmobile.ui.pengajar.institusi.TugasViewModel
import com.example.projectmobile.ui.pengajar.institusi.TaskStatusUtils
import com.example.projectmobile.ui.pengajar.institusi.PengajarUiState
import com.example.projectmobile.ui.pengajar.institusi.TugasViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TugasActivity : AppCompatActivity() {

    private val viewModel: TugasViewModel by viewModels { TugasViewModelFactory("1") }

    private lateinit var tasksContainer: LinearLayout
    private lateinit var tabSemua: TextView
    private lateinit var tabBerlangsung: TextView
    private lateinit var tabSelesai: TextView

    companion object {
        const val EXTRA_CLASS_NAME = "extra_class_name"
        const val EXTRA_CLASS_CODE = "extra_class_code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(245, 250, 255)
        window.navigationBarColor = Color.rgb(245, 250, 255)

        val className = intent.getStringExtra(EXTRA_CLASS_NAME) ?: "Pemrograman Mobile"
        val classCode = intent.getStringExtra(EXTRA_CLASS_CODE) ?: "Kelas A • PM2025-A"

        setContentView(createScreen(className, classCode))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tugasState.collectLatest { state ->
                    when (state) {
                        is PengajarUiState.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as List<Tugas>
                            renderTasks(list)
                        }
                        is PengajarUiState.Empty -> {
                            renderTasks(emptyList())
                        }
                        else -> {
                            renderTasks(emptyList())
                        }
                    }
                }
            }
        }
    }

    private fun createScreen(className: String, classCode: String): View {
        val bgColor = Color.rgb(245, 250, 255)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

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

        // Header Top
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
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
            text = "Tugas"
            textSize = 20f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(8), 0, 0, 0)
        }
        headerLayout.addView(headerTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        contentLayout.addView(headerLayout)

        // Course Banner Card
        val courseBanner = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }
        val bannerParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        }

        val iconBox = TextView(this).apply {
            text = "💻"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        courseBanner.addView(iconBox, LinearLayout.LayoutParams(dp(44), dp(44)))

        val bannerTextCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val bannerTitle = TextView(this).apply {
            text = className
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        bannerTextCol.addView(bannerTitle)

        val bannerSub = TextView(this).apply {
            text = classCode
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        bannerTextCol.addView(bannerSub, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        })

        courseBanner.addView(bannerTextCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        contentLayout.addView(courseBanner, bannerParams)

        // Filter Tabs Row
        val tabsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(4), dp(4), dp(4))
            background = roundedBackground(Color.rgb(232, 240, 252), 12)
        }
        val tabsParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(48)).apply {
            topMargin = dp(16)
        }

        tabSemua = TextView(this).apply {
            text = "Semua"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener {
                viewModel.setFilter("SEMUA")
                updateTabStyles("SEMUA")
            }
        }
        tabsLayout.addView(tabSemua, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabBerlangsung = TextView(this).apply {
            text = "Tugas Berlangsung"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener {
                viewModel.setFilter("BERLANGSUNG")
                updateTabStyles("BERLANGSUNG")
            }
        }
        tabsLayout.addView(tabBerlangsung, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        tabSelesai = TextView(this).apply {
            text = "Tugas Selesai"
            textSize = 12f
            gravity = Gravity.CENTER
            setOnClickListener {
                viewModel.setFilter("SELESAI")
                updateTabStyles("SELESAI")
            }
        }
        tabsLayout.addView(tabSelesai, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f))

        contentLayout.addView(tabsLayout, tabsParams)
        updateTabStyles("SEMUA")

        // Tasks Container
        tasksContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        contentLayout.addView(tasksContainer, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(16)
        })

        scrollView.addView(contentLayout)
        rootLayout.addView(scrollView)
        rootLayout.addView(createBottomNav())

        return rootLayout
    }

    private fun updateTabStyles(filter: String) {
        val activeBg = roundedBackground(Color.rgb(33, 96, 245), 10)
        val inactiveBg = roundedBackground(Color.TRANSPARENT, 10)

        tabSemua.background = inactiveBg
        tabSemua.setTextColor(Color.rgb(117, 139, 169))
        tabSemua.setTypeface(null, Typeface.NORMAL)

        tabBerlangsung.background = inactiveBg
        tabBerlangsung.setTextColor(Color.rgb(117, 139, 169))
        tabBerlangsung.setTypeface(null, Typeface.NORMAL)

        tabSelesai.background = inactiveBg
        tabSelesai.setTextColor(Color.rgb(117, 139, 169))
        tabSelesai.setTypeface(null, Typeface.NORMAL)

        when (filter) {
            "SEMUA" -> {
                tabSemua.background = activeBg
                tabSemua.setTextColor(Color.WHITE)
                tabSemua.setTypeface(null, Typeface.BOLD)
            }
            "BERLANGSUNG" -> {
                tabBerlangsung.background = activeBg
                tabBerlangsung.setTextColor(Color.WHITE)
                tabBerlangsung.setTypeface(null, Typeface.BOLD)
            }
            "SELESAI" -> {
                tabSelesai.background = activeBg
                tabSelesai.setTextColor(Color.WHITE)
                tabSelesai.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    private fun renderTasks(list: List<Tugas>) {
        tasksContainer.removeAllViews()
        if (list.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "Belum ada tugas"
                textSize = 15f
                setTextColor(Color.rgb(117, 139, 169))
                gravity = Gravity.CENTER
                setPadding(0, dp(32), 0, dp(32))
            }
            tasksContainer.addView(emptyTv)
            return
        }

        for (task in list) {
            tasksContainer.addView(createTaskCard(task))
        }
    }

    private fun createTaskCard(task: Tugas): View {
        val statusText = TaskStatusUtils.hitungStatus(task.tenggat)
        val isBerlangsung = statusText == "Berlangsung"

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = roundedBackground(Color.WHITE, 16, Color.rgb(220, 231, 244), 1)
            elevation = dp(1).toFloat()
        }

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val iconBox = TextView(this).apply {
            text = "💻"
            textSize = 20f
            gravity = Gravity.CENTER
            background = roundedBackground(Color.rgb(33, 96, 245), 12)
        }
        topRow.addView(iconBox, LinearLayout.LayoutParams(dp(48), dp(48)))

        val textCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), 0, 0, 0)
        }

        val titleTv = TextView(this).apply {
            text = task.judul
            textSize = 16f
            setTextColor(Color.rgb(25, 54, 95))
            setTypeface(null, Typeface.BOLD)
        }
        textCol.addView(titleTv)

        val meetTv = TextView(this).apply {
            text = "Pertemuan ${task.pertemuanKe}"
            textSize = 13f
            setTextColor(Color.rgb(117, 139, 169))
        }
        textCol.addView(meetTv, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(2)
        })

        topRow.addView(textCol, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val optionsTv = TextView(this).apply {
            text = "•••"
            textSize = 16f
            setTextColor(Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
        }
        topRow.addView(optionsTv, LinearLayout.LayoutParams(dp(24), dp(32)))

        card.addView(topRow)

        card.setOnClickListener {
            val intent = Intent(this, DetailTugasActivity::class.java).apply {
                putExtra(DetailTugasActivity.EXTRA_TASK_TITLE, task.judul)
                putExtra(DetailTugasActivity.EXTRA_TASK_MEETING, "Pemrograman Mobile • Pertemuan ${task.pertemuanKe}")
                putExtra(DetailTugasActivity.EXTRA_TASK_STATUS, if (isBerlangsung) "Tugas Berlangsung" else "Tugas Selesai")
                putExtra(DetailTugasActivity.EXTRA_TASK_DEADLINE, FormatTanggal.formatLengkap(task.tenggat))
            }
            startActivity(intent)
        }

        // Status Badge Row
        val badge = TextView(this).apply {
            text = if (isBerlangsung) "Tugas Berlangsung" else "Tugas Selesai"
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (isBerlangsung) Color.rgb(33, 96, 245) else Color.rgb(46, 125, 50))
            background = roundedBackground(if (isBerlangsung) Color.rgb(232, 242, 255) else Color.rgb(232, 245, 233), 8)
            setPadding(dp(10), dp(4), dp(10), dp(4))
        }
        card.addView(badge, LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        })

        // Deadline & Submissions Info
        val infoRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val infoParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            topMargin = dp(12)
        }

        val deadlineTv = TextView(this).apply {
            text = "🗓️ ${FormatTanggal.formatLengkap(task.tenggat)}"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        infoRow.addView(deadlineTv, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val subTv = TextView(this).apply {
            text = "☑️ ${task.jumlahMengumpulkan}/${task.totalPelajar} mengumpulkan"
            textSize = 12f
            setTextColor(Color.rgb(117, 139, 169))
        }
        infoRow.addView(subTv)

        card.addView(infoRow, infoParams)

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(12)
            }
            addView(card)
        }
    }

    private fun createBottomNav(): View {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            background = roundedBackground(Color.WHITE, 0)
            elevation = dp(8).toFloat()
        }

        nav.addView(createNavItem("🏠", "Beranda", false), createNavParams())
        nav.addView(createNavItem("📚", "Kelas", false), createNavParams())
        nav.addView(createNavItem("📋", "Tugas", true), createNavParams())
        nav.addView(createNavItem("🤖", "AI", false), createNavParams())
        nav.addView(createNavItem("👤", "Profil", false), createNavParams())

        return nav
    }

    private fun createNavItem(icon: String, label: String, isSelected: Boolean): View {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                if (!isSelected) {
                    when (label) {
                        "Beranda" -> {
                            val intent = Intent(this@TugasActivity, com.example.projectmobile.ui.pengajar.institusi.PengajarBerandaActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                        }
                        "Kelas" -> {
                            val intent = Intent(this@TugasActivity, com.example.projectmobile.ui.pengajar.institusi.PengajarKelasSayaActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                        }
                        "Profil" -> {
                            UiKit.openProfile(this@TugasActivity, "pengajar", "institusi")
                        }
                    }
                }
            }
        }

        val iconView = TextView(this).apply {
            text = icon
            textSize = 20f
            gravity = Gravity.CENTER
        }
        item.addView(iconView)

        val textView = TextView(this).apply {
            text = label
            textSize = 11f
            setTextColor(if (isSelected) Color.rgb(33, 96, 245) else Color.rgb(117, 139, 169))
            gravity = Gravity.CENTER
            if (isSelected) setTypeface(null, Typeface.BOLD)
        }
        item.addView(textView)

        return item
    }

    private fun createNavParams(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun roundedBackground(color: Int, radiusDp: Int, strokeColor: Int = Color.TRANSPARENT, strokeWidthPx: Int = 0): GradientDrawable {
        val gd = GradientDrawable()
        gd.setColor(color)
        if (radiusDp > 0) {
            gd.cornerRadius = (radiusDp * resources.displayMetrics.density)
        }
        if (strokeWidthPx > 0) {
            gd.setStroke(strokeWidthPx, strokeColor)
        }
        return gd
    }
}
