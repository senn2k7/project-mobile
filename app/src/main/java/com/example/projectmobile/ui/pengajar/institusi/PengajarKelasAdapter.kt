package com.example.projectmobile.ui.pengajar.institusi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.R
import com.example.projectmobile.UiKit

class PengajarKelasAdapter(
    private val isBeranda: Boolean = false,
    private val onItemClick: (PengajarKelas) -> Unit,
    private val onMenuClick: (PengajarKelas, View) -> Unit
) : ListAdapter<PengajarKelas, PengajarKelasAdapter.PengajarKelasViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<PengajarKelas>() {
        override fun areItemsTheSame(oldItem: PengajarKelas, newItem: PengajarKelas): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PengajarKelas, newItem: PengajarKelas): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PengajarKelasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kelas_pengajar, parent, false)
        return PengajarKelasViewHolder(view)
    }

    override fun onBindViewHolder(holder: PengajarKelasViewHolder, position: Int) {
        holder.bind(getItem(position), isBeranda, onItemClick, onMenuClick)
    }

    class PengajarKelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivKelasIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvKelasTitle)
        private val tvSubtitle: TextView = itemView.findViewById(R.id.tvKelasSubtitle)
        private val tvStudents: TextView = itemView.findViewById(R.id.tvKelasStudents)
        private val ivMenu: ImageView = itemView.findViewById(R.id.ivKelasMenu)
        private val iconContainer: View = itemView.findViewById(R.id.iconContainer)

        fun bind(
            kelas: PengajarKelas,
            isBeranda: Boolean,
            onItemClick: (PengajarKelas) -> Unit,
            onMenuClick: (PengajarKelas, View) -> Unit
        ) {
            tvTitle.text = kelas.namaMataKuliah
            tvSubtitle.text = if (isBeranda) {
                "${kelas.jenisInstitusi} • ${kelas.namaKelas}"
            } else {
                "${kelas.namaKelas} • ${kelas.kodeKelas}"
            }
            tvStudents.text = "${kelas.jumlahPelajar} pelajar"

            try {
                iconContainer.background = UiKit.rounded(itemView.context, Color.parseColor(kelas.warna), 12)
            } catch (e: Exception) {
                iconContainer.background = UiKit.rounded(itemView.context, Color.parseColor("#1D5EFF"), 12)
            }

            val iconRes = when (kelas.ikon) {
                "ic_database" -> R.drawable.ic_database
                "ic_code" -> R.drawable.ic_code
                else -> R.drawable.ic_book
            }
            ivIcon.setImageResource(iconRes)

            itemView.setOnClickListener { onItemClick(kelas) }
            ivMenu.setOnClickListener { onMenuClick(kelas, it) }
        }
    }
}
