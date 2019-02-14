package com.orient.test.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.orient.test.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author WangJie
 * Created on 2019/1/18.
 */
public class PhotoAdapter extends ArrayAdapter<String> {
    public static final String TAG = "PhotoAdapter";

    private GridView mGridView;
    private Handler mHandler;
    private Context mContext;
    // 内存缓存
    private LruCache<String, Bitmap> mMemoryCache;
    // 硬盘缓存
    private DiskLruCache mDiskLruCache;
    private OkHttpClient mOkHttpClient;
    // 线程池
    private ExecutorService service;
    // 记录每一个子项的高度
    private int mItemHeight = 0;

    public PhotoAdapter(@NonNull Context context, int resource, @NonNull String[] objects, GridView gridView, Handler handler) {
        super(context, resource, objects);
        mContext = context;
        mGridView = gridView;
        mHandler = handler;
        init();
    }

    /*
            初始化一些基本使用的配置
         */
    private void init() {
        // 构造OkHttpClient并且设置线程池的数量
        mOkHttpClient = new OkHttpClient.Builder()
                .build();

        // 构建一定线程数量的线程池
        service = Executors.newFixedThreadPool(6);

        // 获取应用可用的最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        // 设置图片缓存的大小最大为缓存的1/8
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getByteCount();
            }
        };
        //  设置硬盘缓存实例
        try {
            File file = getDiskCacheDir(mContext, "photo");
            if (!file.exists())
                file.mkdirs();
            mDiskLruCache = DiskLruCache.open(file, getAppInfoVersion(), 1, 10 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final String url = getItem(position);
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.recycle_item_net_work, parent, false);
        }
        final ImageView imageView = view.findViewById(R.id.grid_photo);
        if (imageView.getLayoutParams().height != mItemHeight)
            imageView.getLayoutParams().height = mItemHeight;
        imageView.setTag(url);
        imageView.setImageResource(R.drawable.shape_item_empty);
        loadBitmaps(imageView, url);
        return view;
    }

    /**
     * 加载Bitmap对象，如果Bitmap不在LruCache中，就开启线程去查询
     *
     * @param imageView 图片
     * @param url       地址
     */
    private void loadBitmaps(ImageView imageView, String url) {
        //Bitmap bitmap = getBitmapFromMemoryCache(url);
        Bitmap bitmap = null;
        if (bitmap != null) {
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            service.execute(new ImageRunnable(url));
        }
    }


    /*
        使用MD5算法进行加密
     */
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    private boolean downloadImage(final String url, OutputStream outputStream) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 执行操作
        Call call = mOkHttpClient.newCall(request);
        Response response = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            response = call.execute();
            in = new BufferedInputStream(response.body().byteStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 取消所有的下载程序
    public void cancelDownloadImage() {
        service.shutdown();
    }

    /*
        根据传入的uniqueName获取唯一的硬盘的缓存路径
    */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public int getAppInfoVersion() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    // 添加Bitmap到内存缓存中
    private void addBitmapToMemoryCache(Bitmap bitmap, String url) {
        if (getBitmapFromMemoryCache(url) == null)
            mMemoryCache.put(url, bitmap);
    }

    // 从内存缓存中获取Bitmap
    private Bitmap getBitmapFromMemoryCache(String url) {
        return mMemoryCache.get(url);
    }

    // 设置高度
    public void setItemHeight(int height) {
        if (height == mItemHeight) {
            return;
        }
        mItemHeight = height;
        notifyDataSetChanged();
    }

    // 将硬盘缓存同步到Journal文件中
    public void flushCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public class ImageRunnable implements Runnable {
        private String url;
        private Bitmap bitmap;

        public ImageRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot = null;
            final String key = hashKeyForDisk(url);
            // 查找key对应的硬盘缓存
            try {
                snapshot = mDiskLruCache.get(key);
                if (snapshot == null) {
                    // 如果对应的硬盘缓存没找到，就开始网络请求，并且写入缓存
                    DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadImage(url, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    snapshot = mDiskLruCache.get(key);
                }

                if (snapshot != null) {
                    fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                    fileDescriptor = fileInputStream.getFD();
                }
                // 将缓存数据解析成Bitmap对象
                if (fileDescriptor != null)
                    bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                if (bitmap != null) {
                    // 将图片添加到内存缓存中
                    addBitmapToMemoryCache(bitmap, url);
                }
                if (bitmap != null)
                    // 在主线程中更新
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ImageView image = mGridView.findViewWithTag(url);
                            if (image != null)
                                image.setImageBitmap(bitmap);
                        }
                    });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
