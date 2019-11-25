package com.cgc.dao

import android.content.ContentValues
import android.content.Context
import com.cgc.pojo.Type
import com.cgc.pojo.TypeParser
import com.cgc.util.SQLUtil
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.wsf.rubbish.R
import java.io.InputStreamReader


class TypeDao {

    inline fun <reified T> KTypeToken() = object : TypeToken<T>() {}.type

    fun insert(type: Type) {
        val values = ContentValues()
        values.put("ITEM", type.ITEM)
        values.put("TYPE", type.TYPE)
        SQLUtil.insertData("TYPE", values)
    }

    fun initType(context: Context) {
        val s = context.resources.openRawResource(R.raw.type)
        val gson = JsonParser()
        val goodList = gson.parse(JsonReader(InputStreamReader(s))).asJsonObject.get("type").asJsonArray
        goodList.forEach {
            val obj = it.asJsonObject
            val good = obj.get("good").asString
            val t = obj.get("type").asString
            val type = Type(good, t)
            insert(type)
        }
    }


    fun selectByName(name: String): List<Type>? = SQLUtil.queryByCondition("TYPE", "ITEM", name).parseList(TypeParser)

    fun vagueQuery(name: String) : List<Type>? = SQLUtil.vagueQuery("TYPE", "ITEM", "%$name%").parseList(TypeParser)
}