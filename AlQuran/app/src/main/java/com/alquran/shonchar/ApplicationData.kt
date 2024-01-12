package com.alquran.shonchar

import android.content.Context
import android.preference.PreferenceManager

class ApplicationData(val context: Context) {

    companion object {
        private const val DATA_NAME = "APPLICATION"
        private const val ICON_TYPE = "ICON_TYPE"
        private const val ARABIC_TYPE = "ARABIC_TYPE"
        private const val TRANSLATION = "TRANSLATION"
        private const val ARABIC_FONT_SIZE = "FONT_SIZE"
        private const val TRANSLITERATION_FONT_SIZE = "TRANSLITERATION_FONT_SIZE"
        private const val TRANSLATION_FONT_SIZE = "TRANSLATION_FONT_SIZE"
        private const val TRANSLITERATION = "TRANSLITERATION"
        private const val PRIMARY_COLOR = "PRIMARY_COLOR"
        private const val DARK_THEME = "DARK_THEME"
        private const val LANGUAGE = "LANGUAGE"

        //Arabic
        const val OFF = 0
        const val TAISIRUL = 1
        const val MUHIUDDIN = 2
        const val ENGLISH = 3
        //Color
        const val PURPLE = 0
        const val BLUE = 1
        const val ORANGE = 2
    }

    var language: String
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getString(LANGUAGE, "en")!!
        }
        set(value) {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = sharedPref.edit()
            editor.putString(LANGUAGE, value)
            editor.apply()
        }

    var darkTheme: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(DARK_THEME, false)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(DARK_THEME, value)
            editor.apply()
        }

    var arabicFontSize: Float
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getFloat(ARABIC_FONT_SIZE, 28f)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putFloat(ARABIC_FONT_SIZE, value)
            editor.apply()
        }

    var transliterationFontSize: Float
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getFloat(TRANSLITERATION_FONT_SIZE, 18f)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putFloat(TRANSLITERATION_FONT_SIZE, value)
            editor.apply()
        }

    var translationFontSize: Float
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getFloat(TRANSLATION_FONT_SIZE, 18f)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putFloat(TRANSLATION_FONT_SIZE, value)
            editor.apply()
        }

    var vectorIcon: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(ICON_TYPE, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(ICON_TYPE, value)
            editor.apply()
        }

    var arabic: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(ARABIC_TYPE, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(ARABIC_TYPE, value)
            editor.apply()
        }

    var transliteration: Boolean
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getBoolean(TRANSLITERATION, true)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putBoolean(TRANSLITERATION, value)
            editor.apply()
        }

    var translation: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(TRANSLATION, MUHIUDDIN)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(TRANSLATION, value)
            editor.apply()
        }

    var primaryColor: Int
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getInt(PRIMARY_COLOR, PURPLE)
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putInt(PRIMARY_COLOR, value)
            editor.apply()
        }
}