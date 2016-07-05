package com.ghostery.privacy.appnoticesdk.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Switch;

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

    public static class ViewHolder {
        public AppCompatTextView trackerName;
        public Switch optInOutSwitch;
        public Boolean isOn;
    }

    public TrackerArrayAdapter(ManagePreferences_TrackerList_Fragment managePreferences_trackerList_fragment, int resource, AppNoticeData appNoticeData) {
        super();//(managePreferences_trackerList_fragment.getActivity(), resource, appNoticeData.trackerArrayList);

        this.appNoticeData = appNoticeData;
        if (appNoticeData != null && appNoticeData.trackerArrayList != null) {
            this.trackerArrayList = appNoticeData.trackerArrayList;
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
            holder.optInOutSwitch = (Switch) itemView.findViewById(R.id.opt_in_out_switch);

            Log.d(TAG,  "Cat: " + tracker.getCategory() + " Name: " + tracker.getName() +" ID:" + tracker.getTrackerId());

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();

        }

        itemView.setId(tracker.uId);

        if (tracker.isEssential()) {
            holder.optInOutSwitch.setVisibility(View.INVISIBLE);
        } else {
            holder.optInOutSwitch.setVisibility(View.VISIBLE);
            holder.optInOutSwitch.setTag(tracker.uId);

            // If this tracker is a duplicate of an essential tracker, disable it
            if (appNoticeData.isTrackerDuplicateOfEssentialTracker(tracker.getTrackerId())){
                holder.optInOutSwitch.setChecked(true);     // Make sure it is checked
                holder.optInOutSwitch.setEnabled(false);    // Disable the switch
            } else {
                holder.optInOutSwitch.setChecked(tracker.isOn());
                holder.optInOutSwitch.setEnabled(true);     // Enable the switch
            }
        }

        // Make the header visible and set its text if needed
        LinearLayout categoryHeader_linearLayout = (LinearLayout)itemView.findViewById(R.id.category_header_layout);
        AppCompatTextView categoryHeader_textView = (AppCompatTextView)itemView.findViewById(R.id.category_header_text);
        if (categoryHeader_linearLayout != null && categoryHeader_textView != null) {
            if (tracker.hasHeader()) {
                categoryHeader_linearLayout.setVisibility(View.VISIBLE);
                categoryHeader_textView.setText(tracker.getCategory());
            } else {
                categoryHeader_linearLayout.setVisibility(View.GONE);
            }
        }

        holder.trackerName.setText(trackerArrayList.get(position).getName());

        return itemView;
    }
}
