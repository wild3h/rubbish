package com.cgc.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(
        var context: Context,
        var name: String,
        var factory: SQLiteDatabase.CursorFactory?,
        var version: Int
) : SQLiteOpenHelper(context, name, factory, version) {


    override fun onCreate(db: SQLiteDatabase) {
        //db.execSQL("CREATE TABLE IF NOT EXISTS IMAGE(IMAGE_ID INT PRIMARY KEY AUTOINCREMENT,PREV_IMAGE BLOB,IMAGE BLOB)")
        db.execSQL("CREATE TABLE IF NOT EXISTS MODEL(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(255),TYPE VARCHAR(255),IMAGE_ID VARCHAR(255))")
        db.execSQL("CREATE TABLE IF NOT EXISTS TYPE(ID INTEGER PRIMARY KEY AUTOINCREMENT,ITEM VARCHAR(255),TYPE VARCHAR(255))")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
    }

}