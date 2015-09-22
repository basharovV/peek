package com.peekapps.peek.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.peekapps.peek.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Slav on 22/07/2015.
 */
public class PlaceAutocompleteAdapter extends ArrayAdapter<PlaceAutocompleteAdapter.AutoCompleteItem> implements Filterable{
    private Context context;
    private GoogleApiClient googleApiClient;
    private LatLngBounds bounds;
    private int resource;
    private PendingResult<AutocompletePredictionBuffer> resultObject;
    private ArrayList<AutoCompleteItem> autocompleteResults;

    private static final String TAG = "AutocompleteAdapter";

    public PlaceAutocompleteAdapter(Context context, int resource, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter) {
        super(context, resource);
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent)  {
        View view = null;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.autocomplete_item, parent, false);
                TextView textItem = (TextView) view.findViewById(R.id.autocompleteItem);
                textItem.setText(autocompleteResults.get(position).toString());
                break;
            case 1:
                //View
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.autocomplete_attribution, parent, false);
                break;
        }
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        if (autocompleteResults.get(position).description.equals("footer")) {
            return 1;
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                //Check if the input (constraint) is empty
                if (constraint != null) {
                    ArrayList<AutoCompleteItem> tempResults = getAutocompleteResults(constraint.toString());
                    if (tempResults != null) {
                        tempResults.add(new AutoCompleteItem("footer", "footer"));
                        filterResults.values = tempResults;
                        filterResults.count = tempResults.size();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                autocompleteResults = (ArrayList) results.values;
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

    public static class AutocompleteViewHolder {
        public TextView itemTitle;
        public ImageView googleAttribution;
    }
}
