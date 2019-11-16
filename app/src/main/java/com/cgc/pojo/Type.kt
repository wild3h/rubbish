package com.cgc.pojo

import org.jetbrains.anko.db.RowParser

data class Type(var ITEM: String, var TYPE: String)

object TypeParser : RowParser<Type> {
    override fun parseRow(columns: Array<Any?>): Type {
        return Type(columns[1] as String, columns[2] as String)
    }
}