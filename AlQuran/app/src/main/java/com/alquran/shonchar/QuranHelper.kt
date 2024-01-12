package com.alquran.shonchar

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alquran.shonchar.model.Quran

class QuranHelper (context: Context)
    : SQLiteOpenHelper(context, "Al_Quran.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            ("CREATE TABLE " + "Quran") + " (" +
                    "pos INTEGER PRIMARY KEY, " +
                    "surah INTEGER," +
                    "ayat INTEGER," +
                    "indopak TEXT, " +
                    "utsmani TEXT, " +
                    "jalalayn TEXT, " +
                    "latin TEXT, " +
                    "terjemahan TEXT, " +
                    "englishPro TEXT, " +
                    "englishTran TEXT" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Quran")
        onCreate(db)
    }

    fun insertData(data: Quran, mark: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("pos", data.pos)
        contentValues.put("surah", data.surah)
        contentValues.put("ayat", data.ayat)
        contentValues.put("indopak", data.indopak)
        contentValues.put("utsmani", data.utsmani)
        contentValues.put("jalalayn", data.jalalayn)
        contentValues.put("latin", data.latin)
        contentValues.put("terjemahan", data.terjemahan)
        contentValues.put("englishPro", mark)
        contentValues.put("englishTran", data.englishT)
        db.update("Quran", contentValues, "pos = ${data.pos}", null)
        return true
    }

    fun readSurahNo(pos: Int): ArrayList<Quran> {
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM Quran WHERE surah == $pos"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        val data = ArrayList<Quran>()
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Quran(
                        pos = cursor.getInt(0),
                        surah = cursor.getInt(1),
                        ayat = cursor.getInt(2),
                        indopak = cursor.getString(3),
                        utsmani = cursor.getString(4),
                        jalalayn = cursor.getString(5),
                        latin = cursor.getString(6),
                        terjemahan = cursor.getString(7),
                        englishPro = cursor.getString(8),
                        englishT = cursor.getString(9),

                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readBookmark(): ArrayList<Quran> {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM Quran WHERE englishPro == ?"
        val cursor: Cursor = db.rawQuery(selectQuery,  arrayOf("T"))
        val data = ArrayList<Quran>()
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Quran(
                        pos = cursor.getInt(0),
                        surah = cursor.getInt(1),
                        ayat = cursor.getInt(2),
                        indopak = cursor.getString(3),
                        utsmani = cursor.getString(4),
                        jalalayn = cursor.getString(5),
                        latin = cursor.getString(6),
                        terjemahan = cursor.getString(7),
                        englishPro = cursor.getString(8),
                        englishT = cursor.getString(9)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readAyatNo(pos: Int): Quran? {
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM Quran WHERE pos == $pos"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        var data: Quran? = null
        if (cursor.moveToFirst()) {
            data = Quran(
                pos = cursor.getInt(0),
                surah = cursor.getInt(1),
                ayat = cursor.getInt(2),
                indopak = cursor.getString(3),
                utsmani = cursor.getString(4),
                jalalayn = cursor.getString(5),
                latin = cursor.getString(6),
                terjemahan = cursor.getString(7),
                englishPro = cursor.getString(8),
                englishT = cursor.getString(9)
            )
        }
        cursor.close()
        return data
    }

    fun readAyatXtoY(x: Int, y: Int): ArrayList<Quran> {
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM Quran WHERE pos BETWEEN $x AND $y"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        val data = ArrayList<Quran>()
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Quran(
                        pos = cursor.getInt(0),
                        surah = cursor.getInt(1),
                        ayat = cursor.getInt(2),
                        indopak = cursor.getString(3),
                        utsmani = cursor.getString(4),
                        jalalayn = cursor.getString(5),
                        latin = cursor.getString(6),
                        terjemahan = cursor.getString(7),
                        englishPro = cursor.getString(8),
                        englishT = cursor.getString(9)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }

    fun readData(): ArrayList<Quran> {
        val db = this.readableDatabase
        val selectQuery = "SELECT  * FROM Quran"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        val data = ArrayList<Quran>()
        if (cursor.moveToFirst()) {
            do {
                data.add(
                    Quran(
                        pos = cursor.getInt(0),
                        surah = cursor.getInt(1),
                        ayat = cursor.getInt(2),
                        indopak = cursor.getString(3),
                        utsmani = cursor.getString(4),
                        jalalayn = cursor.getString(5),
                        latin = cursor.getString(6),
                        terjemahan = cursor.getString(7),
                        englishPro = cursor.getString(8),
                        englishT = cursor.getString(9)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return data
    }
}