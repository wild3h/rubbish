package com.cgc.presenter.impl

import com.cgc.dao.ModelDao
import com.cgc.presenter.interf.HistoryPresenter
import com.cgc.view.HistoryView

class HistoryPresenterImpl(var historyView: HistoryView) : HistoryPresenter {
    override fun loadMore(offset: Int) {
        loadData()
    }

    override fun loadData() {
        val modelDao = ModelDao()
        modelDao.getHistory()?.also {
            historyView.loadMore(it)
        }
    }
}