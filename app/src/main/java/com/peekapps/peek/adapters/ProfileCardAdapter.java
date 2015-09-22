package com.peekapps.peek.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peekapps.peek.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Slav on 21/06/2015.
 */
public class ProfileCardAdapter extends RecyclerView.Adapter<ProfileCardAdapter.ViewHolder>{

    private String[] datesList;

    public ProfileCardAdapter() {
        super();
        setUpList();
    }

    private void setUpList() {
        TimeZone tZone = TimeZone.getDefault();
        Calendar cal = new GregorianCalendar();
        datesList = new String[7];
        for (int i = 0; i < 7; i++) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            StringBuilder dateBuilder = new StringBuilder();
            dateBuilder.append(cal.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.LONG, Locale.getDefault()) + " ");
            dateBuilder.append(cal.get(Calendar.DAY_OF_MONTH));
            datesList[i] = dateBuilder.toString();
        }
    }

    @Override
    public void onBindViewHolder(ProfileCardAdapter.ViewHolder holder, int position) {
        holder.dateText.setText(datesList[position]);
    }

    @Override
    public ProfileCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardLayoutView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.view_profile_card, parent, false);
        return new ViewHolder(cardLayoutView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView dateText;
        public ImageView cardPhoto;

        public ViewHolder(View cardLayoutView) {
            super(cardLayoutView);
            dateText = (TextView) cardLayoutView.findViewById(R.id.profile_card_date);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        return datesList.length;
    }
}
