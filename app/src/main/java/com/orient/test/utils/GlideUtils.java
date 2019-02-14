package com.orient.test.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.orient.test.R;

/**
 * Created by wangjie on 2019/2/12.
 */

public class GlideUtils {
    /*
        加载图片
     */
    public static void loadUrl(Context context, String path, final ImageView imageView){
        Glide.with(context).load(path).placeholder(R.drawable.shape_item_empty).error(R.drawable.shape_item_empty).centerCrop()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        imageView.setImageDrawable(resource);
                    }
                });
    }
}
