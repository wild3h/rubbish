package com.wsf.rubbish;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cgc.dao.TypeDao;
import com.cgc.pojo.Type;
import com.cgc.ui.activity.HistoryActivity;
import com.cgc.util.SQLUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;
import com.wsf.Until.ACache;
import com.wsf.Until.CompressImage;
import com.wsf.Until.RomUtil;
import com.wsf.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.wsf.Until.SetStatusBarLightMode.MIUISetStatusBarLightMode;

/**
 * 主页面
 */
public class MainActivity extends Permission implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ACache acache;
    private ACache mcache;
    private Uri uritempFile;
    private static final String IMAGE_FILE_LOCATION = "file:///" + Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    private Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);
    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView videoLayout;
    private TextView musicLayout;
    private ImageView searchButton;
    private EditText textName;
    private ListView listView;
    private TextView history;
    private TextView diary;
    private TextView individuation;
    private ImageView icon;
    private TextView uploadtext;
    private String good=null;
    RelativeLayout change_background;
    RelativeLayout dialog_view;
    ListView detail;
    ImageView backgroundImage;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    //一倍滚动量
    private int one;
    //模糊搜索的数据数组
    List<String> listdata;
    final private int IMAGE_REQUEST_CODE = 256;
    final private int CHOOSE_SMALL_PICTURE = 111;
    final private int IMAGE_REQUEST_BACKGROUND = 233;
    final private int IMAGE_REQUEST_ICON = 666;
    private int request_code = -100;
    private String path;

    private TypeDao typeDao = new TypeDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        datainit();
        aCacheInit();
        Log.e("ROM", RomUtil.getName() + " " + RomUtil.getVersion());
        Log.e("Mac", getMacAddress(this.getApplicationContext()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Type> appleList = typeDao.selectByName("苹果");
                if (appleList == null || appleList.isEmpty()) {
                    SQLUtil.dbHelper.copyDataBase();
                    //typeDao.initType(MainActivity.this);
                }
            }
        }).start();

        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

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
                return arg0 == arg1;
            }

            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中

            /**
             * pageview内部设置监听事件并返回view
             */
            public Object instantiateItem(View arg0, int arg1) {
                ((ViewPager) arg0).addView(pageview.get(arg1));
                switch (arg1) {
                    case 0:
                        searchButton = (ImageView) pageview.get(0).findViewById(R.id.searchButton);
                        searchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Log.e("TAG","点击查询");
                            }
                        });
                        textName = (EditText) pageview.get(0).findViewById(R.id.textName);
                        textName.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                Log.e("第一个", s.toString());
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                Log.e("第二个", s.toString());
                                if ("".equals(textName.getText().toString())) {
                                    Log.e("TAG", "yes");
                                    listdata.clear();
                                    Adapter adapter = (ArrayAdapter) listView.getAdapter();
                                    listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.listitem, new ArrayList<String>()));
                                } else {
                                    listdata.clear();
                                    sendGetRequest(s.toString());
                                }
                            }

                            @Override
                            public synchronized void afterTextChanged(Editable s) {
                                Log.e("第三个", s.toString());
                                if ("".equals(textName.getText().toString())) {
                                    listdata.clear();
                                }
                                good=s.toString();
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
                                if ((good!=null&&event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                                    List<Type> m = typeDao.selectByName(good);
                                    String s=null;
                                    if (m != null && !m.isEmpty()) {
                                        s = m.get(0).getTYPE();
                                    }else {
                                        s="未知物品";
                                    }
                                    int ImageID = 0;
                                    if(s.equals("干垃圾")){
                                        ImageID=R.mipmap.gan;
                                    }else if(s.equals("湿垃圾")){
                                        ImageID = R.mipmap.shi;
                                    }else if(s.equals("有害垃圾")){
                                        ImageID = R.mipmap.you;
                                    }else if(s.equals("可回收垃圾")){
                                        ImageID = R.mipmap.ke;
                                    }else{
                                        ImageID = R.mipmap.notfound;
                                    }
                                    //操作生成卡片
                                    List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
                                    Map<String, Object> showitem = new HashMap<String, Object>();
                                    showitem.put("name", good+":");
                                    showitem.put("type_image", ImageID);
                                    listitem.add(showitem);
                                    //创建SimpleAdapter
                                    SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), listitem, R.layout.item_detail, new String[]{"name", "type_image"}, new int[]{R.id.name, R.id.type_image});
                                    detail.setAlpha(0f);
                                    detail.setVisibility(View.GONE);
                                    detail.setAdapter(simpleAdapter);
                                    detail.setVisibility(View.VISIBLE);
                                    detail.animate().alpha(1f).setDuration(500).setListener(null);
                                    //清空listview
                                    listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.listitem, new ArrayList<String>()));
//                                    List<Type> types = typeDao.selectByName(textName.getText().toString());
//                                    if (types.size()!=0){
//                                        listdata.clear();
//                                        for (int i = 0; i<types.size();i++){
//                                            String str = "物品名:" + types.get(i).getITEM() +"类型:"+ types.get(i).getTYPE();
//                                            listdata.add(str);
//                                        }
//                                    }
//                                    listdata.add("aa");
                                    Log.e("TAG", "回车了");
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
                                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                                startActivity(intent);
                            }
                        });
                        icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                request_code = IMAGE_REQUEST_ICON;
                                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                            }
                        });
                        diary.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, SquareActivity.class);
                                startActivity(intent);
                            }
                        });
                        individuation.setOnClickListener(new View.OnClickListener() {
                            /**
                             * 更换背景墙
                             * @param view
                             */
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                request_code = IMAGE_REQUEST_BACKGROUND;
                                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                            }
                        });
                        change_background.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog_view.setAlpha(0f);
                                dialog_view.setVisibility(View.VISIBLE);
                                dialog_view.animate().alpha(1f).setDuration(200).setListener(null);
                            }
                        });
                        dialog_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog_view.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        dialog_view.setVisibility(View.GONE);
                                    }
                                });
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (listView.equals(adapterView)) {
            Log.e("TAG", listdata.get(i).toString());
            List<Type> m = typeDao.selectByName(listdata.get(i).toString());
            String s=null;
            if (m != null && !m.isEmpty()) {
                s = m.get(0).getTYPE();
            }else {
                s="未知物品";
            }
            int ImageID = 0;
            if(s.equals("干垃圾")){
                ImageID=R.mipmap.gan;
            }else if(s.equals("湿垃圾")){
                ImageID = R.mipmap.shi;
            }else if(s.equals("有害垃圾")){
                ImageID = R.mipmap.you;
            }else if(s.equals("可回收垃圾")){
                ImageID = R.mipmap.ke;
            }else{
                ImageID = R.mipmap.notfound;
            }
            //操作生成卡片
            List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("name", listdata.get(i)+":");
            showitem.put("type_image", ImageID);
            listitem.add(showitem);
            //创建SimpleAdapter
            SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), listitem, R.layout.item_detail, new String[]{"name", "type_image"}, new int[]{R.id.name, R.id.type_image});
            detail.setAlpha(0f);
            detail.setVisibility(View.GONE);
            detail.setAdapter(simpleAdapter);
            detail.setVisibility(View.VISIBLE);
            detail.animate().alpha(1f).setDuration(500).setListener(null);
            //清空listview
            listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.listitem, new ArrayList<String>()));
        }
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    setBottomImage(videoLayout, R.mipmap.indeximg_pressed);
                    setBottomImage(musicLayout, R.mipmap.myimg);
                    videoLayout.setTextColor(Color.argb(255, 18, 150, 219));
                    musicLayout.setTextColor(Color.BLACK);
                    MIUISetStatusBarLightMode(MainActivity.this, true);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    setBottomImage(videoLayout, R.mipmap.indeximg);
                    setBottomImage(musicLayout, R.mipmap.myimg_pressed);
                    musicLayout.setTextColor(Color.argb(255, 18, 150, 219));
                    videoLayout.setTextColor(Color.BLACK);
                    MIUISetStatusBarLightMode(MainActivity.this, false);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.videoLayout:
                //点击"视频“时切换到第一页
                viewPager.setCurrentItem(0);
                setBottomImage(videoLayout, R.mipmap.indeximg_pressed);
                setBottomImage(musicLayout, R.mipmap.myimg);
                videoLayout.setTextColor(Color.argb(255, 18, 150, 219));
                musicLayout.setTextColor(Color.BLACK);
                break;
            case R.id.musicLayout:
                //点击“音乐”时切换的第二页
                viewPager.setCurrentItem(1);
                setBottomImage(videoLayout, R.mipmap.indeximg);
                setBottomImage(musicLayout, R.mipmap.myimg_pressed);
                musicLayout.setTextColor(Color.argb(255, 18, 150, 219));
                videoLayout.setTextColor(Color.BLACK);
                break;
        }
    }

    private void setBottomImage(TextView textView, int imageId) {
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
        drawable = new BitmapDrawable(changedBitmap);
        drawable.setBounds(0, 0, 0, 0);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    private void sendGetRequest(final String name) {
        Log.e("TAG", name);
        AsyncHttpClient client = new AsyncHttpClient();
        /*https://www.metalgearjoe.cn/mn/search?search=%E8%8B%B9%E6%9E%9C
        http://api.choviwu.top/garbage/getGarbage?garbageName=*/
        String url = "http://api.choviwu.top/garbage/getGarbage?garbageName=" + name;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                Log.e("TAG", new String(bytes));
                Boolean isdone = false;
                try {
                    for (int j = 0; j < (new JSONObject(new String(bytes))).getJSONArray("data").length(); j++) {
//                        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        final TextView textView = new TextView(MainActivity.this);
//                        textView.setLayoutParams(layoutParams);
                        listdata.add((new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(j).getString("gname"));
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.listitem, listdata);//listdata和str均可
                        listView.setAdapter(arrayAdapter);
                        listView.setOnItemClickListener(MainActivity.this);
                    }
                    /*if(!isdone){
                        similar(bytes);//没找到完全相等的那个，然后展示一下相似的供选择
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("TAG", new String(bytes));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        startPhotoZoom(selectedImage);
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;
            case Crop.REQUEST_CROP:

                if (data != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                        Log.e(TAG, "裁剪完毕");
                        Log.e(TAG, "图像大小=" + bitmap.getByteCount());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (request_code == IMAGE_REQUEST_BACKGROUND) {
                        Bitmap iconbitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, true);
                        backgroundImage.setImageBitmap(iconbitmap);
                        acache.put("backgroundImage", iconbitmap);
                        deleteSingleFile(Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
                    } else if (request_code == IMAGE_REQUEST_ICON) {
                        Bitmap iconbitmap = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                        icon.setImageBitmap(iconbitmap);
                        uploadtext.setText(null);
                        acache.put("iconImage", iconbitmap);
                        deleteSingleFile(Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
                    }
                    request_code = -100;
                }
                break;
            default:
                Log.e("TAG", "不是这一个活动");
                break;
        }
    }

    //定义是否退出程序的标记
    private boolean isExit = false;
    //定义接受用户发送信息的handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //标记用户不退出状态
            isExit = false;
        }
    };

    //监听手机的物理按键点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击的是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果isExit标记为false，提示用户再次按键
            if (!isExit) {
                isExit = true;
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //如果用户没有在2秒内再次按返回键的话，就发送消息标记用户为不退出状态
                mHandler.sendEmptyMessageDelayed(0, 3000);
            }
            //如果isExit标记为true，退出程序
            else {
                //退出程序
                finish();
            }
        }
        return false;
    }

    private void startPhotoZoom(Uri uri) {
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg");
        Crop.of(uri, uritempFile).asSquare().start(this);
    }

    private boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "删除单个文件" + filePath$Name + "失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "删除单个文件失败：" + filePath$Name + "不存在！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public String getMacAddress(Context context) {

        String macAddress = null;
        WifiManager wifiManager;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = (null == wifiManager ? null : wifiManager.getConnectionInfo());

        if (!wifiManager.isWifiEnabled()) {
            //必须先打开，才能获取到MAC地址
            wifiManager.setWifiEnabled(true);
            wifiManager.setWifiEnabled(false);
        }
        if (null != info) {
            macAddress = info.getMacAddress();
        }
        return macAddress;
    }

    private void datainit() {
        listdata = new ArrayList<String>();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.index, null);
        View view2 = inflater.inflate(R.layout.my, null);
        videoLayout = (TextView) findViewById(R.id.videoLayout);
        musicLayout = (TextView) findViewById(R.id.musicLayout);

        setBottomImage(videoLayout, R.mipmap.indeximg_pressed);
        setBottomImage(musicLayout, R.mipmap.myimg);
        videoLayout.setTextColor(Color.argb(255, 18, 150, 219));
        musicLayout.setTextColor(Color.BLACK);

        videoLayout.setOnClickListener(this);
        musicLayout.setOnClickListener(this);

        ImageButton opencamera = (ImageButton) findViewById(R.id.opencamera);
        opencamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyCameraActivity.class);
                startActivity(intent);
            }
        });
        pageview = new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(view1);
        pageview.add(view2);

        listView = (ListView) pageview.get(0).findViewById(R.id.listview);
        history = (TextView) pageview.get(1).findViewById(R.id.history);
        diary = (TextView) pageview.get(1).findViewById(R.id.diary);
        individuation = (TextView) pageview.get(1).findViewById(R.id.individuation);
        icon = (ImageView) pageview.get(1).findViewById(R.id.icon);
        uploadtext = (TextView) pageview.get(1).findViewById(R.id.uploadtext);
        MIUISetStatusBarLightMode(MainActivity.this, true);
        acache = ACache.get(MainActivity.this);
        change_background = (RelativeLayout) pageview.get(1).findViewById(R.id.change_background);
        dialog_view = (RelativeLayout) pageview.get(1).findViewById(R.id.dialog_view);
        backgroundImage = (ImageView) pageview.get(1).findViewById(R.id.backgroundImage);
        detail = pageview.get(0).findViewById(R.id.detail);
    }

    private void aCacheInit() {
        mcache = ACache.get(MainActivity.this);
        Bitmap acacheIcon = mcache.getAsBitmap("iconImage");
        Bitmap acacheBackground = mcache.getAsBitmap("backgroundImage");
        if (acacheIcon != null) {
            icon.setImageBitmap(acacheIcon);
            uploadtext.setText(null);
        }
        if (acacheBackground != null) {
            backgroundImage.setImageBitmap(acacheBackground);
        }
    }
}