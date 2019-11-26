package com.cgc.adapter

import android.annotation.SuppressLint
import android.view.View
import android.content.Context
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.cgc.pojo.Model
import android.view.LayoutInflater
import com.cgc.widget.HistoryItemView
import com.wsf.rubbish.R
import androidx.recyclerview.widget.RecyclerView
import com.cgc.widget.LoadMoreView


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {
    private var list = ArrayList<Model>()

    //私有属性
    private var onItemClickListener: OnItemClickListener? = null

    //setter方法
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    //回调接口
    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

    fun updateList(list: List<Model>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(it)
            notifyDataSetChanged()
        }
    }

    /**
     * 加载更多数据
     */
    fun loadMore(list: List<Model>?) {
        list?.let {
            this.list.clear()
            this.list.addAll(it)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        return if (viewType == 1) {
            HistoryHolder(LoadMoreView(parent.context))
        } else {
            HistoryHolder(HistoryItemView(parent.context))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        //如果是最后一条 就不需要刷新view了
        if (position == list.size) {
            return
        }
        //条目数据
        val data = list[position]
        //条目view
        val itemView = holder.itemView as HistoryItemView
        //条目刷新
        itemView.setData(data)

        //实现点击效果
        holder.itemView.setOnClickListener { v ->
            onItemClickListener?.onItemClick(v, position)
        }
    }

    fun getItem(position: Int): Model {
        return list[position]
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}