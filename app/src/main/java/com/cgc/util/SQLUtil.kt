package com.cgc.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.database.Cursor
import org.jetbrains.anko.db.SelectQueryBuilder
import org.jetbrains.anko.db.select


object SQLUtil {
    private lateinit var dbHelper: DBHelper

    private lateinit var db: SQLiteDatabase
    /**
     * 初始化数据
     */
    fun initSQLData(context: Context) {
        dbHelper = DBHelper(context, "test_db", null, 1)
        db = dbHelper.writableDatabase
    }

    fun insertData(type: String,values: ContentValues) {
        db.insert(type, null, values)
    }

    fun query(type: String): SelectQueryBuilder = db.select(type)

    fun queryByCondition(type: String,
                         condition: String,
                         name: String): SelectQueryBuilder {
        return db.select(type).whereSimple("$condition = ?", name)
    }

    fun queryCount(type: String): Cursor? {
        return db.query(type, arrayOf("count(*)"), null, null, null, null, null)
    }
}

