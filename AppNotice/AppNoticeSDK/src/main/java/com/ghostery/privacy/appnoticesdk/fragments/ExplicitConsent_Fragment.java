package com.ghostery.privacy.appnoticesdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        // Watch for button clicks.
        AppCompatButton preferences_button = (AppCompatButton)view.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Remember that the tracker preferences screen was opened from a consent flow dialog
                Session.set(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, true);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_PREF);

                // Let the calling class know the the manage preferences button was clicked
                Boolean wasHandled = false;
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    wasHandled = appNotice_callback.onManagePreferencesClicked();
                }

                // Open the App Notice manage preferences fragment
                if (!wasHandled) {
                    Util.showManagePreferences(getActivity());
                }
            }
        });

        AppCompatButton accept_button = (AppCompatButton)view.findViewById(R.id.accept_button);
        accept_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_ACCEPT);

                // Remember in a persistent way that the explicit notice has been accepted
                AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, true);

                // Let the calling class know the selected option
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
                }

                // Close this dialog
                getActivity().onBackPressed();
            }
        });

        AppCompatButton decline_button = (AppCompatButton)view.findViewById(R.id.decline_button);
        decline_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User cancelled the dialog...negating consent

                // Close this dialog
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void onBackPressed() {
        // Send notice for this event
        AppNoticeData.sendNotice(AppNoticeData.NoticeType.EXPLICIT_INFO_DECLINE);

        // Let the calling class know the selected option
        if (appNotice_callback != null && !getActivity().isFinishing()) {
            appNoticeData.setTrackerOnOffState(false);   // Set all non-essential tracker to off
            appNoticeData.saveTrackerStates();  // And remember the states
            appNotice_callback.onOptionSelected(false, appNoticeData.getTrackerHashMap(true));
        }

    }

}
