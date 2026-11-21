package com.example.projectmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.NameUtils
import com.example.projectmobile.ui.pengajar.institusi.PesanDiskusi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PelajarDiskusiAdapter(
    private val currentPelajarId: String
) : ListAdapter<PesanDiskusi, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_SAYA = 1
        private const val VIEW_TYPE_ORANG_LAIN = 2
    }

    class DiffCallback : DiffUtil.ItemCallback<PesanDiskusi>() {
        override fun areItemsTheSame(oldItem: PesanDiskusi, newItem: PesanDiskusi): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PesanDiskusi, newItem: PesanDiskusi): Boolean = oldItem == newItem
    }

    override fun getItemViewType(position: Int): Int {
        val pesan = getItem(position)
        return if (pesan.pengirimId == currentPelajarId) VIEW_TYPE_SAYA else VIEW_TYPE_ORANG_LAIN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SAYA) {
            SayaViewHolder(inflater.inflate(R.layout.item_pesan_diskusi_saya, parent, false))
        } else {
            OrangLainViewHolder(inflater.inflate(R.layout.item_pesan_diskusi_orang_lain, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pesan = getItem(position)
        val timeStr = formatWaktu(pesan.waktu)

        if (holder is SayaViewHolder) {
            holder.tvPesan.text = pesan.teks
            holder.tvWaktu.text = timeStr
            if (pesan.isKode) {
                holder.tvKodeSnippet.visibility = View.VISIBLE
                holder.tvKodeSnippet.text = pesan.teks
                holder.tvPesan.visibility = View.GONE
            } else {
                holder.tvKodeSnippet.visibility = View.GONE
                holder.tvPesan.visibility = View.VISIBLE
            }
        } else if (holder is OrangLainViewHolder) {
            holder.tvNamaPengirim.text = pesan.namaPengirim
            holder.tvPesan.text = pesan.teks
            holder.tvWaktu.text = timeStr
            holder.tvAvatar.text = NameUtils.getInisial(pesan.namaPengirim)
            holder.tvAvatar.background = UiKit.rounded(holder.itemView.context, NameUtils.getWarnaAvatar(pesan.namaPengirim), 16)

            if (pesan.isKode) {
                holder.tvKodeSnippet.visibility = View.VISIBLE
                holder.tvKodeSnippet.text = pesan.teks
                holder.tvPesan.visibility = View.GONE
            } else {
                holder.tvKodeSnippet.visibility = View.GONE
                holder.tvPesan.visibility = View.VISIBLE
            }
        }
    }

    private fun formatWaktu(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    class SayaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPesan: TextView = itemView.findViewById(R.id.tvPesan)
        val tvKodeSnippet: TextView = itemView.findViewById(R.id.tvKodeSnippet)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
    }

    class OrangLainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        val tvNamaPengirim: TextView = itemView.findViewById(R.id.tvNamaPengirim)
        val tvPesan: TextView = itemView.findViewById(R.id.tvPesan)
        val tvKodeSnippet: TextView = itemView.findViewById(R.id.tvKodeSnippet)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
    }
}
