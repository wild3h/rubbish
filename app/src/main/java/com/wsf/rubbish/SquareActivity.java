package com.wsf.rubbish;

import android.os.Bundle;


import com.wsf.permission.Permission;

public class SquareActivity extends Permission {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.square);
    }
}
