package com.cgc.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cgc.pojo.Model
import com.cgc.widget.HistoryItemView
import com.cgc.widget.YueDanItemView

/**
 *Description:悦单界面适配器
 */
class YueDanAdapter : RecyclerView.Adapter<YueDanAdapter.YueDanHolder>() {
    private var list = ArrayList<Model>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YueDanHolder {
        return YueDanHolder(YueDanItemView(parent.context))
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: YueDanHolder, position: Int) {
        if (position == list.size) {
            return
        }
        //条目数据
        val data = list[position]
        //条目view
        val itemView = holder.itemView as HistoryItemView
        //条目刷新
        itemView.setData(data)
    }

    fun updateList(list: List<Model>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun loadMore(list: List<Model>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(list)
            notifyDataSetChanged()
        }
    }


    class YueDanHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}