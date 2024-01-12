package com.alquran.shonchar

import android.app.Activity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.alquran.shonchar.model.SearchModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class AlquranSearch(val context: Activity) {

    private var search = ""
    private var option: RadioGroup? = null
    private var adapter: SearchAdapter? = null
    private var searching: ProgressBar? = null
    private val data = ArrayList<SearchModel>()
    private var recyclerView: RecyclerView? = null

    fun searchSheet() {
        val searchSheetDialog = BottomSheetDialog(context, if (ApplicationData(context).darkTheme)
            R.style.bottomSheetDark else R.style.bottomSheet)
        searchSheetDialog.setContentView(R.layout.search_sheet)

        searchSheetDialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            option = bottomSheetDialog.findViewById(R.id.filter_group)
            recyclerView = bottomSheetDialog.findViewById(R.id.search_recycler)
            bottomSheetDialog.findViewById<ImageView>(R.id.search_icon)?.clipToOutline = true

            adapter = SearchAdapter(context, data)
            bottomSheetDialog.findViewById<RecyclerView>(R.id.search_recycler)
                ?.let { r->
                    r.layoutManager = LinearLayoutManager(context)
                    r.adapter = adapter
                }

            parentLayout?.let { layout ->
                val behaviour = BottomSheetBehavior.from(layout)
                val layoutParams = layout.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                layout.layoutParams = layoutParams

                behaviour.setBottomSheetCallback(
                    object : BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, newState: Int) {
                            stateChange(newState, behaviour)
                        }

                        override fun onSlide(bottomSheet: View, slideOffset: Float) {
                            Log.e("TAG", "Slide")
                        }
                    }
                )

                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }

            searchSheetDialog.findViewById<EditText>(R.id.search_text)
                ?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        val key = searchSheetDialog.findViewById<EditText>(R.id.search_text)!!.text.toString()
                        search = if (key.isNotEmpty()) {
                            try {
                                filter(key.toInt())
                            } catch (ex: Exception) {
                                filter(key)
                            }
                            key
                        } else {
                            filter("")
                            ""
                        }
                        closeKeyboard(searchSheetDialog.findViewById<EditText>(R.id.search_text))
                        return@OnEditorActionListener true
                    }
                    false
                })

            buttonClick(searchSheetDialog)

            filter(search)

            searchSheetDialog.findViewById<TextView>(R.id.closet)
                ?.let { close->
                    close.clipToOutline = true
                    close.setOnClickListener { searchSheetDialog.dismiss() }
                }
        }
        searchSheetDialog.show()
    }

    private fun stateChange(newState: Int, behaviour: BottomSheetBehavior<View>) {
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            (behaviour as BottomSheetBehavior<*>).state =
                BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun buttonClick(searchSheetDialog: BottomSheetDialog) {
        searchSheetDialog.findViewById<ImageView>(R.id.search_icon)
            ?.setOnClickListener {
                searchSheetDialog.findViewById<EditText>(R.id.search_text)
                    ?.text.toString().let { e->
                        search = if (e.isNotEmpty()) {
                            try {
                                filter(e.toInt())
                            } catch (ex: Exception) {
                                filter(e)
                            }
                            e
                        } else {
                            filter("")
                            ""
                        }
                    }
                closeKeyboard(searchSheetDialog.findViewById<EditText>(R.id.search_text))
            }

        option?.setOnCheckedChangeListener { _, _ ->
            try {
                filter(search.toInt())
            } catch (ex: Exception) {
                filter(search)
            }
        }
    }

    private fun filter(no: Int) {
        adapter?.notifyDataSetChanged()
        searching?.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            data.clear()
            when(option?.checkedRadioButtonId) {
                R.id.surah -> {
                    val a = SurahHelper(context).readData().filter {
                        it.pos == no
                    }
                    a.forEach {
                        data.add(
                            SearchModel(
                                type = SearchAdapter.SURAH,
                                pos = it.pos,
                                name = it.name,
                                revelation = it.revelation,
                                verse = it.verse,
                                nameAr = it.nameAr
                            )
                        )
                    }
                }
                R.id.ayat -> {
                    val a = QuranHelper(context).readData().filter {
                        it.ayat == no
                    }
                    a.forEach {
                        data.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = it.pos,
                                surah = it.surah,
                                ayat = it.ayat,
                                indopak = it.indopak,
                                utsmani = it.utsmani,
                                jalalayn = it.jalalayn,
                                latin = it.latin,
                                terjemahan = it.terjemahan,
                                englishPro = it.englishPro,
                                englishT = it.englishT,
                                trans = when(ApplicationData(context).translation) {
                                    ApplicationData.TAISIRUL -> it.terjemahan
                                    ApplicationData.MUHIUDDIN -> it.jalalayn
                                    else -> it.englishT
                                }
                            )
                        )
                    }
                }
            }
            context.runOnUiThread {
                searching?.visibility = View.GONE
                adapter?.notifyDataSetChanged()
                Toast.makeText(context,
                    "${data.size} Search result found."
                    , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filter(filter: String) {
        adapter?.notifyDataSetChanged()
        searching?.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            data.clear()
            when(option?.checkedRadioButtonId) {
                R.id.surah -> {
                    val a = SurahHelper(context).readData().filter {
                        it.name.contains(filter)
                    }
                    a.forEach {
                        data.add(
                            SearchModel(
                                type = SearchAdapter.SURAH,
                                pos = it.pos,
                                name = it.name,
                                revelation = it.revelation,
                                verse = it.verse,
                                nameAr = it.nameAr
                            )
                        )
                    }
                }

                R.id.ayat -> {
                    val a = QuranHelper(context).readData().filter {
                        it.indopak.contains(filter) //|| it.utsmani.contains(filter)
                    }
                    a.forEach {
                        val temp = it.indopak
                        val start = temp.indexOf(filter)
                        val indopak = "${temp.substring(0, start)}<b><font color=#2979FF>" +
                                "$filter</font></b>${temp.substring(start+filter.length)}"
                        data.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = it.pos,
                                surah = it.surah,
                                ayat = it.ayat,
                                indopak = indopak,
                                utsmani = it.utsmani,
                                latin = it.latin,
                                terjemahan = it.terjemahan,
                                englishPro = it.englishPro,
                                trans = when(ApplicationData(context).translation) {
                                    ApplicationData.TAISIRUL -> it.terjemahan
                                    ApplicationData.MUHIUDDIN -> it.jalalayn
                                    else -> it.englishT
                                }
                            )
                        )
                    }
                }

                R.id.meaning -> {
                    val a = QuranHelper(context).readData().filter {
                        when(ApplicationData(context).translation) {
                            ApplicationData.TAISIRUL -> it.terjemahan
                            ApplicationData.MUHIUDDIN -> it.jalalayn
                            else -> it.englishT.lowercase(Locale.getDefault())
                        }.contains(filter.lowercase())
                    }
                    a.forEach {
                        val temp = when(ApplicationData(context).translation) {
                            ApplicationData.TAISIRUL -> it.terjemahan
                            ApplicationData.MUHIUDDIN -> it.jalalayn
                            else -> it.englishT
                        }
                        val start = temp.lowercase(Locale.getDefault())
                            .indexOf(filter.lowercase(Locale.getDefault()))
                        val translation = "${temp.substring(0, start)}<b><font color=#2979FF>" +
                                "${temp.substring(start, start+filter.length)}</font></b>${temp.substring(start+filter.length)}"
                        data.add(
                            SearchModel(
                                type = SearchAdapter.AYAT,
                                pos = it.pos,
                                surah = it.surah,
                                ayat = it.ayat,
                                indopak = it.indopak,
                                utsmani = it.utsmani,
                                jalalayn = it.jalalayn,
                                latin = it.latin,
                                terjemahan = it.terjemahan,
                                englishPro = it.englishPro,
                                trans = translation
                            )
                        )
                    }
                }
            }
            context.runOnUiThread {
                searching?.visibility = View.GONE
                adapter?.notifyDataSetChanged()
                Toast.makeText(context,
                    "${data.size} Search result found."
                    , Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun closeKeyboard(edit: EditText?) {
        edit?.let {
            it.clearFocus()
            KeyboardUtils.hideKeyboard(it)
        }
    }
}