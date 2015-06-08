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
import com.ghostery.privacy.inappconsentsdk.model.InAppConsentData;
import com.ghostery.privacy.inappconsentsdk.utils.Util;

public class ImpliedInfo_DialogFragment extends DialogFragment {
    int mNum;
    private InAppConsentData inAppConsentData;
    private boolean useRemoteValues = true;
    private InAppConsent_Callback inAppConsent_callback;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ImpliedInfo_DialogFragment newInstance(int num) {
        ImpliedInfo_DialogFragment f = new ImpliedInfo_DialogFragment();

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

        View v = inflater.inflate(R.layout.ghostery_impliedinfo_dialogfragment, container, false);

        // Apply the tracker config customizations
        if (useRemoteValues)
            applyCustomConfig(v);

        // Watch for button clicks.
        Button preferences_button = (Button)v.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open the In-App Consent preferences activity
                Util.ShowAdPreferences(getActivity());

                // Send notice for this event
                InAppConsentData.sendNotice(InAppConsentData.NoticeType.IMPLICIT_INFO_PREF);

                // Let the calling class know the selected option
                if (inAppConsent_callback != null)
                    inAppConsent_callback.onOptionSelected(true);

                // Close this dialog
                dismiss();
            }
        });

        Button close_button = (Button)v.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Let the calling class know the selected option
                if (inAppConsent_callback != null)
                    inAppConsent_callback.onOptionSelected(true);

                // Close this dialog
                dismiss();
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

        // Let the calling class know the selected option
        if (inAppConsent_callback != null)
            inAppConsent_callback.onOptionSelected(true);
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

            String ric_bg = inAppConsentData.getRic_bg();
            String ric_access_button_color = inAppConsentData.getBric_access_button_color();


            // Set background color and opacity
            if (linearLayout_outer != null) {
                if (ric_bg != null)
                    linearLayout_outer.setBackgroundColor(Color.parseColor(ric_bg));
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
                if (inAppConsentData.getRic_title() != null)
                    textView_title.setText(inAppConsentData.getRic_title());
                if (inAppConsentData.getRic_title_color() != null)
                    textView_title.setTextColor(Color.parseColor(inAppConsentData.getRic_title_color()));
            }

            // Message
            TextView textView_message = (TextView)v.findViewById(R.id.textView_message);
            if (textView_message != null) {
                if (inAppConsentData.getRic() != null)
                    textView_message.setText(inAppConsentData.getRic());
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
//                    preferences_button.getBackground().setColorFilter(Color.parseColor(ric_access_button_color), PorterDuff.Mode.MULTIPLY);
//                    preferences_button.setBackgroundColor(Color.parseColor(ric_access_button_color));
            }

            // Close button
            Button close_button = (Button)v.findViewById(R.id.close_button);
            if (close_button != null) {
                if (inAppConsentData.getClose_button() != null)
                    close_button.setText(inAppConsentData.getClose_button());
                if (inAppConsentData.getBric_access_button_text_color() != null)
                    close_button.setTextColor(Color.parseColor(inAppConsentData.getBric_access_button_text_color()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ric_access_button_color != null)
                    close_button.getBackground().setColorFilter(Color.parseColor(ric_access_button_color), PorterDuff.Mode.SRC);
//                    close_button.setBackgroundColor(Color.parseColor(ric_access_button_color));
            }

        }
    }
}
