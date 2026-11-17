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
import com.example.projectmobile.UiKit

class PengajarSudahMengumpulkanAdapter(
    private val onItemClick: (Pengumpulan) -> Unit
) : ListAdapter<Pengumpulan, PengajarSudahMengumpulkanAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Pengumpulan>() {
        override fun areItemsTheSame(oldItem: Pengumpulan, newItem: Pengumpulan): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pengumpulan, newItem: Pengumpulan): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengumpulan_sudah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        private val tvNamaPelajar: TextView = itemView.findViewById(R.id.tvNamaPelajar)
        private val tvWaktuKumpul: TextView = itemView.findViewById(R.id.tvWaktuKumpul)
        private val tvNamaFile: TextView = itemView.findViewById(R.id.tvNamaFile)
        private val ivFileIcon: ImageView = itemView.findViewById(R.id.ivFileIcon)

        fun bind(item: Pengumpulan, onItemClick: (Pengumpulan) -> Unit) {
            tvNamaPelajar.text = item.namaPelajar
            tvAvatar.text = NameUtils.getInisial(item.namaPelajar)
            tvAvatar.background = UiKit.rounded(itemView.context, NameUtils.getWarnaAvatar(item.namaPelajar), 20)
            tvWaktuKumpul.text = "Dikumpulkan ${FormatTanggal.formatTanpaTahun(item.waktuKumpul)}"
            tvNamaFile.text = item.namaFile

            val iconInfo = FileIconMapper.getInfo(item.namaFile, item.tipeFile)
            ivFileIcon.setImageResource(iconInfo.iconRes)
            ivFileIcon.setColorFilter(iconInfo.backgroundColor)

            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}

class PengajarBelumMengumpulkanAdapter(
    private val onItemClick: (PelajarKelas) -> Unit
) : ListAdapter<PelajarKelas, PengajarBelumMengumpulkanAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<PelajarKelas>() {
        override fun areItemsTheSame(oldItem: PelajarKelas, newItem: PelajarKelas): Boolean = oldItem.uid == newItem.uid
        override fun areContentsTheSame(oldItem: PelajarKelas, newItem: PelajarKelas): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pengumpulan_belum, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAvatarBelum: TextView = itemView.findViewById(R.id.tvAvatarBelum)
        private val tvNamaPelajarBelum: TextView = itemView.findViewById(R.id.tvNamaPelajarBelum)

        fun bind(pelajar: PelajarKelas, onItemClick: (PelajarKelas) -> Unit) {
            tvNamaPelajarBelum.text = pelajar.nama
            tvAvatarBelum.text = NameUtils.getInisial(pelajar.nama)
            tvAvatarBelum.background = UiKit.rounded(itemView.context, NameUtils.getWarnaAvatar(pelajar.nama), 20)

            itemView.setOnClickListener { onItemClick(pelajar) }
        }
    }
}
