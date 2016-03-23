package com.ghostery.privacy.waterdrop_aar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ghostery.privacy.appnoticesdk.AppNotice;
import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnClickListener, AppNotice_Callback {

    private final static String TAG = "MainActivity";
    private AppNotice_Callback appNotice_Callback;
    private static AppNotice appNotice;
    private Button btn_consent_flow;
    private Button btn_manage_preferences;
    private Button btn_get_preferences;
    private Button btn_reset_sdk;
    private Button btn_reset_app;
    private Button btn_close_app;
    private Boolean isHybridApp;

    // Tracker ID tags
    private final static int ADMOB_TRACKERID = 464;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = App.getContext();
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
        String configIdString = Util.getSharedPreference(this, Util.SP_CONFIG_ID, "");
        String useRemoteValuesString = Util.getSharedPreference(this, Util.SP_USE_REMOTEVALUES, "");
        String isHybridAppString = Util.getSharedPreference(this, Util.SP_IS_HYBRIDAPP, "");

        AppCompatEditText companyIdEditText = (AppCompatEditText)findViewById(R.id.editText_companyId);
        companyIdEditText.setText(companyIdString);

        AppCompatEditText configIdEditText = (AppCompatEditText)findViewById(R.id.editText_configId);
        configIdEditText.setText(configIdString);

        AppCompatCheckBox checkBox_useRemoteValues = (AppCompatCheckBox)this.findViewById(R.id.checkBox_useRemoteValues);
        if (useRemoteValuesString != null && useRemoteValuesString.equals("1")) {
            checkBox_useRemoteValues.setChecked(true);
        } else {
            checkBox_useRemoteValues.setChecked(false);
        }

        AppCompatCheckBox checkBox_hybridApp = (AppCompatCheckBox)this.findViewById(R.id.checkBox_hybridApp);
        if (isHybridAppString != null && isHybridAppString.equals("1")) {
            checkBox_hybridApp.setChecked(true);
        } else {
            checkBox_hybridApp.setChecked(false);
        }

        btn_consent_flow = (AppCompatButton) findViewById(R.id.btn_consent_flow) ;
        btn_manage_preferences = (AppCompatButton) findViewById(R.id.btn_manage_preferences) ;
        btn_get_preferences = (AppCompatButton) findViewById(R.id.btn_get_preferences) ;
        btn_reset_sdk = (AppCompatButton) findViewById(R.id.btn_reset_sdk) ;
        btn_reset_app = (AppCompatButton) findViewById(R.id.btn_reset_app) ;
        btn_close_app = (AppCompatButton) findViewById(R.id.btn_close_app) ;

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
        String configIdString = "";
        int companyId = 0;
        int configId = 0;
        Boolean useRemoteValues = true;
        isHybridApp = true;

        AppCompatEditText tv = (AppCompatEditText)this.findViewById(R.id.editText_companyId);
        if (tv != null)
            companyIdString = tv.getText().toString();

        tv = (AppCompatEditText)this.findViewById(R.id.editText_configId);
        if (tv != null)
            configIdString = tv.getText().toString();

        AppCompatCheckBox checkBox_useRemoteValues = (AppCompatCheckBox)this.findViewById(R.id.checkBox_useRemoteValues);
        if (checkBox_useRemoteValues != null) {
            useRemoteValues = checkBox_useRemoteValues.isChecked();
        }

        AppCompatCheckBox checkBox_hybridApp = (AppCompatCheckBox)this.findViewById(R.id.checkBox_hybridApp);
        if (checkBox_hybridApp != null) {
            isHybridApp = checkBox_hybridApp.isChecked();
        }

        // Save these values as defaults for next session
        Util.setSharedPreference(this, Util.SP_COMPANY_ID, companyIdString);
        Util.setSharedPreference(this, Util.SP_CONFIG_ID, configIdString);
        Util.setSharedPreference(this, Util.SP_USE_REMOTEVALUES, useRemoteValues ? "1" : "0");
        Util.setSharedPreference(this, Util.SP_IS_HYBRIDAPP, isHybridApp ? "1" : "0");

		if (companyIdString.length() == 0 || configIdString.length() == 0) {
			Toast.makeText(this, "You must supply a Company ID and Notice ID.", Toast.LENGTH_LONG).show();
		} else {
			companyId = Integer.valueOf(companyIdString);
			configId = Integer.valueOf(configIdString);

			appNotice = new AppNotice(this, companyId, configId, useRemoteValues, this);

			if (view == btn_reset_sdk) {
				appNotice.resetSDK();

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

			} else if (view == btn_manage_preferences) {
                appNotice.showManagePreferences();

			} else if (view == btn_get_preferences) {
				HashMap<Integer, Boolean> trackerHashMap = appNotice.getTrackerPreferences();
				showTrackerPreferenceResults(trackerHashMap, "Get Tracker Preferences");

			} else if (view == btn_consent_flow) {
				appNotice.startConsentFlow();

			}
		}
    }

    // Handle callbacks for the App Notice Consent SDK
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
        HashMap<Integer, Boolean> trackerHashMap = appNotice.getTrackerPreferences();
        showTrackerPreferenceResults(trackerHashMap, "Dialog skipped: Tracking Accepted"); // Show preference results in a dialog
    }

    @Override
    public void onTrackerStateChanged(HashMap<Integer, Boolean> trackerHashMap) {
        showTrackerPreferenceResults(trackerHashMap, "Tracker State Changed"); // Show preference results in a dialog

    }

    @Override
    public boolean onManagePreferencesClicked() {
        boolean wasHandled = false;
        if (isHybridApp) {
            // Open local preferences screen
            Intent i = new Intent(getBaseContext(), HybridPrivacySettings.class);
            startActivity(i);
            wasHandled = true;  // Handled

        } else {
            wasHandled = false; // Not handled
        }
        return wasHandled;
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

    public static AppNotice getAppNotice() {
        return appNotice;
    }
}