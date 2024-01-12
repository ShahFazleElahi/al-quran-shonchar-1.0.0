package com.alquran.shonchar.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alquran.shonchar.SurahAyatAdapter
import com.alquran.shonchar.Constant
import com.alquran.shonchar.SurahName
import com.alquran.shonchar.ApplicationData
import com.alquran.shonchar.LastRead
import com.alquran.shonchar.model.Quran
import com.alquran.shonchar.QuranHelper
import com.alquran.shonchar.SurahHelper
import com.alquran.shonchar.KeyboardUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.alquran.shonchar.R
import com.alquran.shonchar.databinding.FragmentSurahAyatBinding
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.ixuea.android.downloader.DownloadService
import com.ixuea.android.downloader.callback.DownloadListener
import com.ixuea.android.downloader.domain.DownloadInfo
import com.ixuea.android.downloader.exception.DownloadException
import com.techiness.progressdialoglibrary.ProgressDialog
import java.io.File

class SurahAyat(private val position: Int, val ayat: Int, private val scroll: Boolean) : Fragment(),
    SurahAyatAdapter.PlayClickListener {

    private var player: SimpleExoPlayer? = null
    private var playButton: Button? = null
    private var pauseButton: Button? = null
    private var resumeButton: Button? = null
    private var search = ""
    private lateinit var smoothScroller: RecyclerView.SmoothScroller
    private val data = ArrayList<Quran>()
    private var ayatFollower: BroadcastReceiver? = null
    private lateinit var layoutManager: LinearLayoutManager
    private var adapterSurah: SurahAyatAdapter? = null
    private var binding: FragmentSurahAyatBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSurahAyatBinding.inflate(inflater, container, false)
        binding?.searchIcon?.clipToOutline = true

        layoutManager = LinearLayoutManager(requireContext())
        smoothScroller = object : LinearSmoothScroller(activity) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            data.add(
                Quran(
                    0, 0, 0, "", "",
                    "", "", "", "", ""
                )
            )
            data.addAll(QuranHelper(requireContext()).readSurahNo(position + 1))
            val temp = SurahHelper(requireContext()).readData()[position]
            val t = "${revelation(temp.revelation)}   |   ${
                NumberFormat.getInstance(
                    Locale(ApplicationData(requireContext()).language)
                ).format(temp.verse)
            }" +
                    "  " + resources.getString(R.string.verses)
            adapterSurah = SurahAyatAdapter(
                requireContext(),
                temp.name, SurahName().data()[position],
                t, data, this@SurahAyat
            )

            activity?.runOnUiThread {
                binding?.ayatRecycler?.layoutManager = layoutManager
                binding?.ayatRecycler?.adapter = adapterSurah
                if (scroll) binding?.ayatRecycler?.scrollToPosition(ayat)
            }
        }

        click()

        binding?.searchIcon?.setOnClickListener {
            binding?.searchText?.text.toString().let { e ->
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
            closeKeyboard(binding?.searchText)
        }

        player =
            SimpleExoPlayer.Builder(requireContext(), DefaultRenderersFactory(requireContext()))
                .setTrackSelector(DefaultTrackSelector(requireContext())).build()
        binding?.playerView?.setPlayer(player)
        binding?.fabBtn?.setOnClickListener {
            initializePlayer()
            playButton?.setOnClickListener(View.OnClickListener {
                player!!.playWhenReady = true
                playButton?.setEnabled(false)
                pauseButton?.setEnabled(true)
                resumeButton?.setEnabled(true)
            })
            pauseButton?.setOnClickListener(View.OnClickListener {
                player!!.playWhenReady = false
                playButton?.setEnabled(true)
                pauseButton?.setEnabled(false)
                resumeButton?.setEnabled(true)
            })
            resumeButton?.setOnClickListener(View.OnClickListener {
                player!!.playWhenReady = true
                playButton?.setEnabled(false)
                pauseButton?.setEnabled(true)
                resumeButton?.setEnabled(false)
            })
            binding?.fabBtn?.visibility = View.GONE
            binding?.playerLayid?.setVisibility(View.VISIBLE)
            player!!.playWhenReady = true
            playButton?.setEnabled(false)
            pauseButton?.setEnabled(true)
            resumeButton?.setEnabled(true)
        }

        return binding?.root
    }

    private fun revelation(revelation: String): String {
        return if (revelation == "Meccan")
            resources.getString(R.string.meccan)
        else resources.getString(R.string.medinan)
    }

    private fun click() {
        binding?.searchText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val key = binding?.searchText?.text.toString()
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
                closeKeyboard(binding?.searchText)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun filter(pos: Int) {
        val l = data.size
        data.clear()
        adapterSurah?.notifyItemRangeRemoved(0, l)
        data.add(
            Quran(
                0, 0, 0, "", "",
                "", "", "", "", ""
            )
        )
        data.addAll(QuranHelper(requireContext()).readSurahNo(position + 1))
        adapterSurah?.notifyItemRangeInserted(0, data.size)
        if (pos < data.size) {
            binding?.ayatRecycler?.scrollToPosition(pos)
        }
    }

    private fun filter(filter: String) {
        adapterSurah?.notifyItemRangeRemoved(0, data.size)
        CoroutineScope(Dispatchers.Default).launch {
            data.clear()
            data.add(
                Quran(
                    0, 0, 0, "", "",
                    "", "", "", "", ""
                )
            )
            val a = QuranHelper(requireContext()).readData().filter {
                when (ApplicationData(requireContext()).translation) {
                    ApplicationData.TAISIRUL -> it.terjemahan
                    ApplicationData.MUHIUDDIN -> it.jalalayn
                    else -> it.englishT.lowercase(Locale.getDefault())
                }.contains(filter.lowercase())
            }
            a.forEach {
                val temp = when (ApplicationData(requireContext()).translation) {
                    ApplicationData.TAISIRUL -> it.terjemahan
                    ApplicationData.MUHIUDDIN -> it.jalalayn
                    else -> it.englishT
                }
                val start = temp.lowercase(Locale.getDefault())
                    .indexOf(filter.lowercase(Locale.getDefault()))
                val translation = "${temp.substring(0, start)}<b><font color=#2979FF>" +
                        "${temp.substring(start, start + filter.length)}</font></b>${
                            temp.substring(
                                start + filter.length
                            )
                        }"

                data.add(
                    Quran(
                        pos = it.pos,
                        surah = it.surah,
                        ayat = it.ayat,
                        indopak = it.indopak,
                        utsmani = it.utsmani,
                        jalalayn = if (ApplicationData(requireContext()).translation
                            == ApplicationData.MUHIUDDIN
                        ) translation else it.jalalayn,
                        latin = it.latin,
                        terjemahan = if (ApplicationData(requireContext()).translation
                            == ApplicationData.TAISIRUL
                        ) translation else it.terjemahan,
                        englishPro = it.englishPro,
                        englishT = if (ApplicationData(requireContext()).translation
                            == ApplicationData.ENGLISH
                        ) translation else it.englishT
                    )
                )
            }
            requireActivity().runOnUiThread {
                adapterSurah?.notifyItemRangeRemoved(0, data.size)
                Toast.makeText(
                    context,
                    "${data.size} Search result found.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun closeKeyboard(edit: EditText?) {
        edit?.let {
            it.clearFocus()
            KeyboardUtils.hideKeyboard(it)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAG", (Constant.SURAH + position).toString())
        ayatFollower = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    binding?.appBar?.setExpanded(false, true)
                    smoothScroller.targetPosition = intent.getIntExtra("AYAT", 0) + 1
                    layoutManager.startSmoothScroll(smoothScroller)
                    adapterSurah?.read(intent.getIntExtra("AYAT", 0) + 1)
                }
            }
        }

        activity?.registerReceiver(ayatFollower, IntentFilter(Constant.SURAH + position))
    }

    override fun onPause() {
        activity?.unregisterReceiver(ayatFollower)
        if (LastRead(requireContext()).surahNo == position) {
            LastRead(requireContext()).ayatNo =
                layoutManager.findFirstCompletelyVisibleItemPosition().let {
                    if (it < 0)
                        layoutManager.findFirstVisibleItemPosition()
                    else it
                }
        }
        super.onPause()
    }

    override fun onDetach() {
        super.onDetach()
        binding = null
        player!!.release()
    }

    private fun initializePlayer() {
        val data = position + 1
        val strData = data.toString()
        val url =
            "https://firebasestorage.googleapis.com/v0/b/al-quran-shonchar-2b2df.appspot.com/o/" + strData + ".mp3?alt=media&token=e0fbe8e9-9b1d-4a6b-9dfa-8a10733503b7&fbclid=IwAR04eJZfNoxscXPGmlOcwYokddBYGwgd0g-rKi2AvlSBXx0WBsVKGIhlPEM"
        if (isAlreadyExists(url)) {
            initializePlayerWithDownloadedAudio(Uri.fromFile(getPrivateDir(url)))
        } else {
            downloadAudioUsingIxuea(url)
        }
    }

    private fun isAlreadyExists(url: String): Boolean {
        return getPrivateDir(url).exists()
    }

    private fun downloadAudioUsingIxuea(url: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.theme = ProgressDialog.THEME_DARK
        progressDialog.mode = ProgressDialog.MODE_DETERMINATE
        progressDialog.isCancelable = false
        progressDialog.hideProgressText()
        val downloadManager = DownloadService.getDownloadManager(requireContext())
        val downloadInfo =
            DownloadInfo.Builder().setUrl(url).setPath(getPrivateDir(url).absolutePath).build()
        downloadInfo.downloadListener = object : DownloadListener {
            override fun onStart() {
                progressDialog.show()
            }

            override fun onWaited() {}
            override fun onPaused() {}
            override fun onDownloading(progress: Long, size: Long) {
                if (progress == 0L) return
                val progressPercent = progress * 100 / size
                progressDialog.progress = progressPercent.toInt()
                progressDialog.show()
            }

            override fun onRemoved() {}
            override fun onDownloadSuccess() {
                progressDialog.dismiss()
                initializePlayerWithDownloadedAudio(Uri.fromFile(getPrivateDir(url)))
            }

            override fun onDownloadFailed(e: DownloadException) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "No internet connection Or \n" + e.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        downloadManager.download(downloadInfo)
    }

    private fun getPrivateDir(url: String): File {
        val fileName = url.substring(url.lastIndexOf('/') + 1)
        return File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
    }

    private fun initializePlayerWithDownloadedAudio(downloadedAudioUri: Uri) {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(
                requireContext(),
                Util.getUserAgent(requireContext(), "YourApplicationName")
            )
        val mediaSource: MediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(downloadedAudioUri)
            )
        player!!.setMediaSource(mediaSource)
        player!!.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        player!!.release()
    }

    override fun onPlayClicked(position: Int) {
        binding?.fabBtn?.callOnClick()
    }


}