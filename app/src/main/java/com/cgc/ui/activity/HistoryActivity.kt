package com.cgc.ui.activity

import com.cgc.base.BaseActivity
import com.cgc.ui.fragment.HistoryFragment
import com.wsf.rubbish.R

class HistoryActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.history
    }

    override fun initData() {
        val transaction = supportFragmentManager.beginTransaction()
        val h = HistoryFragment()
        transaction.replace(R.id.HistoryFragment,h)
        transaction.commit()
    }
}