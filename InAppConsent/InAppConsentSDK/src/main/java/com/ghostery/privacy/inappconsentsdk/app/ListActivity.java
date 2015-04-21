package com.ghostery.privacy.inappconsentsdk.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ghostery.privacy.inappconsentsdk.Identifiers.AdvertisingId;
import com.ghostery.privacy.inappconsentsdk.Identifiers.DigestUtil;
import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.fragments.DetailFragment;
import com.ghostery.privacy.inappconsentsdk.fragments.ListFragment;
import com.ghostery.privacy.inappconsentsdk.model.Company;
import com.ghostery.privacy.inappconsentsdk.model.Optout;
import com.ghostery.privacy.inappconsentsdk.utils.AppData;
import com.ghostery.privacy.inappconsentsdk.utils.Network;
import com.ghostery.privacy.inappconsentsdk.utils.Notification;
import com.ghostery.privacy.inappconsentsdk.utils.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class ListActivity extends ActionBarActivity implements ListFragment.Callbacks {

    private static final String TAG = "AppChoices";

    public String noticeId;
    public String appids;
    public String ocid;
    public static Notification notification;
    public boolean network;
    public boolean showInfoIcon = false;
    public static boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ghostery_activity_layout);
        notification = new Notification(this);
        // Set the screen orientation
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // hack to get around devices with a menu button not showing an overflow icon
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }

        // Check for app update
        new Version(this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.ghostery_action_bar_layout);

        // Set the contents
        //getSupportFragmentManager().findFragmentById(R.id.item_list);
        Bundle arguments = new Bundle();
        arguments.putBoolean("ARG_APP_DESC", true);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();

//        // change font on elements on footer
//        Typeface arial = Typeface.createFromAsset(ListActivity.super.getAssets(),
//                "fonts/arial.ttf");

//        TextView understand = (TextView) ListActivity.this.findViewById(R.id.understand_these_choices);
//        understand.setTypeface(arial);

//        Button optOut_all = (Button) ListActivity.this.findViewById(R.id.choose_all_companies);
//        optOut_all.setTypeface(arial);

//        // change font on elements on header
//        Typeface arial_bold = Typeface.createFromAsset(ListActivity.super.getAssets(),
//                "fonts/arial_bold.ttf");

//        TextView header_company = (TextView) ListActivity.this.findViewById(R.id.header_company);
//        header_company.setTypeface(arial_bold);
//
//        TextView header_ad_personalization = (TextView) ListActivity.this.findViewById(R.id.header_ad_personalization);
//        header_ad_personalization.setTypeface(arial_bold);

    }

    @Override
    public void onResume() {
        super.onResume();
        network = Network.isNetworkAvailable(this);

        // Get IDs passed from creative
        this.noticeId = "";
        this.appids = "";
        this.ocid = "";
        Intent intent = getIntent();

        Log.v(TAG, "--------------------intent-------------------->"+intent.getAction());

        if (intent != null) {
            this.noticeId = AppData.getString(AppData.APPDATA_NOTICE_ID);
            this.appids = AppData.getString(AppData.APPDATA_APPIDS);
            this.ocid = AppData.getString(AppData.APPDATA_OCID);
            // reset the shared prefs data
            AppData.setString(AppData.APPDATA_NOTICE_ID, "");
            AppData.setString(AppData.APPDATA_APPIDS, "");
            AppData.setString(AppData.APPDATA_OCID, "");
        }

        Log.v(TAG, "NOTICE ID RETRIEVED: " + this.noticeId);
        Log.v(TAG, "APPIDS RETRIEVED: " + this.appids);
        Log.v(TAG, "OCID RETRIEVED: " + this.ocid);

        // Log app install and opens
        firstRun = AppData.getBoolean(AppData.APPDATA_FIRSTRUN, true);
        if (firstRun && network) {
            Log.v(TAG, "Logging App Install for first launch: " + firstRun);
            (new EventLogger()).logAppInstall(this.noticeId);
            AppData.setBoolean(AppData.APPDATA_FIRSTRUN, false);
        }
        (new EventLogger()).logAppOpen(this.noticeId);

           /*
        boolean network = Network.isNetworkAvailable(ListActivity.this);
        OptOutManager oom = new OptOutManager(ListActivity.this);
        View listview = ListActivity.this.findViewById(R.id.opt_out_button);

        if(network)
        {
            for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
                Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
                Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

                if(optout.storeOptoutAll)
                {
                    Log.v(TAG, "OptOutAll request detected!");
                    optOutAll(null);
                    break;
                }

                Log.v(TAG, "company="+company.name+" optout="+optout.storeOptout);
                if(optout.storeOptout)
                {
                    CompoundButton toggle = (CompoundButton) listview.findViewWithTag(company.id);
                    oom.processOptOutInBackground(company, "", "", (CompoundButton) toggle, true);
                }


            }

        }
        */

        //check if there are new companies added to optout
        Button optOut_all = (Button) ListActivity.this.findViewById(R.id.choose_all_companies);
        optOut_all.setEnabled(false);
        optOut_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_gray);
        for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

            if(!optout.optoutStatus && !company.goToSite) {
                optOut_all.setEnabled(true);
                optOut_all.setBackgroundResource(R.drawable.ghostery_rounded_corner_blue);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar.
        getMenuInflater().inflate(R.menu.ghostery_legal_docs, menu);
        getMenuInflater().inflate(R.menu.ghostery_app_info, menu);

        MenuItem infoIcon = menu.findItem(R.id.info_icon);
        infoIcon.setVisible(showInfoIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle arguments = new Bundle();

        int i = item.getItemId();
        if (i == R.id.info_icon) {
            arguments.putBoolean("ARG_APP_DESC", true);
            showInfoIcon = false;

        } else if (i == R.id.menu_privacy) {
            arguments.putString("ARG_LEGAL_FILE", getString(R.string.ghostery_file_privacy_statement));
            showInfoIcon = true;

        } else if (i == R.id.menu_terms) {
            arguments.putString("ARG_LEGAL_FILE", getString(R.string.ghostery_file_terms_of_use));
            showInfoIcon = true;

        }

        showInfoIconInActionBar();

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();

        return true;
    }

    /*
     * Callback method from ListFragment.Callbacks indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        // update action bar menu info icon
        showInfoIcon = true;
        showInfoIconInActionBar();

        Bundle arguments = new Bundle();
        arguments.putString(DetailFragment.ARG_COMPANY_ID, id);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment).commit();
    }

    /*
     * Will show an "i" icon in action bar to display the application description. Calls onCreateOptionsMenu() to reset
     * the action bar menu items
     */
    private void showInfoIconInActionBar() {
        this.supportInvalidateOptionsMenu();
    }

    /*
     * Called when I user presses the toggle button
     */
    public void onOptoutClick(View view) {
        Company.CompanyData mCompany = (Company.CompanyData) view.getTag();
        Optout.OptoutData optout = Optout.OPTOUT_MAP.get(mCompany.id.toString());

        // this is to have a pointer to the optout all button in case this is the last
        // company available, we disable the button if all companies were opted out separately
        OptOutManager oom = new OptOutManager(this);
        oom.optOutAllButton = (Button)ListActivity.this.findViewById(R.id.choose_all_companies);



        if (!network && !mCompany.goToSite) {
            Log.v(TAG, "Store optout for " + mCompany.name);
            optout.storeOptout = true;
            Notification notification = new Notification(ListActivity.this);
            notification.noNetworkConnection();
        }
        else
        {
            if (view instanceof CompoundButton) {
                if (!((CompoundButton) view).isChecked()) {
                    oom.processOptOutInBackground(mCompany, this.noticeId, this.ocid, (CompoundButton) view, false);
                    view.setEnabled(false); // disable toggle button
                }
            } else if (view instanceof TextView) {
                oom.processOptOutInBackground(mCompany, this.noticeId, this.ocid, (TextView) view, false);
            }
        }
    }

    /*
     * For the choose all companies button
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void optOutAll(View view){

        android.support.v4.app.FragmentManager manager = ListActivity.this.getSupportFragmentManager();
        ListFragment listview = (ListFragment)manager.findFragmentById(R.id.item_list);

        listview.optOutAll();
    }

    // Check if Advertising ID has changed since last opt out
    public static Boolean didAdvertisingIdChange() {
        String stored = AppData.getString(AppData.APPDATA_ADVERTISING_ID_HASH);
        String current = "";
        try {
            if (AdvertisingId.id.size() > 0) {
                current = DigestUtil.md5Hash(AdvertisingId.id.get(0).getValue());
            }
        } catch (NoSuchAlgorithmException e) {
        }

        Log.i(TAG, "Stored Id: " + stored);
        Log.i(TAG, "Current Id: " + current);
        if (!current.equals(stored) && !firstRun) {
            resetOptoutData();
            // Update the stored advertising id hash
            AppData.setString(AppData.APPDATA_ADVERTISING_ID_HASH, current);
            Log.i(TAG, "Storing the Advertising Id: " + current);
            ListFragment.adapter.notifyDataSetChanged();
            notification.advertisingIdHasChanged();

            return true;
        }
        else
        {
            return false;
        }
    }

    // Reset all opt out status
    private static void resetOptoutData() {
        Log.v(TAG, "Resetting opt out data");
        for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

            if (company.id.toString().equalsIgnoreCase(optout.companyId.toString())) {
                optout.storeOptout = false;
                optout.optoutStatus = false;
                optout.storeOptoutAll = false;
            }
        }
    }

    // Reset all opt out status
    public static void updateListWithTrusteResult(String result) {

        try{
            JSONObject root = new JSONObject(result);
            JSONArray jsonArray = root.getJSONArray("optouts");
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    JSONObject cmp = new JSONObject(jsonArray.get(i).toString());
                    String cmp_id = cmp.getString("daaId");

                    for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
                        Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
                        Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

                        if (company.id.toString().equalsIgnoreCase(cmp_id)) {
                            optout.storeOptout = false;
                            optout.optoutStatus = true;
                        }
                    }
                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        ListFragment.adapter.notifyDataSetChanged();
        /*

        */
    }
}
