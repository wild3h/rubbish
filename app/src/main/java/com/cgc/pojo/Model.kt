package com.cgc.pojo

import org.jetbrains.anko.db.RowParser
import java.util.*

data class Model(
        var name: String,
        var type: String,
        var imageId: String,
        var date: String
) {

}

object ModelParser : RowParser<Model> {
    override fun parseRow(columns: Array<Any?>): Model {
        return Model(columns[1] as String, columns[2] as String, columns[3] as String, columns[4] as String)
    }
}