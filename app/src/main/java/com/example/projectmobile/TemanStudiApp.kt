package com.example.projectmobile

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Application kelas aplikasi. Tugasnya satu: memastikan isi setiap layar tidak tertimpa
 * status bar (jam, baterai) dan tombol navigasi Android.
 */
class TemanStudiApp : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                InsetsFixer.attach(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}

/**
 * Android versi baru menggambar aplikasi sampai ke tepi layar. Objek ini menambahkan jarak
 * sebesar tinggi status bar dan tombol navigasi pada isi tiap layar, termasuk layar yang
 * membangun ulang isinya (setContentView dipanggil lagi, misalnya setelah tombol kembali).
 */
object InsetsFixer {

    /** Tanda pada view yang sudah mengatur jaraknya sendiri (lihat UiKit.root). */
    const val HANDLED = "insets_handled"

    private val SKIP = setOf("OnboardingActivity", "ModePembelajaranActivity")

    fun attach(activity: Activity) {
        if (!activity.javaClass.name.startsWith("com.example.projectmobile")) return
        if (activity.javaClass.simpleName in SKIP) return
        val content = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        content.setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewAdded(parent: View?, child: View?) {
                if (child != null) fix(child)
            }

            override fun onChildViewRemoved(parent: View?, child: View?) {}
        })
        for (i in 0 until content.childCount) fix(content.getChildAt(i))
    }

    private fun fix(view: View) {
        if (view.tag == HANDLED) return
        // Padding bawaan layar dipertahankan, jarak status bar dan navigasi ditambahkan di atasnya.
        val left = view.paddingLeft
        val top = view.paddingTop
        val right = view.paddingRight
        val bottom = view.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(view)
    }
}
