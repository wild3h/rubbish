package com.wsf.rubbish;


import android.os.Bundle;
import com.cgc.dao.ModelDao;
import com.cgc.pojo.Model;
import com.cgc.widget.HistoryItemView;
import com.wsf.permission.Permission;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends Permission {

    ModelDao modelDao = new ModelDao();
    List<Model> history = new ArrayList<Model>();
    private HistoryItemView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelDao modelDao = new ModelDao();
        List<Model> history = modelDao.getHistory();
        setContentView(R.layout.history);

    }


}
