package com.cgc.dao

import android.content.ContentValues
import com.cgc.pojo.Model
import com.cgc.pojo.ModelParser
import com.cgc.util.SQLUtil

class ModelDao {

    fun insert(model: Model) {
        val values = ContentValues()
        values.put("NAME", model.name)
        values.put("TYPE", model.type)
        values.put("IMAGE_ID", model.imageId)
        values.put("DATE", model.date)
        SQLUtil.insertData("MODEL", values)
    }

    fun getHistory(): List<Model>? = SQLUtil.queryAll("MODEL").parseList(ModelParser)

    fun deleteAllHistory(){
        SQLUtil.delete("MODEL")
    }

    fun selectByName(name: String): List<Model>? = SQLUtil.queryByCondition("MODEL", "NAME", name).parseList(ModelParser)

    fun getHistoryCount(): Int {
        val result = SQLUtil.queryCount("MODEL")
        result?.moveToNext()
        var count = 0
        if (result != null) {
            count = result.getInt(0)
        }
        result?.close()
        return count
    }
}
