package com.cgc.pojo

import org.jetbrains.anko.db.RowParser

data class Image(var IMAGE_ID: String, var url: String) {
}

object ImageParser : RowParser<Image> {
    override fun parseRow(columns: Array<Any?>): Image {
        return Image(columns[0] as String, columns[1] as String)
    }
}