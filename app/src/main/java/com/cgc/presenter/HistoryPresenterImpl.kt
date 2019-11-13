package com.cgc.presenter

import com.cgc.dao.ModelDao
import com.cgc.interf.HistoryPresenter
import com.cgc.pojo.Model
import com.cgc.view.HistoryView

class HistoryPresenterImpl(private var historyView: HistoryView?) : HistoryPresenter {
    /**
     * 解绑view和presenter
     */
    fun destoryView() {
        if (historyView != null) {
            historyView = null
        }
    }

    /**
     * 初始化数据或刷新数据
     */
    override fun loadDatas() {
        val modelDao = ModelDao()
        val history: List<Model>? = modelDao.getHistory()
        if (history != null) {
            historyView!!.loadSuccess(history)
        }
    }

    override fun loadMore(offset: Int) {
        val modelDao = ModelDao()
        val history: List<Model>? = modelDao.getHistory()
        if (history != null) {
            historyView!!.loadMore(history)
        }
    }

}