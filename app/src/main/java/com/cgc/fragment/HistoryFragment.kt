package com.cgc.fragment

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cgc.adapter.HistoryAdapter
import com.cgc.pojo.Model
import com.cgc.presenter.HistoryPresenterImpl
import com.cgc.view.HistoryView
import com.wsf.rubbish.R
import kotlinx.android.synthetic.main.history.*

class HistoryFragment: BaseFragment(),HistoryView {
    override fun onError(message: String?) {
        myToast("加载数据失败")
    }

    override fun loadSuccess(list: List<Model>?) {
        //隐藏刷新控件
        refreshLayout.isRefreshing = false
        //刷新列表
        adapter.updateList(list)
    }

    override fun loadMore(list: List<Model>?) {
        adapter.loadMore(list)
    }

    val adapter by lazy { HistoryAdapter() }
    val presenter by lazy { HistoryPresenterImpl(this) }

    override fun initView(): View? {
        return View.inflate(context, R.layout.history, null)
    }

    override fun initListener() {
        //初始化recycleView
        recycleView.layoutManager = LinearLayoutManager(context)

        recycleView.adapter = adapter

        //初始化刷新控件
        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN)
        //刷新监听
        refreshLayout.setOnRefreshListener {
            presenter.loadDatas()
        }
        //监听列表滑动
        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //最后一条已经显示
                    val layoutManager = recyclerView.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val manager: LinearLayoutManager = layoutManager
                        val lastPosition = manager.findLastVisibleItemPosition()
                        if (lastPosition == adapter.itemCount - 1) {
                            //最后一条已经显示了
                            presenter.loadMore(adapter.itemCount - 1)
                        }
                    }
                }
            }
        })
    }

    override fun initData() {

        //初始化数据
        presenter.loadDatas()
    }

}