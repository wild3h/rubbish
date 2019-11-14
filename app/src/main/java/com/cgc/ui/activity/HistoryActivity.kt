package com.cgc.ui.activity

import com.cgc.base.BaseActivity
import com.cgc.ui.fragment.YueDanFragment
import com.cgc.util.DBHelper
import com.cgc.util.SQLUtil
import com.wsf.rubbish.R

class HistoryActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.history
    }

    override fun initData() {
        SQLUtil.initSQLData(this)
        val transaction = supportFragmentManager.beginTransaction()
        val y = YueDanFragment()
        transaction.replace(R.id.HistoryFragment,y)
        transaction.commit()
    }
}