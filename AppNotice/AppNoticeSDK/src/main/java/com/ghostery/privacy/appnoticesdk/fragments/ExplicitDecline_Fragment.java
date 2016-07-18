package com.ghostery.privacy.appnoticesdk.fragments;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ghostery.privacy.appnoticesdk.R;

/**
 *
 */
public class ExplicitDecline_Fragment extends Fragment {
    private static final String TAG = "ExplicitDecline_Frag";

    public ExplicitDecline_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_explicit_decline, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.hide();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        handleOrientationConfig(getActivity().getResources().getConfiguration().orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientationConfig(newConfig.orientation);
    }

    protected void handleOrientationConfig(int orientation) {
        // Get the layout components
        ImageView imageView_host_app_logo = (ImageView)getActivity().findViewById(R.id.imageView_host_app_logo);

        // See if there is a host app logo
        boolean wasLogoFound = false;
        int imageResourceId = getResources().getIdentifier("@drawable/ghostery_host_app_logo", null, getActivity().getPackageName());
        if (imageResourceId > 0) {
            Drawable hostAppLogo = ResourcesCompat.getDrawable(getResources(), imageResourceId, null);
            if (hostAppLogo != null) {
                wasLogoFound = true;
                imageView_host_app_logo.setImageDrawable(hostAppLogo);
            }
        }

        // Enable and disable layout components depending on orientation and existence
        if (imageView_host_app_logo != null) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (wasLogoFound) {
                    imageView_host_app_logo.setVisibility(View.VISIBLE);
                }
            } else {
                imageView_host_app_logo.setVisibility(View.GONE);
            }
        }
    }

    public void onBackPressed() {
    }

}
