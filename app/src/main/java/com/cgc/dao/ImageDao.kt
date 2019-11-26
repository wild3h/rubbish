package com.cgc.dao

import android.content.ContentValues
import com.cgc.pojo.Image
import com.cgc.pojo.ImageParser
import com.cgc.util.SQLUtil


object ImageDao {

    var imgPath = ""
    fun insert(img: Image) {
        val values = ContentValues()
        values.put("IMAGE_ID", img.IMAGE_ID)
        values.put("TYPE", img.url)
        SQLUtil.insertData("IMAGE", values)
    }

    fun queryImgByName(name: String): List<Image>? = SQLUtil.queryByCondition("IMAGE", "IMAGE_ID", name).parseList(ImageParser)


}

