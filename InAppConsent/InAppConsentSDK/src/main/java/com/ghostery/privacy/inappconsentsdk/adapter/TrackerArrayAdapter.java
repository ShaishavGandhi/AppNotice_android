package com.ghostery.privacy.inappconsentsdk.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.callbacks.LogoDownloadCallback;
import com.ghostery.privacy.inappconsentsdk.model.Tracker;
import com.ghostery.privacy.inappconsentsdk.utils.ImageDownloader;

import java.util.ArrayList;


public class TrackerArrayAdapter extends ArrayAdapter {

    private ArrayList<Tracker> trackerArrayList;
    private static LayoutInflater mInflater = null;
    private static final String TAG = "SDK_CustomListAdapter";
    public ListFragment listFragment;

    public static class ViewHolder {
        public TextView trackerName;
        public ImageView trackerLogo;
        public Switch optInOutSwitch;
        public Boolean isOn;
    }

    public TrackerArrayAdapter(ListFragment listFragment, int resource, ArrayList<Tracker> trackerArrayList) {
        super(listFragment.getActivity(), resource, trackerArrayList);

        this.trackerArrayList = trackerArrayList;
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
            holder.trackerLogo = (ImageView) itemView.findViewById(R.id.tracker_logo);
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
            holder.optInOutSwitch.setChecked(tracker.isOn());
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

        // Company Logo
        ImageDownloader imageDownloader = new ImageDownloader(listFragment.getActivity(), position, new LogoDownloadCallback() {

            @Override
            public void onDownloaded(int position) {
                // Now that the image is downloaded, find its list item and refresh it
                ListView listView = listFragment.getListView();
                int firstPosition = listView.getFirstVisiblePosition();
                int lastPosition = listView.getLastVisiblePosition();
                if (position >= firstPosition && position <= lastPosition) {
                    View view = listView.getChildAt(position - firstPosition);
                    listView.getAdapter().getView(position, view, listView);
                }
            }
        });

        imageDownloader.download(trackerArrayList.get(position).getLogo_url(), holder.trackerLogo);

        // Company Name (if logo not available/displayed)
        Drawable trackerLogo = holder.trackerLogo.getDrawable();
        if (trackerLogo == null || trackerLogo.getIntrinsicHeight() <= 0) {
            holder.trackerName.setText(trackerArrayList.get(position).getName());
            holder.trackerName.setVisibility(View.VISIBLE);
            holder.trackerLogo.setVisibility(View.GONE);
        } else {
            holder.trackerName.setVisibility(View.GONE);
            holder.trackerLogo.setVisibility(View.VISIBLE);
        }

        return itemView;
    }
}
