
package com.peekapp.example.peek.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.nineoldandroids.animation.Animator;
import com.peekapp.example.peek.activities.PlaceProfile;
import com.peekapp.example.peek.adapters.CardAdapter;
import com.peekapp.example.peek.adapters.PlaceAutocompleteAdapter;
import com.peekapp.example.peek.database.PlaceDbHelper;
import com.peekapp.example.peek.place_api.Place;
import com.peekapp.example.peek.R;
import com.peekapp.example.peek.place_api.PlaceActions;
import com.peekapp.example.peek.views.MediaDialog;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FeedFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private SwipeRefreshLayout refreshLayout;
    private RefreshListener refreshListener;

    private Toolbar toolbar;
    private LinearLayout optionsBarHolder;
    private ViewPager optionsBar;
    private ImageButton search;
    private AutoCompleteTextView autocompleteView;
    private ProgressBar feedProgressBar;
    private List<Place> placesList;
    private MediaDialog mediaDialog;

    GoogleApiClient apiClient;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        layoutManager = new LinearLayoutManager(getActivity());
        mediaDialog =  new MediaDialog(getActivity());
        cardAdapter = new CardAdapter(getActivity(), mediaDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_feed, container, false);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#FFAE00"), Color.parseColor("#02BFA9"));
        refreshLayout.setSize(SwipeRefreshLayout.LARGE);
        refreshListener = new RefreshListener();
        refreshLayout.setOnRefreshListener(refreshListener);

        //Set up options bar
        optionsBarHolder = (LinearLayout) rootView.findViewById(R.id.optionsBarHolder);
        optionsBar = (ViewPager) rootView.findViewById(R.id.scrollOptionsBar);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        optionsBar.setAdapter(new BarPagerAdapter(fragmentManager));
        optionsBar.setCurrentItem(0);
        //Set up the recycler view
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setOnScrollListener(new ScrollListener());
        //Set adapter when list of places is fetched from the Places API.

        return rootView;
    }

    private class BarPagerAdapter extends FragmentStatePagerAdapter {
        public BarPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SortBarFragment();
                case 1:
                    return new SearchBarFragment();
            }
            return null;
        }
    }



    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 5 && optionsBarHolder.getAlpha() == 1) {
                YoYo.with(Techniques.FadeOutUp)
                        .duration(300)
                        .playOn(optionsBarHolder);
            }
            else if (dy < -5 && optionsBarHolder.getAlpha() == 0) {
                YoYo.with(Techniques.FadeInDown)
                        .duration(300)
                        .playOn(optionsBarHolder);
            }
        }
    }
    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
            @Override
            public void onRefresh() {
                updateFeed();
            }
    }
    public void updateFeed() {
        if (cardAdapter != null) {
            if (placesList == null) {
                placesList = generatePlaces();
                cardAdapter.setPlaceList(placesList);
            } else {
                if (isDifferentList(generatePlaces())) {
                    cardAdapter.setPlaceList(generatePlaces());
                }
            }
            if (!placesList.isEmpty()) {
                refreshLayout.setRefreshing(false);
            }
        }
    }

    public void refreshLayout() {
        refreshListener.onRefresh();
    }

    public boolean isDifferentList(List<Place> plList) {
        for (Place pl : plList) {
            if (placesList.contains(pl)) {
                return false;
            }
        }
        return true;
    }
    public List<Place> generatePlaces() {
        List<Place> places;
//        for (HashMap<String, String> place : placesList) {
//            Place pl = new Place(place.get("placeid"), place.get("place_name"),
//                    place.get("vicinity"),
//                    place.get("types"), place.get("icon"));
//            places.add(pl);
//        }
        PlaceDbHelper dbHelper = new PlaceDbHelper(getActivity());
        places = dbHelper.getAllPlaces();
        return places;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Connection failed (autocomplete)", Toast.LENGTH_LONG).show();
    }
}
