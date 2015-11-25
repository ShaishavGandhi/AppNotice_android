package com.ghostery.privacy.appnoticesdk.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

public class ImpliedInfo_DialogFragment extends DialogFragment {
    int mNum;
    private AppNoticeData appNoticeData;
    private boolean useRemoteValues = true;
    private AppNotice_Callback appNotice_callback;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static ImpliedInfo_DialogFragment newInstance(int num) {
        ImpliedInfo_DialogFragment dialogFragment = new ImpliedInfo_DialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        dialogFragment.setArguments(args);

        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appNoticeData = AppNoticeData.getInstance(getActivity());
        appNotice_callback = (AppNotice_Callback)Session.get(Session.APPNOTICE_CALLBACK);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ghostery_impliedinfo_dialogfragment, container, false);

        // Apply the tracker config customizations
        //if (useRemoteValues)
            applyCustomConfig(view);

        // Watch for button clicks.
        Button preferences_button = (Button)view.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Remember that the tracker preferences screen was opened from a consent flow dialog
                Session.set(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, true);

                // Open the In-App Consent preferences fragmentActivity
                Util.showManagePreferences(getActivity());

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.IMPLIED_INFO_PREF);
            }
        });

        Button close_button = (Button)view.findViewById(R.id.close_button);
        close_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Let the calling class know the selected option
                if (appNotice_callback != null)
                    appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));

                // Close this dialog
                dismiss();
            }
        });

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onResume() {
        Boolean prefOpenedFromDialog = (Boolean)Session.get(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, false);
        if (prefOpenedFromDialog) {
            // Now that we're back, remove this session var
            Session.remove(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG);

            // Let the calling class know the selected option
            if (appNotice_callback != null)
                appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));

            // Close this dialog
            dismiss();
        } else {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            //params.height = WindowManager.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        }

        super.onResume();
    }

    @Override
    public void onCancel (DialogInterface dialog) {
        // Let the calling class know the selected option
        if (appNotice_callback != null)
            appNotice_callback.onOptionSelected(true, appNoticeData.getTrackerHashMap(true));
    }

    public void setUseRemoteValues(boolean useRemoteValues) {
        this.useRemoteValues = useRemoteValues;
    }

    private void applyCustomConfig(View v) {
        // Set custom config values from the appNoticeData object
        if (appNoticeData != null && appNoticeData.isInitialized()) {
            LinearLayout linearLayout_outer = (LinearLayout)v.findViewById(R.id.linearLayout_outer);

            int ric_bg = appNoticeData.getRic_bg();
            int ric_access_button_color = appNoticeData.getBric_access_button_color();


            // Set background color and opacity
            if (linearLayout_outer != null) {
                linearLayout_outer.setBackgroundColor(ric_bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    float opacityFloat = appNoticeData.getRic_opacity();
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
                if (appNoticeData.getRic_title() != null)
                    textView_title.setText(appNoticeData.getRic_title());
                textView_title.setTextColor(appNoticeData.getRic_title_color());
            }

            // Message
            TextView textView_message = (TextView)v.findViewById(R.id.textView_message);
            if (textView_message != null) {
                if (appNoticeData.getRic() != null)
                    textView_message.setText(appNoticeData.getRic());
                textView_message.setTextColor(appNoticeData.getRic_color());
            }

            // Preferences button
            Button preferences_button = (Button)v.findViewById(R.id.preferences_button);
            if (preferences_button != null) {
                if (appNoticeData.getRic_click_manage_settings() != null)
                    preferences_button.setText(appNoticeData.getRic_click_manage_settings());
                preferences_button.setTextColor(appNoticeData.getBric_access_button_text_color());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    preferences_button.getBackground().setColorFilter(ric_access_button_color, PorterDuff.Mode.SRC);
//                    preferences_button.getBackground().setColorFilter(Color.parseColor(ric_access_button_color), PorterDuff.Mode.MULTIPLY);
//                    preferences_button.setBackgroundColor(Color.parseColor(ric_access_button_color));
                } else {
                    preferences_button.setBackgroundColor(ric_access_button_color);
                }
            }

            // Close button
            Button close_button = (Button)v.findViewById(R.id.close_button);
            if (close_button != null) {
                if (appNoticeData.getClose_button() != null)
                    close_button.setText(appNoticeData.getClose_button());
                close_button.setTextColor(appNoticeData.getBric_access_button_text_color());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    close_button.getBackground().setColorFilter(ric_access_button_color, PorterDuff.Mode.SRC);
//                    close_button.setBackgroundColor(Color.parseColor(ric_access_button_color));
                } else {
                    close_button.setBackgroundColor(ric_access_button_color);
                }
            }

        }
    }
}
