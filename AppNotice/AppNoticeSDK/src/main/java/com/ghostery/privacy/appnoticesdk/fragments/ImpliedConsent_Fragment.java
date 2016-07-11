package com.ghostery.privacy.appnoticesdk.fragments;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Util;

/**
 *
 */
public class ImpliedConsent_Fragment extends Fragment {
    private static final String TAG = "ImpliedConsent_Frag";

    private AppNoticeData appNoticeData;

    public ImpliedConsent_Fragment() {
        // Required empty public constructor
        appNoticeData = AppNoticeData.getInstance(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_implied_consent, container, false);
        AppNotice_Activity.isConsentActive = true;

        // Watch for button clicks.
        AppCompatButton preferences_button = (AppCompatButton)view.findViewById(R.id.preferences_button_portrait);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.IMPLIED_INFO_PREF);

                // Open the App Notice manage preferences fragment
                Util.showManagePreferences(getActivity());
            }
        });

        AppCompatButton preferences_button_land = (AppCompatButton)view.findViewById(R.id.preferences_button_landscape);
        preferences_button_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.IMPLIED_INFO_PREF);

                // Open the App Notice manage preferences fragment
                Util.showManagePreferences(getActivity());
            }
        });

        AppCompatButton close_button = (AppCompatButton)view.findViewById(R.id.close_button_portrait);
        close_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Let the calling class know the selected option
                if (AppNotice_Activity.appNotice_callback != null) {
                    AppNotice_Activity.appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                }

                // Close this fragment
                AppNotice_Activity.isConsentActive = false;
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });

        AppCompatButton close_button_land = (AppCompatButton)view.findViewById(R.id.close_button_landscape);
        close_button_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Let the calling class know the selected option
                if (AppNotice_Activity.appNotice_callback != null) {
                    AppNotice_Activity.appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                }

                // Close this fragment
                AppNotice_Activity.isConsentActive = false;
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        handleOrientationConfig(getActivity().getResources().getConfiguration().orientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        handleOrientationConfig(newConfig.orientation);
    }

    protected void handleOrientationConfig(int orientation) {
        // Get the layout components
        ImageView imageView_hoat_app_logo = (ImageView)getActivity().findViewById(R.id.imageView_hoat_app_logo);
        LinearLayout linearLayout_port = (LinearLayout)getActivity().findViewById(R.id.buttons_layout_portrait);
        LinearLayout linearLayout_land = (LinearLayout)getActivity().findViewById(R.id.buttons_layout_landscape);

        // See if there is a host app logo
        boolean wasLogoFound = false;
        int imageResourceId = getResources().getIdentifier("@drawable/ghostery_host_app_logo", null, getActivity().getPackageName());
        if (imageResourceId > 0) {
            Drawable hostAppLogo = ResourcesCompat.getDrawable(getResources(), imageResourceId, null);
            if (hostAppLogo != null) {
                wasLogoFound = true;
                imageView_hoat_app_logo.setImageDrawable(hostAppLogo);
            }
        }

        // Enable and disable layout components depending on orientation and existence
        if (linearLayout_port != null && linearLayout_land != null && imageView_hoat_app_logo != null) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (wasLogoFound) {
                    imageView_hoat_app_logo.setVisibility(View.VISIBLE);
                }
                linearLayout_land.setVisibility(View.GONE);
                linearLayout_port.setVisibility(View.VISIBLE);
            } else {
                imageView_hoat_app_logo.setVisibility(View.GONE);
                linearLayout_port.setVisibility(View.GONE);
                linearLayout_land.setVisibility(View.VISIBLE);
            }
        }
    }

    public void onBackPressed() {
        AppNotice_Activity.isConsentActive = false;

        // Let the calling class know the selected option
        if (AppNotice_Activity.appNotice_callback != null) {
            AppNotice_Activity.appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
        }
    }

}
