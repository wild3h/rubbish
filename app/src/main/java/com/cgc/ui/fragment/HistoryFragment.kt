package com.cgc.ui.fragment

import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cgc.adapter.HistoryAdapter
import com.cgc.base.BaseFragment
import com.cgc.pojo.Model
import com.cgc.presenter.impl.HistoryPresenterImpl
import com.cgc.view.HistoryView
import com.wsf.rubbish.R
import kotlinx.android.synthetic.main.fragment_list.*
import org.jetbrains.anko.support.v4.toast
import androidx.core.view.GestureDetectorCompat
import android.view.GestureDetector
import android.provider.ContactsContract.CommonDataKinds.Note
import android.widget.SimpleAdapter
import com.cgc.dao.ImageDao
import kotlinx.android.synthetic.main.index.*
import android.app.Activity
import android.app.ActivityOptions
import com.cgc.ui.activity.OtherActivity
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.img_history.*
import kotlinx.android.synthetic.main.item_history.view.*


class HistoryFragment : BaseFragment(), HistoryView {

    private val mGestureDetectorCompat: GestureDetectorCompat? = null
    private val mRecyclerView: RecyclerView? = null

    override fun onError(message: String?) {
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
        return View.inflate(context, R.layout.fragment_list, null)
    }

    override fun initListener() {
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = adapter

        //初始化刷新控件
        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN)
        //刷新监听
        refreshLayout.setOnRefreshListener {
            presenter.loadData()
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

        adapter.setOnItemClickListener(object : HistoryAdapter.OnItemClickListener {
            override fun onItemClick(v: View, position: Int) {
                val item = adapter.getItem(position)

                var savePath = "/storage/emulated/0/DCIM/Camera/"
                ImageDao.imgPath = savePath + item.imageId
                toast(ImageDao.imgPath)

//                val intent = Intent(v.context, OtherActivity::class.java)
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, s);

                v.context.startActivity(
                        Intent(v.context, OtherActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(v.context as Activity,v,"sharedView").toBundle())
//                var bm = BitmapFactory.decodeFile(s);
//                historyImageView.setImageBitmap(bm);
//                val image = ImageDao.queryImgByName(item.imageId)
//                val imgPath = image?.get(0)?.url
//                imgPath?.let { toast(it) }
            }
        })
    }

    override fun initData() {
        //加载数据
        presenter.loadData()
    }


}

