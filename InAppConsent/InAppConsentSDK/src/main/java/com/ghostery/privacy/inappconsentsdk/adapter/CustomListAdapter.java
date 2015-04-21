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
import com.ghostery.privacy.inappconsentsdk.model.Company;
import com.ghostery.privacy.inappconsentsdk.model.Optout;
import com.ghostery.privacy.inappconsentsdk.utils.ImageDownloader;

import java.util.List;
import java.util.Map;


public class CustomListAdapter extends BaseAdapter {

    private List<Company.CompanyData> mCompany;
    private static LayoutInflater mInflater = null;
    private static final String TAG = "AppChoices";
    public FragmentActivity context;

    public static class ViewHolder {
        public TextView name;
        public ImageView logo;
        public CompoundButton btn;
        public TextView gts;
    }

    public CustomListAdapter(FragmentActivity ctx, List<Company.CompanyData> company) {
        this.mCompany = company;
        this.mInflater = ctx.getLayoutInflater();
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return mCompany.size();
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
            holder.name = (TextView) convertView.findViewById(R.id.company_name);
            holder.logo = (ImageView) convertView.findViewById(R.id.company_logo);
            holder.btn = (CompoundButton) convertView.findViewById(R.id.opt_out_button);
            Company.CompanyData cmp = mCompany.get(position);
            holder.btn.setTag(cmp.id);
            Log.v(TAG, "name: " + cmp.name +" id:" + cmp.id);
            holder.gts = (TextView) convertView.findViewById(R.id.go_to_site);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Opt-out button
        if (mCompany.get(position).goToSite) {
            holder.btn.setVisibility(View.GONE);
            holder.gts.setVisibility(View.VISIBLE);
            holder.gts.setTag(mCompany.get(position));
        } else {
            holder.btn.setVisibility(View.VISIBLE);
            holder.gts.setVisibility(View.GONE);
            holder.btn.setTag(mCompany.get(position));
            holder.btn.setChecked(!Optout.OPTOUT_MAP.get(mCompany.get(position).id.toString()).optoutStatus);
            holder.btn.setEnabled(!Optout.OPTOUT_MAP.get(mCompany.get(position).id.toString()).optoutStatus);
        }



        // Company Logo
        ImageDownloader img = new ImageDownloader();
        img.download(mCompany.get(position).logo, holder.logo);

        // Company Name (if logo not available/displayed)
        if (holder.logo.getDrawable().getIntrinsicHeight() <= 0) {
            holder.name.setText(mCompany.get(position).name);
            holder.name.setVisibility(View.VISIBLE);
            holder.logo.setVisibility(View.GONE);
        } else {
            holder.name.setVisibility(View.GONE);
            holder.logo.setVisibility(View.VISIBLE);
        }


        Button disable_all = (Button)this.context.findViewById(R.id.choose_all_companies);
        if(checkifLastoptout())
        {
            disable_all.setEnabled(false);
            disable_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_gray);
        }
        else
        {
            disable_all.setEnabled(true);
            disable_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_blue);
        }

        // this was to process saved optouts, not used anymore
        /*
        boolean network = Network.isNetworkAvailable(this.context);
        OptOutManager oom = new OptOutManager(this.context);

        if(network)
        {

            if(Optout.OPTOUT_MAP.get(mCompany.get(position).id.toString()).storeOptout || Optout.OPTOUT_MAP.get(mCompany.get(position).id.toString()).storeOptoutAll)
            {
                if(!Optout.OPTOUT_MAP.get(mCompany.get(position).id.toString()).optoutStatus)
                {
                    oom.processOptOutInBackground(mCompany.get(position), "", "", (CompoundButton) holder.btn, false);
                }

            }
        }*/

        return convertView;
    }

    private boolean checkifLastoptout ()
    {
        boolean isLastOptOut = false;
        for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

            //Log.v(TAG, " ======== optout status for "+ company.name + "= "+optout.optoutStatus);
            isLastOptOut = optout.optoutStatus;
            if(!optout.optoutStatus && !company.goToSite)
            {
                break;
            }

        }
        return isLastOptOut;
    }
}
