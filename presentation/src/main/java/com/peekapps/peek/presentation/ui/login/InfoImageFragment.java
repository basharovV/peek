package com.peekapps.peek.presentation.ui.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.peekapps.peek.presentation.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Slav on 19/09/2015.
 */
public class InfoImageFragment extends Fragment {
    private int[] imagesArray;
    private int position;
    private Context context;

    public InfoImageFragment() {
        imagesArray = new int[] {
                R.drawable.login_info_img_0,
                R.drawable.login_info_img_1,
                R.drawable.login_info_img_2,
                R.drawable.login_info_img_3};
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_login_info, container, false);
        ImageView image = (ImageView) rootView.findViewById(R.id.loginInfoImage);
        Bundle args = getArguments();
        int pos = args.getInt("pos", 0);
//        String uri = "drawable/login_info_img_" + pos;
//        int resource = getResources().getIdentifier(uri, null, context.getPackageName());
//        Drawable imageDrawable = getResources().getDrawable(resource);
        Picasso.with(getActivity()).
                load(imagesArray[pos])
                .into(image);

        return rootView;
    }
}
