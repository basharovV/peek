package com.peekapp.example.peek.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.peekapp.example.peek.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by Slav on 22/07/2015.
 */
public class PlaceAutocompleteAdapter extends ArrayAdapter<PlaceAutocompleteAdapter.AutoCompleteItem> implements Filterable{

    private GoogleApiClient googleApiClient;
    private LatLngBounds bounds;
    private int resource;
    private PendingResult<AutocompletePredictionBuffer> resultObject;
    private ArrayList<AutoCompleteItem> autocompleteResults;

    private static final String TAG = "AutocompleteAdapter";

    public PlaceAutocompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, resource);
        this.googleApiClient = googleApiClient;
        this.bounds = bounds;
        this.resource = resource;

//        mPlaceFilter = filter;
    }

    @Override
    public int getCount() {
        return autocompleteResults.size();
    }

    @Override
    public AutoCompleteItem getItem(int position) {
        return autocompleteResults.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                //Check if the input (constraint) is empty
                if (constraint != null) {
                    autocompleteResults = getAutocompleteResults(constraint.toString());
                    if (autocompleteResults != null) {
                        filterResults.values = autocompleteResults;
                        filterResults.count = autocompleteResults.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
    }


    private ArrayList<AutoCompleteItem> getAutocompleteResults(String query) {
        resultObject = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, query, bounds
                , null);
        AutocompletePredictionBuffer buffer = resultObject.await(30, TimeUnit.SECONDS);
        ArrayList<AutoCompleteItem> results = new ArrayList<AutoCompleteItem>();
        if (!buffer.getStatus().isSuccess()) {
            Log.e(TAG, buffer.getStatus().getStatusMessage());

            buffer.release();
            return null;
        }

        for (AutocompletePrediction prediction: buffer) {
            if (prediction.getPlaceTypes().contains(Place.TYPE_ESTABLISHMENT)) {
                results.add(new AutoCompleteItem(prediction.getPlaceId(), prediction.getDescription()));
            }
        }
        buffer.release();

        return results;
    }

    public class AutoCompleteItem {

        public CharSequence placeId;
        public CharSequence description;

        AutoCompleteItem(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }

        public String getPlaceId() {
            return placeId.toString();
        }
    }
}
