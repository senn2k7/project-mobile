package com.example.projectmobile.ui.pengajar.institusi

import android.graphics.Color
import com.example.projectmobile.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileIconMapper {
    data class FileIconInfo(val iconRes: Int, val backgroundColor: Int)

    fun getInfo(fileName: String, fileType: String): FileIconInfo {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        return when (ext) {
            "pdf" -> FileIconInfo(R.drawable.ic_description, Color.parseColor("#E53935")) // Merah
            "zip", "rar", "tar" -> FileIconInfo(R.drawable.ic_folder, Color.parseColor("#FB8C00")) // Oranye
            "pptx", "ppt" -> FileIconInfo(R.drawable.ic_description, Color.parseColor("#FB8C00")) // Oranye
            "docx", "doc" -> FileIconInfo(R.drawable.ic_description, Color.parseColor("#1D5EFF")) // Biru
            "png", "jpg", "jpeg" -> FileIconInfo(R.drawable.ic_camera, Color.parseColor("#E53935")) // Merah
            "kt", "java", "xml" -> FileIconInfo(R.drawable.ic_code, Color.parseColor("#1D5EFF")) // Biru
            else -> FileIconInfo(R.drawable.ic_description, Color.parseColor("#7186A5")) // Abu-abu
        }
    }
}

object FormatUkuran {
    fun format(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        return when {
            gb >= 1.0 -> String.format(Locale.getDefault(), "%.1f GB", gb)
            mb >= 1.0 -> String.format(Locale.getDefault(), "%.1f MB", mb)
            kb >= 1.0 -> String.format(Locale.getDefault(), "%.1f KB", kb)
            else -> "$bytes B"
        }
    }
}

object FormatTanggal {
    private val localeId = Locale("id", "ID")

    fun formatLengkap(timestamp: Long): String {
        if (timestamp <= 0L) return "-"
        val sdf = SimpleDateFormat("dd MMM yyyy, HH.mm", localeId)
        return sdf.format(Date(timestamp))
    }

    fun formatTanpaTahun(timestamp: Long): String {
        if (timestamp <= 0L) return "-"
        val sdf = SimpleDateFormat("dd MMM, HH.mm", localeId)
        return sdf.format(Date(timestamp))
    }
}

object NameUtils {
    fun getInisial(nama: String): String {
        if (nama.isBlank()) return "?"
        val parts = nama.trim().split("\\s+".toRegex())
        return when {
            parts.size >= 2 -> "${parts[0].take(1)}${parts[1].take(1)}".uppercase()
            parts.isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "?"
        }
    }

    fun getWarnaAvatar(nama: String): Int {
        val colors = listOf(
            Color.parseColor("#1D5EFF"),
            Color.parseColor("#1FAF6E"),
            Color.parseColor("#8A5CF5"),
            Color.parseColor("#FB8C00"),
            Color.parseColor("#E53935")
        )
        val hash = nama.hashCode()
        val index = Math.abs(hash) % colors.size
        return colors[index]
    }
}

object TaskStatusUtils {
    fun hitungStatus(tenggatTimestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        return if (currentTime > tenggatTimestamp) {
            "Selesai"
        } else {
            "Berlangsung"
        }
    }
}
