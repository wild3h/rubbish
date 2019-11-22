package com.cgc.ui.activity

import com.cgc.base.BaseActivity
import com.cgc.ui.fragment.HistoryFragment
import com.cgc.util.SQLUtil
import com.wsf.Until.SetStatusBarLightMode.MIUISetStatusBarLightMode
import com.wsf.rubbish.R

class HistoryActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.history
    }

    override fun initData() {
        SQLUtil.initSQLData(this)
        val transaction = supportFragmentManager.beginTransaction()
        val y = HistoryFragment()
        transaction.replace(R.id.HistoryFragment,y)
        transaction.commit()
        MIUISetStatusBarLightMode(this,true)
    }
}