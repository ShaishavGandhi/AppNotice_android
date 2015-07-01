package com.ghostery.privacy.use_sdk_aar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
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


public class MainActivity extends AppCompatActivity implements OnClickListener, InAppConsent_Callback {

    private InAppConsent_Callback inAppConsent_Callback;
    private Button btn_consent_flow;
    private Button btn_manage_preferences;
    private Button btn_reset_sdk;
    private Button btn_close_app;
    private HashMap<Integer, Boolean> trackerHashMap;

    // Tracker ID tags
    private final static int ADMOB_TRACKERID = 464;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        btn_reset_sdk = (Button) findViewById(R.id.btn_reset_sdk) ;
        btn_close_app = (Button) findViewById(R.id.btn_close_app) ;

        btn_consent_flow.setOnClickListener(this);
        btn_manage_preferences.setOnClickListener(this);
        btn_reset_sdk.setOnClickListener(this);
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
            InAppConsent inAppConsent = new InAppConsent();
            inAppConsent.resetSDK();

            Toast.makeText(this, "SDK was reset.", Toast.LENGTH_SHORT).show();

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

            InAppConsent inAppConsent = new InAppConsent();

            this.trackerHashMap = inAppConsent.getTrackerPreferences();

            if (view == btn_manage_preferences) {
                inAppConsent.showManagePreferences(this, companyId, pubNoticeId, useRemoteValues, this);

            } else if (view == btn_consent_flow) {
                inAppConsent.startConsentFlow(this, companyId, pubNoticeId, useRemoteValues, this);

            }
        }
    }

    // Handle callbacks for the In-App Consent SDK
    @Override
    public void onOptionSelected(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap) {
        // Handle your response
        if (isAccepted) {
            Toast.makeText(this, "Tracking accepted", Toast.LENGTH_LONG).show();

//      if (trackerHashMap.get(ADMOB_TRACKERID))  // Only init AdMob if allowed by user
//          AdMob.init();

        } else {
            Toast.makeText(this, "Tracking declined", Toast.LENGTH_LONG).show();

            // Close the app
//          getApplication().finish();
            System.exit(0);
        }
    }

    @Override
    public void onNoticeSkipped() {
        // Handle your response
        Toast.makeText(this, "Dialog skipped: Tracking accepted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTrackerStateChanged(HashMap<Integer, Boolean> trackerHashMap) {
        this.trackerHashMap = trackerHashMap;

    }

}
