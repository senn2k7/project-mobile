package com.example.projectmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.FileIconMapper
import com.example.projectmobile.ui.pengajar.institusi.FormatUkuran
import com.example.projectmobile.ui.pengajar.institusi.Materi

class PelajarFileMateriAdapter(
    private val onItemClick: (Materi) -> Unit
) : ListAdapter<Materi, PelajarFileMateriAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Materi>() {
        override fun areItemsTheSame(oldItem: Materi, newItem: Materi): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Materi, newItem: Materi): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file_materi_pelajar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaFile: TextView = itemView.findViewById(R.id.tvNamaFile)
        private val tvUkuranFile: TextView = itemView.findViewById(R.id.tvUkuranFile)
        private val ivFileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)
        private val btnActionFile: TextView = itemView.findViewById(R.id.btnActionFile)

        fun bind(materi: Materi, onItemClick: (Materi) -> Unit) {
            val fileName = if (materi.namaFile.isNotBlank()) materi.namaFile else "${materi.judul}.pdf"
            tvNamaFile.text = fileName
            tvUkuranFile.text = FormatUkuran.format(materi.ukuranByte)

            val iconInfo = FileIconMapper.getInfo(fileName, materi.tipeFile)
            ivFileIcon.setImageResource(iconInfo.iconRes)
            ivFileIcon.setColorFilter(iconInfo.backgroundColor)

            btnActionFile.text = if (materi.url.isNotBlank()) "Buka" else "Unduh"

            itemView.setOnClickListener { onItemClick(materi) }
            btnActionFile.setOnClickListener { onItemClick(materi) }
        }
    }
}
