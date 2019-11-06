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
        SQLUtil.insertData("model", values)
    }

    fun getHistory(): List<Model>? = SQLUtil.query("MODEL").parseList(ModelParser)

    fun selectByName(name: String): List<Model>? = SQLUtil.queryByCondition("MODEL", "NAME", name).parseList(ModelParser)
}
