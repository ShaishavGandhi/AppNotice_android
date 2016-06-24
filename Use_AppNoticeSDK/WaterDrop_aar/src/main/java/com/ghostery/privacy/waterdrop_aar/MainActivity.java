package com.ghostery.privacy.waterdrop_aar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
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
    private Button btn_get_accept_state;
    private Button btn_reset_sdk;
    private Button btn_reset_app;
    private Button btn_close_app;
    public static boolean isInitialized = false;

    // Tracker ID tags
    private final static int ADMOB_TRACKERID = 464;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = App.getContext();
        isInitialized = true;
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
        String noticeIdString = Util.getSharedPreference(this, Util.SP_NOTICE_ID, "");
        String isImplied_String = Util.getSharedPreference(this, Util.SP_IS_IMPLIED, "1");
        String implied30dayDisplayMaxString = Util.getSharedPreference(this, Util.SP_IS_30DAY_MAX, "0");

        AppCompatEditText companyIdEditText = (AppCompatEditText)findViewById(R.id.editText_companyId);
        companyIdEditText.setText(companyIdString);

        AppCompatEditText noticeIdEditText = (AppCompatEditText)findViewById(R.id.editText_noticeId);
        noticeIdEditText.setText(noticeIdString);

        AppCompatRadioButton radioButton_implied = (AppCompatRadioButton)this.findViewById(R.id.radioButton_implied);
        AppCompatRadioButton radioButton_explicit = (AppCompatRadioButton)this.findViewById(R.id.radioButton_explicit);
        Boolean isImplied = isImplied_String.equals("1");
        if (isImplied_String != null) {
            radioButton_implied.setChecked(isImplied);
            radioButton_explicit.setChecked(!isImplied);
        }

        AppCompatEditText implied30dayDisplayMaxEditText = (AppCompatEditText)findViewById(R.id.editText_implied_30day_max);
        implied30dayDisplayMaxEditText.setText(implied30dayDisplayMaxString);

        btn_consent_flow = (AppCompatButton) findViewById(R.id.btn_consent_flow) ;
        btn_manage_preferences = (AppCompatButton) findViewById(R.id.btn_manage_preferences) ;
        btn_get_preferences = (AppCompatButton) findViewById(R.id.btn_get_preferences) ;
        btn_get_accept_state = (AppCompatButton) findViewById(R.id.btn_get_accept_state) ;
        btn_reset_sdk = (AppCompatButton) findViewById(R.id.btn_reset_sdk) ;
        btn_reset_app = (AppCompatButton) findViewById(R.id.btn_reset_app) ;
        btn_close_app = (AppCompatButton) findViewById(R.id.btn_close_app) ;

        btn_consent_flow.setOnClickListener(this);
        btn_manage_preferences.setOnClickListener(this);
        btn_get_preferences.setOnClickListener(this);
        btn_get_accept_state.setOnClickListener(this);
        btn_reset_sdk.setOnClickListener(this);
        btn_reset_app.setOnClickListener(this);
        btn_close_app.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        String companyIdString = "";
        String noticeIdString = "";
        int companyId = 0;
        int noticeId = 0;
        Boolean isImplied = true;
        int implied30dayDisplayMax = 0;

        AppCompatEditText tv = (AppCompatEditText)this.findViewById(R.id.editText_companyId);
        if (tv != null) {
            companyIdString = tv.getText().toString();
        }

        tv = (AppCompatEditText)this.findViewById(R.id.editText_noticeId);
        if (tv != null) {
            noticeIdString = tv.getText().toString();
        }

        Boolean usingToken = false;
        if (!noticeIdString.isEmpty() && noticeIdString.length() > 5) {
            usingToken = true;
        }

        AppCompatRadioButton radioButton_implied = (AppCompatRadioButton)this.findViewById(R.id.radioButton_implied);
        if (radioButton_implied != null) {
            isImplied = radioButton_implied.isChecked();
        }

        tv = (AppCompatEditText)this.findViewById(R.id.editText_implied_30day_max);
        if (tv != null) {
            String implied30dayDisplayMaxString = tv.getText().toString();
            try {
                implied30dayDisplayMax = Integer.parseInt(implied30dayDisplayMaxString);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error while parsing 30-day Display Max (" + implied30dayDisplayMaxString + ")", e);
            }
        }

        // Save these values as defaults for next session
        Util.setSharedPreference(this, Util.SP_COMPANY_ID, companyIdString);
        Util.setSharedPreference(this, Util.SP_NOTICE_ID, noticeIdString);
        Util.setSharedPreference(this, Util.SP_IS_IMPLIED, isImplied ? "1" : "0");
        Util.setSharedPreference(this, Util.SP_IS_30DAY_MAX, String.valueOf(implied30dayDisplayMax));

		if (noticeIdString.length() == 0 || (companyIdString.length() == 0 && !usingToken)) {
			Toast.makeText(this, "You must supply a Company ID and Notice ID...or a token in the Notice ID field.", Toast.LENGTH_LONG).show();
		} else {
            if (usingToken) {
//                appNotice = new AppNotice(this, noticeIdString, this);
            } else {
                try {
                    companyId = Integer.valueOf(companyIdString);
                    noticeId = Integer.valueOf(noticeIdString);
                    appNotice = new AppNotice(this, companyId, noticeId, this);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "CID and NID must be integers.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

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
				showTrackerPreferenceResults(trackerHashMap, getResources().getString(R.string.message_get_tracker_preferences));

            } else if (view == btn_get_accept_state) {
                String message;
                try {
                    Boolean isAccepted = appNotice.getAcceptedState();
                    if (isAccepted) {
                        message = getResources().getString(R.string.message_explicit_tracking_accepted);
                    } else {
                        message = getResources().getString(R.string.message_explicit_tracking_not_accepted);
                    }
                } catch (Exception e) {
                    message = e.getMessage();
                }
                showMessage(message); // Show selected status

			} else if (view == btn_consent_flow) {
                if (isImplied) {
                    appNotice.startImpliedConsentFlow(implied30dayDisplayMax);
                } else {
                    appNotice.startExplicitConsentFlow();
                }
            }
		}
    }

    // Handle callbacks for the App Notice SDK
    @Override
    public void onOptionSelected(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap) {
        // Handle your response
        if (isAccepted) {

            showTrackerPreferenceResults(trackerHashMap, getResources().getString(R.string.message_option_selected)); // Show preference results in a dialog
        } else {
            try {
                showMessage(getString(R.string.declineConfirmDialog_title), getString(R.string.declineConfirmDialog_message));
                showTrackerPreferenceResults(trackerHashMap, getResources().getString(R.string.message_tracking_declined)); // Show preference results in a dialog
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
    public void onNoticeSkipped(boolean isAccepted, HashMap<Integer, Boolean> trackerHashMap) {
        // Handle your response
        String message = getResources().getString(R.string.message_dialog_skipped);
        if (isAccepted) {
            message += ": " + getResources().getString(R.string.message_explicit_tracking_accepted);
        } else {
            message += ": " + getResources().getString(R.string.message_explicit_tracking_not_accepted);
        }
        showTrackerPreferenceResults(trackerHashMap, message); // Show preference results in a dialog
    }

    @Override
    public void onTrackerStateChanged(HashMap<Integer, Boolean> trackerHashMap) {
        showTrackerPreferenceResults(trackerHashMap, getResources().getString(R.string.message_tracker_state_changed)); // Show preference results in a dialog

    }

    private void showTrackerPreferenceResults(HashMap<Integer, Boolean> trackerHashMap, String title) {
        String prefResults = "";
        if (trackerHashMap == null || trackerHashMap.size() == 0) {
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

    private void showMessage(String title, String message) {
//        String prefResults = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showMessage(String title) {
//        String prefResults = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
//        builder.setMessage(prefResults);
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
