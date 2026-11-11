package com.example.projectmobile

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView

/** Komponen kartu kelas yang dipakai di beberapa halaman pelajar. */
object ClassUi {

    fun chipLabel(type: String) = if (type == ClassStore.TYPE_MANDIRI) "Kelas Mandiri" else "Institusi"

    fun chip(activity: Activity, label: String, textColor: Int = UiKit.PRIMARY, bg: Int = UiKit.SOFT): TextView =
        UiKit.tv(activity, label, 11.5f, textColor, true).apply {
            background = UiKit.rounded(activity, bg, 8)
            setPadding(activity.px(10), activity.px(3), activity.px(10), activity.px(3))
        }

    fun progressBar(activity: Activity, percent: Int): ProgressBar =
        ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            progress = percent
            progressTintList = ColorStateList.valueOf(UiKit.PRIMARY)
            progressBackgroundTintList = ColorStateList.valueOf(UiKit.SOFT)
        }

    fun statusChip(activity: Activity, status: String): TextView = when (status) {
        "Selesai" -> chip(activity, "Selesai", UiKit.SUCCESS, UiKit.SUCCESS_SOFT)
        "Sedang" -> chip(activity, "Sedang", UiKit.PRIMARY, UiKit.SOFT)
        else -> chip(activity, "Belum", UiKit.MUTED, UiKit.DISABLED)
    }

    private fun iconText(activity: Activity, iconRes: Int, text: String): LinearLayout =
        LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            addView(UiKit.icon(activity, iconRes, 14, UiKit.MUTED), LinearLayout.LayoutParams(activity.px(14), activity.px(14)))
            addView(UiKit.tv(activity, text, 11.5f, UiKit.MUTED), UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, start = 6))
        }

    /** Kartu di halaman "Kelas Saya" (daftar kelas). */
    fun listCard(activity: Activity, info: ClassInfo, onClick: () -> Unit): View {
        val card = UiKit.card(activity).apply {
            setPadding(activity.px(16), activity.px(16), activity.px(16), activity.px(14))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

        val top = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
        }
        top.addView(UiKit.badge(activity, info.iconRes, Color.WHITE, UiKit.PRIMARY, 48, 12, 26))

        val col = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(activity, info.name, 16f, UiKit.NAVY, true))
        col.addView(
            UiKit.tv(activity, "${info.teacher} · ${info.field}", 11.5f, UiKit.MUTED),
            UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, top = 2)
        )
        col.addView(chip(activity, chipLabel(info.type)), UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, top = 8))
        top.addView(col, UiKit.params(activity, 0, UiKit.WRAP, start = 14, weight = 1f))
        top.addView(
            UiKit.icon(activity, R.drawable.ic_more_vert, 22, UiKit.MUTED),
            LinearLayout.LayoutParams(activity.px(22), activity.px(22))
        )
        card.addView(top)

        val row = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(
            iconText(activity, R.drawable.ic_calendar, info.nextMeeting),
            UiKit.params(activity, 0, UiKit.WRAP, weight = 1f)
        )
        row.addView(iconText(activity, R.drawable.ic_people, "${info.participants} peserta"))
        card.addView(row, UiKit.params(activity, UiKit.MATCH, UiKit.WRAP, top = 14))
        card.addView(
            iconText(activity, R.drawable.ic_clock, info.schedule),
            UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, top = 8)
        )
        return card
    }

    /** Kartu kelas yang sudah diikuti di Beranda (mode mandiri). */
    fun homeCard(activity: Activity, info: ClassInfo, onClick: () -> Unit): View {
        val progress = ClassStore.progress(activity, info)
        val card = UiKit.card(activity).apply {
            setPadding(activity.px(16), activity.px(16), activity.px(16), activity.px(16))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

        val top = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
        }
        top.addView(UiKit.badge(activity, info.iconRes, Color.WHITE, UiKit.PRIMARY, 64, 14, 34))
        val col = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }
        col.addView(UiKit.tv(activity, info.name, 16f, UiKit.NAVY, true))
        col.addView(chip(activity, chipLabel(info.type)), UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, top = 4))
        col.addView(
            UiKit.tv(activity, "Pengajar: ${info.teacher}", 12.5f, UiKit.MUTED),
            UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, top = 4)
        )
        val bar = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        bar.addView(progressBar(activity, progress), UiKit.params(activity, 0, activity.px(6), weight = 1f))
        bar.addView(UiKit.tv(activity, "$progress%", 12f, UiKit.MUTED), UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, start = 8))
        col.addView(bar, UiKit.params(activity, UiKit.MATCH, UiKit.WRAP, top = 8))
        top.addView(col, UiKit.params(activity, 0, UiKit.WRAP, start = 14, weight = 1f))
        card.addView(top)

        val stats = LinearLayout(activity).apply { orientation = LinearLayout.HORIZONTAL }
        fun stat(iconRes: Int, value: String, label: String) {
            val box = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            box.addView(UiKit.icon(activity, iconRes, 20, UiKit.MUTED), LinearLayout.LayoutParams(activity.px(20), activity.px(20)))
            val t = LinearLayout(activity).apply { orientation = LinearLayout.VERTICAL }
            t.addView(UiKit.tv(activity, value, 14f, UiKit.NAVY, true))
            t.addView(UiKit.tv(activity, label, 11f, UiKit.MUTED))
            box.addView(t, UiKit.params(activity, UiKit.WRAP, UiKit.WRAP, start = 6))
            stats.addView(box, UiKit.params(activity, 0, UiKit.WRAP, weight = 1f))
        }
        stat(R.drawable.ic_people, info.participants.toString(), "Peserta")
        stat(R.drawable.ic_folder, ClassStore.totalMaterials(activity, info).toString(), "Materi")
        stat(R.drawable.ic_description, TaskStore.countFor(activity, info.code).toString(), "Tugas")
        stat(R.drawable.ic_assignment_check, "0", "Kuis")
        card.addView(stats, UiKit.params(activity, UiKit.MATCH, UiKit.WRAP, top = 16))
        return card
    }
}
