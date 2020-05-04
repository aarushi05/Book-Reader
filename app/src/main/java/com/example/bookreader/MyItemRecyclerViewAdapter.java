package com.example.bookreader;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookreader.ItemFragment.OnListFragmentInteractionListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<PictureItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<PictureItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mImageView.setImageURI(mValues.get(position).uri);
        holder.mDateView.setText(mValues.get(position).date);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        holder.mDeleteView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              PictureItem theRemovedItem = mValues.get(position);

                // remove your item from data base
                Uri uri = theRemovedItem.uri;
                String[] split = uri.toString().split("/");
                String filename = split[split.length - 1];
                String storage = "/storage/emulated/0/Android/data/com.example.bookreader/files/Pictures/";
                File file = new File(storage,filename);
                boolean b = file.delete();

                mValues.remove(position);  // remove the item from list
                notifyItemRemoved(position); // notify the adapter about the removed item
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mDateView;
        public final ImageView mDeleteView;
        public PictureItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = view.findViewById(R.id.item_image_view);
            mDateView = view.findViewById(R.id.item_date_tv);
            mDeleteView = view.findViewById(R.id.delete);
        }
    }
}