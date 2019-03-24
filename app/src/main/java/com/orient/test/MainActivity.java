package com.orient.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.orient.test.adapter.MainAdapter;
import com.orient.test.ui.activity.ConstraintActivity;
import com.orient.test.ui.activity.NetWorkActivity;
import com.orient.test.ui.activity.OpenBookActivity;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnSelectListener{
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidget();
    }

    private void initWidget() {
        mRecyclerView = findViewById(R.id.recycle);
        mAdapter = new MainAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSelectStr(String str) {
        switch (str){
            case "书籍打开动画":
                OpenBookActivity.show(this);
                break;
            case "LruCache和DiskLruCache":
                NetWorkActivity.show(this);
                break;
            case "ConstraintLayout中Circular positioning":
                ConstraintActivity.show(this);
                break;
            default:
                break;
        }
    }
}
