package com.ghostery.privacy.appnoticesdk.adapter;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.model.Tracker;

import java.util.ArrayList;


public class TrackerArrayAdapter extends ArrayAdapter {

    private AppNoticeData appNoticeData;
    private ArrayList<Tracker> trackerArrayList;
    private static LayoutInflater mInflater = null;
    private static final String TAG = "SDK_CustomListAdapter";
    public ListFragment listFragment;

    public static class ViewHolder {
        public TextView trackerName;
        public Switch optInOutSwitch;
        public Boolean isOn;
    }

    public TrackerArrayAdapter(ListFragment listFragment, int resource, AppNoticeData appNoticeData) {
        super(listFragment.getActivity(), resource, appNoticeData.trackerArrayList);

        this.appNoticeData = appNoticeData;
        this.trackerArrayList = appNoticeData.trackerArrayList;
        this.mInflater = listFragment.getActivity().getLayoutInflater();
        this.listFragment = listFragment;
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return trackerArrayList.size();
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
            holder.trackerName = (TextView) itemView.findViewById(R.id.tracker_name);
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
        TextView tvCategoryHeader = (TextView)itemView.findViewById(R.id.category_header);
        if (tvCategoryHeader != null) {
            if (tracker.hasHeader()) {
                tvCategoryHeader.setVisibility(View.VISIBLE);
                tvCategoryHeader.setText(tracker.getCategory());
            } else {
                tvCategoryHeader.setVisibility(View.GONE);
            }
        }

        holder.trackerName.setText(trackerArrayList.get(position).getName());

        return itemView;
    }
}
