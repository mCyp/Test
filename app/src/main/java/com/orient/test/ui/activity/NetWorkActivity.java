package com.orient.test.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.GridView;

import com.orient.test.R;
import com.orient.test.adapter.PhotoAdapter;
import com.orient.test.common.Image;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
    照片墙的例子 结合LruCache和DisLruCache使用
 */
public class NetWorkActivity extends AppCompatActivity {

    public final static String[] imageThumbUrls = new String[]{
            "https://img-my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383248_3693.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383243_5120.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383242_3127.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383242_9576.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383242_1721.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383219_5806.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383214_7794.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383213_4418.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383213_3557.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383210_8779.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383172_4577.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383166_3407.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383166_2224.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383166_7301.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383165_7197.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383150_8410.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383131_3736.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383130_5094.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383130_7393.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383129_8813.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383100_3554.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383093_7894.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383092_2432.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383092_3071.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383091_3119.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383059_6589.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383059_8814.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383059_2237.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383058_4330.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406383038_3602.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382942_3079.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382942_8125.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382942_4881.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382941_4559.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382941_3845.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382924_8955.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382923_2141.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382923_8437.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382922_6166.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382922_4843.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382905_5804.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382904_3362.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382904_2312.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382904_4960.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382900_2418.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382881_4490.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382881_5935.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382880_3865.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382880_4662.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382879_2553.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382862_5375.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382862_1748.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382861_7618.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382861_8606.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382861_8949.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382841_9821.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382840_6603.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382840_2405.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382840_6354.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382839_5779.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382810_7578.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382810_2436.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382809_3883.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382809_6269.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382808_4179.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382790_8326.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382789_7174.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382789_5170.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382789_4118.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382788_9532.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382767_3184.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382767_4772.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382766_4924.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382766_5762.jpg",
            "https://img-my.csdn.net/uploads/201407/26/1406382765_7341.jpg"
    };

    private GridView mGridView;
    private PhotoAdapter mAdapter;

    private int mImageThumbSize;
    private int mImageThumbSpacing;

    // 主线程的Handler
    private Handler mHandler;

    public static void show(Context context) {
        Intent intent = new Intent(context, NetWorkActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_work);

        initWidget();

    }

    private void initWidget() {
        mGridView = findViewById(R.id.grid_photo);
        mImageThumbSize = getResources().getDimensionPixelSize(
                R.dimen.common_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(
                R.dimen.common_size);
        mHandler = new Handler(Looper.getMainLooper());
        mAdapter = new PhotoAdapter(this, R.layout.recycle_item_net_work, Image.imageThumbUrls, mGridView, mHandler);
        mAdapter.addAll();
        mGridView.setAdapter(mAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        final int numColumns = (int) Math.floor(mGridView
                                .getWidth()
                                / (mImageThumbSize + mImageThumbSpacing));
                        if (numColumns > 0) {
                            int columnWidth = (mGridView.getWidth() / numColumns)
                                    - mImageThumbSpacing;
                            mAdapter.setItemHeight(columnWidth);
                            mGridView.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出程序时结束所有的下载任务
        mAdapter.cancelDownloadImage();
    }


}
