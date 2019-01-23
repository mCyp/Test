package com.orient.test.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.orient.test.R;

public class BookSampleActivity extends AppCompatActivity {

    // 结束当前界面
    private Button mBtnFinish;

    public static void show(Context context){
        Intent intent = new Intent(context,BookSampleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_sample);

        initWidget();
    }

    // 初始化布局
    private void initWidget() {
        mBtnFinish = findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
