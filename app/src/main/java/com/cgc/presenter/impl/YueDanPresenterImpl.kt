package com.cgc.presenter.impl

import com.cgc.dao.ModelDao
import com.cgc.pojo.Model
import com.cgc.presenter.interf.YueDanPresenter
import com.cgc.view.HistoryView

class YueDanPresenterImpl(var historyView: HistoryView): YueDanPresenter {
    override fun loadMore(offset: Int) {
        val modelDao = ModelDao()
        val history: List<Model>? = modelDao.getHistory()
        if (history != null) {
            historyView!!.loadMore(history)
        }
    }

    override fun loadDatas() {
        val modelDao = ModelDao()
        val history: List<Model>? = modelDao.getHistory()
        if (history != null) {
            historyView!!.loadSuccess(history)
        }
    }
}