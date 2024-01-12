package com.alquran.shonchar

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import com.alquran.shonchar.databinding.ActivityQuranMainBinding
import com.google.android.material.tabs.TabLayout
import com.alquran.shonchar.fragment.Bookmark
import com.alquran.shonchar.fragment.Para
import com.alquran.shonchar.fragment.Surah
import java.text.NumberFormat
import java.util.*

class QuranMain : AppCompatActivity() {

    private var lang = ""
    private var dark = false
    private var currentTheme = 0

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when(result.resultCode) {
                RESULT_OK -> {
                    if (dark != ApplicationData(this).darkTheme
                        || currentTheme != ApplicationData(this).primaryColor
                        || lang != ApplicationData(this).language)
                        reCreate()
                }
            }
        }

    private var binding: ActivityQuranMainBinding? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        ApplicationData(this).run {
            dark = darkTheme
            lang = language
            currentTheme = primaryColor
        }
        binding = ActivityQuranMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.let {
            it.settings.clipToOutline = true
            it.headerCard.clipToOutline = true
            it.back.setOnClickListener { finish() }

            it.quranPager.adapter = PagerAdapter(
                supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ).apply {
                addFragment(Surah())
                addFragment(Para())
                addFragment(Bookmark())
            }
            it.quranPager.offscreenPageLimit = 3
            it.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

            it.quranPager.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(binding?.tabLayout)
            )

            it.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) { /****/ }

                override fun onTabUnselected(tab: TabLayout.Tab?) { /****/ }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    it.quranPager.currentItem = tab.position
                }
            })

            it.search.setOnClickListener {
                AlquranSearch(this).searchSheet()
            }

            it.settings.setOnClickListener {
                startForResult.launch(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
            }
        }
    }

    private fun reCreate() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this,
                    QuranMain::class.java
                )
            )
            Objects.requireNonNull(
                overridePendingTransition(
                    R.anim.fade_in,
                    R.anim.fade_out
                )
            )
            finish()
        }, 150)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding?.let {
            LastRead(this).let { last->
                it.surahName.text = resources.getStringArray(R.array.surah_name)[last.surahNo] //last.surahName
                it.ayahNo.text =
                    if (last.ayatNo == 0) "${resources.getString(R.string.ayat_no)}: " + NumberFormat.getInstance(Locale(
                        ApplicationData(this).language)).format(1)
                    else "${resources.getString(R.string.ayat_no)}: " + NumberFormat.getInstance(Locale(
                        ApplicationData(this).language)).format(last.ayatNo)
                it.headerCard.setOnClickListener { _ ->
                    SurahActivity.launch(this, last.surahNo, last.ayatNo)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(ApplicationData(newBase!!).language)
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }
}