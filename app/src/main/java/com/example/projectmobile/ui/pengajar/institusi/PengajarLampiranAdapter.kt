package com.example.projectmobile.ui.pengajar.institusi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.R

class PengajarLampiranAdapter(
    private val onItemClick: (LampiranTugas) -> Unit
) : ListAdapter<LampiranTugas, PengajarLampiranAdapter.LampiranViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<LampiranTugas>() {
        override fun areItemsTheSame(oldItem: LampiranTugas, newItem: LampiranTugas): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: LampiranTugas, newItem: LampiranTugas): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LampiranViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lampiran_tugas, parent, false)
        return LampiranViewHolder(view)
    }

    override fun onBindViewHolder(holder: LampiranViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class LampiranViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvLampiranName)
        private val tvSize: TextView = itemView.findViewById(R.id.tvLampiranSize)

        fun bind(lampiran: LampiranTugas, onItemClick: (LampiranTugas) -> Unit) {
            tvName.text = lampiran.nama
            tvSize.text = FormatUkuran.format(lampiran.ukuranByte)
            itemView.setOnClickListener { onItemClick(lampiran) }
        }
    }
}
