package com.ghostery.privacy.appnoticesdk.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
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
    private static ArrayList<Tracker> optionalTrackerArrayList;
    private ArrayList<Tracker> essentialtrackerArrayList;
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
                essentialtrackerArrayList = AppNotice_Activity.appNoticeData.essentialTrackerArrayList;
            } else {
                optionalTrackerArrayList = AppNotice_Activity.appNoticeData.optionalTrackerArrayList;
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

        AppCompatTextView preferencesTrackerMessage = (AppCompatTextView) view.findViewById(R.id.preferences_tracker_message);
        if (preferencesTrackerMessage != null) {
            if (isEssential) {
                preferencesTrackerMessage.setText(R.string.ghostery_preferences_essential_message);
            } else {
                preferencesTrackerMessage.setText(R.string.ghostery_preferences_optional_message);
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

}
