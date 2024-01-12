package com.alquran.shonchar

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.alquran.shonchar.model.ParaAyat
import com.alquran.shonchar.model.Quran
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ParaAyatAdapter(val context: Context, val data: ArrayList<ParaAyat>)
    : RecyclerView.Adapter<ParaAyatAdapter.ViewHolder>() {

    companion object {
        const val BISMILLAH = 1
        const val DEFAULT = 0
    }

    private val quran = QuranHelper(context)

    inner class ViewHolder(view: View,  val type: Int): RecyclerView.ViewHolder(view) {
        var name: TextView? = null
        var details: TextView? = null
        var meaning: TextView? = null

        var pron: TextView? = null
        var play: ImageView? = null
        var share: ImageView? = null
        var copy: ImageView? = null
        var arabic: TextView? = null
        var ayatNo: TextView? = null
        var surahName: TextView? = null
        var bookmark: ImageView? = null
        var translation: TextView? = null

        init {
            if (type == DEFAULT) {
                pron = view.findViewById(R.id.pron)
                play = view.findViewById(R.id.play)
                share = view.findViewById(R.id.share)
                copy = view.findViewById(R.id.copybtn)
                arabic = view.findViewById(R.id.arabic)
                ayatNo = view.findViewById(R.id.ayat_no)
                bookmark = view.findViewById(R.id.bookmark)
                surahName = view.findViewById(R.id.surah_name)
                translation = view.findViewById(R.id.translation)

                play!!.clipToOutline = true
                share!!.clipToOutline = true
                copy!!.clipToOutline = true
                bookmark!!.clipToOutline = true
            } else {
                name = view.findViewById(R.id.name)
                details = view.findViewById(R.id.details)
                meaning = view.findViewById(R.id.meaning)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    if (viewType == BISMILLAH)
                        R.layout.layout_ayat_header
                    else R.layout.layout_ayat,
                    parent, false
                ), viewType
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            data[position].let {
                if (data[position].type == DEFAULT) {
                    holder.run {
                        ayatNo?.text = numberFormat.format(it.ayat)
                        arabic?.text = it.indopak
                        pron?.text = it.latin
//            holder.translation?.text = data[position].englishT
//                    translation?.text = it.terjemahan
                        surahName?.text = SurahHelper(context).readDataAt(it.surah)?.name
                        translation?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(it.terjemahan, Html.FROM_HTML_MODE_COMPACT)
                        } else Html.fromHtml(it.terjemahan)

                        maintainClicks(share,copy, bookmark, it)
                    }
                } else {
                    holder.name?.text = it.name
                    holder.details?.text = it.details
                    holder.meaning?.text = it.meaning
                }
            }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun modelExchange(temp: ParaAyat): Quran {
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

    private fun maintainClicks(share: ImageView?, copy: ImageView?, bookmark: ImageView?, it: ParaAyat) {
        share?.setOnClickListener { v ->
            var text = "Surah-> ${SurahHelper(context).readDataAt(it.surah)!!.name}," +
                    " Ayat-> ${it.ayat}\n\n"
            text += if (ApplicationData(context).arabic) it.utsmani else it.indopak
            text += "\n\nঅর্থ :  "+when(ApplicationData(context).translation) {
                ApplicationData.TAISIRUL -> it.terjemahan
                ApplicationData.MUHIUDDIN -> it.jalalayn
                ApplicationData.ENGLISH -> it.englishT
                else -> ""
            }
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, text)
            sendIntent.type = "text/plain"
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }

        copy?.setOnClickListener { v ->
            val clipboardManager =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var text = "Surah-> ${SurahHelper(context).readDataAt(it.surah)!!.name}," +
                    " Ayat-> ${it.ayat}\n\n"
            text += if (ApplicationData(context).arabic) it.utsmani else it.indopak
            text += "\n\nঅর্থ :  "+when(ApplicationData(context).translation) {
                ApplicationData.TAISIRUL -> it.terjemahan
                ApplicationData.MUHIUDDIN -> it.jalalayn
                ApplicationData.ENGLISH -> it.englishT
                else -> ""
            }
            val clipData = ClipData.newPlainText("Ayat", text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
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
            bookmark.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    if (quran.readAyatNo(it.pos)!!.englishPro == "T")
                        R.drawable.ic_baseline_bookmark_24
                    else R.drawable.ic_baseline_bookmark_border_24,
                    null
                )
            )
        }
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(ApplicationData(context).language))
}