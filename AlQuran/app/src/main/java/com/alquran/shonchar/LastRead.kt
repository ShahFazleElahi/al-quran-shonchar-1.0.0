package com.alquran.shonchar

import android.content.Context

class LastRead(val context: Context) {

    companion object {
        private const val DATA_NAME = "APPLICATION"
        private const val AYAT_NO = "AYAT_NO"
        private const val SURAH_NAME = "SURAH_NAME"
        private const val SURAH_NO = "SURAH_NO"
        private const val CURRENT = "CURRENT"
    }

    var surahName: String
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(SURAH_NAME, "Al-Faatiha")!!
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(SURAH_NAME, value)
            editor.apply()
        }

    var surahNo: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(SURAH_NO, 0)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(SURAH_NO, value)
            editor.apply()
        }

    var ayatNo: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(AYAT_NO, 0)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(AYAT_NO, value)
            editor.apply()
        }
}