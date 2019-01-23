package com.orient.test.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orient.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author WangJie
 * Created on 2019/1/21.
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private List<Integer> values;
    private OnBookClickListener listener;

    public BookAdapter(List<Integer> values,OnBookClickListener listener) {
        this.values = values;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_book,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(root);
        viewHolder.cover = root.findViewById(R.id.preview);
        viewHolder.mTitle = root.findViewById(R.id.txt_name);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder,int i) {
        viewHolder.cover.setImageResource(R.drawable.preview);
        viewHolder.mTitle.setText("平凡的世界");

        viewHolder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(viewHolder.getAdapterPosition(),viewHolder.cover);
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        TextView mTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnBookClickListener{
        void onItemClick(int pos,View view);
    }
}
