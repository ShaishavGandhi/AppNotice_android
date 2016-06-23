package com.ghostery.privacy.appnoticesdk.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
                // Remember that the tracker preferences screen was opened from a consent flow dialog
                Session.set(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, true);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_PREF);

                // Let the calling class know the the manage preferences button was clicked
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    Fragment fragment = appNotice_callback.onManagePreferencesClicked();
                    if (fragment != null) {
                        // Open the host apps intermediate fragment
                        FragmentTransaction transaction = AppNotice_Activity.getInstance().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_HOST_SETTINGS);
                        transaction.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_HOST_SETTINGS);
                        transaction.commit();
                    } else {
                        // Open the App Notice manage preferences fragment
                        Util.showManagePreferences(getActivity());
                    }
                }
            }
        });

        AppCompatButton preferences_button_land = (AppCompatButton)view.findViewById(R.id.preferences_button_land);
        preferences_button_land.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Remember that the tracker preferences screen was opened from a consent flow dialog
                Session.set(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, true);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_PREF);

                // Let the calling class know the the manage preferences button was clicked
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    Fragment fragment = appNotice_callback.onManagePreferencesClicked();
                    if (fragment != null) {
                        // Open the host apps intermediate fragment
                        FragmentTransaction transaction = AppNotice_Activity.getInstance().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_HOST_SETTINGS);
                        transaction.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_HOST_SETTINGS);
                        transaction.commit();
                    } else {
                        // Open the App Notice manage preferences fragment
                        Util.showManagePreferences(getActivity());
                    }
                }
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
        LinearLayout linearLayout_port = (LinearLayout)getActivity().findViewById(R.id.buttons_layout_portrait);
        LinearLayout linearLayout_land = (LinearLayout)getActivity().findViewById(R.id.buttons_layout_landscape);
        if (linearLayout_port != null && linearLayout_land != null) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                linearLayout_land.setVisibility(View.GONE);
                linearLayout_port.setVisibility(View.VISIBLE);
            } else {
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
