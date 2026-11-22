package com.example.projectmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.PesanChatAi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PelajarChatAiAdapter : ListAdapter<PesanChatAi, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    class DiffCallback : DiffUtil.ItemCallback<PesanChatAi>() {
        override fun areItemsTheSame(oldItem: PesanChatAi, newItem: PesanChatAi): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PesanChatAi, newItem: PesanChatAi): Boolean = oldItem == newItem
    }

    override fun getItemViewType(position: Int): Int {
        val pesan = getItem(position)
        return if (pesan.isDariUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_USER) {
            UserViewHolder(inflater.inflate(R.layout.item_pesan_ai_user, parent, false))
        } else {
            BotViewHolder(inflater.inflate(R.layout.item_pesan_ai_bot, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pesan = getItem(position)
        val timeStr = formatWaktu(pesan.waktu)

        if (holder is UserViewHolder) {
            holder.tvPesan.text = pesan.teks
            holder.tvWaktu.text = timeStr
        } else if (holder is BotViewHolder) {
            holder.tvPesan.text = pesan.teks
            holder.tvWaktu.text = timeStr
        }
    }

    private fun formatWaktu(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPesan: TextView = itemView.findViewById(R.id.tvPesan)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
    }

    class BotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPesan: TextView = itemView.findViewById(R.id.tvPesan)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
    }
}
