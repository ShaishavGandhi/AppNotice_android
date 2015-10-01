package com.ghostery.privacy.use_sdk_module;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ghostery.privacy.inappconsentsdk.callbacks.InAppConsent_Callback;
import com.ghostery.privacy.inappconsentsdk.model.InAppConsent;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnClickListener, InAppConsent_Callback {

    private final static String TAG = "MainActivity";
    private InAppConsent_Callback inAppConsent_Callback;
    private InAppConsent inAppConsent;
    private Button btn_consent_flow;
    private Button btn_manage_preferences;
    private Button btn_get_preferences;
    private Button btn_reset_sdk;
    private Button btn_reset_app;
    private Button btn_close_app;

    // Tracker ID tags
    private final static int ADMOB_TRACKERID = 464;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inAppConsent = new InAppConsent(this);
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If there are saved IDs, use them
        String companyIdString = Util.getSharedPreference(this, Util.SP_COMPANY_ID, "");
        String pubNoticeIdString = Util.getSharedPreference(this, Util.SP_PUB_NOTICE_ID, "");

        EditText companyIdEditText = (EditText)findViewById(R.id.editText_companyId);
        EditText pubNoticeIdEditText = (EditText)findViewById(R.id.editText_pubNoticeId);

        companyIdEditText.setText(companyIdString);
        pubNoticeIdEditText.setText(pubNoticeIdString);

        btn_consent_flow = (Button) findViewById(R.id.btn_consent_flow) ;
        btn_manage_preferences = (Button) findViewById(R.id.btn_manage_preferences) ;
        btn_get_preferences = (Button) findViewById(R.id.btn_get_preferences) ;
        btn_reset_sdk = (Button) findViewById(R.id.btn_reset_sdk) ;
        btn_reset_app = (Button) findViewById(R.id.btn_reset_app) ;
        btn_close_app = (Button) findViewById(R.id.btn_close_app) ;

        btn_consent_flow.setOnClickListener(this);
        btn_manage_preferences.setOnClickListener(this);
        btn_get_preferences.setOnClickListener(this);
        btn_reset_sdk.setOnClickListener(this);
        btn_reset_app.setOnClickListener(this);
        btn_close_app.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String companyIdString = "";
        String pubNoticeIdString = "";
        int companyId = 0;
        int pubNoticeId = 0;
        Boolean useRemoteValues = true;

        TextView tv = (TextView)this.findViewById(R.id.editText_companyId);
        if (tv != null)
            companyIdString = tv.getText().toString();

        tv = (TextView)this.findViewById(R.id.editText_pubNoticeId);
        if (tv != null)
            pubNoticeIdString = tv.getText().toString();

        // Save these values as defaults for next session
        Util.setSharedPreference(this, Util.SP_COMPANY_ID, companyIdString);
        Util.setSharedPreference(this, Util.SP_PUB_NOTICE_ID, pubNoticeIdString);

        if (view == btn_reset_sdk) {
            inAppConsent.resetSDK();

            Toast.makeText(this, "SDK was reset.", Toast.LENGTH_SHORT).show();

        } else if (view == btn_reset_app) {
            // Reset the app
            Util.clearSharedPreferences(this, "com.ghostery.privacy.use_sdk_module");
            //Util.clearSharedPreferences(this, "com.ghostery.privacy.use_sdk_module_preferences");

            // Close the app
            this.finish();
            System.exit(0);

        } else if (view == btn_close_app) {
            // Close the app
            this.finish();
            System.exit(0);
        }

        else if (companyIdString.length() == 0 || pubNoticeIdString.length() == 0) {
            Toast.makeText(this, "You must supply a Company ID and Pub-notice ID.", Toast.LENGTH_LONG).show();
        } else {
            companyId = Integer.valueOf(companyIdString);
            pubNoticeId = Integer.valueOf(pubNoticeIdString);

            CheckBox cb = (CheckBox)this.findViewById(R.id.checkBox_useRemoteValues);
            if (cb != null)
                useRemoteValues = cb.isChecked();

            InAppConsent inAppConsent = new InAppConsent(this);

            if (view == btn_manage_preferences) {
                inAppConsent.showManagePreferences(companyId, pubNoticeId, useRemoteValues, this);

            } else if (view == btn_get_preferences) {
                HashMap<Integer, Boolean> trackerHashMap = inAppConsent.getTrackerPreferences();
                showTrackerPreferenceResults(trackerHashMap, "Get Tracker Preferences");

            } else if (view == btn_consent_flow) {
                inAppConsent.startConsentFlow(companyId, pubNoticeId, useRemoteValues, this);

            }
        }
    }

    // Handle callbacks for the In-App Consent SDK
    @Override
    public void onOptionSelected(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap) {
        // Handle your response
        if (isAccepted) {

            showTrackerPreferenceResults(trackerHashMap, "Option Selected"); // Show preference results in a dialog
        } else {
            try {
                DeclineConfirmation_DialogFragment dialog = new DeclineConfirmation_DialogFragment();
                dialog.show(getFragmentManager(), "DeclineConfirmation_DialogFragment");
            } catch (IllegalStateException e) {
                Log.e(TAG, "Error while trying to display the decline-confirmation dialog.", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Close the app
        System.exit(0);
    }

    @Override
    public void onNoticeSkipped() {
        // Handle your response
        HashMap<Integer, Boolean> trackerHashMap = inAppConsent.getTrackerPreferences();
        showTrackerPreferenceResults(trackerHashMap, "Dialog skipped: Tracking Accepted"); // Show preference results in a dialog
    }

    @Override
    public void onTrackerStateChanged(HashMap<Integer, Boolean> trackerHashMap) {
        showTrackerPreferenceResults(trackerHashMap, "Tracker State Changed"); // Show preference results in a dialog

    }

    private void showTrackerPreferenceResults(HashMap<Integer, Boolean> trackerHashMap, String title) {
        String prefResults = "";
        if (trackerHashMap.size() == 0) {
            Toast.makeText(this, "No privacy preferences returned.", Toast.LENGTH_LONG).show();
        } else {
            for (Map.Entry<Integer, Boolean> entry : trackerHashMap.entrySet()) {
                int key = entry.getKey();
                Boolean value = entry.getValue();
                prefResults += Integer.toString(key) + ": " + Boolean.toString(value) + "\n";
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(prefResults);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
