package com.cgc.util

import com.cgc.adapter.BaseFragment
import com.cgc.ui.fragment.HistoryFragment


/**
 * Description:管理fragment的util类
 */
class FragmentUtil(){
    val historyFragment by lazy { HistoryFragment() }

    companion object{
        val fragmentUtil by lazy { FragmentUtil() }
    }

    /**
     * 根据tabId获取对应的fragment
     */
    fun getFragment(tabId:Int): BaseFragment?{
        return historyFragment
    }
}