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
import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.AppData;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

/**
 *
 */
public class ExplicitConsent_Fragment extends Fragment {
    private static final String TAG = "ExplicitConsent_Frag";

    private AppNotice_Callback appNotice_callback;
    private AppNoticeData appNoticeData;

    public ExplicitConsent_Fragment() {
        // Required empty public constructor
        appNotice_callback = (AppNotice_Callback) Session.get(Session.APPNOTICE_CALLBACK);
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
        View view = inflater.inflate(R.layout.ghostery_fragment_explicit_consent, container, false);
        AppNotice_Activity.isConsentActive = true;

        // Watch for button clicks.
        AppCompatButton preferences_button_port = (AppCompatButton)view.findViewById(R.id.preferences_button_portrait);
        preferences_button_port.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_PREF);

                // Open the App Notice manage preferences fragment
                Util.showManagePreferences(getActivity());
            }
        });

        AppCompatButton preferences_button_land = (AppCompatButton)view.findViewById(R.id.preferences_button_land);
        preferences_button_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_PREF);

                // Open the App Notice manage preferences fragment
                Util.showManagePreferences(getActivity());
            }
        });

        AppCompatButton accept_button_port = (AppCompatButton)view.findViewById(R.id.accept_button_portrait);
        accept_button_port.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_ACCEPT);

                // Remember in a persistent way that the explicit notice has been accepted
                AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, true);

                // Let the calling class know the selected option
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                }

                // Close this fragment
                AppNotice_Activity.isConsentActive = false;
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });

        AppCompatButton accept_button_land = (AppCompatButton)view.findViewById(R.id.accept_button_land);
        accept_button_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_ACCEPT);

                // Remember in a persistent way that the explicit notice has been accepted
                AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, true);

                // Let the calling class know the selected option
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                }

                // Close this fragment
                AppNotice_Activity.isConsentActive = false;
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().finish();
            }
        });

        AppCompatButton decline_button_port = (AppCompatButton)view.findViewById(R.id.decline_button_portrait);
        decline_button_port.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User cancelled the dialog...negating consent

                // Close this dialog
                getActivity().onBackPressed();
            }
        });

        AppCompatButton decline_button_land = (AppCompatButton)view.findViewById(R.id.decline_button_land);
        decline_button_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User cancelled the dialog...negating consent

                // Close this dialog
                getActivity().onBackPressed();
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
        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_DECLINE);
        AppNotice_Activity.isConsentActive = false;

        // Let the calling class know the selected option
        if (appNotice_callback != null && !getActivity().isFinishing()) {
            appNoticeData.setTrackerOnOffState(false);   // Set all non-essential tracker to off
            appNoticeData.saveTrackerStates();  // And remember the states
            appNotice_callback.onOptionSelected(false, appNoticeData.getTrackerHashMap(true));
        }

    }

}
