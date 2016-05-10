package com.ghostery.privacy.appnoticesdk.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.ghostery.privacy.appnoticesdk.R;
import com.ghostery.privacy.appnoticesdk.callbacks.AppNotice_Callback;
import com.ghostery.privacy.appnoticesdk.model.AppNoticeData;
import com.ghostery.privacy.appnoticesdk.utils.Session;
import com.ghostery.privacy.appnoticesdk.utils.Util;

public class ImpliedInfo_DialogFragment extends DialogFragment {
    private static final String TAG = "ImpliedInfo_Dialog";
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
        AppCompatButton preferences_button = (AppCompatButton)view.findViewById(R.id.preferences_button);
        preferences_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Remember that the tracker preferences screen was opened from a consent flow dialog
                Session.set(Session.APPNOTICE_PREF_OPENED_FROM_DIALOG, true);

                // Send notice for this event
                AppNoticeData.sendNotice(AppNoticeData.NoticeType.IMPLIED_INFO_PREF);

                // Let the calling class know the the manage preferences button was clicked
                Boolean wasHandled = false;
                if (appNotice_callback != null && !getActivity().isFinishing()) {
                    wasHandled = appNotice_callback.onManagePreferencesClicked();
                }

                // Open the App Notice Consent preferences fragmentActivity
                if (!wasHandled) {
                    Util.showManagePreferences(getActivity());
                }
            }
        });

        AppCompatButton close_button = (AppCompatButton)view.findViewById(R.id.close_button);
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

    @SuppressWarnings("deprecation")    // This is for pre-Android-M getColorStateList which is deprecated in M (level 23)
    private void applyCustomConfig(View v) {
        // Set custom config values from the appNoticeData object
        if (appNoticeData == null || !appNoticeData.isInitialized()) {
            // This handles a rare case where the app object has been killed, but the SDK activity continues to run.
            // This forces the app to restart in a way that the SDK gets properly initialized.
            // TODO: Should this be a callback to the host app?
            Log.d(TAG, "Force restart the host app to correctly init the SDK.");
            Util.forceAppRestart(getActivity());
        } else {
            LinearLayout linearLayout_outer = (LinearLayout)v.findViewById(R.id.linearLayout_outer);

            int ric_bg = appNoticeData.getDialogBackgroundColor();
            int ric_access_button_color = appNoticeData.getDialogButtonColor();


            // Set background color and opacity
            if (linearLayout_outer != null) {
                linearLayout_outer.setBackgroundColor(ric_bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    float opacityFloat = appNoticeData.getConsentFlowDialogOpacity();
                    if (opacityFloat < 1F && opacityFloat >= 0) {
                        Drawable d = new ColorDrawable(Color.BLACK);
                        d.setAlpha((int)(255 * opacityFloat));
                        getDialog().getWindow().setBackgroundDrawable(d);
                        linearLayout_outer.setAlpha(opacityFloat);
                    }
                }
            }

            // Title
            AppCompatTextView textView_title = (AppCompatTextView)v.findViewById(R.id.textView_title);
            if (textView_title != null) {
                if (appNoticeData.getDialogHeaderText() != null)
                    textView_title.setText(appNoticeData.getDialogHeaderText());
                textView_title.setTextColor(appNoticeData.getDialogHeaderTextColor());
            }

            // Message
            AppCompatTextView textView_message = (AppCompatTextView)v.findViewById(R.id.textView_message);
            if (textView_message != null) {
                if (appNoticeData.getDialogImplicitMessage() != null)
                    textView_message.setText(appNoticeData.getDialogImplicitMessage());
                textView_message.setTextColor(appNoticeData.getDialogMessageTextColor());
            }

            Context context = this.getActivity();
            Resources resources = context.getResources();

            // Preferences button
            AppCompatButton preferences_button = (AppCompatButton)v.findViewById(R.id.preferences_button);
            if (preferences_button != null) {
                if (appNoticeData.getDialogButtonPreferences() != null) {
                    preferences_button.setText(appNoticeData.getDialogButtonPreferences());
                }
                preferences_button.setTextColor(appNoticeData.getDialogExplicitAcceptButtonTextColor());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    preferences_button.setSupportBackgroundTintList(resources.getColorStateList(R.color.ghostery_dialog_button_color, context.getTheme()));
                } else {
                    preferences_button.setSupportBackgroundTintList(resources.getColorStateList(R.color.ghostery_dialog_button_color));
                }
            }

            // Close button
            AppCompatButton close_button = (AppCompatButton)v.findViewById(R.id.close_button);
            if (close_button != null) {
                if (appNoticeData.getDialogButtonClose() != null) {
                    close_button.setText(appNoticeData.getDialogButtonClose());
                }
                close_button.setTextColor(appNoticeData.getDialogExplicitAcceptButtonTextColor());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    close_button.setSupportBackgroundTintList(resources.getColorStateList(R.color.ghostery_dialog_button_color, context.getTheme()));
                } else {
                    close_button.setSupportBackgroundTintList(resources.getColorStateList(R.color.ghostery_dialog_button_color));
                }
            }

        }
    }
}
