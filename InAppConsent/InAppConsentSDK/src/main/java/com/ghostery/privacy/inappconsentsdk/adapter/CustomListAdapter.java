package com.ghostery.privacy.inappconsentsdk.adapter;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.Tracker;
import com.ghostery.privacy.inappconsentsdk.utils.ImageDownloader;

import java.util.ArrayList;


public class CustomListAdapter extends BaseAdapter {

    private ArrayList<Tracker> trackerArrayList;
    private static LayoutInflater mInflater = null;
    private static final String TAG = "SDK_CustomListAdapter";
    public FragmentActivity context;

    public static class ViewHolder {
        public TextView name;
        public ImageView logo;
        public CompoundButton btn;
        public Boolean isOn;
//        public TextView gts;
    }

    public CustomListAdapter(FragmentActivity ctx, ArrayList<Tracker> trackerArrayList) {
        this.trackerArrayList = trackerArrayList;
        this.mInflater = ctx.getLayoutInflater();
        this.context = ctx;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ghostery_custom_list_adapter, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tracker_name);
            holder.logo = (ImageView) convertView.findViewById(R.id.tracker_logo);
            holder.btn = (CompoundButton) convertView.findViewById(R.id.opt_in_out_button);
            Tracker tracker = trackerArrayList.get(position);
            holder.btn.setTag(tracker.getTrackerId());
            Log.v(TAG, "name: " + tracker.getName() +" id:" + tracker.getTrackerId());
//            holder.isOn = (TextView) convertView.findViewById(R.id.go_to_site);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        // Opt-out button
//        if (trackerArrayList.get(position).goToSite) {
//            holder.btn.setVisibility(View.GONE);
//            holder.gts.setVisibility(View.VISIBLE);
//            holder.gts.setTag(trackerArrayList.get(position));
//        } else {
//            holder.btn.setVisibility(View.VISIBLE);
//            holder.gts.setVisibility(View.GONE);
//            holder.btn.setTag(trackerArrayList.get(position));
//            holder.btn.setChecked(!Optout.OPTOUT_MAP.get(trackerArrayList.get(position).id.toString()).optoutStatus);
//            holder.btn.setEnabled(!Optout.OPTOUT_MAP.get(trackerArrayList.get(position).id.toString()).optoutStatus);
//        }



        // Company Logo
        ImageDownloader img = new ImageDownloader();
        img.download(trackerArrayList.get(position).getLogo_url(), holder.logo);

        // Company Name (if logo not available/displayed)
        if (holder.logo.getDrawable().getIntrinsicHeight() <= 0) {
            holder.name.setText(trackerArrayList.get(position).getName());
            holder.name.setVisibility(View.VISIBLE);
            holder.logo.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.GONE);
            holder.logo.setVisibility(View.VISIBLE);
        }


        Button disable_all = (Button)this.context.findViewById(R.id.choose_all_companies);
//        if(checkifLastoptout())
//        {
//            disable_all.setEnabled(false);
//            disable_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_gray);
//        }
//        else
//        {
//            disable_all.setEnabled(true);
//            disable_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_blue);
//        }

        // this was to process saved optouts, not used anymore
        /*
        boolean network = Network.isNetworkAvailable(this.context);
        OptOutManager oom = new OptOutManager(this.context);

        if(network)
        {

            if(Optout.OPTOUT_MAP.get(trackerArrayList.get(position).id.toString()).storeOptout || Optout.OPTOUT_MAP.get(trackerArrayList.get(position).id.toString()).storeOptoutAll)
            {
                if(!Optout.OPTOUT_MAP.get(trackerArrayList.get(position).id.toString()).optoutStatus)
                {
                    oom.processOptOutInBackground(trackerArrayList.get(position), "", "", (CompoundButton) holder.btn, false);
                }

            }
        }*/

        return convertView;
    }

//    private boolean checkifLastoptout ()
//    {
//        boolean isLastOptOut = false;
//        for (Map.Entry<String, ArrayList<Tracker>> entry : Company.COMPANY_MAP.entrySet()) {
//            ArrayList<Tracker> company = Company.COMPANY_MAP.get(entry.getKey());
//            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());
//
//            //Log.v(TAG, " ======== optout status for "+ company.name + "= "+optout.optoutStatus);
//            isLastOptOut = optout.optoutStatus;
//            if(!optout.optoutStatus && !company.goToSite)
//            {
//                break;
//            }
//
//        }
//        return isLastOptOut;
//    }
}
