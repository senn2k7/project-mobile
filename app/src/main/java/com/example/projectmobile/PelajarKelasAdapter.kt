package com.example.projectmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.PengajarKelas

class PelajarKelasAdapter(
    private val onItemClick: (PengajarKelas) -> Unit
) : ListAdapter<PengajarKelas, PelajarKelasAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<PengajarKelas>() {
        override fun areItemsTheSame(oldItem: PengajarKelas, newItem: PengajarKelas): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PengajarKelas, newItem: PengajarKelas): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas_pelajar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaMataKuliah: TextView = itemView.findViewById(R.id.tvNamaMataKuliah)
        private val tvDosenProdi: TextView = itemView.findViewById(R.id.tvDosenProdi)
        private val tvPeserta: TextView = itemView.findViewById(R.id.tvPeserta)
        private val ivKelasIcon: ImageView = itemView.findViewById(R.id.ivKelasIcon)

        fun bind(kelas: PengajarKelas, onItemClick: (PengajarKelas) -> Unit) {
            tvNamaMataKuliah.text = kelas.namaMataKuliah
            tvDosenProdi.text = "${kelas.namaKelas} • ${kelas.semester}"
            tvPeserta.text = "${kelas.jumlahPelajar} peserta"

            itemView.setOnClickListener { onItemClick(kelas) }
        }
    }
}
