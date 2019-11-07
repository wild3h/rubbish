package com.cgc.view

import com.cgc.pojo.Model

interface HistoryView {
    /**
     * 获取数据失败
     */
    fun onError(message: String?)

    /**
     * 初始化数据或刷新数据成功
     */
    fun loadSuccess(list: List<Model>?)

    /**
     * 加载数据成功
     */
    fun loadMore(list: List<Model>?)
}