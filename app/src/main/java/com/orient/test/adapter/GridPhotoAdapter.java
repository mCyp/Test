package com.orient.test.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 瀑布适配器
 *
 * Created by wangjie on 2019/1/30.
 */

public class GridPhotoAdapter extends RecyclerView.Adapter<GridPhotoAdapter.ViewHolder> {

    public static final String TAG = "GridPhotoAdapter";
    // 照片的网络路径
    private String[] urls;
    // 内存缓存
    private LruCache<String,Bitmap> mMemoryCache;
    // 硬盘缓存
    private DiskLruCache mDisLruCache;
    // OkhttpClient
    private OkHttpClient okHttpClient;
    // 线程池 用来请求下载图片
    private ExecutorService service;
    // 主线程Handler 用来图片下载完成后更新ImageView
    private Handler mHandler;
    // 上下文
    private Context mContext;

    public GridPhotoAdapter(String[] urls, Handler mHandler, Context context) {
        this.urls = urls;
        this.mHandler = mHandler;
        this.mContext = context;
        init();
    }

    /*
        一些必要的初始化的工作
     */
    private void init() {
        okHttpClient = new OkHttpClient.Builder()
                .build();

        // 构建一定数量的线程池
        service = Executors.newFixedThreadPool(6);

        // 构建内存缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/8;
        mMemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
                return value.getByteCount();
            }
        };

        // 构建硬盘缓存实例
        File file = getDiskCacheDir(mContext,"photo");
        if(!file.exists())
            file.mkdirs();
        try {
            mDisLruCache = DiskLruCache.open(file,getAppInfoVersion(),1,10*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        根据传入的uniqueName获取唯一的硬盘的缓存路径
    */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    private int getAppInfoVersion() {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_net_work,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(root);
        viewHolder.imageView = root.findViewById(R.id.grid_photo);
        // root.setTag(urls[i]);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ImageView imageView = viewHolder.imageView;
        String url = urls[i];
        imageView.setTag(url);
        imageView.setImageResource(R.drawable.shape_item_empty);
        loadBitmaps(imageView, url);
    }

    /**
     * 加载Bitmap对象，如果Bitmap不在LruCache中，就开启线程去查询
     *
     * @param imageView 图片
     * @param url       地址
     */
    private void loadBitmaps(ImageView imageView, String url) {
        Bitmap bitmap = getBitmapFromMemoryCache(url);
        if (bitmap != null) {
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        } else {
            service.execute(new ImageRunnable(url,imageView));
        }
    }

    @Override
    public int getItemCount() {
        return urls.length;
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

    /*
        下载图片
     */
    private boolean downloadImage(final String url, OutputStream outputStream) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 执行操作
        Call call = okHttpClient.newCall(request);
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

    // 添加Bitmap到内存缓存中
    private void addBitmapToMemoryCache(Bitmap bitmap, String url) {
        if (getBitmapFromMemoryCache(url) == null)
            mMemoryCache.put(url, bitmap);
    }

    // 从内存缓存中获取Bitmap
    private Bitmap getBitmapFromMemoryCache(String url) {
        return mMemoryCache.get(url);
    }

    // 取消所有的下载程序
    public void cancelDownloadImage() {
        service.shutdown();
    }

    // 将硬盘缓存同步到Journal文件中
    public void flushCache() {
        if (mDisLruCache != null) {
            try {
                mDisLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class ImageRunnable implements Runnable {
        private String url;
        private Bitmap bitmap;
        private ImageView mView;

        public ImageRunnable(String url,ImageView imageView) {
            this.url = url;
            this.mView = imageView;
        }

        @Override
        public void run() {
            FileDescriptor fileDescriptor = null;
            FileInputStream fileInputStream = null;
            DiskLruCache.Snapshot snapshot = null;
            final String key = hashKeyForDisk(url);
            // 查找key对应的硬盘缓存
            try {
                snapshot = mDisLruCache.get(key);
                if (snapshot == null) {
                    // 如果对应的硬盘缓存没找到，就开始网络请求，并且写入缓存
                    DiskLruCache.Editor editor = mDisLruCache.edit(key);
                    if (editor != null) {
                        OutputStream outputStream = editor.newOutputStream(0);
                        if (downloadImage(url, outputStream)) {
                            editor.commit();
                        } else {
                            editor.abort();
                        }
                    }
                    snapshot = mDisLruCache.get(key);
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
                           /* ImageView image = mRecyclerView.findViewWithTag(url);
                            if (image != null)
                                image.setImageBitmap(bitmap);*/
                           mView.setImageBitmap(bitmap);
                        }
                    });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
