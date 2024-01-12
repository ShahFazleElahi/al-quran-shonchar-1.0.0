package com.alquran.shonchar

import android.content.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApplicationTheme(this)
        setContentView(R.layout.splash_alquran)
        launch()
    }

    private fun launch() {
        CoroutineScope(Dispatchers.Default).launch {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(
                        this@SplashActivity, activity()
                    )
                )
                finish()
            }, 1000)
        }
    }

    private fun activity(): Class<*> {
        return if (UserData(this@SplashActivity).quranLaunched) {
            try {
                if (SurahHelper(this@SplashActivity).readData().size == 114 && QuranHelper(this@SplashActivity).readData().size == 6236)
                    QuranMain::class.java
                else QuranActivity::class.java
            } catch (e: Exception) {
                QuranActivity::class.java
            }
        } else QuranActivity::class.java
    }

    override fun attachBaseContext(newBase: Context?) {
        val localeToSwitchTo = Locale(ApplicationData(newBase!!).language)
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }
}