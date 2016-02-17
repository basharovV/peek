
package com.peekapps.peek.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.peekapps.peek.activities.PeekViewPager;
import com.peekapps.peek.adapters.CardAdapter;
import com.peekapps.peek.database.PlaceDbHelper;
import com.peekapps.peek.fragments_utils.OnPermissionsListener;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.R;
import com.peekapps.peek.place_api.PlaceListSorter;

import java.util.List;

public class FeedFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
    , OnPermissionsListener, OnChangeSortListener {

    // Places attributes static values for reference
    // when choosing sorting criteria
    public static final int SORT_TYPE_POPULARITY = 0;
    public static final int SORT_TYPE_LAST_UPDATE = 1;
    public static final int SORT_TYPE_DISTANCE = 2;
    public static final int SORT_TYPE_FRIENDS = 3;
    public int currentSortType = 0; //DEFAULT

    public static final int POSITION_SORT_BAR = 0;
    public static final int POSITION_SEARCH_BAR = 1;


    // OPTIONS BAR
    // - Components
    private LinearLayout optionsBarHolder;
    private AppCompatSpinner sortSpinner;
    private ImageView searchButton;
    // - Utils
    private AdapterView.OnItemSelectedListener sortTypeSelectedListener;

    // FEED
    // - Components
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private FragmentManager fragmentManager;
    // - Utils
    private RefreshListener refreshListener;
    private CardAdapter cardAdapter;
    private LinearLayoutManager layoutManager;
    private View statusBarBackground;

    //List of places shown in feed
    private List<Place> placesList;

    GoogleApiClient apiClient;

    @Override
    public void onPermissionsGranted() {
        enableFeed();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        layoutManager = new LinearLayoutManager(getActivity());
        cardAdapter = new CardAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_feed, container, false);


        optionsBarHolder = (LinearLayout) rootView.findViewById(R.id.optionsBarHolder);
        sortSpinner = (AppCompatSpinner) rootView.findViewById(R.id.feedSortSpinner);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        setupUI();

        if (Build.VERSION.SDK_INT >= 23) {
            if (((PeekViewPager) getActivity()).allPermissionsGranted()) {
                enableFeed();
            }
        }
        else enableFeed();
        return rootView;
    }

    private void setupUI() {
        //Recycler view
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new ScrollListener());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Refresh functionality
        refreshLayout.setColorSchemeColors(Color.parseColor("#FFAE00"), Color.parseColor("#02BFA9"));
        refreshListener = new RefreshListener();
        refreshLayout.setOnRefreshListener(refreshListener);

        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        refreshLayout.setProgressViewOffset(false, size.y / 10, size.y / 5);

        //Sorting spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.feed_sort_options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setup spinner functionality
        sortSpinner.setAdapter(adapter);
        sortTypeSelectedListener = new OnSortTypeSelectedListener();
        sortSpinner.setOnItemSelectedListener(sortTypeSelectedListener);

        //Set up options bar
//        optionsBar = (ViewPager) rootView.findViewById(R.id.scrollOptionsBar);

        fragmentManager = getChildFragmentManager();
    }

    public void enableFeed() {
//        optionsBar.setAdapter(new BarPagerAdapter(fragmentManager));
//        optionsBar.setCurrentItem(0);

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
            Log.d("FeedFragment", "Options height: " + optionsBarHolder.getMeasuredHeight());
            int verticalOffset = recyclerView.computeVerticalScrollOffset();

            Log.d("FeedFragment", "dy: " + verticalOffset + "| Options Y: " + optionsBarHolder.getTranslationY());
            if (verticalOffset < optionsBarHolder.getMeasuredHeight()
                    || verticalOffset < optionsBarHolder.getHeight()) {
                optionsBarHolder.setTranslationY(-verticalOffset);
            }
//            if (dy > 2 && optionsBarHolder.getAlpha() == 1) {
//                hideOptionsBar();
//            }
//            else if (dy < -2 && optionsBarHolder.getAlpha() == 0) {
//                showOptionsBar();
//            }
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
//        if (PlaceActions.getInstance().getCurrentLocation(getActivity()) != null) {
        if (cardAdapter != null) {
            if (placesList == null) {
                placesList = generatePlaces();
                cardAdapter.setPlaceList(placesList);
            } else {
                List<Place> updatedPlaces = generatePlaces();
                if (updatedPlaces != null && isDifferentList(updatedPlaces)) {
                    cardAdapter.setPlaceList(generatePlaces());
                }
            }
            refreshLayout.setRefreshing(false);
        }
    }

//    public void updatePlaces() {
//        ParseQuery<PlaceObject> placesQuery = ParseQuery.getQuery("Place");
//        ParseGeoPoint userGeoPoint = new ParseGeoPoint();
//        Location userLocation =  PlaceActions.getInstance().getCurrentLocation(getActivity());
//        userLocation.setLatitude(userLocation.getLatitude());
//        userLocation.setLongitude(userLocation.getLongitude());
//        placesQuery.whereNear("placeGeoPoint", userGeoPoint);
//        placesQuery.setLimit(10);
//        placesQuery.findInBackground(new FindCallback<PlaceObject>() {
//            @Override
//            public void done(List<PlaceObject> plObjects, ParseException e) {
//                if (e == null) { //Success
//                    placesList = ParseUtils.toPeekPlaceArray(getActivity(), plObjects);
//                    cardAdapter.setPlaceList(generatePlaces());
//                    refreshLayout.setRefreshing(false);
//                }
//            }
//        });
//    }

    //------------- SORTING --------------------

    private class OnSortTypeSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != currentSortType) {
                onRequestFeedSort(position);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void onRequestFeedSort(int criteria) {
        //Call the sorting method
        PlaceListSorter.sort(placesList, criteria);

        //Update the adapter to use the sorted list
        cardAdapter.setPlaceList(placesList);
    }

    public void showOptionsBar() {
//        YoYo.with(Techniques.FadeInDown)
//                .duration(300)
//                .playOn(optionsBarHolder);
    }

    public void hideOptionsBar() {
//        YoYo.with(Techniques.FadeOutUp)
//                .duration(300)
//                .playOn(optionsBarHolder);
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

