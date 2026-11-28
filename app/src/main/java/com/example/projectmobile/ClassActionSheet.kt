package com.example.projectmobile

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/** Bottom sheet aksi kelas: judul, lalu beberapa baris aksi besar dengan ikon, label, dan panah. */
object ClassActionSheet {

    data class Row(
        val iconRes: Int,
        val label: String,
        val trailing: String? = null,
        val danger: Boolean = false,
        val checked: Boolean = false,
        val onClick: () -> Unit
    )

    fun show(activity: Activity, title: String, rows: List<Row>) {
        val navy = UiKit.NAVY
        val muted = UiKit.MUTED
        val blue = UiKit.PRIMARY
        val dialog = BottomSheetDialog(activity)

        val sheet = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(activity.px(20), activity.px(10), activity.px(20), activity.px(24))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                val r = activity.px(24).toFloat()
                cornerRadii = floatArrayOf(r, r, r, r, 0f, 0f, 0f, 0f)
            }
        }

        // Pegangan kecil
        sheet.addView(
            View(activity).apply { background = UiKit.rounded(activity, Color.rgb(205, 214, 228), 2) },
            LinearLayout.LayoutParams(activity.px(40), activity.px(4)).apply { gravity = Gravity.CENTER_HORIZONTAL }
        )

        sheet.addView(
            TextView(activity).apply {
                text = title
                textSize = 18f
                setTextColor(navy)
                setTypeface(null, Typeface.BOLD)
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            },
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = activity.px(18)
                bottomMargin = activity.px(8)
            }
        )

        rows.forEachIndexed { index, r ->
            val row = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(activity.px(4), activity.px(12), activity.px(4), activity.px(12))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    dialog.dismiss()
                    r.onClick()
                }
                foreground = RippleDrawable(
                    ColorStateList.valueOf(Color.argb(30, 29, 94, 255)),
                    null,
                    GradientDrawable().apply {
                        setColor(Color.WHITE)
                        cornerRadius = activity.px(12).toFloat()
                    }
                )
            }
            val iconBox = FrameLayout(activity).apply {
                background = UiKit.rounded(activity, if (r.danger) Color.rgb(255, 236, 238) else Color.rgb(232, 242, 255), 12)
                addView(
                    ImageView(activity).apply {
                        setImageResource(r.iconRes)
                        setColorFilter(if (r.danger) UiKit.DANGER else blue)
                    },
                    FrameLayout.LayoutParams(activity.px(20), activity.px(20), Gravity.CENTER)
                )
            }
            row.addView(iconBox, LinearLayout.LayoutParams(activity.px(44), activity.px(44)))
            row.addView(
                TextView(activity).apply {
                    text = r.label
                    textSize = 16f
                    setTextColor(if (r.danger) UiKit.DANGER else navy)
                    if (r.checked) setTypeface(null, Typeface.BOLD)
                    setPadding(activity.px(14), 0, 0, 0)
                },
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
            if (r.trailing != null) {
                row.addView(
                    TextView(activity).apply {
                        text = r.trailing
                        textSize = 14f
                        setTextColor(muted)
                        setPadding(0, 0, activity.px(6), 0)
                    }
                )
            }
            row.addView(
                ImageView(activity).apply {
                    setImageResource(if (r.checked) R.drawable.ic_check else R.drawable.ic_chevron_right)
                    setColorFilter(if (r.checked) blue else muted)
                },
                LinearLayout.LayoutParams(activity.px(20), activity.px(20))
            )
            sheet.addView(row)
            if (index < rows.lastIndex) {
                sheet.addView(
                    View(activity).apply { setBackgroundColor(Color.rgb(232, 238, 247)) },
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, activity.px(1)).apply {
                        marginStart = activity.px(62)
                    }
                )
            }
        }

        dialog.setContentView(sheet)
        // Latar bawaan dibuat transparan agar sudut atas yang membulat terlihat
        (sheet.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.behavior.skipCollapsed = true
        dialog.show()
    }

    /** Menu aksi standar untuk satu kelas: Buka Kelas, Salin Kode, Bagikan Kode. */
    fun showForClass(activity: Activity, c: TeacherClass, onOpen: () -> Unit) {
        show(
            activity,
            "Aksi Kelas ${c.name}",
            listOf(
                Row(R.drawable.ic_login, "Buka Kelas", onClick = onOpen),
                Row(R.drawable.ic_copy, "Salin Kode", c.code) { TeacherClassStore.copyCode(activity, c) },
                Row(R.drawable.ic_share, "Bagikan Kode") { TeacherClassStore.share(activity, c) }
            )
        )
    }

    /** Memilih satu kelas dari daftar (menggantikan dialog gelap bawaan). */
    fun pickClass(activity: Activity, title: String, classes: List<TeacherClass>, onPick: (TeacherClass) -> Unit) {
        show(
            activity,
            title,
            classes.map { c ->
                Row(TeacherClassStore.iconFor(c.topic), c.name, c.code) { onPick(c) }
            }
        )
    }
}
