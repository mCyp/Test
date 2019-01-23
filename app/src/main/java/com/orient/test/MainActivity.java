package com.orient.test;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.orient.test.adapter.MainAdapter;
import com.orient.test.ui.activity.DragActivity;
import com.orient.test.ui.activity.NetWorkActivity;
import com.orient.test.ui.activity.OpenBookActivity;

import java.io.IOException;
import java.net.Socket;

import okhttp3.Connection;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

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
            case "自定义View1":
                DragActivity.show(this);
                break;
            case "书籍打开动画":
                OpenBookActivity.show(this);
                break;
            case "LruCache和DiskLruCache":
                NetWorkActivity.show(this);
                break;
            default:
                break;
        }
    }
}
