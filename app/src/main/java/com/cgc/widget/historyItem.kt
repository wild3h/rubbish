package com.cgc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.cgc.pojo.Model
import com.wsf.rubbish.R

class historyItem: RelativeLayout {
    constructor(context: Context?):super(context)
    constructor(context: Context?, attrs: AttributeSet?):super(context,attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr:Int):super(context,attrs,defStyleAttr)

    /**
     * 初始化方法
     */
    init {
        View.inflate(context, R.layout.history,this)
    }

    /**
     * 刷新条目view数据
     */
    fun setData(data: Model) {
//        //歌曲名称
//        name.setText(data.name)
//        //简介
//        desc.setText(data.description)
//        //获取图片
//        Picasso.get().load(data.posterPic).into(bg)
    }
}