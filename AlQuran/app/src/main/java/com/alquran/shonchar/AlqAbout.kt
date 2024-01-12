package com.alquran.shonchar

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alquran.shonchar.Constant.Companion.EMAIL
import com.alquran.shonchar.Constant.Companion.FACEBOOK
import com.alquran.shonchar.Constant.Companion.FACEBOOK_WEB
import com.alquran.shonchar.Constant.Companion.INSTAGRAM
import com.alquran.shonchar.Constant.Companion.INSTAGRAM_WEB
import com.alquran.shonchar.Constant.Companion.PHONE
import com.alquran.shonchar.Constant.Companion.TWITTER
import com.alquran.shonchar.Constant.Companion.TWITTER_WEB
import com.alquran.shonchar.databinding.AboutXmlBinding
import java.util.*

class AlqAbout : AppCompatActivity() {

    private lateinit var binding: AboutXmlBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        binding = AboutXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(ApplicationData(newBase!!).language)
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }
}