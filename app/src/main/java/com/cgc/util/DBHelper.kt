package com.cgc.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteException
import com.wsf.rubbish.R
import java.io.FileOutputStream
import java.io.IOException


class DBHelper(
        var context: Context
        ,
        var name: String,
        var factory: SQLiteDatabase.CursorFactory?,
        var version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    private val DB_PATH = "/data/data/com.wsf.rubbish/databases/"
    private val DB_NAME = "temp.db"
    private var myDataBase: SQLiteDatabase? = null

    override fun onCreate(db: SQLiteDatabase) {
        //db.execSQL("CREATE TABLE IF NOT EXISTS IMAGE(IMAGE_ID INT PRIMARY KEY AUTOINCREMENT,PREV_IMAGE BLOB,IMAGE BLOB)")
        db.execSQL("CREATE TABLE IF NOT EXISTS MODEL(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(255) NOT NULL,TYPE VARCHAR(255) NOT NULL,IMAGE_ID VARCHAR(255) NOT NULL,DATE VARCHAR(255) NOT NULL) ")
        db.execSQL("CREATE TABLE IF NOT EXISTS TYPE(ID INTEGER PRIMARY KEY AUTOINCREMENT,ITEM VARCHAR(255),TYPE VARCHAR(255))")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
    }

    fun copyDataBase() {
        val myInput = context.resources.openRawResource(R.raw.temp)
        val outFileName = DB_PATH + DB_NAME
        val myOutput = FileOutputStream(outFileName)
        val buffer = ByteArray(1024)
        var length: Int = myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length = myInput.read(buffer)
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }
}