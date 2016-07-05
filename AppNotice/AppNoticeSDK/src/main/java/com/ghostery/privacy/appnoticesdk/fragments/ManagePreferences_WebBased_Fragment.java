package com.ghostery.privacy.appnoticesdk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ghostery.privacy.appnoticesdk.R;

/**
 *
 */
public class ManagePreferences_WebBased_Fragment extends Fragment {
//    private ArrayList<Tracker> trackerArrayList;
//    private ArrayList<Tracker> trackerArrayListClone;
//    private AppNoticeData appNoticeData;
//    private TrackerArrayAdapter trackerArrayAdapter;
//    private ListView trackerListView;

    /**
     * Whether or not the fragmentActivity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Session.set(Session.APPNOTICE_ALL_BTN_SELECT, false);    // "All" not clicked yet
//        Session.set(Session.APPNOTICE_NONE_BTN_SELECT, false);   // "None" not clicked yet
//
//        appNoticeData = (AppNoticeData)Session.get(Session.APPNOTICE_DATA);
//        if (appNoticeData != null && appNoticeData.isTrackerListInitialized()) {
//            trackerArrayList = appNoticeData.trackerArrayList;
//            trackerArrayListClone = appNoticeData.getTrackerListClone(); // Get a copy of the current tracker list so it can be compared on save
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_manage_preferences_webbased, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

//            // TODO: replace with a real list adapter.
//        trackerArrayAdapter = new TrackerArrayAdapter(this, R.id.tracker_name, appNoticeData);
//        trackerListView = (ListView) view.findViewById(R.id.tracker_list);
//        trackerListView.setAdapter(trackerArrayAdapter);
//        trackerListView.setOnItemClickListener((AppNotice_Activity)getActivity());
//        trackerListView.setItemsCanFocus(false);
//        trackerListView.setTextFilterEnabled(true);
//
//        AppCompatTextView manage_preferences_description = (AppCompatTextView)view.findViewById(R.id.manage_preferences_description);
//        if (manage_preferences_description != null) {
//            final AppNoticeData appNoticeData = AppNoticeData.getInstance(getActivity());
//            manage_preferences_description.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (appNoticeData != null) {
//                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
//                        alertDialog.setTitle(R.string.ghostery_preferences_header);
//                        alertDialog.setMessage(getActivity().getResources().getString(R.string.ghostery_preferences_description));
//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getActivity().getResources().getString(R.string.ghostery_dialog_button_close),
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        alertDialog.show();
//                    }
//                }
//            });
//
//            final AppCompatRadioButton rbAll = (AppCompatRadioButton)view.findViewById(R.id.rb_all);
//            final AppCompatRadioButton rbNone = (AppCompatRadioButton)view.findViewById(R.id.rb_none);
//
//            rbAll.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (appNoticeData != null) {
//                        appNoticeData.setTrackerOnOffState(true);
//                    }
//                    rbAll.setChecked(true);
//                    rbNone.setChecked(false);
//                    Session.set(Session.APPNOTICE_ALL_BTN_SELECT, true);    // If they selected "All", remember it.
//                    Session.set(Session.APPNOTICE_NONE_BTN_SELECT, false);  // If they selected "None", remember that "None" wasn't the last set state.
//
//                    refreshTrackerList();
//                }
//            });
//
//            rbNone.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (appNoticeData != null) {
//                        appNoticeData.setTrackerOnOffState(false);
//                    }
//                    rbAll.setChecked(false);
//                    rbNone.setChecked(true);
//                    Session.set(Session.APPNOTICE_NONE_BTN_SELECT, true);   // If they selected "None", remember it.
//                    Session.set(Session.APPNOTICE_ALL_BTN_SELECT, false);   // If they selected "None", remember that "All" wasn't the last set state.
//
//                    refreshTrackerList();
//                }
//            });
//        }

        return view;
    }

//    protected void refreshTrackerList() {
//
//        if (trackerArrayAdapter != null) {
//            trackerArrayAdapter.notifyDataSetChanged();
//        }
//
//        if (trackerListView == null) {
//            trackerListView = (ListView) getActivity().findViewById(R.id.tracker_list);
//        }
//
//        if (trackerListView != null) {
//            trackerListView.invalidate();
//        }
//    }

    @Override
    public void onResume()
    {
        super.onResume();
//        refreshTrackerList();
//        setAllNoneControlState();

        getActivity().setTitle(R.string.ghostery_preferences_header);
    }

    public void onBackPressed() {
//        saveTrackerStates();
//        sendOptInOutNotices();    // Send opt-in/out ping-back
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        Bundle arguments = new Bundle();
//
//        int i = item.getItemId();
//        if (item.getItemId() == android.R.id.home) {
//            saveTrackerStates();
//            sendOptInOutNotices();    // Send opt-in/out ping-back
//
//            onBackPressed();
//        }
//
//        return true;
//    }

//    private void sendOptInOutNotices() {
//        // Opt-in/out ping-back parameters
//        Boolean allBtnSelected = (boolean)Session.get(Session.APPNOTICE_ALL_BTN_SELECT, false);
//        Boolean noneBtnSelected = (boolean)Session.get(Session.APPNOTICE_NONE_BTN_SELECT, false);
//        int pingBackCount = 0;      // Count the ping-backs
//
//        // Send opt-in/out ping-back for each changed non-essential tracker
//        if (trackerArrayList != null && trackerArrayListClone != null &&
//                trackerArrayList.size() == trackerArrayListClone.size()) {
//
//            for (int i = 0; i < trackerArrayList.size(); i++) {
//                Tracker tracker = trackerArrayList.get(i);
//                Tracker trackerClone = trackerArrayListClone.get(i);
//
//                // If the tracker is non-essential and is changed...
//                if (!tracker.isEssential() && (tracker.isOn() != trackerClone.isOn())) {
//                    Boolean optOut = tracker.isOn() == false;
//                    Boolean uniqueVisit = ((allBtnSelected == false && noneBtnSelected == false) || pingBackCount == 0);
//                    Boolean firstOptOut = pingBackCount == 0;
//                    Boolean selectAll = ((allBtnSelected == true || noneBtnSelected == true) && pingBackCount == 0);
//
//                    AppNoticeData.sendOptInOutNotice(tracker.getTrackerId(), optOut, uniqueVisit, firstOptOut, selectAll);    // Send opt-in/out ping-back
//                    pingBackCount++;
//                }
//            }
//        }
//    }

//    public void saveTrackerStates() {
//        if (appNoticeData != null) {
//            appNoticeData.saveTrackerStates();
//
//            // If trackers have been changed and a consent dialog is not showing, send an updated tracker state hashmap to the calling app
//            int trackerStateChangeCount = appNoticeData.getTrackerStateChangeCount(trackerArrayListClone);
//            if (trackerStateChangeCount > 0 && !(boolean)Session.get(Session.APPNOTICE_PREF_OPENED_FROM_CONSENT, false)) {
//                AppNotice_Callback appNotice_callback = (AppNotice_Callback)Session.get(Session.APPNOTICE_CALLBACK);
//                appNotice_callback.onTrackerStateChanged(appNoticeData.getTrackerHashMap(true));
//            }
//        }
//    }

//    public void setAllNoneControlState() {
//        if (appNoticeData != null) {
//            int nonEssentialTrackerCount = appNoticeData.getNonEssentialTrackerCount();
//            AppCompatRadioButton rbAll = (AppCompatRadioButton)getActivity().findViewById(R.id.rb_all);
//            AppCompatRadioButton rbNone = (AppCompatRadioButton)getActivity().findViewById(R.id.rb_none);
//
//            if (nonEssentialTrackerCount > 0) {
//                int trackerOnOffStates = appNoticeData.getTrackerOnOffStates();
//                if (trackerOnOffStates == 1) {              // All on
//                    rbAll.setChecked(true);
//                    rbNone.setChecked(false);
//                } else if (trackerOnOffStates == -1) {      // None on
//                    rbAll.setChecked(false);
//                    rbNone.setChecked(true);
//                } else {                                    // Some on, some off
//                    rbAll.setChecked(false);
//                    rbNone.setChecked(false);
//                }
//            } else {
//                // Set both to unchecked and disabled
//                rbAll.setChecked(false);
//                rbNone.setChecked(false);
//                rbAll.setEnabled(false);
//                rbNone.setEnabled(false);
//            }
//        }
//    }

}
