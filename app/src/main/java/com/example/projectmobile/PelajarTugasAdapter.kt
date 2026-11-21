package com.example.projectmobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.FormatTanggal
import com.example.projectmobile.ui.pengajar.institusi.Tugas

data class TugasItemUi(
    val tugas: Tugas,
    val statusText: String,
    val statusTextColor: Int,
    val statusBgColor: Int
)

class PelajarTugasAdapter(
    private val onItemClick: (Tugas) -> Unit
) : ListAdapter<TugasItemUi, PelajarTugasAdapter.ViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<TugasItemUi>() {
        override fun areItemsTheSame(oldItem: TugasItemUi, newItem: TugasItemUi): Boolean = oldItem.tugas.id == newItem.tugas.id
        override fun areContentsTheSame(oldItem: TugasItemUi, newItem: TugasItemUi): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas_pelajar_institusi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJudulTugas: TextView = itemView.findViewById(R.id.tvJudulTugas)
        private val tvMataKuliah: TextView = itemView.findViewById(R.id.tvMataKuliah)
        private val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        private val tvStatusChip: TextView = itemView.findViewById(R.id.tvStatusChip)

        fun bind(item: TugasItemUi, onItemClick: (Tugas) -> Unit) {
            tvJudulTugas.text = item.tugas.judul
            tvMataKuliah.text = "Pemrograman Mobile"
            tvDeadline.text = "🕒 ${FormatTanggal.formatLengkap(item.tugas.tenggat)}"
            tvStatusChip.text = item.statusText
            tvStatusChip.setTextColor(item.statusTextColor)
            tvStatusChip.background = UiKit.rounded(itemView.context, item.statusBgColor, 8)

            itemView.setOnClickListener { onItemClick(item.tugas) }
        }
    }
}
