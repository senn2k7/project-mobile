package com.example.projectmobile

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmobile.ui.pengajar.institusi.OpsiSoal

class PelajarOpsiAdapter(
    private val opsiList: List<OpsiSoal>,
    private val selectedKode: String?,
    private val onOpsiClick: (OpsiSoal) -> Unit
) : RecyclerView.Adapter<PelajarOpsiAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKodeOpsi: TextView = itemView.findViewById(R.id.tvKodeOpsi)
        val tvTeksOpsi: TextView = itemView.findViewById(R.id.tvTeksOpsi)
        val cardView: androidx.cardview.widget.CardView = itemView as androidx.cardview.widget.CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_opsi_jawaban_pelajar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val opsi = opsiList[position]
        holder.tvKodeOpsi.text = opsi.kode
        holder.tvTeksOpsi.text = opsi.teks

        val isSelected = opsi.kode == selectedKode
        val density = holder.itemView.resources.displayMetrics.density
        val bgDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 12f * density
            setColor(if (isSelected) Color.parseColor("#E8F2FF") else Color.WHITE)
            setStroke(
                if (isSelected) (2 * density).toInt() else (1 * density).toInt(),
                if (isSelected) Color.parseColor("#1D5EFF") else Color.parseColor("#DCE7F4")
            )
        }
        holder.cardView.background = bgDrawable

        if (isSelected) {
            holder.tvKodeOpsi.background = UiKit.rounded(holder.itemView.context, Color.parseColor("#1D5EFF"), 8)
            holder.tvKodeOpsi.setTextColor(Color.WHITE)
        } else {
            holder.tvKodeOpsi.background = UiKit.rounded(holder.itemView.context, Color.parseColor("#E8F2FF"), 8)
            holder.tvKodeOpsi.setTextColor(Color.parseColor("#1D5EFF"))
        }

        holder.itemView.setOnClickListener { onOpsiClick(opsi) }
    }

    override fun getItemCount(): Int = opsiList.size
}
