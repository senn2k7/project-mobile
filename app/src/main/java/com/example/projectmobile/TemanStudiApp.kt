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

    /** Tanda lama, dibiarkan supaya UiKit.root() tidak error. */
    const val HANDLED = "insets_handled"

    private val SKIP = setOf("OnboardingActivity", "ModePembelajaranActivity")
    private val BG = android.graphics.Color.parseColor("#F5FAFF")

    fun attach(activity: Activity) {
        if (!activity.javaClass.name.startsWith("com.example.projectmobile")) return
        if (activity.javaClass.simpleName in SKIP) return
        val content = activity.findViewById<ViewGroup>(android.R.id.content) ?: return

        // Warna di belakang status bar dan tombol navigasi.
        content.setBackgroundColor(BG)

        ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout() or
                    WindowInsetsCompat.Type.ime()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(content)
    }
}
