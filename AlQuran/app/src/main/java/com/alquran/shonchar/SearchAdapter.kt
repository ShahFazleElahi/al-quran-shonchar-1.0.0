package com.alquran.shonchar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.alquran.shonchar.model.Quran
import com.alquran.shonchar.model.SearchModel

class SearchAdapter(val context: Context, val data: ArrayList<SearchModel>):
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private val quran = QuranHelper(context)

    inner class ViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder(view) {
        var count: TextView? = null
        var name: TextView? = null
        var from: TextView? = null

        var arabic: TextView? = null

        var pron: TextView? = null
        var share: ImageView? = null
        var ayatNo: TextView? = null
        var surahName: TextView? = null
        var bookmark: ImageView? = null
        var translation: TextView? = null

        init {
            when (viewType) {
                SURAH -> {
                    count = view.findViewById(R.id.count)
                    name = view.findViewById(R.id.name)
                    from = view.findViewById(R.id.from)
                    arabic = view.findViewById(R.id.arabic)
                }
                else -> {
                    pron = view.findViewById(R.id.pron)
                    share = view.findViewById(R.id.share)
                    arabic = view.findViewById(R.id.arabic)
                    ayatNo = view.findViewById(R.id.ayat_no)
                    bookmark = view.findViewById(R.id.bookmark)
                    surahName = view.findViewById(R.id.surah_name)
                    translation = view.findViewById(R.id.translation)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    when(viewType) {
                        SURAH -> R.layout.layout_surah_name
                        else -> R.layout.layout_ayat
                    }, parent, false
                ), viewType
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            SURAH -> {
                holder.count?.text = data[position].pos.toString()
                holder.name?.text = data[position].name
                holder.from?.text = "${data[position].revelation}   |   ${data[position].verse} VERSES"
                holder.arabic?.text = data[position].nameAr

                holder.itemView.setOnClickListener {
                    SurahActivity.launch(context, data[position].pos - 1, 0)
                }
            }
            else -> {
                data[position].let {
                    holder.run {
                        ayatNo?.text = it.ayat.toString()
                        arabic?.text = if (ApplicationData(context).arabic)
                            textToHtml(it.utsmani) else textToHtml(it.indopak)

                        if (ApplicationData(context).transliteration) {
                            pron?.text = it.latin
                            pron?.visibility = View.VISIBLE
                        } else {
                            pron?.visibility = View.GONE
                        }

                        surahName?.text = SurahHelper(context).readDataAt(it.surah)?.name

                        translation?.visibility = View.VISIBLE
                        translation?.text = textToHtml(it.trans)

                        maintainClicks(share, bookmark, holder, it)

                        arabic?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData(context).arabicFontSize)
                        pron?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData(context).transliterationFontSize)
                        translation?.setTextSize(TypedValue.COMPLEX_UNIT_SP, ApplicationData(context).translationFontSize)
                    }
                }
            }
        }
    }

    private fun maintainClicks(
        share: ImageView?,
        bookmark: ImageView?,
        holder: ViewHolder,
        it: SearchModel
    ) {
        share?.setOnClickListener { v ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, it.indopak)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }

        bookmark?.setImageDrawable(
            ResourcesCompat.getDrawable(
                context.resources,
                if (quran.readAyatNo(it.pos)!!.englishPro == "T")
                    R.drawable.ic_baseline_bookmark_24
                else R.drawable.ic_baseline_bookmark_border_24,
                null
            )
        )

        bookmark?.setOnClickListener { _ ->
            if (quran.readAyatNo(it.pos)!!.englishPro == "T") {
                quran.insertData(modelExchange(it), "F")
            } else quran.insertData(modelExchange(it), "T")
            holder.bookmark?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    if (quran.readAyatNo(it.pos)!!.englishPro == "T")
                        R.drawable.ic_baseline_bookmark_24
                    else R.drawable.ic_baseline_bookmark_border_24,
                    null
                )
            )
        }

        holder.itemView.setOnClickListener { _ ->
            SurahActivity.launch(context, it.surah - 1, it.ayat)
        }
    }

    private fun modelExchange(temp: SearchModel): Quran {
        return Quran(
            pos = temp.pos,
            surah = temp.surah,
            ayat = temp.ayat,
            indopak = temp.indopak,
            utsmani = temp.utsmani,
            jalalayn = temp.jalalayn,
            latin = temp.latin,
            terjemahan = temp.terjemahan,
            englishPro = temp.englishPro,
            englishT = temp.englishT
        )
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun textToHtml(it: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
        else Html.fromHtml(it)
    }

    companion object {
        const val AYAT = 1
        const val SURAH = 0
    }
}