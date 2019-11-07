package com.cgc.presenter

import com.cgc.fragment.HistoryFragment
import com.cgc.interf.HistoryPresenter
import com.cgc.net.ResponseHandle
import com.cgc.pojo.Model
import com.cgc.view.HistoryView

class HistoryPresenterImpl(var historyView: HistoryView?) : HistoryPresenter,
        ResponseHandle<List<Model>> {
    /**
     * 解绑view和presenter
     */
    fun destoryView() {
        if (historyView != null) {
            historyView = null
        }
    }

    override fun onError(type: Int, msg: String?) {
        historyView?.onError(msg)
    }

    /**
     * 加载数据成功
     */
    override fun onSuccess(type: Int, result: List<Model>) {
        when (type) {
            HistoryPresenter.TYPE_INIT_OR_REFRESH -> historyView?.loadSuccess(result)
            HistoryPresenter.TYPE_LOAD_MORE -> historyView?.loadMore(result)
        }
    }


    /**
     * 初始化数据或刷新数据
     */
    override fun loadDatas() {

    }

    override fun loadMore(offset: Int) {

    }

}