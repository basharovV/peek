/*
 * Copyright (c) 2016.  Peek Apps, Inc - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Vyacheslav Basharov <basharov@peekapp.com>, February 2016
 */

package com.peekapps.peek.presentation.ui.feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peekapps.peek.domain.University;
import com.peekapps.peek.presentation.R;
import com.peekapps.peek.presentation.common.navigation.Navigator;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Slav on 26/05/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<University> universityList;
    private Context context;
    private MediaDialog mDialog;

    @Inject
    Navigator navigator;

    @Inject
    public CardAdapter(Context context) {
        this.context = context;
        universityList = new ArrayList<>();
    }

    public CardAdapter(ArrayList<University> universityList) {
        this.universityList = universityList;
    }

    public void setUniversityList(List<University> universityList) {
        this.universityList.clear();
        this.universityList.addAll(universityList);
    }

    public void addItem(University item) {
        universityList.add(item);
    }

    public void removeItem(int position) {
        if (universityList.get(position) != null){
            universityList.remove(position);
        }
        notifyItemRemoved(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.empty_cell, parent, false);
                return new EmptyHolder(emptyView, this.context);
            case 1:
                //View
                View cardLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_card, parent, false);

                //Viewholder
                return new ViewHolder(cardLayoutView, this.context);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int adjusted_pos = position - 1;
        if (position > 0) {
            ((ViewHolder) holder).title.setText(universityList.get(adjusted_pos).getName());
//            ((ViewHolder) holder).location.setText(universityList.get(position).getVicinity());
//            ((ViewHolder) holder).rank.setText(String.valueOf(position));
            ((ViewHolder) holder).image.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // CHANGE TO CUSTOM VIEW? VIA EVENT BUS
//                            FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
//                            Fragment prev = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag("feed_fragment");
//                            if (prev != null) {
//                                ft.remove(prev);
//                            }
//                            ft.addToBackStack(null);

                            // Create and show the dialog.
//                            mDialog = new MediaDialog();
//                            mDialog.show(ft, "dialog");
                        }
                    });
            ((ViewHolder) holder).headerOnTouchListener.setCurrentPosition(position);

            //Set the formatted 'distance to the university'
//            String formattedDistance = UniversityActions.getInstance(context).
//                    formatDistance(universityList.get(position).getDistanceFromUser());
//            ((ViewHolder) holder).distance.setText(formattedDistance);
            String formattedDistance = "1km";

            //Set and format the type of university
            String type = universityList.get(position).getType() != null ? universityList.get(position).getType()
                    : "no type";
            String formattedType = type.replaceAll("_", " ")
                    .substring(0, 1).toUpperCase() + type.substring(1);
            ((ViewHolder) holder).type.setText(formattedType);

            //Set the 'last updated' time attribute text
            int time = universityList.get(position).getMinutesAgoUpdated();
            ((ViewHolder) holder).time
                    .setText(String.format("%d m", time));

            int numberOfPhotos = universityList.get(position).getNumberOfPhotos();
            ((ViewHolder) holder).ranking
                    .setText(String.format("%d", numberOfPhotos));
        }
    }

    public static Bitmap loadImageFromWebOperations(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public class EmptyHolder extends RecyclerView.ViewHolder {

        public View topView;

        public EmptyHolder(final View emptyView, Context context) {
            super(emptyView);

            topView = emptyView.findViewById(R.id.feedStatusBarBg);

            //Set up height of status bar background (empty recycler view)
            int statusBarHeight = 0;
            int resourceId = context.getResources().
                    getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
            ViewGroup.LayoutParams layoutParams = topView.getLayoutParams();
            layoutParams.height = statusBarHeight;
            topView.setLayoutParams(layoutParams);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.feedCardHeader)      LinearLayout header;
        @Bind(R.id.feedCardTitle)       TextView title;
        @Bind(R.id.feedCardDistance)    TextView distance;
        @Bind(R.id.feedCardTime)        TextView time;
        @Bind(R.id.feedCardType)        TextView type;
        @Bind(R.id.feedCardImage)       ImageView image;
        @Bind(R.id.feedCardRanking)     TextView ranking;
        @Bind(R.id.feedCardView)        CardView cardView;

        public HeaderOnTouchListener headerOnTouchListener;
        private Context context;

        public ViewHolder(final View cardLayoutView, final Context context) {
            super(cardLayoutView);
            try {
                ButterKnife.bind(this, cardLayoutView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.context = context;

            cardView.setPreventCornerOverlap(false);

            //Handle header clicks - leads to profile
            headerOnTouchListener = new HeaderOnTouchListener();
            header.setOnTouchListener(headerOnTouchListener);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   navigator.navigateToUniProfile(context, 0);
                }
            });
        }
    }

    public University getUniversity(int position) {
        return universityList.get(position);
    }

    @Override
    public int getItemCount() {
        return universityList.size();
    }

    public class HeaderOnTouchListener implements View.OnTouchListener {
        int currentPosition = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.bg_card_header_rounded_pressed);
                    break;
                case MotionEvent.ACTION_UP | MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.bg_card_header_rounded);
            }
            return false;
        }

        public void setCurrentPosition(int position) {
            currentPosition = position;
        }
    }
}
