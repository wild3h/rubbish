package com.cgc.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.database.Cursor
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select


object SQLUtil {
    lateinit var dbHelper: DBHelper

    lateinit var db: SQLiteDatabase
    /**
     * 初始化数据
     */
    fun initSQLData(context: Context) {
        dbHelper = DBHelper(context, "temp.db", null, 1)
        db = dbHelper.writableDatabase
    }

    fun insertData(table: String, values: ContentValues) {
        db.insert(table, null, values)
    }

    fun queryAll(table: String): SelectQueryBuilder = db.select(table)

    fun queryByCondition(table: String,
                         condition: String,
                         name: String): SelectQueryBuilder {
        return db.select(table).whereSimple("$condition = ?", name)
    }

    fun queryCount(table: String): Cursor? {
        return db.query(table, arrayOf("count(*)"), null, null, null, null, null)
    }

    fun delete(table :String){
        db.delete(table,null,null)
    }
}

