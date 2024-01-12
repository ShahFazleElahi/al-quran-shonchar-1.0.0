package com.alquran.shonchar.model

data class Quran(
    val pos: Int,
    val surah: Int,
    val ayat: Int,
    val indopak: String,
    val utsmani: String,
    val jalalayn: String,
    val latin: String,
    val terjemahan: String,
    val englishPro: String,
    val englishT: String,
    var reading: Boolean = false
)