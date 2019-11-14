package com.wsf.rubbish;


import android.os.Bundle;

import com.cgc.adapter.HistoryAdapter;
import com.cgc.dao.ModelDao;
import com.cgc.pojo.Model;
import com.cgc.ui.fragment.HistoryFragment;
import com.wsf.permission.Permission;


import java.util.List;


public class HistoryActivity extends Permission {
    ModelDao modelDao = new ModelDao();
    List<Model> history = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

    }

    private void initTheView(){
        history = modelDao.getHistory();
        HistoryAdapter historyAdapter = new HistoryAdapter();
        //historyAdapter.onCreateViewHolder(this,1);
    }

}
