package com.alquran.shonchar

import android.content.ClipData
import android.content.ClipboardManager
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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.alquran.shonchar.model.Quran
import java.text.NumberFormat
import java.util.Locale

class SurahAyatAdapter(
    val context: Context,
    val name: String,
    val meaning: String,
    private val details: String,
    val data: ArrayList<Quran>,
    private val playClickListener: PlayClickListener
) : RecyclerView.Adapter<SurahAyatAdapter.ViewHolder>() {

    private var reading = -1
    private val quran = QuranHelper(context)

    inner class ViewHolder(view: View, val type: Int) : RecyclerView.ViewHolder(view) {

        var name: TextView? = null
        var details: TextView? = null
        var meaning: TextView? = null
        var pron: TextView? = null
        var play: ImageView? = null
        var share: ImageView? = null
        var copy: ImageView? = null
        var arabic: TextView? = null
        var ayatNo: TextView? = null
        var bookmark: ImageView? = null
        var translation: TextView? = null

        init {
            if (type > 0) {
                pron = view.findViewById(R.id.pron)
                play = view.findViewById(R.id.play)
                share = view.findViewById(R.id.share)
                copy = view.findViewById(R.id.copybtn)
                arabic = view.findViewById(R.id.arabic)
                ayatNo = view.findViewById(R.id.ayat_no)
                bookmark = view.findViewById(R.id.bookmark)
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

    interface PlayClickListener {
        fun onPlayClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                if (viewType == 0) R.layout.layout_ayat_header
                else {
//                        if (viewType == reading)
                    R.layout.layout_ayat_reading
//                        else  R.layout.layout_ayat_reading
                }, parent, false
            ), viewType
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) > 0) {
            data[position].let {
                holder.run {
                    if (it.reading) {
                        itemView.findViewById<LinearLayout>(R.id.main_layout).background =
                            ResourcesCompat.getDrawable(
                                context.resources, R.drawable.reading_background, null
                            )
                    } else itemView.findViewById<LinearLayout>(R.id.main_layout).background = null
                    ayatNo?.text = numberFormat.format(it.ayat)//it.ayat.toString()
                    arabic?.text = if (ApplicationData(context).arabic) it.utsmani else it.indopak

                    if (ApplicationData(context).transliteration) {
                        pron?.text = it.latin
                        pron?.visibility = View.VISIBLE
                        translation?.visibility = View.VISIBLE
                        translation(translation, it)
                    } else {
                        translation?.visibility = View.GONE
                        pron?.visibility = View.VISIBLE
                        setPron(pron, it)
                    }

                    maintainClicks(play, copy, share, bookmark, it)
                    arabic?.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP, ApplicationData(context).arabicFontSize
                    )
                    pron?.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP, ApplicationData(context).transliterationFontSize
                    )
                    translation?.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP, ApplicationData(context).translationFontSize
                    )
                }
            }
        } else {
            holder.name?.text = name
            holder.details?.text = details
            holder.meaning?.text = meaning
        }

        holder.play?.setOnClickListener {
            playClickListener.onPlayClicked(position)
        }

    }

    private fun translation(translation: TextView?, it: Quran) {
        translation?.text = when (ApplicationData(context).translation) {
            ApplicationData.TAISIRUL -> textToHtml(it.terjemahan)
            ApplicationData.MUHIUDDIN -> textToHtml(it.jalalayn)
            ApplicationData.ENGLISH -> textToHtml(it.englishT)
            else -> {
                translation?.visibility = View.GONE
                ""
            }
        }
    }

    private fun setPron(pron: TextView?, it: Quran) {
        pron?.text = when (ApplicationData(context).translation) {
            ApplicationData.TAISIRUL -> textToHtml(it.terjemahan)
            ApplicationData.MUHIUDDIN -> textToHtml(it.jalalayn)
            ApplicationData.ENGLISH -> textToHtml(it.englishT)
            else -> {
                pron?.visibility = View.GONE
                ""
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun textToHtml(it: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(
            it, Html.FROM_HTML_MODE_COMPACT
        )
        else Html.fromHtml(it)
    }

    private fun maintainClicks(
        play: ImageView?, copy: ImageView?, share: ImageView?, bookmark: ImageView?, it: Quran
    ) {
        share?.setOnClickListener { v ->
            var text = "${context.resources.getString(R.string.surah)}: $name, " + "${
                context.resources.getString(R.string.ayat)
            }: ${it.ayat}\n\n"
            text += if (ApplicationData(context).arabic) it.utsmani else it.indopak
            text += "\n\n${context.resources.getString(R.string.meaning)} " + ":  " + when (ApplicationData(
                context
            ).translation) {
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
            var text = "${context.resources.getString(R.string.surah)}: $name, " + "${
                context.resources.getString(R.string.ayat)
            }: ${it.ayat}\n\n"
            text += if (ApplicationData(context).arabic) it.utsmani else it.indopak
            text += "\n\n${context.resources.getString(R.string.meaning)} " + ":  " + when (ApplicationData(
                context
            ).translation) {
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
                if (this.quran.readAyatNo(it.pos)!!.englishPro == "T") R.drawable.ic_baseline_bookmark_24
                else R.drawable.ic_baseline_bookmark_border_24,
                null
            )
        )

        bookmark?.setOnClickListener { _ ->
            if (this.quran.readAyatNo(it.pos)!!.englishPro == "T") {
                this.quran.insertData(it, "F")
            } else this.quran.insertData(it, "T")
            bookmark.setImageDrawable(
                ResourcesCompat.getDrawable(
                    context.resources,
                    if (this.quran.readAyatNo(it.pos)!!.englishPro == "T") R.drawable.ic_baseline_bookmark_24
                    else R.drawable.ic_baseline_bookmark_border_24,
                    null
                )
            )
        }

        /*play?.setOnClickListener { _ ->
            AudioProcess(context as Activity).play(it.surah, it.ayat)
        }*/
    }

    fun read(pos: Int) {
        data[pos] = data[pos].apply { reading = true }
        if (reading > 0) {
            data[reading] = data[reading].apply { reading = false }
        }
        notifyItemChanged(pos)
        notifyItemChanged(reading)
        reading = pos
    }

    private val numberFormat: NumberFormat =
        NumberFormat.getInstance(Locale(ApplicationData(context).language))
}