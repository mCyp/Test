package com.orient.test.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orient.test.R;
import com.orient.test.adapter.BookAdapter;
import com.orient.test.animation.ContentScaleAnimation;
import com.orient.test.animation.Rotate3DAnimation;

import java.util.ArrayList;
import java.util.List;

public class OpenBookActivity extends AppCompatActivity implements Animation.AnimationListener,BookAdapter.OnBookClickListener {
    private static final String TAG = "OpenBookActivity";

    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    // 资源文件列表
    private List<Integer> values = new ArrayList<>();
    // 记录View的位置
    private int[] location = new int[2];
    // 内容页
    private ImageView mContent;
    // 封面
    private ImageView mFirst;
    // 缩放动画
    private ContentScaleAnimation scaleAnimation;
    // 3D旋转动画
    private Rotate3DAnimation threeDAnimation;
    // 状态栏的高度
    private int statusHeight;
    // 是否打开书籍 其实是是否离开当前界面，跳转到其他的界面
    private boolean isOpenBook = false;

    public static void show(Context context) {
        Intent intent = new Intent(context, OpenBookActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_book);

        initWidget();
    }

    private void initWidget() {
        mRecyclerView = findViewById(R.id.recycle);
        mContent = findViewById(R.id.img_content);
        mFirst = findViewById(R.id.img_first);

        // 获取状态栏高度
        statusHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusHeight = getResources().getDimensionPixelSize(resourceId);
        }

        initData();
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mAdapter = new BookAdapter(values,this);
        mRecyclerView.setAdapter(mAdapter);
    }

    // 重复添加数据
    private void initData() {
        for(int i = 0;i<10;i++){
            values.add(R.drawable.preview);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // 当界面重新进入的时候进行合书的动画
        if(isOpenBook) {
            scaleAnimation.reverse();
            threeDAnimation.reverse();
            mFirst.clearAnimation();
            mFirst.startAnimation(threeDAnimation);
            mContent.clearAnimation();
            mContent.startAnimation(scaleAnimation);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(scaleAnimation.hasEnded() && threeDAnimation.hasEnded()) {
            // 两个动画都结束的时候再处理后续操作
            if (!isOpenBook) {
                isOpenBook = true;
                BookSampleActivity.show(this);
            } else {
                isOpenBook = false;
                mFirst.clearAnimation();
                mContent.clearAnimation();
                mFirst.setVisibility(View.GONE);
                mContent.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onItemClick(int pos,View view) {
        mFirst.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.VISIBLE);

        // 计算当前的位置坐标
        view.getLocationInWindow(location);
        int width = view.getWidth();
        int height = view.getHeight();

        // 两个ImageView设置大小和位置
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFirst.getLayoutParams();
        params.leftMargin = location[0];
        params.topMargin = location[1] - statusHeight;
        params.width = width;
        params.height = height;
        mFirst.setLayoutParams(params);
        mContent.setLayoutParams(params);

        //mContent = new ImageView(MainActivity.this);
        Bitmap contentBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        contentBitmap.eraseColor(getResources().getColor(R.color.read_theme_yellow));
        mContent.setImageBitmap(contentBitmap);

        // mCover = new ImageView(MainActivity.this);
        Bitmap coverBitmap = BitmapFactory.decodeResource(getResources(),values.get(pos));
        mFirst.setImageBitmap(coverBitmap);

        initAnimation(view);
        Log.i(TAG,"left:"+mFirst.getLeft()+"top:"+mFirst.getTop());

        mContent.clearAnimation();
        mContent.startAnimation(scaleAnimation);
        mFirst.clearAnimation();
        mFirst.startAnimation(threeDAnimation);
    }

    // 初始化动画
    private void initAnimation(View view) {
        float viewWidth = view.getWidth();
        float viewHeight = view.getHeight();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float maxWidth = displayMetrics.widthPixels;
        float maxHeight = displayMetrics.heightPixels;
        float horScale = maxWidth / viewWidth;
        float verScale = maxHeight / viewHeight;
        float scale = horScale > verScale ? horScale : verScale;

        scaleAnimation = new ContentScaleAnimation(location[0], location[1], scale, false);
        scaleAnimation.setInterpolator(new DecelerateInterpolator());  //设置插值器
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);  //动画停留在最后一帧
        scaleAnimation.setAnimationListener(OpenBookActivity.this);

        threeDAnimation = new Rotate3DAnimation(OpenBookActivity.this, -180, 0
                , location[0], location[1], scale, true);
        threeDAnimation.setDuration(1000);                         //设置动画时长
        threeDAnimation.setFillAfter(true);                        //保持旋转后效果
        threeDAnimation.setInterpolator(new DecelerateInterpolator());
    }
}
