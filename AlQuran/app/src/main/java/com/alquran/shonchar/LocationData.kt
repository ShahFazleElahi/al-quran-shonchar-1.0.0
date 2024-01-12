package com.alquran.shonchar

import android.content.Context

class LocationData(val context: Context) {

    companion object {
        private const val DATA_NAME = "LOCATION_DATA"
        private const val CITY_NAME = "CITY_NAME"
        private const val COUNTRY_NAME = "COUNTRY_NAME"
    }

    var cityName: String?
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(CITY_NAME, "Not Found")
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(cityName, value)
            editor.apply()
        }

    var countryName: String?
        get() {
            val prefs = context.getSharedPreferences(DATA_NAME, 0)
            return prefs.getString(COUNTRY_NAME, "Not Found")
        }
        set(value) {
            val sharedPref = context.getSharedPreferences(DATA_NAME, 0)
            val editor = sharedPref.edit()
            editor.putString(COUNTRY_NAME, value)
            editor.apply()
        }
}