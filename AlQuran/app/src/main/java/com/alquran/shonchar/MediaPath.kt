package com.alquran.shonchar

import android.content.Context

class MediaPath(val context: Context) {

    companion object {
        private const val DATA_NAME = "MEDIA_DATA"
        private const val PLAY = "PLAY"
    }

    var lastRead: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(PLAY, "")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(PLAY, value)
            editor.apply()
        }
}