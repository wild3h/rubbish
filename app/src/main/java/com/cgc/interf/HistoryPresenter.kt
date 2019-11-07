package com.cgc.interf

interface HistoryPresenter {
    companion object{
        val TYPE_INIT_OR_REFRESH = 1
        val TYPE_LOAD_MORE = 2
    }

    fun loadDatas()

    fun loadMore(offset: Int)
}