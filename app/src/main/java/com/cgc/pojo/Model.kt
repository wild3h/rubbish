package com.cgc.pojo

import org.jetbrains.anko.db.RowParser

class Model(
        var name: String,
        var type: String,
        var imageId:String
) {

}

object ModelParser : RowParser<Model> {
    override fun parseRow(columns: Array<Any?>): Model {
        return Model(columns[0] as String, columns[1] as String, columns[2] as String)
    }
}