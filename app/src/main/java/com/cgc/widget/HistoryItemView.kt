package com.cgc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.cgc.pojo.Model
import com.wsf.rubbish.R
import kotlinx.android.synthetic.main.history.view.*

class HistoryItemView: RelativeLayout {
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
        //名称
        name.text = data.name
        //类别
        type.text = data.type
    }


}