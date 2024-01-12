package com.alquran.shonchar

import android.content.Context

class UserData(val context: Context) {

    companion object {
        private const val DARK = "DARK"
        private const val QURAN = "QURAN"
        private const val DATA_NAME = "USER"
        private const val LAST_READ = "LAST_READ"
    }

    var quranLaunched: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(QURAN, false)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(QURAN, value)
            editor.apply()
        }

    var lastRead: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(LAST_READ, 0)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(LAST_READ, value)
            editor.apply()
        }

    var dark: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(DARK, false)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(DARK, value)
            editor.apply()
        }
}