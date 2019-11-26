package com.cgc.ui.activity

import android.graphics.BitmapFactory
import com.cgc.base.BaseActivity
import com.wsf.rubbish.R
import androidx.core.app.ActivityCompat
import android.view.View
import com.cgc.dao.ImageDao
import kotlinx.android.synthetic.main.img_history.*


class OtherActivity : BaseActivity(){
    override fun getLayoutId(): Int {
        return R.layout.img_history
    }

    override fun initData() {
        var bm = BitmapFactory.decodeFile(ImageDao.imgPath);
        historyImageView.setImageBitmap(bm);
    }

    override fun initListener() {
        findViewById<View>(R.id.historyImageView).setOnClickListener {
            // 注意这里不使用finish
            ActivityCompat.finishAfterTransition(this@OtherActivity)
        }
    }
}