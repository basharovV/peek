/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.uniprofile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.model.PhotoModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Slav on 23/05/2016.
 */
public class UniProfileThumbnailAdapter extends RecyclerView.Adapter<UniProfileThumbnailAdapter.ThumbnailHolder> {

    private List<PhotoModel> photoList;
    private Context context;


    @Inject
    public UniProfileThumbnailAdapter(Context context) {
        this.context = context;
    }

    public void setPhotoList(List<PhotoModel> photoList) {
        this.photoList = photoList;
        notifyDataSetChanged();
    }


    @Override
    public ThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View thumbnailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.uni_profile_thumbnail,
                parent, false);
        return new ThumbnailHolder(thumbnailView);
    }

    @Override
    public void onBindViewHolder(ThumbnailHolder holder, int position) {
        holder.thumbText.setText(String.valueOf(position));
        Picasso.with(context)
                .load(photoList.get(position).getPhotoUrl())
                .into(holder.thumbnail);
    }

    @Override
    public void onBindViewHolder(ThumbnailHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ThumbnailHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public TextView thumbText;

        public ThumbnailHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView) itemView.findViewById(R.id.uniProfileThumbnailImg);
            thumbText = (TextView) itemView.findViewById(R.id.uniProfileThumbnailText);
        }
    }
}
