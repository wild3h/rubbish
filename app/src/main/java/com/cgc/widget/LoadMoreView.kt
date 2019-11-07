package com.cgc.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.wsf.rubbish.R

class LoadMoreView : RelativeLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, arrts: AttributeSet?) : super(context,arrts)
    constructor(context: Context?, arrts: AttributeSet?, defStyle:Int) : super(context,arrts,defStyle)
    init {
        View.inflate(context, R.layout.view_loadmore,this)
    }
}