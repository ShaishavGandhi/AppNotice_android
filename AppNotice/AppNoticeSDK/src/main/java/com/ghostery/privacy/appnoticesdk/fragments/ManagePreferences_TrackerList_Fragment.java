package com.ghostery.privacy.appnoticesdk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ghostery.privacy.appnoticesdk.AppNotice_Activity;
import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.adapter.TrackerArrayAdapter;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;

import java.util.ArrayList;

/**
 *
 */
public class ManagePreferences_TrackerList_Fragment extends Fragment {
    private ArrayList<Tracker> trackerArrayList;
    private TrackerArrayAdapter trackerArrayAdapter;
    private ListView trackerListView;
    private boolean isEssential = false;

    /**
     * Whether or not the fragmentActivity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isEssential = getArguments().getBoolean("isEssential", false);

        AppNotice_Activity.appNoticeData = AppNoticeData.getInstance(getActivity());
        if (AppNotice_Activity.appNoticeData != null && AppNotice_Activity.appNoticeData.isTrackerListInitialized()) {
            if (isEssential) {
                trackerArrayList = AppNotice_Activity.appNoticeData.essentialTrackerArrayList;
            } else {
                trackerArrayList = AppNotice_Activity.appNoticeData.optionalTrackerArrayList;
                AppNotice_Activity.optionalTrackerArrayListClone = AppNotice_Activity.appNoticeData.getOptionalTrackerListClone(); // Get a copy of the current tracker list so it can be compared on save
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ghostery_fragment_manage_preferences_trackerlist, container, false);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.show();
        }

        AppCompatTextView managePreferencesDescription = (AppCompatTextView) view.findViewById(R.id.manage_preferences_description);
        if (managePreferencesDescription != null) {
            if (isEssential) {
                managePreferencesDescription.setText(R.string.ghostery_preferences_essential_message);
            } else {
                managePreferencesDescription.setText(R.string.ghostery_preferences_optional_message);
            }
        }

        trackerArrayAdapter = new TrackerArrayAdapter(this, R.id.tracker_name, AppNotice_Activity.appNoticeData, isEssential);
        trackerListView = (ListView) view.findViewById(R.id.tracker_list);
        trackerListView.setAdapter(trackerArrayAdapter);
        trackerListView.setOnItemClickListener((AppNotice_Activity)getActivity());
        trackerListView.setItemsCanFocus(false);
        trackerListView.setTextFilterEnabled(true);

        return view;
    }

    protected void refreshTrackerList() {

        if (trackerArrayAdapter != null) {
            trackerArrayAdapter.notifyDataSetChanged();
        }

        if (trackerListView == null) {
            trackerListView = (ListView) getActivity().findViewById(R.id.tracker_list);
        }

        if (trackerListView != null) {
            trackerListView.invalidate();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        refreshTrackerList();

        getActivity().setTitle(R.string.ghostery_preferences_header);
    }

    public void onBackPressed() {
        saveTrackerStates();
        sendOptInOutNotices();    // Send opt-in/out ping-back
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle arguments = new Bundle();

        int i = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            saveTrackerStates();
            sendOptInOutNotices();    // Send opt-in/out ping-back

            onBackPressed();
        }

        return true;
    }

    public void sendOptInOutNotices() {
        // Opt-in/out ping-back parameters
        int pingBackCount = 0;      // Count the ping-backs

        // Send opt-in/out ping-back for each changed non-essential tracker
        if (trackerArrayList != null && AppNotice_Activity.optionalTrackerArrayListClone != null &&
                trackerArrayList.size() == AppNotice_Activity.optionalTrackerArrayListClone.size()) {

            for (int i = 0; i < trackerArrayList.size(); i++) {
                Tracker tracker = trackerArrayList.get(i);
                Tracker trackerClone = AppNotice_Activity.optionalTrackerArrayListClone.get(i);

                // If the tracker is non-essential and is changed...
                if (!tracker.isEssential() && (tracker.isOn() != trackerClone.isOn())) {
                    Boolean optOut = tracker.isOn() == false;
                    Boolean uniqueVisit = false;//((allBtnSelected == false && noneBtnSelected == false) || pingBackCount == 0);
                    Boolean firstOptOut = pingBackCount == 0;
                    Boolean selectAll = false;//((allBtnSelected == true || noneBtnSelected == true) && pingBackCount == 0);

                    // TODO: Get correct values for uniqueVisit and selectAll
                    AppNoticeData.sendOptInOutNotice(tracker.getTrackerId(), optOut, uniqueVisit, firstOptOut, selectAll);    // Send opt-in/out ping-back
                    pingBackCount++;
                }
            }
        }
    }

    public void saveTrackerStates() {
        if (AppNotice_Activity.appNoticeData != null) {
            AppNotice_Activity.appNoticeData.saveTrackerStates();

            // If trackers have been changed and a consent dialog is not showing, send an updated tracker state hashmap to the calling app
            int trackerStateChangeCount = AppNotice_Activity.appNoticeData.getTrackerStateChangeCount(AppNotice_Activity.optionalTrackerArrayListClone);
            if (trackerStateChangeCount > 0 && !AppNotice_Activity.isConsentActive) {
                AppNotice_Activity.appNotice_callback.onTrackerStateChanged(AppNotice_Activity.appNoticeData.getTrackerHashMap(true));
            }
        }
    }

}
