package com.peekapps.peek.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.UiThread;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.peekapps.peek.R;
import com.peekapps.peek.activities.PlaceProfile;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.place_api.PlaceActions;
import com.peekapps.peek.views.*;
import com.squareup.picasso.Picasso;
import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Slav on 26/05/2015.
 */
public class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Place> placeList;
    private Context context;
    private MediaDialog mDialog;

    public CardAdapter(Context context, MediaDialog dialog) {
        this.context = context;
        //Display loading card
        placeList = new ArrayList<>();
        placeList.add(new Place("LOADING...", "...", "...", "...", null));
        mDialog = dialog;
    }

    public CardAdapter(ArrayList<Place> venueList) {
        this.placeList = venueList;
    }

    @UiThread
    public void setPlaceList(List<Place> placeList) {
        this.placeList.clear();
        this.placeList.addAll(placeList);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.empty_cell, parent, false);
                return new EmptyHolder(emptyView);
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
            ((ViewHolder) holder).title.setText(placeList.get(position).getName());
//            ((ViewHolder) holder).location.setText(placeList.get(position).getVicinity());
//            ((ViewHolder) holder).rank.setText(String.valueOf(position));
            ((ViewHolder) holder).image.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mDialog != null) {
                                mDialog.getDirectory(placeList.get(position).getID());
                                mDialog.start();
                            }
                        }
                    });
            ((ViewHolder) holder).headerOnTouchListener.setCurrentPosition(position);

            //Set the formatted 'distance to the place'
            String formattedDistance = PlaceActions.getInstance().
                    formatDistance(placeList.get(position).getDistance());
            ((ViewHolder) holder).distance.setText(formattedDistance);

            //Set and format the type of place
            String type = placeList.get(position).getType();
            String formattedType = type.replaceAll("_", " ")
                    .substring(0, 1).toUpperCase() + type.substring(1);
            ((ViewHolder) holder).type.setText(formattedType);

            //Set the 'last updated' time attribute text
            int time = placeList.get(position).getMinutesAgoUpdated();
            ((ViewHolder) holder).time
                    .setText(String.format("%d m", time));

            int numberOfPhotos = placeList.get(position).getNumberOfPhotos();
            ((ViewHolder) holder).ranking
                    .setText(String.format("%d", numberOfPhotos));

            //Load place photo from cache
            String path = context.getExternalCacheDir() + "/" + placeList.get(position).getID() + "photo.jpg";
            File photo = new File(path);
            if (photo.exists()) {
                Picasso.with(context)
                        .load(photo)
                        .fit()
                        .centerCrop()
                        .into(((ViewHolder) holder).image);
//                ImageLoader loader = ImageLoader.getInstance();
//                DisplayImageOptions options = new DisplayImageOptions.Builder()
//                        .cacheOnDisk(true)
//                        .cacheInMemory(true)
//                        .considerExifParams(true)
//                        .imageScaleType(ImageScaleType.EXACTLY)
//                        .build();
//                loader.displayImage("file://" + path, ((ViewHolder) holder).image, options);
            }
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
        public EmptyHolder(final View emptyView) {
            super(emptyView);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public LinearLayout header;
        public HeaderOnTouchListener headerOnTouchListener;
        public TextView title;
        public TextView location;
        public TextView distance;
        public TextView time;
        public TextView type;
        public ImageView image;
        public TextView ranking;
        public View startMediaButton;

        private Context context;

        public ViewHolder(final View cardLayoutView, final Context context) {
            super(cardLayoutView);
            this.context = context;
            header = (LinearLayout) cardLayoutView.findViewById(R.id.card_header_layout);
            title = (TextView) cardLayoutView.findViewById(R.id.card_title);
            distance = (TextView) cardLayoutView.findViewById(R.id.card_distance);
            image = (ImageView) cardLayoutView.findViewById(R.id.placePhoto);
            time = (TextView) cardLayoutView.findViewById(R.id.card_time);
            type = (TextView) cardLayoutView.findViewById(R.id.card_type);
            ranking = (TextView) cardLayoutView.findViewById(R.id.placeRanking);

            CardView cardView = (CardView) cardLayoutView.findViewById(R.id.cardView);
            cardView.setPreventCornerOverlap(false);

            //Handle header clicks - leads to profile
            headerOnTouchListener = new HeaderOnTouchListener();
            header.setOnTouchListener(headerOnTouchListener);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent plProfileIntent = new Intent(context, PlaceProfile.class).
                            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    plProfileIntent.putExtra("place_object", getPlace(getAdapterPosition()));
                    context.startActivity(plProfileIntent);
                }
            });
        }
    }

    public Place getPlace(int position) {
        return placeList.get(position);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class HeaderOnTouchListener implements View.OnTouchListener {
        int currentPosition = 0;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setBackgroundResource(R.drawable.bg_card_header_rounded_pressed);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    v.setBackgroundResource(R.drawable.bg_card_header_rounded);            }
            return false;
        }

        public void setCurrentPosition(int position) {
            currentPosition = position;
        }
    }
}
