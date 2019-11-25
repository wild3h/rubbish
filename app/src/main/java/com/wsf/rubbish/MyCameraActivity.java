package com.wsf.rubbish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.baidu.aip.imageclassify.AipImageClassify;
import com.cgc.dao.ModelDao;
import com.cgc.dao.TypeDao;
import com.cgc.pojo.Model;
import com.cgc.pojo.Type;
import com.cgc.util.SQLUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.wsf.Until.PreviewSizeUtil;
import com.wsf.permission.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 相机启动
 */
public class MyCameraActivity extends Permission implements View.OnClickListener,
        SurfaceHolder.Callback, Camera.PictureCallback {
    List<String> list = new ArrayList<>();
    private Camera camera;
    private static final String TAG = "调试信息";
    private Camera.Parameters parameters;
    private int orientationDegrees = 90;
    private RelativeLayout frameLayout;
    private ImageView imageButton, reset, ok;
    /**
     * 路径: /storage/emulated/0/Pictures/
     */
    private String savePath;
    private String path;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;

    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 0;//后置摄像头标记
    private int currentCameraType = -1;//当前打开的摄像头标记
    final private int IMAGE_REQUEST_CODE = 256;
    final private int RESIZE_REQUEST_CODE = 100;


    private ModelDao modelDao = new ModelDao();
    private TypeDao typeDao = new TypeDao();


    public static final String APP_ID = "17583743";
    public static final String API_KEY = "s7cFf05LbPONjr7zne6oruYn";
    public static final String SECRET_KEY = "I59akZEGCVZ6rMuSVYpOoPy0HFN22V1o";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savePath = "/storage/emulated/0/DCIM/Camera/";
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView2);
        surfaceView.setOnClickListener(this);

        frameLayout = (RelativeLayout) findViewById(R.id.frameLayout);
        imageButton = (ImageView) findViewById(R.id.imageButton);
        reset = (ImageView) findViewById(R.id.reset);
        ok = (ImageView) findViewById(R.id.ok);

        frameLayout.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        reset.setOnClickListener(this);
        ok.setOnClickListener(this);
        // 到SurfaceHolder,SurfaceHolder相当于一个监听器,可以通过CallBack来监听 SurfaceView上的变化。
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        // 为了实现照片预览功能，需要将SurfaceHolder的类型设置为PUSH,这样画图缓存就由Camera类来管理，画图缓存是独立于Surface的
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        startOrientationChangeListener();

        SQLUtil.INSTANCE.initSQLData(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseCamera();
    }

    private final void startOrientationChangeListener() {

        OrientationEventListener mOrEventListener = new OrientationEventListener(
                this) {
            @Override
            public void onOrientationChanged(int rotation) {

                if (((rotation >= 0) && (rotation <= 45)) || (rotation >= 315)) {

                    orientationDegrees = 90;
                } else if ((rotation > 45) && (rotation < 135)) {

                    orientationDegrees = 180;
                } else if ((rotation >= 135) && (rotation <= 225)) {

                    orientationDegrees = 270;
                } else if ((rotation > 225) && (rotation < 315)) {

                    orientationDegrees = 0;
                }

                //Log.e(TAG, "rotation："+rotation);

            }
        };
        mOrEventListener.enable();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        surfaceHolder = holder;
        initCamera(BACK);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e(TAG, "surfaceChanged");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed");

        // 当Surface被销毁的时候，该方法被调用
        // 在这里需要释放Camera资源
        releaseCamera();
    }

    @SuppressWarnings("deprecation")
    private void initCamera(int cameraId) {

        // 判断是否有摄像头
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA))
            return;

        // 获取摄像头的个数
        // int numberOfCameras = Camera.getNumberOfCameras();
        // 后置摄像头
        camera = Camera.open(cameraId);
        currentCameraType = cameraId;
        /**
         * 获取设备Camera特性参数。由于不同的设备Camera的特性是不同的，所以在设置时需要首先判断设备对应的特性，再加以设置
         */
        parameters = camera.getParameters();
        // 获取可预览的尺寸集合
        List<Camera.Size> supportedPreviewSizes = parameters
                .getSupportedPreviewSizes();
        Camera.Size previewSize = PreviewSizeUtil
                .getSupportSize(supportedPreviewSizes);
        // 获取可设置图片的大小集合
        List<Camera.Size> supportedPictureSizes = parameters
                .getSupportedPictureSizes();
        // 设置生成最大的图片
        // Size pictureSize =
        // PreviewSizeUtil.getSupportSize(supportedPictureSizes);
        Camera.Size pictureSize = supportedPictureSizes.get((supportedPictureSizes
                .size() - 1) / 2);
        // 获取可设置的帧数
        List<Integer> supportedPreviewFrameRates = parameters
                .getSupportedPreviewFrameRates();
        Integer frameRates = supportedPreviewFrameRates
                .get((supportedPreviewFrameRates.size() - 1) / 2);
        // 设置Camera的参数
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        // 设置帧数(每秒从摄像头里面获得几个画面)
        parameters.setPreviewFrameRate(frameRates);

        // 设置图片格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        // 设置照片质量
        parameters.setJpegQuality(100);

        // 首先获取系统设备支持的所有颜色特效，如果设备不支持颜色特性将返回一个null， 如果有符合我们的则设置
        List<String> colorEffects = parameters.getSupportedColorEffects();
        Iterator<String> colorItor = colorEffects.iterator();
        while (colorItor.hasNext()) {
            String currColor = colorItor.next();
            if (currColor.equals(Camera.Parameters.EFFECT_SOLARIZE)) {
                // parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                break;
            }
        }

        // 获取对焦模式
        List<String> focusModes = parameters.getSupportedFocusModes();
        // [auto, infinity, macro, continuous-video, continuous-picture, manual]
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // 设置自动对焦
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        if (currentCameraType == BACK) {
            List<String> flashModes = parameters.getSupportedFlashModes();
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                // 自动闪光
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
        }// 设置闪光灯自动开启


        int orientationDegrees = PreviewSizeUtil.getCameraDisplayOrientation(
                this, Camera.CameraInfo.CAMERA_FACING_BACK);
        // 设置相机镜头旋转角度(默认摄像头是横拍)
        camera.setDisplayOrientation(orientationDegrees);

        try {
            // 设置显示
            camera.setPreviewDisplay(surfaceHolder);

        } catch (IOException exception) {

            releaseCamera();
        }
        // 设置完成需要再次调用setParameter方法才能生效
        camera.setParameters(parameters);

        // 开始预览
        Log.e("TAG", "正在开始预览");
        camera.startPreview();

    }

    private void releaseCamera() {
        if (camera != null) {

            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        path = savePath + "XiuMF_" + System.currentTimeMillis() + ".jpg";
        File file = new File(path);
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(data);
            Toast toast = Toast.makeText(MyCameraActivity.this, "已保存", Toast.LENGTH_LONG);
            toast.show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        distinguish();
        // 拍照后预览会停止，重新开启下
        camera.startPreview();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.frameLayout:
            case R.id.imageButton:
                if (list != null) {
                    if (!list.isEmpty()) {
                        list.clear();
                    }
                }


                if (parameters != null) {
                    // 设置照片在相册的旋转角度(默认摄像头是横放)
                    // parameters.setRotation(orientationDegrees);
                    if (currentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        parameters.set("rotation", orientationDegrees);
                    } else {
                        parameters.set("rotation", 360 - orientationDegrees);
                    }

                    // 获取当前手机屏幕方向
                    camera.setParameters(parameters);
                    //Log.e(TAG, "orientationDegrees:" + orientationDegrees);
                }
                /**
                 * ShutterCallback shutter:按下快门的回调 PictureCallback raw:原始图像数据的回调
                 * PictureCallback postview:压缩图像 PictureCallback
                 * jpeg:压缩成jpg格式的图像数据的回调
                 */
                camera.takePicture(null, null, null, this);
                break;
            case R.id.ok:// 获取照片
                Log.e("TAG", "点击ok");
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
                break;
            case R.id.reset:
                try {
                    changeCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    private void changeCamera() throws IOException {
        camera.stopPreview();
        camera.release();
        if (currentCameraType == FRONT) {
            initCamera(BACK);
        } else if (currentCameraType == BACK) {
            initCamera(FRONT);
        }
    }

    /*private void sendGetRequest(final String name, final Button button) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.choviwu.top/garbage/getGarbage?garbageName=" + name;
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                Log.e(TAG, new String(bytes));
                Boolean isdone = false;
                try {
                    for (int j = 0; j < (new JSONObject(new String(bytes))).getJSONArray("data").length(); j++) {
                        if ((new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(j).getString("gname").equals(name)) {
                            button.setText(name + (new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(j).getString("gtype"));
                            isdone = true;
                            break;
                        }
                    }
                    if (!isdone) {
                        similar(bytes);//没找到完全相等的那个，然后展示一下相似的供选择
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });
    }*/

    private void showButton() {
        //Activity不允许在子线程中进行UI操作
        //通过该方法可以将线程切换到主线程，然后再更新UI元素
        runOnUiThread(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
                buttonLayout.removeAllViews();
                for (int i = 0; i < list.size(); i++) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, list.get(i).length() + 3);
                    layoutParams.setMargins(5, 5, 5, 5);
                    final Button button = new Button(MyCameraActivity.this);
                    Drawable drawable = ContextCompat.getDrawable(MyCameraActivity.this, R.drawable.shadow);
                    button.setBackground(drawable);
                    List<Type> m = typeDao.selectByName(list.get(i));
                    String s=null;
                    if (m != null && !m.isEmpty()) {
                        s = m.get(0).getTYPE();
                    }else {
                        s="未知物品";
                    }
                    button.setText(list.get(i)+":"+s);
                    button.setTextSize(20f);
                    button.setTextColor(Color.WHITE);
                    button.setLayoutParams(layoutParams);
                    buttonLayout.addView(button);
                    /*button.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendGetRequest(button.getText().toString(), button);
                        }
                    });*/
                }
            }
        });
    }

    @SuppressLint("NewApi")
    private void similar(final byte[] bytes) {
        LinearLayout buttonLayout = findViewById(R.id.similarbutton);
        buttonLayout.removeAllViews();

        try {
            for (int i = 0; i < (new JSONObject(new String(bytes))).getJSONArray("data").length() && i < 5; i++) {
                int len = (new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(i).getString("gname").length() + 3;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, len);
                layoutParams.setMargins(15, 15, 15, 5);
                final Button button = new Button(MyCameraActivity.this);
                Drawable drawable = ContextCompat.getDrawable(MyCameraActivity.this, R.drawable.btn_selector);
                button.setBackground(drawable);
                button.setText((new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(i).getString("gname"));
                button.setTextColor(Color.WHITE);
                button.setLayoutParams(layoutParams);
                buttonLayout.addView(button);
                /*button.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendGetRequest(button.getText().toString(), button);
                    }
                });*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        distinguish();
                    } catch (Exception e) {
                        // TODO Auto-generatedcatch block
                        e.printStackTrace();
                    }
                }
                break;

            default:
                Log.e(TAG, "不是这一个活动");
                break;
        }
    }

    /**
     * 图片识别的模块
     */
    private void distinguish() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 初始化一个AipImageClassify
                AipImageClassify client = new AipImageClassify(APP_ID, API_KEY, SECRET_KEY);
                // 传入可选参数调用接口
                HashMap<String, String> options = new HashMap<>();
                options.put("baike_num", "0");
                // 参数为本地路径
                JSONObject res = client.advancedGeneral(path, options);

                try {
                    Log.e("TAG", res.toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (!list.isEmpty()) {
                        list.clear();

                    }
                    Log.e(TAG, String.valueOf(res.getJSONArray("result").getJSONObject(4).getDouble("score") > 0.1));
                    for (int i = 0; i < Integer.parseInt(res.getString("result_num")); i++) {
                        if (res.getJSONArray("result").getJSONObject(i).getDouble("score") >= 0.1) {
                            String n = res.getJSONArray("result").getJSONObject(i).getString("keyword");

                            list.add(n);

                            List<Type> m = typeDao.selectByName(n);
                            if (m != null && !m.isEmpty()) {
                                String s = m.get(0).getTYPE();
                                Model l = new Model(n, s, "", "");
                                modelDao.insert(l);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showButton();//动态显示按钮数量
            }
        }).start();
    }
}
