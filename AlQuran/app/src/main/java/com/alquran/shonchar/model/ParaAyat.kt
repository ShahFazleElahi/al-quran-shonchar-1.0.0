package com.alquran.shonchar.model

data class ParaAyat(
    val type: Int,
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
    val name: String,
    val meaning: String,
    val details: String
)