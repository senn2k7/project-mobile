package com.example.projectmobile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

object PelajarBottomNavHelper {
    fun create(activity: Activity, activeTab: String): View {
        val container = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.WHITE)
            gravity = Gravity.CENTER_VERTICAL
            elevation = 8f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                activity.px(64)
            )
        }

        val role = activity.intent.getStringExtra(PilihPeranActivity.EXTRA_ROLE) ?: PilihPeranActivity.ROLE_PELAJAR
        val mode = activity.intent.getStringExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN) ?: ModePembelajaranActivity.MODE_INSTITUSI

        val tabs = listOf(
            Triple("Beranda", R.drawable.ic_book, BerandaPelajarInstitusiActivity::class.java),
            Triple("Kelas", R.drawable.ic_folder, KelasPelajarInstitusiActivity::class.java),
            Triple("AI", R.drawable.ic_code, KelolaAiAsistenPelajarActivity::class.java),
            Triple("Aktivitas", R.drawable.ic_assignment_check, null),
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
                    if (title == "Aktivitas") {
                        Toast.makeText(activity, "Aktivitas Pelajar (TODO)", Toast.LENGTH_SHORT).show()
                    } else if (title == "AI") {
                        val intent = Intent(activity, KelolaAiAsistenPelajarActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        activity.startActivity(intent)
                        Toast.makeText(activity, "Kelola AI Asisten (TODO)", Toast.LENGTH_SHORT).show()
                    } else if (!isActive) {
                        if (targetClass == ProfilMenuActivity::class.java) {
                            UiKit.openProfile(activity, role, mode)
                        } else {
                            val intent = Intent(activity, targetClass!!).apply {
                                putExtra(PilihPeranActivity.EXTRA_ROLE, role)
                                putExtra(ModePembelajaranActivity.EXTRA_MODE_PEMBELAJARAN, mode)
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
