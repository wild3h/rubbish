package com.cgc.dao

import com.cgc.pojo.Type
import com.cgc.pojo.TypeParser
import com.cgc.util.SQLUtil

class TypeDao {
    fun selectByName(name: String): List<Type>? = SQLUtil.queryByCondition("TYPE", "ITEM", name).parseList(TypeParser)
}