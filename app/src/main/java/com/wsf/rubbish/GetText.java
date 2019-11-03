package com.wsf.rubbish;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class GetText implements TextWatcher {
    private String TAG=this.getClass().getName();
    private Context context=null;
    private LinearLayout textListLayout;
    public GetText(Context con,LinearLayout textLayout){
        context=con;
        textListLayout=textLayout;
    }
    /**
     * 编辑框的内容发生改变之前的回调方法
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.i("文本监视器", "beforeTextChanged---" + charSequence.toString());
    }

    /**
     * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
     * 我们可以在这里实时地 通过搜索匹配用户的输入
     */
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Button button = null;
        Log.i("文本监视器", "onTextChanged---" + charSequence.toString());
        sendGetRequest(charSequence.toString(),button);
    }

    /**
     * 编辑框的内容改变以后,用户没有继续输入时 的回调方法
     */
    @Override
    public void afterTextChanged(Editable editable) {
        Log.i("文本监视器", "afterTextChanged---");
    }
    private void sendGetRequest(final String name, final Button button){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.choviwu.top/garbage/getGarbage?garbageName="+name;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                Log.e(TAG,new String(bytes));
                Boolean isdone=false;
                try {
                    for(int j = 0; j<(new JSONObject(new String(bytes))).getJSONArray("data").length(); j++){
                        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        final TextView textView = new TextView(context);
                        textView.setLayoutParams(layoutParams);
                        textView.setText((new JSONObject(new String(bytes))).getJSONArray("data").getJSONObject(j).getString("gname"));
                        textListLayout.addView(textView);
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
            }
        });
    }
}
