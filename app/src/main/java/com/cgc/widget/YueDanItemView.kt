package com.cgc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.cgc.pojo.Model
import com.wsf.rubbish.R
import kotlinx.android.synthetic.main.item_history.view.*

/**
 * Description:悦单界面每个条目的自定义view
 */
class YueDanItemView :RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.item_history,this)
    }

    fun setData(data: Model) {
        //名称
        name.text = data.name
        //类别
        type.text = data.type
    }

}