package com.example.projectmobile.ui.pengajar.institusi

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.projectmobile.AiAssistantActivity
import com.example.projectmobile.ProfilMenuActivity
import com.example.projectmobile.R
import com.example.projectmobile.TugasActivity
import com.example.projectmobile.UiKit
import com.example.projectmobile.px

object PengajarBottomNavHelper {
    fun create(activity: Activity, activeTab: String): View {
        val container = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER
            elevation = 8f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                activity.px(64)
            )
        }

        val tabs = listOf(
            Triple("Beranda", R.drawable.ic_book, PengajarBerandaActivity::class.java),
            Triple("Kelas", R.drawable.ic_folder, PengajarKelasSayaActivity::class.java),
            Triple("Tugas", R.drawable.ic_assignment_check, TugasActivity::class.java),
            Triple("AI", R.drawable.ic_code, AiAssistantActivity::class.java),
            Triple("Profil", R.drawable.ic_person, ProfilMenuActivity::class.java)
        )

        val activeColor = Color.parseColor("#1D5EFF")
        val inactiveColor = Color.parseColor("#7186A5")

        for ((title, iconRes, targetClass) in tabs) {
            val isActive = title.equals(activeTab, ignoreCase = true)
            val color = if (isActive) activeColor else inactiveColor

            val tabLayout = LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    if (!isActive) {
                        if (targetClass == ProfilMenuActivity::class.java) {
                            UiKit.openProfile(activity, "pengajar", "institusi")
                        } else {
                            val intent = Intent(activity, targetClass).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            activity.startActivity(intent)
                        }
                    }
                }
            }

            val iconView = ImageView(activity).apply {
                setImageResource(iconRes)
                setColorFilter(color)
                layoutParams = LinearLayout.LayoutParams(activity.px(22), activity.px(22))
            }
            tabLayout.addView(iconView)

            val textView = TextView(activity).apply {
                text = title
                textSize = 11f
                setTextColor(color)
                gravity = Gravity.CENTER
                setPadding(0, activity.px(2), 0, 0)
                if (isActive) setTypeface(null, Typeface.BOLD)
            }
            tabLayout.addView(textView)

            container.addView(tabLayout)
        }

        return container
    }
}
