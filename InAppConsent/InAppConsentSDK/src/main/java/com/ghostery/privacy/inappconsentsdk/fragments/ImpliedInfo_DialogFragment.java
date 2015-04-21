package com.ghostery.privacy.inappconsentsdk.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

import com.ghostery.privacy.inappconsentsdk.callbacks.InAppNotice_Callback;
import com.ghostery.privacy.inappconsentsdk.R;
import com.ghostery.privacy.inappconsentsdk.utils.TrackerConfig;
import com.ghostery.privacy.inappconsentsdk.utils.Util;

public class ImpliedInfo_DialogFragment extends DialogFragment {
    int mNum;
    private TrackerConfig trackerConfig;
    private boolean useRemoteValues = true;
    private InAppNotice_Callback inAppNotice_callback;

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
            applyTrackerConfig(v);

        // Watch for button clicks.
        Button preferences_button = (Button)v.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open the In-App Consent preferences activity
                Util.ShowAdPreferences(getActivity());

                // Send notice for this event
                TrackerConfig.sendNotice(TrackerConfig.NoticeType.IMPLICIT_INFO_PREF);

                // Let the calling class know the selected option
                if (inAppNotice_callback != null)
                    inAppNotice_callback.onOptionSelected(true);

                // Close this dialog
                dismiss();
            }
        });

        Button close_button = (Button)v.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Let the calling class know the selected option
                if (inAppNotice_callback != null)
                    inAppNotice_callback.onOptionSelected(true);

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
        if (inAppNotice_callback != null)
            inAppNotice_callback.onOptionSelected(true);
    }

    public void setTrackerConfig(TrackerConfig trackerConfig) {
        this.trackerConfig = trackerConfig;
    }

    public void setUseRemoteValues(boolean useRemoteValues) {
        this.useRemoteValues = useRemoteValues;
    }

    public void setInAppNotice_Callback(InAppNotice_Callback inAppNotice_callback) {
        this.inAppNotice_callback = inAppNotice_callback;
    }

    private void applyTrackerConfig(View v) {
        // Set custom config values from the trackerConfig object
        if (trackerConfig != null && trackerConfig.isInitialized()) {
            LinearLayout linearLayout_outer = (LinearLayout)v.findViewById(R.id.linearLayout_outer);

            // Set background color and opacity
            if (linearLayout_outer != null) {
                if (trackerConfig.getRic_bg() != null)
                    linearLayout_outer.setBackgroundColor(Color.parseColor(trackerConfig.getRic_bg()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    float opacityFloat = trackerConfig.getRic_opacity();
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
                if (trackerConfig.getRic_title() != null)
                    textView_title.setText(trackerConfig.getRic_title());
                if (trackerConfig.getRic_title_color() != null)
                    textView_title.setTextColor(Color.parseColor(trackerConfig.getRic_title_color()));
            }

            // Message
            TextView textView_message = (TextView)v.findViewById(R.id.textView_message);
            if (textView_message != null) {
                if (trackerConfig.getRic() != null)
                    textView_message.setText(trackerConfig.getRic());
                if (trackerConfig.getRic_color() != null)
                    textView_message.setTextColor(Color.parseColor(trackerConfig.getRic_color()));
            }

            // Preferences button
            Button preferences_button = (Button)v.findViewById(R.id.preferences_button);
            if (preferences_button != null) {
                if (trackerConfig.getRic_click_manage_settings() != null)
                    preferences_button.setText(trackerConfig.getRic_click_manage_settings());
                if (trackerConfig.getRic_color() != null)
                    preferences_button.setTextColor(Color.parseColor(trackerConfig.getRic_color()));
            }

            // Close button
            Button close_button = (Button)v.findViewById(R.id.close_button);
            if (close_button != null) {
                if (trackerConfig.getClose_button() != null)
                    close_button.setText(trackerConfig.getClose_button());
                if (trackerConfig.getRic_color() != null)
                    close_button.setTextColor(Color.parseColor(trackerConfig.getRic_color()));
            }
        }
    }
}
