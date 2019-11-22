package com.wsf.rubbish;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.cgc.dao.TypeDao;
import com.cgc.pojo.Type;
import com.cgc.util.SQLUtil;
import com.wsf.permission.Permission;

import java.util.List;

public class LoadingActivity extends Permission{
    private TypeDao typeDao = new TypeDao();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);


        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLUtil.INSTANCE.initSQLData(LoadingActivity.this);

                List<Type> appleList = typeDao.selectByName("苹果");
                if (appleList == null || appleList.isEmpty()) {
                    SQLUtil.dbHelper.copyDataBase();
                    //typeDao.initType(MainActivity.this);
                }
                Intent intent=new Intent(LoadingActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).start();

    }
}
