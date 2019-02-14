package com.orient.test.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orient.test.R;
import com.orient.test.utils.GlideUtils;

/**
 * Created by wangjie on 2019/2/12.
 */

public class GlideAdapter extends RecyclerView.Adapter<GlideAdapter.ViewHolder>{

    // 照片的网络路径
    private String[] urls;
    private Context mContext;

    public GlideAdapter(String[] urls, Context context) {
        this.urls = urls;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_item_net_work,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(root);
        viewHolder.imageView = root.findViewById(R.id.grid_photo);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ImageView imageView = viewHolder.imageView;
        String url = urls[i];
        GlideUtils.loadUrl(mContext,url,imageView);
    }

    @Override
    public int getItemCount() {
        return urls.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
