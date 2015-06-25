package com.ghostery.privacy.inappconsentsdk.adapter;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.Tracker;
import com.ghostery.privacy.inappconsentsdk.utils.ImageDownloader;

import java.util.ArrayList;


public class TrackerArrayAdapter extends ArrayAdapter {

    private ArrayList<Tracker> trackerArrayList;
    private static LayoutInflater mInflater = null;
    private static final String TAG = "SDK_CustomListAdapter";
    public FragmentActivity context;

    public static class ViewHolder {
        public TextView tracker_name;
        public ImageView logo;
        public CompoundButton btn;
        public Boolean isOn;
    }

    public TrackerArrayAdapter(FragmentActivity ctx, int resource, ArrayList<Tracker> trackerArrayList) {
        super(ctx, resource, trackerArrayList);

        this.trackerArrayList = trackerArrayList;
        this.mInflater = ctx.getLayoutInflater();
        this.context = ctx;
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
            holder.tracker_name = (TextView) itemView.findViewById(R.id.tracker_name);
            holder.logo = (ImageView) itemView.findViewById(R.id.tracker_logo);
            holder.btn = (ToggleButton) itemView.findViewById(R.id.opt_in_out_button);

            if (tracker.isEssential()) {
                holder.btn.setVisibility(View.GONE);
            } else {
                holder.btn.setTag(tracker.getTrackerId());
                holder.btn.setChecked(tracker.isOn());
            }

            // Make the header visible and set its text if needed
            if (tracker.hasHeader()) {
                TextView tvCategoryHeader = (TextView)itemView.findViewById(R.id.category_header);
                if (tvCategoryHeader != null) {
                    tvCategoryHeader.setVisibility(View.VISIBLE);
                    tvCategoryHeader.setText(tracker.getCategory());
                }
            }

            Log.v(TAG, "name: " + tracker.getName() +" id:" + tracker.getTrackerId());
//            holder.isOn = (TextView) convertView.findViewById(R.id.go_to_site);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();

            if (!tracker.isEssential()) {
                holder.btn.setChecked(tracker.isOn());
            }
        }

        // Company Logo
        ImageDownloader imageDownloader = new ImageDownloader();
        imageDownloader.download(trackerArrayList.get(position).getLogo_url(), holder.logo);

        // Company Name (if logo not available/displayed)
        if (holder.logo.getDrawable().getIntrinsicHeight() <= 0) {
            holder.tracker_name.setText(trackerArrayList.get(position).getName());
            holder.tracker_name.setVisibility(View.VISIBLE);
            holder.logo.setVisibility(View.GONE);
        } else {
            holder.tracker_name.setVisibility(View.GONE);
            holder.logo.setVisibility(View.VISIBLE);
        }

        return itemView;
    }
}
