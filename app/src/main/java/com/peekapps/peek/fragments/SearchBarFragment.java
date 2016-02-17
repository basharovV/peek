package com.peekapps.peek.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.support.v7.widget.AppCompatAutoCompleteTextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.peekapps.peek.R;
import com.peekapps.peek.activities.PlaceProfile;
import com.peekapps.peek.adapters.PlaceAutocompleteAdapter;
import com.peekapps.peek.place_api.Place;
import com.peekapps.peek.place_api.PlaceActions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Slav on 26/07/2015.
 */

public class SearchBarFragment extends Fragment {

    private AppCompatAutoCompleteTextView autocompleteView;
    private ImageButton search;

    private GoogleApiClient apiClient;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Set up GoogleApiClient
        apiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .build();

        apiClient.connect();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search_bar, container, false);

        //Set up autocomplete
        autocompleteView = (AppCompatAutoCompleteTextView) rootView.findViewById(R.id.feedAutocompleteView);
        autocompleteView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (autocompleteView.getRight() - autocompleteView.getCompoundDrawables()[2].getBounds().width())) {
                        autocompleteView.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });
        //Set up autocomplete
        List<Integer> filterTypes = new ArrayList<Integer>();
        filterTypes.add(com.google.android.gms.location.places.Place.TYPE_ESTABLISHMENT);
        final PlaceAutocompleteAdapter autocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(),
                R.layout.autocomplete_item, apiClient,
                new LatLngBounds(new LatLng(-85, -180), new LatLng(85, 180)),
                AutocompleteFilter.create(filterTypes));
        autocompleteView.setThreshold(2);
        autocompleteView.setAdapter(autocompleteAdapter);
        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchSelector selector = new SearchSelector(autocompleteAdapter, position);
                selector.execute();
            }
        });
        return rootView;
    }

    private class SearchSelector extends AsyncTask<Void, Place, Place> {

        private int position;
        private PlaceAutocompleteAdapter adapter;

        public SearchSelector(PlaceAutocompleteAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        protected Place doInBackground(Void... params) {
            return PlaceActions.getInstance(getContext()).getPlace(getActivity(),
                    adapter.getItem(position).getPlaceId(), true);
        }

        @Override
        protected void onPostExecute(Place place) {
            Intent plProfileIntent = new Intent(getActivity(), PlaceProfile.class).
                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            plProfileIntent.putExtra("place_object", place);
            getActivity().startActivity(plProfileIntent);
        }
    }
}

