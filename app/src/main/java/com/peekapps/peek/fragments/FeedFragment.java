
package com.peekapps.peek.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.adapters.CardAdapter;
import com.peekapps.peek.database.PlaceDbHelper;
import com.peekapps.peek.fragments_utils.OnPermissionsListener;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.R;
import com.peekapps.peek.place_api.PlaceListSorter;
import com.peekapps.peek.views.MediaDialog;

import java.util.List;

public class FeedFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
    , OnPermissionsListener, OnChangeSortListener {

    // Places attributes static values for reference
    // eg. when choosing sorting criteria
    public static final int FEED_TYPE_POPULARITY = 0;
    public static final int FEED_TYPE_LAST_UPDATE = 1;
    public static final int FEED_TYPE_DISTANCE = 2;
    public static final int FEED_TYPE_FRIENDS = 3;
    //------------------------------------------------

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private LinearLayoutManager layoutManager;
    private View statusBarBackground;

    private SwipeRefreshLayout refreshLayout;
    private RefreshListener refreshListener;

    private Toolbar toolbar;

    public static final int POSITION_SORT_BAR = 0;
    public static final int POSITION_SEARCH_BAR = 1;

    LinearLayout optionsBarHolder;
    private FragmentManager fragmentManager;
    private ViewPager optionsBar;
    private ImageButton search;
    private AutoCompleteTextView autocompleteView;
    private ProgressBar feedProgressBar;
    private List<Place> placesList;
    private MediaDialog mediaDialog;

    GoogleApiClient apiClient;

    @Override
    public void onPermissionsGranted() {
        enableFeed();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        layoutManager = new LinearLayoutManager(getActivity());
        cardAdapter = new CardAdapter(getActivity(), mediaDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_feed, container, false);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setColorSchemeColors(Color.parseColor("#FFAE00"), Color.parseColor("#02BFA9"));
        refreshListener = new RefreshListener();
        refreshLayout.setOnRefreshListener(refreshListener);

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        refreshLayout.setProgressViewOffset(false, size.y / 9, size.y / 5);


        //Set up options bar
        optionsBarHolder = (LinearLayout) rootView.findViewById(R.id.optionsBarHolder);
        optionsBar = (ViewPager) rootView.findViewById(R.id.scrollOptionsBar);

        fragmentManager = getChildFragmentManager();

        //Set up the recycler view

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new ScrollListener());
        //Set adapter when list of places is fetched from the Places API.

        if (Build.VERSION.SDK_INT >= 23) {
            if (((PeekViewPager) getActivity()).allPermissionsGranted()) {
                enableFeed();
            }
        }
        else enableFeed();
        return rootView;
    }

    public void enableFeed() {
        optionsBar.setAdapter(new BarPagerAdapter(fragmentManager));
        optionsBar.setCurrentItem(0);

        recyclerView.setAdapter(cardAdapter);
        updateFeed();
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
                    SortBarFragment sortFragment = new SortBarFragment();
                    sortFragment.setChangeSortListener(FeedFragment.this);
                    return sortFragment;
                case 1:
                    return new SearchBarFragment();
            }
            return null;
        }

        public void setSortingBarListener() {
            FragmentManager fm = getChildFragmentManager();
            SortBarFragment sortBarFragment = (SortBarFragment)fm.getFragments()
                    .get(POSITION_SORT_BAR);
            sortBarFragment.setChangeSortListener(FeedFragment.this);
            }
        }


    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (Build.VERSION.SDK_INT >= 21) {
//                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
//                    Window window = getActivity().getWindow();
//                    // clear FLAG_TRANSLUCENT_STATUS flag:
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                    // add
//                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                    // finally change the color
//                    window.setStatusBarColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
//                } else {
//                    Window window = getActivity().getWindow();
//                    // clear FLAG_TRANSLUCENT_STATUS flag:
//                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 2 && optionsBarHolder.getAlpha() == 1) {
                hideOptionsBar();
            }
            else if (dy < -2 && optionsBarHolder.getAlpha() == 0) {
                showOptionsBar();
            }
        }
    }
    private class RefreshListener implements SwipeRefreshLayout.OnRefreshListener {
            @Override
            public void onRefresh() {
                hideOptionsBar();
                updateFeed();
                showOptionsBar();
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
            refreshLayout.setRefreshing(false);
        }
    }

    public void onRequestFeedSort(int criteria) {
        //Call the sorting method
        PlaceListSorter.sort(placesList, criteria);

        //Update the adapter to use the sorted list
        cardAdapter.setPlaceList(placesList);
    }

    public void showOptionsBar() {
        YoYo.with(Techniques.FadeInDown)
                .duration(300)
                .playOn(optionsBarHolder);
    }

    public void hideOptionsBar() {
        YoYo.with(Techniques.FadeOutUp)
                .duration(300)
                .playOn(optionsBarHolder);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

