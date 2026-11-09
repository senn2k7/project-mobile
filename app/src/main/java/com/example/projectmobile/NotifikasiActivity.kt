package com.example.snapchatsphlasscreen

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

/** Halaman Preferensi Notifikasi. Pilihan disimpan di perangkat. */
class NotifikasiActivity : AppCompatActivity() {

    private val switches = LinkedHashMap<String, SwitchCompat>()
    private var bulkUpdate = false

    private data class Pref(val key: String, val icon: Int, val title: String, val default: Boolean = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(build())
    }

    private fun build(): View {
        val prefs = getSharedPreferences("notif_prefs", MODE_PRIVATE)

        val root = UiKit.root(this)
        root.addView(UiKit.topBar(this, "Preferensi Notifikasi") { finish() })
        val (scroll, content) = UiKit.scroll(this)

        content.addView(
            UiKit.tv(this, "Pilih notifikasi yang ingin kamu terima.", 13f, UiKit.MUTED),
            UiKit.params(this, UiKit.WRAP, UiKit.WRAP, bottom = 8)
        )

        // Saklar utama: menyalakan / mematikan semua sekaligus
        val allOn = prefs.getBoolean("all", true)
        val (masterRow, masterSwitch) = UiKit.toggleRow(this, R.drawable.ic_bell, "Semua notifikasi", allOn) { checked ->
            if (bulkUpdate) return@toggleRow
            prefs.edit().putBoolean("all", checked).apply()
            bulkUpdate = true
            for ((key, sw) in switches) {
                sw.isChecked = checked
                prefs.edit().putBoolean(key, checked).apply()
            }
            bulkUpdate = false
        }
        content.addView(masterRow)

        val sections = listOf(
            "Akademik" to listOf(
                Pref("pengumuman", R.drawable.ic_bell, "Pengumuman kelas"),
                Pref("jadwal", R.drawable.ic_calendar, "Jadwal pembelajaran"),
                Pref("presensi", R.drawable.ic_assignment_check, "Presensi kelas", false)
            ),
            "Tugas" to listOf(
                Pref("tugas_baru", R.drawable.ic_description, "Tugas baru"),
                Pref("deadline", R.drawable.ic_bell, "Pengingat deadline"),
                Pref("nilai", R.drawable.ic_assignment_check, "Nilai tugas")
            ),
            "Sistem" to listOf(
                Pref("update", R.drawable.ic_refresh, "Update aplikasi"),
                Pref("akun", R.drawable.ic_person, "Informasi akun")
            )
        )

        for ((sectionTitle, items) in sections) {
            content.addView(
                UiKit.sectionBand(this, sectionTitle),
                UiKit.params(this, UiKit.MATCH, UiKit.WRAP, top = 8, bottom = 4)
            )
            for (p in items) {
                val (row, sw) = UiKit.toggleRow(this, p.icon, p.title, prefs.getBoolean(p.key, p.default)) { checked ->
                    prefs.edit().putBoolean(p.key, checked).apply()
                    if (!bulkUpdate && !checked && masterSwitch.isChecked) {
                        // Satu saja dimatikan: saklar utama ikut mati tanpa mengubah yang lain.
                        bulkUpdate = true
                        masterSwitch.isChecked = false
                        prefs.edit().putBoolean("all", false).apply()
                        bulkUpdate = false
                    }
                }
                switches[p.key] = sw
                content.addView(row)
            }
        }

        root.addView(scroll, UiKit.scrollParams())
        return root
    }
}
