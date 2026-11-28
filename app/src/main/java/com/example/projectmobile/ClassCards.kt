package com.example.projectmobile

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/** Komponen kartu kelas pengajar yang dipakai bersama (beranda, Kelas Saya). */
object ClassCards {

    /** Efek riak sentuhan dengan sudut membulat. */
    fun ripple(activity: Activity, view: View, radiusDp: Int, color: Int = Color.argb(30, 29, 94, 255)) {
        val mask = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = activity.px(radiusDp).toFloat()
        }
        view.foreground = RippleDrawable(ColorStateList.valueOf(color), null, mask)
    }

    /** Empat angka ringkas: Peserta, Materi, Tugas, Kuis. */
    fun statsRow(activity: Activity, peserta: Int, materi: Int, tugas: Int, kuis: Int): View {
        val muted = UiKit.MUTED
        val navy = UiKit.NAVY
        val row = LinearLayout(activity).apply { orientation = LinearLayout.HORIZONTAL }

        fun stat(icon: Int, value: Int, label: String): View {
            val box = LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
            }
            val top = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            top.addView(
                ImageView(activity).apply {
                    setImageResource(icon)
                    setColorFilter(muted)
                },
                LinearLayout.LayoutParams(activity.px(14), activity.px(14))
            )
            top.addView(
                TextView(activity).apply {
                    text = "$value"
                    textSize = 14f
                    setTextColor(navy)
                    setTypeface(null, Typeface.BOLD)
                    setPadding(activity.px(4), 0, 0, 0)
                }
            )
            box.addView(top)
            box.addView(
                TextView(activity).apply {
                    text = label
                    textSize = 11f
                    setTextColor(muted)
                },
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    topMargin = activity.px(2)
                }
            )
            return box
        }

        fun lp() = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        row.addView(stat(R.drawable.ic_people, peserta, "Peserta"), lp())
        row.addView(stat(R.drawable.ic_description, materi, "Materi"), lp())
        row.addView(stat(R.drawable.ic_assignment_check, tugas, "Tugas"), lp())
        row.addView(stat(R.drawable.ic_check_circle, kuis, "Kuis"), lp())
        return row
    }

    /**
     * Kartu kelas lengkap: ikon, nama, chip kode (ketuk untuk salin), menu titik tiga,
     * bar kapasitas, dan empat angka ringkas.
     */
    fun teacherClassCard(activity: Activity, c: TeacherClass, onOpen: () -> Unit, onMore: () -> Unit): View {
        val muted = UiKit.MUTED
        val navy = UiKit.NAVY
        val blue = UiKit.PRIMARY

        val nPeserta = TeacherClassStore.participantCount(activity, c.code)
        val nMateri = TeacherClassStore.materials(activity, c.code).size
        val nTugas = TeacherClassStore.tasks(activity, c.code).size
        val nKuis = QuizStore.quizzes(activity, c.code).size

        val card = UiKit.card(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(activity.px(14), activity.px(14), activity.px(14), activity.px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { onOpen() }
        }
        ripple(activity, card, 16)

        // Baris atas
        val head = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        head.addView(UiKit.badge(activity, TeacherClassStore.iconFor(c.topic), Color.WHITE, blue, 44, 12, 22))

        val col = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(activity, c.name, 15f, navy, true).apply {
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
        })
        val chip = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(activity.px(8), activity.px(4), activity.px(8), activity.px(4))
            background = UiKit.rounded(activity, Color.rgb(232, 242, 255), 8)
            isClickable = true
            isFocusable = true
            contentDescription = "Salin kode kelas"
            setOnClickListener { TeacherClassStore.copyCode(activity, c) }
        }
        ripple(activity, chip, 8, Color.argb(40, 29, 94, 255))
        chip.addView(UiKit.tv(activity, "Kode: ${c.code}", 12f, blue, true).apply { maxLines = 1 })
        chip.addView(
            ImageView(activity).apply {
                setImageResource(R.drawable.ic_copy)
                setColorFilter(blue)
            },
            UiKit.params(activity, activity.px(14), activity.px(14), start = 6)
        )
        col.addView(chip, UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, top = 6))
        head.addView(col, UiKit.params(activity, 0, UiKit.WRAP, start = 12, weight = 1f))

        val more = ImageView(activity).apply {
            setImageResource(R.drawable.ic_more_vert)
            setColorFilter(muted)
            setPadding(activity.px(6), activity.px(6), activity.px(6), activity.px(6))
            contentDescription = "Menu kelas"
            isClickable = true
            isFocusable = true
            setOnClickListener { onMore() }
        }
        ripple(activity, more, 18, Color.argb(40, 29, 94, 255))
        head.addView(more, LinearLayout.LayoutParams(activity.px(36), activity.px(36)))
        card.addView(head)

        // Kapasitas terisi
        val unlimited = TeacherClassStore.isUnlimited(c.capacity)
        val capRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        capRow.addView(UiKit.tv(activity, "Kapasitas terisi", 11f, muted), UiKit.params(activity, 0, UiKit.WRAP, weight = 1f))
        capRow.addView(
            UiKit.tv(activity, if (unlimited) "$nPeserta peserta • Tanpa batas" else "$nPeserta/${c.capacity}", 11f, navy, true)
        )
        card.addView(capRow, UiKit.params(activity, UiKit.MATCH, UiKit.WRAP, top = 14))

        if (!unlimited && c.capacity > 0) {
            val track = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = c.capacity.toFloat()
                background = UiKit.rounded(activity, Color.rgb(226, 236, 252), 4)
            }
            val filled = nPeserta.coerceAtMost(c.capacity)
            if (filled > 0) {
                track.addView(
                    View(activity).apply { background = UiKit.rounded(activity, blue, 4) },
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, filled.toFloat())
                )
            }
            card.addView(track, UiKit.params(activity, UiKit.MATCH, activity.px(8), top = 6))
        }

        card.addView(
            View(activity).apply { setBackgroundColor(Color.rgb(232, 238, 247)) },
            UiKit.params(activity, UiKit.MATCH, activity.px(1), top = 14)
        )
        card.addView(statsRow(activity, nPeserta, nMateri, nTugas, nKuis), UiKit.params(activity, UiKit.MATCH, UiKit.WRAP, top = 12))
        return card
    }
}
