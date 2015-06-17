package com.ghostery.privacy.inappconsentsdk.app;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ghostery.privacy.inappconsentsdk.Identifiers.AdvertisingId;
import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.model.Company;
import com.ghostery.privacy.inappconsentsdk.model.Optout;
import com.ghostery.privacy.inappconsentsdk.model.OwnerCompany;
import com.ghostery.privacy.inappconsentsdk.utils.AppData;
import com.ghostery.privacy.inappconsentsdk.utils.CopyToDevice;
import com.ghostery.privacy.inappconsentsdk.utils.FileReader;
import com.ghostery.privacy.inappconsentsdk.utils.Network;
import com.ghostery.privacy.inappconsentsdk.utils.XMLParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.Map;


/**
 * Splash screen seen on launch of application This activity downloads the XML data from web
 */
public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "AppChoices";

    public String noticeId;
    public String appids;
    public String ocid;
    public boolean network;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title/action bar to make activity full screen
        //getWindow().requestFeature((int) Window.FEATURE_ACTION_BAR);
        //getSupportActionBar().hide();

        setContentView(R.layout.ghostery_activity_splash_screen);
    }

    @Override
    public void onResume() {
        super.onResume();
        network = Network.isNetworkAvailable(getBaseContext());

        // Get the notice and appids if passed in from creative
        this.noticeId = "";
        this.appids = "";

        final Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri incomingUri = intent.getData();
            this.noticeId = incomingUri.getHost();
            if (incomingUri.getPathSegments().size() > 0) {
                this.appids = incomingUri.getPathSegments().get(0);
            }
            Log.v(TAG, "NOTICE ID SEND: " + noticeId);
            Log.v(TAG, "APPIDS SEND: " + appids);
            Log.v(TAG, "OCID SEND: " + ocid);
        }
        this.ocid = ocid();

        DownloadDataTask task = new DownloadDataTask();
        task.execute();

        checkGooglePlayServicesSupport();
    }

    // Check if the devices supports Google Play Services for the Advertising ID
    private void checkGooglePlayServicesSupport() {
        AdvertisingId adId = new AdvertisingId();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {

            adId.getIdThread(this);

        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 69);
            // Don't show dialog on Kindle or Android 2.2
            if (dialog != null && !Build.MANUFACTURER.equalsIgnoreCase("Amazon") && Build.VERSION.SDK_INT > 8) {
                dialog.show();
            }
        }
    }

    /*
     * Downloads files and stores them to the device
     */
    private class DownloadDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // Copy pre-packaged assets files to device
            new CopyToDevice(getBaseContext());

            // Download and parse mobile_opt_out_providers.xml file
            XMLParser parser = new XMLParser();
            parser.parse(getBaseContext());

            // Update legal files from web
            // Temp disable this functionality
//			if (network) {
//				String privacy = FileDownloader.getDataFromUrl(getString(R.string.url_privacy_statement));
//				FileWriter.writeFile(getBaseContext(), getString(R.string.file_privacy_statement), privacy);
//
//				String terms = FileDownloader.getDataFromUrl(getString(R.string.url_terms_of_use));
//				FileWriter.writeFile(getBaseContext(), getString(R.string.file_terms_of_use), terms);
//
//				String disclaimer = FileDownloader.getDataFromUrl(getString(R.string.url_disclaimer_disclosure));
//				FileWriter.writeFile(getBaseContext(), getString(R.string.file_disclaimer_disclosure), disclaimer);
//			}

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Network.isNetworkAvailable(getBaseContext())) {
                //processPendingRequest();
            }

            restoreOptoutData();
            // Use shared prefs to pass data to ListActivity
            AppData.setString(AppData.APPDATA_NOTICE_ID, noticeId);
            AppData.setString(AppData.APPDATA_APPIDS, appids);
            AppData.setString(AppData.APPDATA_OCID, ocid);

            // Start ListActivity
            Intent intent = new Intent(getBaseContext(), ListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    /*
     * Restores the state of each company and opt out status
     */
    private void restoreOptoutData() {
        Map<String, Optout.OptoutData> restore = (Map<String, Optout.OptoutData>) FileReader.readObject(getBaseContext(), getString(R.string.ghostery_file_optout_status));

        for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

            if (null != restore && !restore.isEmpty() && company.id.toString().equalsIgnoreCase(optout.companyId.toString())) {
                if (null != restore.get(optout.companyId.toString())) {
                    optout.storeOptout = restore.get(optout.companyId.toString()).storeOptout;
                    optout.optoutStatus = restore.get(optout.companyId.toString()).optoutStatus;
                }
            }
        }
    }

    private void processPendingRequest() {
        Map<String, Optout.OptoutData> restore = (Map<String, Optout.OptoutData>) FileReader.readObject(getBaseContext(), getString(R.string.ghostery_file_optout_status));

        for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(entry.getKey());

            if(optout.storeOptoutAll)
            {
                Log.v(TAG,"OptOutAll detected - do not handle separately");
                break;
            }
            else if (optout.storeOptout)
            {
                //optout.storeOptout
                Log.v(TAG, "Stored optout for " + company.name);
                OptOutManager oom = new OptOutManager(getBaseContext());
                oom.processPendingOptOutInBackground(company, "", "", false);
            }
        }
    }

    /*
     * Returns the Owner Company Id (ocid) based on the noticeId
     */
    private String ocid() {
        String ocid = "";
        if (this.noticeId != "") {
            for (Map.Entry<String, OwnerCompany.OwnerCompanyData> entry : OwnerCompany.OWNER_COMPANY_MAP.entrySet()) {
                OwnerCompany.OwnerCompanyData owner = OwnerCompany.OWNER_COMPANY_MAP.get(entry.getKey());
                if (owner.adNoticeId.contains(this.noticeId)) {
                    ocid = owner.id.toString();
                }
            }
        }
        return ocid;
    }

}
