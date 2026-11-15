package com.example.projectmobile.ui.pengajar.institusi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.R

class PengajarMateriAdapter(
    private val onItemClick: (Materi) -> Unit,
    private val onMenuClick: (Materi, View) -> Unit
) : ListAdapter<Materi, PengajarMateriAdapter.MateriViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Materi>() {
        override fun areItemsTheSame(oldItem: Materi, newItem: Materi): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Materi, newItem: Materi): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MateriViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_materi_pengajar, parent, false)
        return MateriViewHolder(view)
    }

    override fun onBindViewHolder(holder: MateriViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onMenuClick)
    }

    class MateriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvMateriTitle)
        private val tvSize: TextView = itemView.findViewById(R.id.tvMateriSize)
        private val ivMenu: ImageView = itemView.findViewById(R.id.ivMateriMenu)

        fun bind(
            materi: Materi,
            onItemClick: (Materi) -> Unit,
            onMenuClick: (Materi, View) -> Unit
        ) {
            tvTitle.text = materi.judul.ifBlank { materi.namaFile }
            tvSize.text = FormatUkuran.format(materi.ukuranByte)

            itemView.setOnClickListener { onItemClick(materi) }
            ivMenu.setOnClickListener { onMenuClick(materi, it) }
        }
    }
}
