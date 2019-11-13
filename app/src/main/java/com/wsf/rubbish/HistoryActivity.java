package com.wsf.rubbish;


import android.os.Bundle;

import com.cgc.ui.fragment.HistoryFragment;
import com.cgc.util.FragmentUtil;
import com.wsf.permission.Permission;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class HistoryActivity extends Permission {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
//        ModelDao modelDao = new ModelDao();
//        List<Model> history = modelDao.getHistory();
        HistoryFragment h = new HistoryFragment();

//        getFragmentManager().beginTransaction().replace(R.id.HistoryFragment, h).commit();
    }


}
