package com.ghostery.privacy.inappconsentsdk.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.ghostery.privacy.inappconsentsdk.Identifiers.AdvertisingId;
import com.ghostery.privacy.inappconsentsdk.Identifiers.AndroidId;
import com.ghostery.privacy.inappconsentsdk.Identifiers.PhoneId;
import com.ghostery.privacy.inappconsentsdk.Identifiers.TypedIdentifier;
import com.ghostery.privacy.inappconsentsdk.Identifiers.WifiMac;
import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.fragments.ListFragment;
import com.ghostery.privacy.inappconsentsdk.model.Company;
import com.ghostery.privacy.inappconsentsdk.model.Optout;
import com.ghostery.privacy.inappconsentsdk.utils.FileWriter;
import com.ghostery.privacy.inappconsentsdk.utils.Notification;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptOutManager extends AsyncTask<URL, Integer, Long> {

    private static final String TAG = "AppChoices";
    private static final String access_token = "0cdc0c3e-2883-4b15-952e-0c27b0c075bb";

    Status mStatus;
    Context mContext;
    Company.CompanyData mCompany;
    View mView;
    String mNoticeId;
    String mOcid;
    Boolean mOptOutAll;
    public int mOptoutTotal;
    public static int mOptoutSucess;
    public static int mOptoutFail;
    public static int mOptoutGTS; //for go to site instances
    public ListFragment lf;
    Button optOutAllButton;

    public enum Status {
        FAIL, SUCCESS, INPROGRESS
    }

    public OptOutManager(Context context) {
        this.mContext = context;
    }

    public void processPendingOptOutInBackground(Company.CompanyData company, String noticeId, String ocid, Boolean optOutall) {
        this.mCompany = company;
        this.mNoticeId = noticeId;
        this.mOcid = ocid;
        this.mOptOutAll = optOutall;

        String manf = Build.MANUFACTURER;
        String model = Build.MODEL;
        String release = Build.VERSION.RELEASE;

        String urlString = null;

        try {
            urlString = this.injectIDs(company.moo);
            URL url = new URL(urlString);

            if(mCompany.id == 1934)
            {

                Log.v("Company", mCompany.name);
                Log.v("Manf",manf);
                Log.v("Model",model);
                Log.v("Release", release);
                (new EventLogger()).logCompanyOptOut(mCompany.id.toString(), mNoticeId, mOcid, manf, model, release, urlString);
            }

            Log.v(TAG, "Opting out of URL: " + urlString);
            this.execute(url);
        } catch (MalformedURLException e) {
            Log.v(TAG, "Malformed URL: " + urlString);
        }
    }

    public void processOptOutInBackground(Company.CompanyData company, String noticeId, String ocid, View view, Boolean optOutall) {
        this.mCompany = company;
        this.mNoticeId = noticeId;
        this.mOcid = ocid;
        this.mView = view;
        this.mOptOutAll = optOutall;

        String manf = Build.MANUFACTURER;
        String model = Build.MODEL;
        String release = Build.VERSION.RELEASE;

        String urlString = null;
        try {
            urlString = this.injectIDs(company.moo);
            URL url = new URL(urlString);

            if(mCompany.id == 1934)
            {

                Log.v("Company", mCompany.name);
                Log.v("Manf",manf);
                Log.v("Model",model);
                Log.v("Release", release);
                (new EventLogger()).logCompanyOptOut(mCompany.id.toString(), mNoticeId, mOcid, manf, model, release, urlString);
            }

            Log.v(TAG, "Opting out of URL: " + urlString);

            Log.v(TAG, "METHOD "+this.mCompany.method);
            if(this.mCompany.method.equals("POST"))
            {

                HashMap<String, String> data = new HashMap<String, String>();

                String postList = this.mCompany.requestBodyPOST;
                Log.v(TAG, "POST STRING = "+postList);

                String[] parts = postList.split("&");

                for(String p: parts)
                {
                    if(p.toLowerCase().contains("="))
                    {
                        String[] pair = p.split("=");
                        String macro = pair[1];

                        if(macro.toLowerCase().contains("%"))
                        {
                            macro = injectIDs(macro);
                        }

                        Log.v(TAG, "Pair: "+pair[0]+"="+macro);

                        data.put(pair[0], macro);
                    }
                }

                OptOutPOSTManager asyncHttpPost = new OptOutPOSTManager(data, this);
                asyncHttpPost.execute(urlString);


            }else {
                this.execute(url);
            }
        } catch (MalformedURLException e) {
            Log.v(TAG, "Malformed URL: " + urlString);
        }
    }

    private void optOutForUrlString(URL url) {
        if (mCompany.goToSite) {
            // create intent to open browser for "go to site" opt outs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            mContext.startActivity(intent);
        } else {
            HttpURLConnection urlConnection = null;
            try {
                this.mStatus = Status.INPROGRESS;
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
                    String out = s.hasNext() ? s.next() : "";
                    Log.v(TAG, "OptOutManger Sucess \n" + out);
                    this.mStatus = Status.SUCCESS;
                } catch (IOException e) {
                    this.mStatus = Status.FAIL;
                    Log.v(TAG, "OptOutManger Fail \n" + e.getMessage());
                }
            } catch (MalformedURLException e) {
                this.mStatus = Status.FAIL;
                Log.v(TAG, "Fail MalformedURLException \n " + e.getMessage());
            } catch (IOException e) {
                this.mStatus = Status.FAIL;
                Log.v(TAG, "Fail IOException \n " + e.getMessage());
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    private String injectIDs(String urlString) {
        List<TypedIdentifier> andid = new AndroidId().get(this.mContext);
        List<TypedIdentifier> phoneid = new PhoneId().get(this.mContext);
        List<TypedIdentifier> wifimac = new WifiMac().get(this.mContext);
        List<TypedIdentifier> adid = new AdvertisingId().get(this.mContext);

        urlString = urlString.replaceAll("%company-id", ""+this.mCompany.id);
        urlString = urlString.replaceAll("%optout", "true");

        urlString = urlString.replaceAll("%access-token", access_token);

        urlString = urlString.replaceAll("%appids", "123");
        urlString = urlString.replaceAll("%android-id", andid.get(0).getValue());

        urlString = urlString.replaceAll("%md5-android-id", andid.get(1).getValue());
        urlString = urlString.replaceAll("%sha1-android-id", andid.get(2).getValue());

        urlString = urlString.replaceAll("%md5-android-phone-id", phoneid.get(0).getValue());
        urlString = urlString.replaceAll("%sha1-android-phone-id", phoneid.get(1).getValue());

        urlString = urlString.replaceAll("%md5-mac", wifimac.get(0).getValue());
        urlString = urlString.replaceAll("%sha1-mac", wifimac.get(1).getValue());

        urlString = urlString.replaceAll("%ad-id", adid.get(0).getValue());
        urlString = urlString.replaceAll("%md5-ad-id", adid.get(1).getValue());
        urlString = urlString.replaceAll("%sha1-ad-id", adid.get(2).getValue());

        Log.i(TAG, "urlString: " + urlString);
        return urlString;
    }

    @Override
    protected Long doInBackground(URL... urls) {
        this.optOutForUrlString(urls[0]);
        return null;
    }

    @Override
    protected void onPostExecute(Long result) {
        executeResult();
    }

    public void executeResult(){
        Boolean lastone = checkIfLastCompanyToOptout();
        if (!mCompany.goToSite) {
            if(!mOptOutAll)
            {
                Notification notification = new Notification(mContext);
                notification.optoutNotification(mStatus);

            }
            if (mStatus == Status.SUCCESS) {
                mOptoutSucess++;
                Log.v(TAG, "Opt out success");
                (new EventLogger()).logOptOut(mCompany.id.toString(), this.mNoticeId, this.mOcid, (mOptOutAll) ? "1" : "0");

                if (mView != null)
                    mView.setEnabled(false); // disable toggle button

                if(lastone && !mOptOutAll) {
                    //this.optOutAllButton.setEnabled(false);
                    //this.optOutAllButton.setBackgroundResource(R.drawable.rounded_corner_gray);
                }
                Optout.OptoutData optout = Optout.OPTOUT_MAP.get(mCompany.id.toString());
                optout.optoutStatus = true;
                if (optout.storeOptout) {
                    optout.storeOptout = false;
                }
            } else {
                mOptoutFail++;
                Log.v(TAG, "Opt out failed");
                if (mView != null && mView instanceof CompoundButton) {
                    mView.setEnabled(true); // enable toggle button
                    ((CompoundButton) mView).setChecked(true); // turn toggle button "on"
                }
            }
            saveOptOutState();
            ListFragment.adapter.notifyDataSetChanged(); // update UI
        }

        boolean storedOptOutAll = false;

        if(mOptOutAll || storedOptOutAll)
        {
            lf.optOutResults();
        }

        Optout.OptoutData optout = Optout.OPTOUT_MAP.get(mCompany.id.toString());
        optout.storeOptoutAll = false;
        optout.storeOptout = false;

    }

    /*
     * This function helps to define if the current request is the last one
     * left on the table, if so then we disable the "choose all companies" button
     */
    private Boolean checkIfLastCompanyToOptout()
    {
        Boolean result = true;
        for (Map.Entry<String, Company.CompanyData> entry : Company.COMPANY_MAP.entrySet()) {
            Company.CompanyData company = Company.COMPANY_MAP.get(entry.getKey());
            Optout.OptoutData optout = Optout.OPTOUT_MAP.get(company.id.toString());

            if(!optout.optoutStatus && (company.id != mCompany.id) && !company.goToSite)
            {
                result = false;
                break;
            }

        }
        return result;
    }

    /*
     * Save the state of opt outs
     */
    private void saveOptOutState() {
        FileWriter.writeObject(mContext, Optout.OPTOUT_MAP, mContext.getString(R.string.ghostery_file_optout_status));
    }
}

