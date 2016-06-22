package com.ghostery.privacy.appnoticesdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

/**
 *
 */
public class ImpliedConsent_Fragment extends Fragment {
    private static final String TAG = "ImpliedConsent_Frag";

    private AppNotice_Callback appNotice_callback;
    private AppNoticeData appNoticeData;

    public ImpliedConsent_Fragment() {
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
        View view = inflater.inflate(R.layout.ghostery_fragment_implied_consent, container, false);
        AppNotice_Activity.isConsentActive = true;

        // Watch for button clicks.
        AppCompatButton preferences_button = (AppCompatButton)view.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Remember that the tracker preferences screen was opened from a consent flow dialog
                Session.set(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, true);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.IMPLIED_INFO_PREF);

                // Let the calling class know the the manage preferences button was clicked
                Boolean wasHandled = false;
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    Fragment fragment = appNotice_callback.onManagePreferencesClicked();
                    if (fragment != null) {
                        FragmentTransaction transaction = AppNotice_Activity.getInstance().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.appnotice_fragment_container, fragment, AppNotice_Activity.FRAGMENT_TAG_HOST_SETTINGS);
                        transaction.addToBackStack(AppNotice_Activity.FRAGMENT_TAG_HOST_SETTINGS);
                        transaction.commit();
                        wasHandled = true;
                    }
                }

                // Open the App Notice manage preferences fragment
                if (!wasHandled) {
                    Util.showManagePreferences(getActivity());
                }
            }
        });

        AppCompatButton close_button = (AppCompatButton)view.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Let the calling class know the selected option
                if (appNotice_callback != null) {
                    appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                }

                // Close this fragment
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
    }

    public void onBackPressed() {
        AppNotice_Activity.isConsentActive = true;

        // Let the calling class know the selected option
        if (appNotice_callback != null) {
            appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
        }
    }

}
