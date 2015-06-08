package com.ghostery.privacy.inappconsentsdk.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.callbacks.InAppConsent_Callback;
import com.ghostery.privacy.inappconsentsdk.utils.AppData;
import com.ghostery.privacy.inappconsentsdk.model.InAppConsentData;
import com.ghostery.privacy.inappconsentsdk.utils.Util;

public class ExplicitInfo_DialogFragment extends DialogFragment {
    int mNum;
    private InAppConsentData inAppConsentData;
    private boolean useRemoteValues = true;
    private InAppConsent_Callback inAppConsent_callback;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ExplicitInfo_DialogFragment newInstance(int num) {
        ExplicitInfo_DialogFragment f = new ExplicitInfo_DialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.ghostery_explicitinfo_dialogfragment, container, false);

        // Apply the tracker config customizations
        if (useRemoteValues)
            applyCustomConfig(v);

        // Watch for button clicks.
        Button preferences_button = (Button)v.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                InAppConsentData.sendNotice(InAppConsentData.NoticeType.EXPLICIT_INFO_PREF);

                // Open the In-App Consent preferences activity
                Util.ShowAdPreferences(getActivity());
            }
        });

        Button accept_button = (Button)v.findViewById(R.id.accept_button);
        accept_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Send notice for this event
                InAppConsentData.sendNotice(InAppConsentData.NoticeType.EXPLICIT_INFO_ACCEPT);

                // Remember in a persistent way that the explicit notice has been accepted
                AppData.setBoolean(AppData.APPDATA_EXPLICIT_ACCEPTED, true);

                // Let the calling class know the selected option
                if (inAppConsent_callback != null)
                    inAppConsent_callback.onOptionSelected(true);

                // Close this dialog
                dismiss();
            }
        });

        Button decline_button = (Button)v.findViewById(R.id.decline_button);
        decline_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // User cancelled the dialog...negating consent

                // Send notice for this event
                InAppConsentData.sendNotice(InAppConsentData.NoticeType.EXPLICIT_INFO_DECLINE);

                // Let the calling class know the selected option
                if (inAppConsent_callback != null)
                    inAppConsent_callback.onOptionSelected(false);
            }
        });

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        return dialog;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        //params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);

        super.onResume();
    }

    @Override
    public void onCancel (DialogInterface dialog) {
        // User cancelled the dialog...negating consent

        // Send notice for this event
        InAppConsentData.sendNotice(InAppConsentData.NoticeType.EXPLICIT_INFO_DECLINE);


        // Let the calling class know the selected option
        if (inAppConsent_callback != null)
            inAppConsent_callback.onOptionSelected(false);
    }

    public void setInAppConsentData(InAppConsentData inAppConsentData) {
        this.inAppConsentData = inAppConsentData;
    }

    public void setUseRemoteValues(boolean useRemoteValues) {
        this.useRemoteValues = useRemoteValues;
    }

    public void setInAppConsent_Callback(InAppConsent_Callback inAppConsent_callback) {
        this.inAppConsent_callback = inAppConsent_callback;
    }

    private void applyCustomConfig(View v) {
        // Set custom config values from the inAppConsentData object
        if (inAppConsentData != null && inAppConsentData.isInitialized()) {
            LinearLayout linearLayout_outer = (LinearLayout)v.findViewById(R.id.linearLayout_outer);

            String ric_access_button_color = inAppConsentData.getBric_access_button_color();
            String ric_decline_button_color = inAppConsentData.getBric_decline_button_color();

            // Set background color and opacity
            if (linearLayout_outer != null) {
                if (inAppConsentData.getBric_bg() != null) {
                    int bric_bg = Color.parseColor(inAppConsentData.getBric_bg());
                    linearLayout_outer.setBackgroundColor(bric_bg);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    float opacityFloat = inAppConsentData.getRic_opacity();
                    if (opacityFloat < 1F && opacityFloat >= 0) {
                        Drawable d = new ColorDrawable(Color.BLACK);
                        d.setAlpha((int)(255 * opacityFloat));
                        getDialog().getWindow().setBackgroundDrawable(d);
                        linearLayout_outer.setAlpha(opacityFloat);
                    }
                }
            }

            // Title
            TextView textView_title = (TextView)v.findViewById(R.id.textView_title);
            if (textView_title != null) {
                if (inAppConsentData.getBric_header_text() != null)
                    textView_title.setText(inAppConsentData.getBric_header_text());
                if (inAppConsentData.getBric_header_text_color() != null)
                    textView_title.setTextColor(Color.parseColor(inAppConsentData.getBric_header_text_color()));
            }

            // Message
            TextView textView_message = (TextView)v.findViewById(R.id.textView_message);
            if (textView_message != null) {
                if (inAppConsentData.getBric_content1() != null)
                    textView_message.setText(inAppConsentData.getBric_content1());
                if (inAppConsentData.getRic_color() != null)
                    textView_message.setTextColor(Color.parseColor(inAppConsentData.getRic_color()));
            }

            // Preferences button
            Button preferences_button = (Button)v.findViewById(R.id.preferences_button);
            if (preferences_button != null) {
                if (inAppConsentData.getRic_click_manage_settings() != null)
                    preferences_button.setText(inAppConsentData.getRic_click_manage_settings());
                if (inAppConsentData.getBric_access_button_text_color() != null)
                    preferences_button.setTextColor(Color.parseColor(inAppConsentData.getBric_access_button_text_color()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ric_access_button_color != null)
                    preferences_button.getBackground().setColorFilter(Color.parseColor(ric_access_button_color), PorterDuff.Mode.SRC);
//                    preferences_button.setBackgroundColor(Color.parseColor(inAppConsentData.getBric_access_button_color()));
            }

            // Accept button
            Button accept_button = (Button)v.findViewById(R.id.accept_button);
            if (accept_button != null) {
                if (inAppConsentData.getBric_access_button_text() != null)
                    accept_button.setText(inAppConsentData.getBric_access_button_text());
                if (inAppConsentData.getBric_access_button_text_color() != null)
                    accept_button.setTextColor(Color.parseColor(inAppConsentData.getBric_access_button_text_color()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ric_access_button_color != null)
                    accept_button.getBackground().setColorFilter(Color.parseColor(ric_access_button_color), PorterDuff.Mode.SRC);
//                    accept_button.setBackgroundColor(Color.parseColor(inAppConsentData.getBric_access_button_color()));
            }

            // Decline button
            Button decline_button = (Button)v.findViewById(R.id.decline_button);
            if (decline_button != null) {
                if (inAppConsentData.getBric_decline_button_text() != null)
                    decline_button.setText(inAppConsentData.getBric_decline_button_text());
                if (inAppConsentData.getBric_decline_button_text_color() != null)
                    decline_button.setTextColor(Color.parseColor(inAppConsentData.getBric_decline_button_text_color()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ric_decline_button_color != null)
                    decline_button.getBackground().setColorFilter(Color.parseColor(ric_decline_button_color), PorterDuff.Mode.SRC);
//                    decline_button.setBackgroundColor(Color.parseColor(inAppConsentData.getBric_decline_button_color()));
            }
        }
    }
}
