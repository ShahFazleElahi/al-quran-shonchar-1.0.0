package com.alquran.shonchar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alquran.shonchar.model.SurahList
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SurahListAdapter(val context: Context, val data: ArrayList<SurahList>):
    RecyclerView.Adapter<SurahListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val count: TextView = view.findViewById(R.id.count)
        val name: TextView = view.findViewById(R.id.name)
        val from: TextView = view.findViewById(R.id.from)
        val arabic: TextView = view.findViewById(R.id.arabic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    R.layout.layout_surah_name,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.count.text = numberFormat.format(data[position].pos)//data[position].pos.toString()
        holder.name.text = data[position].name
        holder.from.text = "${revelation(data[position].revelation)}   |   " +
                "${numberFormat.format(data[position].verse)}  " + context.resources.getString(R.string.verses)
        holder.arabic.text = data[position].nameAr

        holder.itemView.setOnClickListener {
            SurahActivity.launch(context, position, 0)
        }
    }

    private fun revelation(revelation: String): String {
        return if (revelation == "Meccan")
            context.resources.getString(R.string.meccan)
        else context.resources.getString(R.string.medinan)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(ApplicationData(context).language))
}