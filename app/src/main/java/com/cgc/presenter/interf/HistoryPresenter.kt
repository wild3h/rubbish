package com.cgc.presenter.interf

interface HistoryPresenter {
    fun loadData()

    fun loadMore(offset: Int)
}