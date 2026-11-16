package com.example.projectmobile.ui.pengajar.institusi

import android.graphics.Color
import android.graphics.Typeface
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

class PengajarTugasAdapter(
    private val onItemClick: (Tugas) -> Unit,
    private val onMenuClick: (Tugas, View) -> Unit
) : ListAdapter<Tugas, PengajarTugasAdapter.TugasViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Tugas>() {
        override fun areItemsTheSame(oldItem: Tugas, newItem: Tugas): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Tugas, newItem: Tugas): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas_pengajar, parent, false)
        return TugasViewHolder(view)
    }

    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick, onMenuClick)
    }

    class TugasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvMeeting: TextView = itemView.findViewById(R.id.tvTaskMeeting)
        private val tvBadge: TextView = itemView.findViewById(R.id.tvTaskStatusBadge)
        private val tvDeadline: TextView = itemView.findViewById(R.id.tvTaskDeadline)
        private val tvSubmissions: TextView = itemView.findViewById(R.id.tvTaskSubmissions)
        private val ivMenu: ImageView = itemView.findViewById(R.id.ivTaskMenu)

        fun bind(
            task: Tugas,
            onItemClick: (Tugas) -> Unit,
            onMenuClick: (Tugas, View) -> Unit
        ) {
            tvTitle.text = task.judul
            tvMeeting.text = "Pertemuan ${task.pertemuanKe}"

            val statusText = TaskStatusUtils.hitungStatus(task.tenggat)
            val isBerlangsung = statusText == "Berlangsung"

            tvBadge.text = if (isBerlangsung) "Tugas Berlangsung" else "Tugas Selesai"
            tvBadge.setTextColor(if (isBerlangsung) Color.parseColor("#1D5EFF") else Color.parseColor("#2E7D32"))
            tvBadge.background = UiKit.rounded(itemView.context, if (isBerlangsung) Color.parseColor("#E8F2FF") else Color.parseColor("#E8F5E9"), 8)

            tvDeadline.text = FormatTanggal.formatLengkap(task.tenggat)
            tvSubmissions.text = "${task.jumlahMengumpulkan}/${task.totalPelajar} mengumpulkan"

            itemView.setOnClickListener { onItemClick(task) }
            ivMenu.setOnClickListener { onMenuClick(task, it) }
        }
    }
}
