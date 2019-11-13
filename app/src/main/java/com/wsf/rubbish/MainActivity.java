package com.wsf.rubbish;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wsf.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 */
public class MainActivity extends Permission implements View.OnClickListener{

    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView videoLayout;
    private TextView musicLayout;
    private ImageView searchButton;
    private EditText textName;
    private ListView listView;
    private TextView history;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    //一倍滚动量
    private int one;
//模糊搜索的数据数组
    List<String> listdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        datainit();

        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                switch (arg1){
                    case 0:
                        searchButton=(ImageView) pageview.get(0).findViewById(R.id.searchButton);
                        searchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Log.e("TAG","点击查询");
                            }
                        });
                        textName=(EditText) pageview.get(0).findViewById(R.id.textName);
                        textName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                Log.e("第一个",s.toString());
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Log.e("第二个",s.toString());
                                listdata.clear();
                                sendGetRequest(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                Log.e("第三个",s.toString());
                            }
                        });


                        /**
                         * 监听回车事件
                         */
                        textName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                //当actionId == XX_SEND 或者 XX_DONE时都触发
                                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                                if ((event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                                    //search();
                                    Log.e("TAG","回车了");
                                    return true;
                                }
                                return false;
                            }
                        });
                        break;
                    case 1:
                        history.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(MainActivity.this, HistoryActivity.class);
                                startActivity(intent);
                            }
                        });
                        break;
                }
                return pageview.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.videoLayout:
                //点击"视频“时切换到第一页
                viewPager.setCurrentItem(0);
                setBottomImage(videoLayout,R.mipmap.indeximg_pressed);
                setBottomImage(musicLayout,R.mipmap.myimg);
                videoLayout.setTextColor(Color.	argb(255,18,150,219));
                musicLayout.setTextColor(Color.BLACK);
                break;
            case R.id.musicLayout:
                //点击“音乐”时切换的第二页
                viewPager.setCurrentItem(1);
                setBottomImage(videoLayout,R.mipmap.indeximg);
                setBottomImage(musicLayout,R.mipmap.myimg_pressed);
                musicLayout.setTextColor(Color.	argb(255,18,150,219));
                videoLayout.setTextColor(Color.BLACK);
                break;
        }
    }
    private void setBottomImage(TextView textView,int imageId){
        Drawable drawable;
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),
                imageId);
        int originalWidth = originalBitmap.getWidth();
        int originalHeight = originalBitmap.getHeight();
        int newHeight = 130; // 自定义 高度 暂时没用
        float scale = ((float) newHeight) / originalHeight;
        Matrix matrix0 = new Matrix();
        matrix0.postScale(scale, scale);
        Bitmap changedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalWidth, originalHeight, matrix0, true);
        drawable=new BitmapDrawable(changedBitmap);
        drawable.setBounds(0,0,0,0);
        textView.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
    }
    private void sendGetRequest(final String name){
        Log.e("TAG",name);
        AsyncHttpClient client = new AsyncHttpClient();
        /*https://www.metalgearjoe.cn/mn/search?search=%E8%8B%B9%E6%9E%9C
        http://api.choviwu.top/garbage/getGarbage?garbageName=*/
        String url = "http://api.choviwu.top/garbage/getGarbage?garbageName="+name;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                Log.e("TAG",new String(bytes));
                Boolean isdone=false;
                try {
                    for(int j = 0; j<(new JSONObject(new String(bytes))).getJSONArray("data").length(); j++){
//                        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        final TextView textView = new TextView(MainActivity.this);
//                        textView.setLayoutParams(layoutParams);
                        listdata.add((new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(j).getString("gname"));
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,R.layout.listitem,listdata);//listdata和str均可
                        listView.setAdapter(arrayAdapter);
                    }
                    /*if(!isdone){
                        similar(bytes);//没找到完全相等的那个，然后展示一下相似的供选择
                    }*/
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("TAG", new String(bytes));
            }
        });
    }
    private void datainit(){
        listdata=new ArrayList<String>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.index, null);
        View view2 = inflater.inflate(R.layout.my, null);

        videoLayout = (TextView)findViewById(R.id.videoLayout);
        musicLayout = (TextView)findViewById(R.id.musicLayout);

        setBottomImage(videoLayout,R.mipmap.indeximg_pressed);
        setBottomImage(musicLayout,R.mipmap.myimg);
        videoLayout.setTextColor(Color.	argb(255,18,150,219));
        musicLayout.setTextColor(Color.BLACK);

        videoLayout.setOnClickListener(this);
        musicLayout.setOnClickListener(this);

        ImageButton opencamera=(ImageButton) findViewById(R.id.opencamera);
        opencamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, MyCameraActivity.class);
                startActivity(intent);
            }
        });
        pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(view1);
        pageview.add(view2);

        listView=(ListView) pageview.get(0).findViewById(R.id.listview);
        history=(TextView) pageview.get(1).findViewById(R.id.history);
    }
}