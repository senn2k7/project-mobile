package com.example.projectmobile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.Window
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** Konversi dp ke piksel. */
fun Context.px(v: Int): Int = (v * resources.displayMetrics.density).toInt()

/**
 * Kumpulan komponen tampilan yang dipakai bersama oleh halaman Profil & Pengaturan.
 * Warna dan bentuk mengikuti tema TemanStudi (biru #1D5EFF, latar #F5FAFF).
 */
object UiKit {

    val BG: Int = Color.parseColor("#F5FAFF")
    val NAVY: Int = Color.parseColor("#19365F")
    val MUTED: Int = Color.parseColor("#7186A5")
    val PRIMARY: Int = Color.parseColor("#1D5EFF")
    val BORDER: Int = Color.parseColor("#DCE7F4")
    val SOFT: Int = Color.parseColor("#E8F0FF")
    val DANGER: Int = Color.parseColor("#FF4D6A")
    val DANGER_SOFT: Int = Color.parseColor("#FFE9EC")
    val SUCCESS: Int = Color.parseColor("#1FAF6E")
    val SUCCESS_SOFT: Int = Color.parseColor("#E6F7EF")
    val DISABLED: Int = Color.parseColor("#EEF2F8")

    const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
    const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

    // ------------------------------------------------------------------
    // Navigasi bersama
    // ------------------------------------------------------------------

    fun roleOf(intent: Intent): String =
        intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR

    fun modeOf(intent: Intent): String =
        intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN)
            ?: ModePembelajaranActivity.MODE_INSTITUSI

    fun withProfile(intent: Intent, role: String, mode: String): Intent = intent.apply {
        putExtra(PilihPeranActivity.EXTRA_ROLE, role)
        putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, mode)
    }

    fun open(from: Activity, target: Class<*>, role: String, mode: String) {
        from.startActivity(withProfile(Intent(from, target), role, mode))
    }

    /** Dipanggil dari tombol "Profil" di bottom navigation semua halaman. */
    fun openProfile(from: Activity, role: String, mode: String) {
        val intent = withProfile(Intent(from, ProfilMenuActivity::class.java), role, mode).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        from.startActivity(intent)
    }

    // ------------------------------------------------------------------
    // Bentuk dan teks dasar
    // ------------------------------------------------------------------

    fun rounded(
        ctx: Context,
        color: Int,
        radiusDp: Int,
        strokeColor: Int? = null,
        strokeDp: Int = 1
    ): GradientDrawable = GradientDrawable().apply {
        setColor(color)
        cornerRadius = ctx.px(radiusDp).toFloat()
        if (strokeColor != null) setStroke(ctx.px(strokeDp), strokeColor)
    }

    fun oval(color: Int): GradientDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(color)
    }

    fun params(
        ctx: Context,
        w: Int,
        h: Int,
        top: Int = 0,
        bottom: Int = 0,
        start: Int = 0,
        end: Int = 0,
        weight: Float = 0f
    ): LinearLayout.LayoutParams = LinearLayout.LayoutParams(w, h, weight).apply {
        setMargins(ctx.px(start), ctx.px(top), ctx.px(end), ctx.px(bottom))
    }

    fun tv(
        ctx: Context,
        value: String,
        sizeSp: Float = 14f,
        color: Int = NAVY,
        bold: Boolean = false
    ): TextView = TextView(ctx).apply {
        text = value
        textSize = sizeSp
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    fun icon(ctx: Context, res: Int, sizeDp: Int, tint: Int): ImageView = ImageView(ctx).apply {
        setImageResource(res)
        setColorFilter(tint)
        layoutParams = LinearLayout.LayoutParams(ctx.px(sizeDp), ctx.px(sizeDp))
    }

    /** Kotak ikon dengan latar berwarna (dipakai di menu dan kartu). */
    fun badge(
        ctx: Context,
        iconRes: Int,
        iconTint: Int,
        bg: Int,
        boxDp: Int,
        radiusDp: Int,
        iconDp: Int = 22
    ): FrameLayout = FrameLayout(ctx).apply {
        background = rounded(ctx, bg, radiusDp)
        val ic = ImageView(ctx).apply {
            setImageResource(iconRes)
            setColorFilter(iconTint)
        }
        addView(ic, FrameLayout.LayoutParams(ctx.px(iconDp), ctx.px(iconDp), Gravity.CENTER))
        layoutParams = LinearLayout.LayoutParams(ctx.px(boxDp), ctx.px(boxDp))
    }

    fun divider(ctx: Context): View = View(ctx).apply {
        setBackgroundColor(BORDER)
        layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(1))
    }

    fun card(ctx: Context): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        background = rounded(ctx, Color.WHITE, 16, BORDER)
    }

    fun sectionBand(ctx: Context, title: String): TextView =
        tv(ctx, title, 14f, NAVY, true).apply {
            setBackgroundColor(SOFT)
            setPadding(ctx.px(12), ctx.px(10), ctx.px(12), ctx.px(10))
        }

    fun infoBox(ctx: Context, message: String): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(ctx.px(14), ctx.px(12), ctx.px(14), ctx.px(12))
        background = rounded(ctx, SOFT, 12)
        addView(icon(ctx, R.drawable.ic_info, 20, PRIMARY), LinearLayout.LayoutParams(ctx.px(20), ctx.px(20)))
        addView(tv(ctx, message, 12.5f, MUTED), params(ctx, 0, WRAP, start = 12, weight = 1f))
    }

    fun toast(ctx: Context, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
    }

    // ------------------------------------------------------------------
    // Kerangka halaman
    // ------------------------------------------------------------------

    /** Wadah utama halaman: latar #F5FAFF dan menyesuaikan status bar / keyboard. */
    fun root(activity: Activity): LinearLayout {
        activity.window.statusBarColor = BG
        activity.window.navigationBarColor = BG
        val root = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(BG)
        }
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        return root
    }

    fun topBar(ctx: Context, title: String, onBack: () -> Unit): LinearLayout =
        LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(ctx.px(20), ctx.px(16), ctx.px(20), ctx.px(8))
            val back = ImageView(ctx).apply {
                setImageResource(R.drawable.ic_back)
                contentDescription = "Kembali"
                isClickable = true
                isFocusable = true
                setOnClickListener { onBack() }
            }
            addView(back, LinearLayout.LayoutParams(ctx.px(32), ctx.px(32)))
            addView(tv(ctx, title, 20f, NAVY, true), params(ctx, WRAP, WRAP, start = 16))
        }

    /** ScrollView + isi vertikal. Kembalian: (scroll, isi). */
    fun scroll(ctx: Context, padH: Int = 20, padTop: Int = 8): Pair<ScrollView, LinearLayout> {
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(ctx.px(padH), ctx.px(padTop), ctx.px(padH), ctx.px(24))
        }
        val sv = ScrollView(ctx).apply {
            isFillViewport = true
            setBackgroundColor(BG)
            addView(content, FrameLayout.LayoutParams(MATCH, WRAP))
        }
        return Pair(sv, content)
    }

    fun scrollParams(): LinearLayout.LayoutParams = LinearLayout.LayoutParams(MATCH, 0, 1f)

    /** Area tombol yang menempel di bawah layar. */
    fun bottomBar(ctx: Context, vararg children: View): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(BG)
        setPadding(ctx.px(20), ctx.px(8), ctx.px(20), ctx.px(16))
        for (c in children) addView(c)
    }

    // ------------------------------------------------------------------
    // Tombol
    // ------------------------------------------------------------------

    fun primaryButton(ctx: Context, label: String, onClick: () -> Unit): TextView =
        tv(ctx, label, 16f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            background = rounded(ctx, PRIMARY, 14)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(56))
        }

    fun outlineButton(ctx: Context, label: String, onClick: () -> Unit): TextView =
        tv(ctx, label, 16f, PRIMARY, true).apply {
            gravity = Gravity.CENTER
            background = rounded(ctx, Color.WHITE, 14, BORDER)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(56))
        }

    fun dangerButton(ctx: Context, label: String, onClick: () -> Unit): LinearLayout =
        LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = rounded(ctx, DANGER_SOFT, 14)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            addView(icon(ctx, R.drawable.ic_logout, 22, DANGER), LinearLayout.LayoutParams(ctx.px(22), ctx.px(22)))
            addView(tv(ctx, label, 16f, DANGER, true), params(ctx, WRAP, WRAP, start = 10))
            layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(52))
        }

    // ------------------------------------------------------------------
    // Baris menu, input, dropdown, toggle
    // ------------------------------------------------------------------

    fun menuRow(
        ctx: Context,
        iconRes: Int,
        title: String,
        subtitle: String? = null,
        iconTint: Int = MUTED,
        onClick: () -> Unit
    ): LinearLayout = LinearLayout(ctx).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setPadding(ctx.px(16), ctx.px(14), ctx.px(16), ctx.px(14))
        isClickable = true
        isFocusable = true
        setOnClickListener { onClick() }
        addView(icon(ctx, iconRes, 24, iconTint), LinearLayout.LayoutParams(ctx.px(24), ctx.px(24)))
        val col = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            addView(tv(ctx, title, 15f, NAVY, true))
            if (subtitle != null) {
                addView(tv(ctx, subtitle, 12f, MUTED), params(ctx, WRAP, WRAP, top = 2))
            }
        }
        addView(col, params(ctx, 0, WRAP, start = 16, weight = 1f))
        addView(icon(ctx, R.drawable.ic_chevron_right, 20, MUTED), LinearLayout.LayoutParams(ctx.px(20), ctx.px(20)))
    }

    fun fieldLabel(ctx: Context, label: String, required: Boolean = false): TextView =
        tv(ctx, if (required) "$label *" else label, 13f, NAVY, true).apply {
            layoutParams = params(ctx, MATCH, WRAP, top = 16, bottom = 6)
        }

    fun input(
        ctx: Context,
        hint: String,
        value: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        iconRes: Int? = null
    ): EditText = EditText(ctx).apply {
        setText(value)
        this.hint = hint
        this.inputType = inputType
        setSingleLine(true)
        textSize = 15f
        setTextColor(NAVY)
        setHintTextColor(Color.parseColor("#9AABC4"))
        background = rounded(ctx, Color.WHITE, 12, BORDER)
        setPadding(ctx.px(16), 0, ctx.px(16), 0)
        if (iconRes != null) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(tintedIcon(ctx, iconRes), null, null, null)
            compoundDrawablePadding = ctx.px(12)
        }
        layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(52))
    }

    private fun tintedIcon(ctx: Context, res: Int) =
        ctx.getDrawable(res)?.mutate()?.apply { setTint(MUTED) }

    /** Kotak baca-saja (abu-abu), opsional dengan ikon di sisi kanan. */
    fun readOnlyBox(ctx: Context, value: String, startIcon: Int? = null, endIcon: Int? = null, endTint: Int = MUTED): LinearLayout =
        LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = rounded(ctx, DISABLED, 12, BORDER)
            setPadding(ctx.px(16), 0, ctx.px(16), 0)
            if (startIcon != null) {
                addView(icon(ctx, startIcon, 22, MUTED), LinearLayout.LayoutParams(ctx.px(22), ctx.px(22)))
            }
            addView(
                tv(ctx, value, 15f, MUTED),
                params(ctx, 0, WRAP, start = if (startIcon != null) 12 else 0, weight = 1f)
            )
            if (endIcon != null) {
                addView(icon(ctx, endIcon, 20, endTint), LinearLayout.LayoutParams(ctx.px(20), ctx.px(20)))
            }
            layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(52))
        }

    class DropdownHandle(val view: View, private val valueView: TextView, private val placeholder: String) {
        var selected: String = ""
            set(v) {
                field = v
                valueView.text = if (v.isBlank()) placeholder else v
                valueView.setTextColor(if (v.isBlank()) MUTED else NAVY)
            }
    }

    fun dropdown(
        ctx: Context,
        placeholder: String,
        current: String,
        options: List<String>,
        iconRes: Int? = null
    ): DropdownHandle {
        val valueView = tv(ctx, "", 15f, NAVY)
        val box = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = rounded(ctx, Color.WHITE, 12, BORDER)
            setPadding(ctx.px(16), 0, ctx.px(16), 0)
            isClickable = true
            isFocusable = true
            if (iconRes != null) {
                addView(icon(ctx, iconRes, 22, MUTED), LinearLayout.LayoutParams(ctx.px(22), ctx.px(22)))
            }
            addView(valueView, params(ctx, 0, WRAP, start = if (iconRes != null) 12 else 0, weight = 1f))
            addView(icon(ctx, R.drawable.ic_chevron_down, 22, MUTED), LinearLayout.LayoutParams(ctx.px(22), ctx.px(22)))
            layoutParams = LinearLayout.LayoutParams(MATCH, ctx.px(52))
        }
        val handle = DropdownHandle(box, valueView, placeholder)
        handle.selected = current
        box.setOnClickListener {
            AlertDialog.Builder(ctx)
                .setTitle(placeholder)
                .setItems(options.toTypedArray()) { _, i -> handle.selected = options[i] }
                .show()
        }
        return handle
    }

    fun styledSwitch(ctx: Context, checked: Boolean): SwitchCompat = SwitchCompat(ctx).apply {
        isChecked = checked
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        trackTintList = ColorStateList(states, intArrayOf(PRIMARY, Color.parseColor("#C9D4E5")))
        thumbTintList = ColorStateList(states, intArrayOf(Color.WHITE, Color.WHITE))
    }

    /** Baris dengan ikon, judul, dan saklar. */
    fun toggleRow(
        ctx: Context,
        iconRes: Int,
        title: String,
        checked: Boolean,
        onChange: (Boolean) -> Unit
    ): Pair<LinearLayout, SwitchCompat> {
        val sw = styledSwitch(ctx, checked)
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(ctx.px(4), ctx.px(10), ctx.px(4), ctx.px(10))
            addView(icon(ctx, iconRes, 22, PRIMARY), LinearLayout.LayoutParams(ctx.px(22), ctx.px(22)))
            addView(tv(ctx, title, 15f, NAVY), params(ctx, 0, WRAP, start = 14, weight = 1f))
            addView(sw, LinearLayout.LayoutParams(WRAP, WRAP))
        }
        sw.setOnCheckedChangeListener { _, isChecked -> onChange(isChecked) }
        return Pair(row, sw)
    }

    // ------------------------------------------------------------------
    // Avatar
    // ------------------------------------------------------------------

    class AvatarHandle(val view: FrameLayout, private val image: ImageView) {
        fun setPhoto(uri: Uri) {
            image.setPadding(0, 0, 0, 0)
            image.clearColorFilter()
            image.scaleType = ImageView.ScaleType.CENTER_CROP
            image.setImageURI(uri)
        }
    }

    fun avatar(ctx: Context, sizeDp: Int, withCamera: Boolean, onCamera: (() -> Unit)? = null): AvatarHandle {
        val image = ImageView(ctx).apply {
            background = oval(SOFT)
            outlineProvider = ViewOutlineProvider.BACKGROUND
            clipToOutline = true
            val pad = ctx.px((sizeDp * 0.26f).toInt())
            setPadding(pad, pad, pad, pad)
            setImageResource(R.drawable.ic_person)
            setColorFilter(PRIMARY)
            contentDescription = "Foto profil"
        }
        val frame = FrameLayout(ctx)
        frame.addView(image, FrameLayout.LayoutParams(ctx.px(sizeDp), ctx.px(sizeDp)))
        if (withCamera) {
            val camSize = (sizeDp * 0.36f).toInt().coerceAtLeast(22)
            val cam = badge(ctx, R.drawable.ic_camera, Color.WHITE, PRIMARY, camSize, camSize / 2, camSize / 2)
            cam.background = oval(PRIMARY)
            if (onCamera != null) {
                cam.isClickable = true
                cam.isFocusable = true
                cam.setOnClickListener { onCamera() }
            }
            frame.addView(
                cam,
                FrameLayout.LayoutParams(ctx.px(camSize), ctx.px(camSize), Gravity.END or Gravity.BOTTOM)
            )
        }
        frame.layoutParams = LinearLayout.LayoutParams(WRAP, WRAP)
        return AvatarHandle(frame, image)
    }

    // ------------------------------------------------------------------
    // Bottom navigation (gaya sama dengan halaman pengajar yang sudah ada)
    // ------------------------------------------------------------------

    fun bottomNav(activity: Activity, selected: String, role: String, mode: String): LinearLayout {
        val teacherOrMandiri = ProfileStore.isTeacher(role) || mode == ModePembelajaranActivity.MODE_MANDIRI
        val items = if (teacherOrMandiri) {
            listOf("🏠" to "Beranda", "📚" to "Kelas", "📋" to "Tugas", "🤖" to "AI", "👤" to "Profil")
        } else {
            listOf("🏠" to "Beranda", "📚" to "Kelas", "🤖" to "AI", "📋" to "Aktivitas", "👤" to "Profil")
        }
        return LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(activity.px(16), activity.px(8), activity.px(16), activity.px(8))
            setBackgroundColor(Color.WHITE)
            elevation = activity.px(8).toFloat()
            for ((emoji, label) in items) {
                val item = LinearLayout(activity).apply {
                    orientation = LinearLayout.VERTICAL
                    gravity = Gravity.CENTER
                    setPadding(activity.px(4), activity.px(6), activity.px(4), activity.px(6))
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { navigate(activity, label, role, mode) }
                    addView(TextView(activity).apply { text = emoji; textSize = 18f })
                    val isSel = label == selected
                    addView(
                        tv(activity, label, 11f, if (isSel) PRIMARY else MUTED, isSel),
                        params(activity, WRAP, WRAP, top = 2)
                    )
                }
                addView(item, LinearLayout.LayoutParams(0, WRAP, 1f))
            }
        }
    }

    private fun navigate(activity: Activity, label: String, role: String, mode: String) {
        when (label) {
            "Beranda" -> {
                val intent = withProfile(Intent(activity, HomeActivity::class.java), role, mode).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                activity.startActivity(intent)
            }
            "Kelas" -> activity.startActivity(
                Intent(activity, KelasSayaActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            "Tugas", "Aktivitas" -> activity.startActivity(
                Intent(activity, TugasActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
            )
            "AI" -> toast(activity, "Membuka Asisten AI TemanStudi...")
            "Profil" -> openProfile(activity, role, mode)
        }
    }

    // ------------------------------------------------------------------
    // Dialog keluar akun
    // ------------------------------------------------------------------

    fun showLogoutDialog(activity: Activity, role: String, mode: String) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val box = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            background = rounded(activity, Color.WHITE, 16)
            setPadding(activity.px(24), activity.px(28), activity.px(24), activity.px(24))
        }
        box.addView(tv(activity, "Keluar dari Akun?", 20f, NAVY, true).apply { gravity = Gravity.CENTER })
        box.addView(
            tv(activity, "Apakah Anda yakin ingin keluar dari akun ini?", 14f, MUTED).apply {
                gravity = Gravity.CENTER
            },
            params(activity, WRAP, WRAP, top = 12)
        )

        val buttons = LinearLayout(activity).apply { orientation = LinearLayout.HORIZONTAL }
        val cancel = outlineButton(activity, "Batal") { dialog.dismiss() }.apply { textSize = 15f }
        val confirm = tv(activity, "Keluar", 15f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            background = rounded(activity, DANGER, 12)
            isClickable = true
            isFocusable = true
            setOnClickListener {
                dialog.dismiss()
                open(activity, KeluarActivity::class.java, role, mode)
            }
        }
        buttons.addView(cancel, params(activity, 0, activity.px(48), end = 6, weight = 1f))
        buttons.addView(confirm, params(activity, 0, activity.px(48), start = 6, weight = 1f))
        box.addView(buttons, params(activity, MATCH, WRAP, top = 24))

        dialog.setContentView(box)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout((activity.resources.displayMetrics.widthPixels * 0.88f).toInt(), WRAP)
        }
        dialog.show()
    }
}
