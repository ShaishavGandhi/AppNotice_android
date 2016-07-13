package com.ghostery.privacy.appnoticesdk.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.fragments.ManagePreferences_TrackerList_Fragment;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;

import java.util.ArrayList;


public class TrackerArrayAdapter extends BaseAdapter {

    private AppNoticeData appNoticeData;
    private ArrayList<Tracker> trackerArrayList;
    private static LayoutInflater mInflater = null;
    private static final String TAG = "SDK_CustomListAdapter";
    public ManagePreferences_TrackerList_Fragment managePreferences_trackerList_fragment;
    private boolean isEssential = false;

    public static class ViewHolder {
        public AppCompatTextView trackerName;
        public AppCompatTextView trackerCategory;
        public CheckBox optInOutCheckBox;
        public Boolean isOn;
    }

    public TrackerArrayAdapter(ManagePreferences_TrackerList_Fragment managePreferences_trackerList_fragment, int resource, AppNoticeData appNoticeData, boolean isEssential) {
        super();//(managePreferences_trackerList_fragment.getActivity(), resource, appNoticeData.optionalTrackerArrayList);
        this.isEssential = isEssential;

        this.appNoticeData = appNoticeData;
        if (appNoticeData != null && appNoticeData.optionalTrackerArrayList != null) {
            if (isEssential) {
                trackerArrayList = appNoticeData.essentialTrackerArrayList;
            } else {
                trackerArrayList = appNoticeData.optionalTrackerArrayList;
            }
        } else {
            Log.d(TAG, "");
        }
        this.managePreferences_trackerList_fragment = managePreferences_trackerList_fragment;
        this.mInflater = managePreferences_trackerList_fragment.getActivity().getLayoutInflater();
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        int count = 0;
        if (trackerArrayList != null) {
            count = trackerArrayList.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View itemView, ViewGroup parent) {
        ViewHolder holder;
        Tracker tracker = trackerArrayList.get(position);

        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.ghostery_tracker_list_item, parent, false);

            holder = new ViewHolder();
            holder.trackerName = (AppCompatTextView) itemView.findViewById(R.id.tracker_name);
            holder.trackerCategory = (AppCompatTextView) itemView.findViewById(R.id.tracker_category);
            holder.optInOutCheckBox = (CheckBox) itemView.findViewById(R.id.opt_in_out_checkbox);

            Log.d(TAG,  "Cat: " + tracker.getCategory() + " Name: " + tracker.getName() +" ID:" + tracker.getTrackerId());

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();

        }

        itemView.setId(tracker.uId);

        if (tracker.isEssential()) {
            holder.optInOutCheckBox.setChecked(true);     // All essential trackers should be checked
            holder.optInOutCheckBox.setEnabled(false);
        } else {

            // If this tracker is a duplicate of an essential tracker, disable it
            if (appNoticeData.isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())){
                holder.optInOutCheckBox.setChecked(true);     // Make sure it is checked
                holder.optInOutCheckBox.setEnabled(false);    // Disable the switch
            } else {
                holder.optInOutCheckBox.setChecked(tracker.isOn());
                holder.optInOutCheckBox.setEnabled(true);     // Enable the switch
            }
            holder.optInOutCheckBox.setTag(tracker.uId);
        }

        holder.trackerName.setText(trackerArrayList.get(position).getName());
        holder.trackerCategory.setText(trackerArrayList.get(position).getCategory());

        return itemView;
    }
}
