package com.orient.test.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orient.test.R;

public class DragActivity extends AppCompatActivity {

    public static void show(Context context){
        Intent intent = new Intent(context,DragActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
    }
}
