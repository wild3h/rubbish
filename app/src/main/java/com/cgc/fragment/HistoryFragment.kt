package com.cgc.fragment

import android.view.View
import com.wsf.rubbish.R

class HistoryFragment: BaseFragment() {
    override fun initView(): View? {
        return View.inflate(context, R.layout.history, null)
    }

}