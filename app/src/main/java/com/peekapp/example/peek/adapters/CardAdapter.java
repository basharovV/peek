package com.peekapp.example.peek.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.*;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.peekapp.example.peek.R;
import com.peekapp.example.peek.activities.PlaceProfile;
import com.peekapp.example.peek.place_api.Place;
import com.peekapp.example.peek.views.*;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        else return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int adjusted_pos = position - 1;
        if (position > 0) {
            ((ViewHolder) holder).title.setText(placeList.get(position).getName());
            ((ViewHolder) holder).location.setText(placeList.get(position).getVicinity());
            ((ViewHolder) holder).sortAttribute.setText(String.valueOf(position));
            ((ViewHolder) holder).startMediaButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mDialog != null) {
                                mDialog.reset();
                                mDialog.getDirectory(placeList.get(position).getID());
                                mDialog.show();
                            }
                        }
                    });
                    String type = placeList.get(position).getType();
            String capitalised = type.substring(0, 1).toUpperCase() + type.substring(1);
            ((ViewHolder) holder).type.setText(capitalised);


            //Load place photo from cache
            String path = context.getExternalCacheDir() + "/" + placeList.get(position).getID() + "photo.jpg";
            File photo = new File(path);
            if (photo.exists()) {
                ImageLoader loader = ImageLoader.getInstance();
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .considerExifParams(true)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                loader.displayImage("file://" + path, ((ViewHolder) holder).image, options);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title;
        public TextView location;
        public TextView type;
        public ImageView meter;
        public ImageView image;
        public TextView sortAttribute;
        public View startMediaButton;

        private Context context;

        public ViewHolder(final View cardLayoutView, final Context context) {
            super(cardLayoutView);
            this.context = context;
            title = (TextView) cardLayoutView.findViewById(R.id.card_title);
            meter = (ImageView) cardLayoutView.findViewById(R.id.placeMeter);
            image = (ImageView) cardLayoutView.findViewById(R.id.placePhoto);
            location = (TextView) cardLayoutView.findViewById(R.id.card_location);
            type = (TextView) cardLayoutView.findViewById(R.id.card_type);
            startMediaButton = cardLayoutView.findViewById(R.id.startMediaButton);
            sortAttribute = (TextView) cardLayoutView.findViewById(R.id.placeSortAttribute);
            cardLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent plProfileIntent = new Intent(context, PlaceProfile.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            plProfileIntent.putExtra("place_object", getPlace(getPosition()));
            context.startActivity(plProfileIntent);
        }
    }

    public Place getPlace(int position) {
        return placeList.get(position);
    }
    @Override
    public int getItemCount() {
        return placeList.size();
    }


}