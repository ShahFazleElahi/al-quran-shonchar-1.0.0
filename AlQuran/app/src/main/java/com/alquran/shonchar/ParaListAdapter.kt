package com.alquran.shonchar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alquran.shonchar.model.JuzModel
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ParaListAdapter(val context: Context, val data: ArrayList<JuzModel>):
    RecyclerView.Adapter<ParaListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val from: TextView = view.findViewById(R.id.from)
        val count: TextView = view.findViewById(R.id.count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    R.layout.layout_para,
                    parent, false
                )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        data[position].let {
            val quran = QuranHelper(context).readAyatNo(it.startPos)!!
            holder.run {
                count.text = numberFormat.format(it.paraNo) //"${it.paraNo}"
                name.text = "${context.resources.getString(R.string.para)} " +
                        "${numberFormat.format(it.paraNo)} -> ${numberFormat.format(
                            it.endPos+1-it.startPos)} " +
                        context.resources.getString(R.string.verses)
                from.text = "${context.resources.getString(R.string.starts_at)}: " +
                        "${SurahHelper(context).readDataAt(quran.surah)!!.name}," +
                        " "+context.resources.getString(R.string.verses)+" ${numberFormat.format(quran.ayat)}"

                holder.itemView.setOnClickListener { _->
                    ParaActivity.launch(context, it.paraNo)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(ApplicationData(context).language))
}