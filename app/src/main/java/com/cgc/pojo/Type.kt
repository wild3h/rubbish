package com.cgc.pojo

import org.jetbrains.anko.db.RowParser

class Type(var ITEM: String, var TYPE: String) {
}

object TypeParser : RowParser<Type> {
    override fun parseRow(columns: Array<Any?>): Type {
        return Type(columns[0] as String, columns[1] as String)
    }
}