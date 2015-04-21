package com.ghostery.privacy.inappconsentsdk.Identifiers;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.app.ListActivity;
import com.ghostery.privacy.inappconsentsdk.fragments.ListFragment;
import com.ghostery.privacy.inappconsentsdk.app.TrusteStatusRequest;
import com.ghostery.privacy.inappconsentsdk.model.Optout;
import com.ghostery.privacy.inappconsentsdk.utils.AppData;
import com.ghostery.privacy.inappconsentsdk.utils.FileWriter;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jdonohoo on 6/6/14.
 */
/*
 * Returns the Advertising ID provided by Google Play Services
 *
 * The Advertising ID is only available on devices with Google Play Services 4.0+
 * The Advertising ID must be fetched off the UI thread
 */
public class AdvertisingId implements IdentifierSource
{

    private static final String TAG = "AppChoices";
    private static final String TRUST_TOKEN_STRING = "0cdc0c3e-2883-4b15-952e-0c27b0c075bb";    // Sensitive: don't put in resources

    public static List<TypedIdentifier> id;
    public boolean optOutAll;

    private Context mContext;

    @Override
    public List<TypedIdentifier> get(Context context)
    {
        // Patch for the Kindle
        if (id == null) {
            id = new ArrayList<TypedIdentifier>();
            id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID, ""));
            id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID_MD5, ""));
            id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID_SHA1, ""));
        }
        return id;
    }

    public void getIdThread(Context context)
    {
        mContext = context;
        id = new ArrayList<TypedIdentifier>();
        new GetAdvertisingIdTask().execute();
    }

    private class GetAdvertisingIdTask extends AsyncTask<Void, Void, List<TypedIdentifier>>
    {
        private final static int INITWAIT = 50; // msecs to wait between retries for the adapter to be initialized
        private int retry = 10;                 // Times to recheck for the adapter to be initialized

        @Override
        protected List<TypedIdentifier> doInBackground(Void... params) {
            Log.i(TAG, "AdvertisingID doInBackground");
            AdvertisingIdClient.Info adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
            } catch (Exception e) {
            }
            if (adInfo != null) {
                String ad = adInfo.getId();
                id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID, ad));
                try {
                    id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID_MD5, DigestUtil.md5Hash(ad)));
                } catch (NoSuchAlgorithmException e) {
                    Log.v(TAG, "Error hashing ADVERTISING_ID - MD5 not supported");
                }
                try {
                    id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID_SHA1, DigestUtil.sha1Hash(ad)));
                } catch (NoSuchAlgorithmException e) {
                    Log.v(TAG, "Error hashing ADVERTISING_ID - SHA1 not supported");
                }
            } else {
                Log.v(TAG, "Error hashing ADVERTISING_ID - Google Play Services not supported");
                id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID, ""));
                id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID_MD5, ""));
                id.add(new TypedIdentifier(TypedIdentifier.TYPE_ADVERTISING_ID_SHA1, ""));
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TypedIdentifier> result)
        {
            String adIdMd5 = "";
            String adId = "";
            try {
                adId = id.get(0).getValue();
                adIdMd5 = DigestUtil.md5Hash(id.get(0).getValue());
            } catch (NoSuchAlgorithmException e) {
            }

            if (ListActivity.firstRun) {
                AppData.setString(AppData.APPDATA_ADVERTISING_ID_HASH, adIdMd5);
                AppData.setString(AppData.APPDATA_ADVERTISING_ID, adId);
            }
            Boolean changed = ListActivity.didAdvertisingIdChange();

            if(changed)
            {
                FileWriter.writeObject(mContext, Optout.OPTOUT_MAP, mContext.getString(R.string.ghostery_file_optout_status));
            }

            // @@@@ Check if is first time running
            if(!changed && ListActivity.firstRun)
            {
                // call truste status
                Log.v("TAG", "TOKEN ====" + TRUST_TOKEN_STRING);
                Log.v("TAG", "URL ===="+mContext.getString(R.string.ghostery_url_truste_status_request));

                //retrieve Truste status
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("access_token", TRUST_TOKEN_STRING);

                data.put("anid", adId);
                Log.v("TAG", "ANID === "+adId);
                data.put("optout", "true");

                TrusteStatusRequest asyncHttpPost = new TrusteStatusRequest(data);
                asyncHttpPost.execute(mContext.getString(R.string.ghostery_url_truste_status_request));

            }
            else
            {
                notifyDataSetChanged();
            }

        }

        public void notifyDataSetChanged() {

            if(ListFragment.adapter != null) {
                // If the adapter is initialized, notify.
                ListFragment.adapter.notifyDataSetChanged();
            } else {
                // If the adapter has not been initialized, wait and then retry...
                if(retry > 0) {
                    retry--;

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    }, INITWAIT);
                }
            }
        }
    }

}
