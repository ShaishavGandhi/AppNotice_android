//package com.ghostery.privacy.inappconsentsdk.fragments;
//
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.ghostery.privacy.inappconsentsdk.callbacks.InAppConsent_Callback;
//import com.ghostery.privacy.inappconsentsdk.R;
//import com.ghostery.privacy.inappconsentsdk.utils.TrackerConfig;
//
//public class ImpliedIntro_DialogFragment extends DialogFragment {
//    private int mNum;
//    private TrackerConfig trackerConfig;
//    private boolean useRemoteValues = true;
//    private InAppConsent_Callback inAppConsent_callback;
//
//    /**
//     * Create a new instance of MyDialogFragment, providing "num"
//     * as an argument.
//     */
//    public static ImpliedIntro_DialogFragment newInstance(int num) {
//        ImpliedIntro_DialogFragment f = new ImpliedIntro_DialogFragment();
//
//        // Supply num input as an argument.
//        Bundle args = new Bundle();
//        args.putInt("num", num);
//        f.setArguments(args);
//
//        return f;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//
//        // request a window without the title
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//
//        return dialog;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        View v = inflater.inflate(R.layout.ghostery_impliedintro_dialogfragment, container, false);
//        View tv = v.findViewById(R.id.textView_introText);
//
//        // Apply the tracker config customizations
//        if (useRemoteValues)
//            applyTrackerConfig(v);
//
//        // Watch for button clicks.
//        Button learnMore_button = (Button)v.findViewById(R.id.learnmore_button);
//        learnMore_button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//
//                // Create and show the dialog.
//                ImpliedInfo_DialogFragment impliedInfo_DialogFragment = ImpliedInfo_DialogFragment.newInstance(0);
//                impliedInfo_DialogFragment.setTrackerConfig(trackerConfig);
//                impliedInfo_DialogFragment.setInAppConsent_Callback(inAppConsent_callback);
//                impliedInfo_DialogFragment.setUseRemoteValues(useRemoteValues);
//                impliedInfo_DialogFragment.show(ft, "dialog_fragment_implicitInfo");
//
//                // Send notice for this event
//                TrackerConfig.sendNotice(TrackerConfig.NoticeType.IMPLICIT_INTRO_LEARN);
//
//                // Close this dialog
//                dismiss();
//            }
//        });
//
//        Button close_button = (Button)v.findViewById(R.id.close_button);
//        close_button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Let the calling class know the selected option
//                if (inAppConsent_callback != null)
//                    inAppConsent_callback.onOptionSelected(true);
//
//                // Close this dialog
//                dismiss();
//            }
//        });
//
//        return v;
//    }
//
//    @Override
//    public void onResume() {
////        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
////        params.width = WindowManager.LayoutParams.MATCH_PARENT;
////        //params.height = WindowManager.LayoutParams.MATCH_PARENT;
////        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
//
//        super.onResume();
//    }
//
//    @Override
//    public void onCancel (DialogInterface dialog) {
//        // User cancelled the dialog...negating consent
//
//        // Let the calling class know the selected option
//        if (inAppConsent_callback != null)
//            inAppConsent_callback.onOptionSelected(true);
//    }
//
//    public void setTrackerConfig(TrackerConfig trackerConfig) {
//        this.trackerConfig = trackerConfig;
//    }
//
//    public void setUseRemoteValues(boolean useRemoteValues) {
//        this.useRemoteValues = useRemoteValues;
//    }
//
//    public void setInAppConsent_Callback(InAppConsent_Callback inAppConsent_callback) {
//        this.inAppConsent_callback = inAppConsent_callback;
//    }
//
//    private void applyTrackerConfig(View v) {
//        // Set custom config values from the trackerConfig object
//        if (trackerConfig != null && trackerConfig.isInitialized()) {
//            LinearLayout linearLayout_outer = (LinearLayout) v.findViewById(R.id.linearLayout_outer);
//
//            // Set background color and opacity
//            if (linearLayout_outer != null) {
//                if (trackerConfig.getRic_bg() != null)
//                    linearLayout_outer.setBackgroundColor(Color.parseColor(trackerConfig.getRic_bg()));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                    float opacityFloat = trackerConfig.getRic_opacity();
//                    if (opacityFloat < 1F && opacityFloat >= 0) {
//                        Drawable d = new ColorDrawable(Color.BLACK);
//                        d.setAlpha((int)(255 * opacityFloat));
//                        getDialog().getWindow().setBackgroundDrawable(d);
//                        linearLayout_outer.setAlpha(opacityFloat);
//                    }
//                }
//            }
//
//            // Intro text
//            TextView textView_introText = (TextView) v.findViewById(R.id.textView_introText);
//            if (textView_introText != null) {
//                if (trackerConfig.getRic_intro() != null)
//                    textView_introText.setText(trackerConfig.getRic_intro());
//                if (trackerConfig.getRic_color() != null)
//                    textView_introText.setTextColor(Color.parseColor(trackerConfig.getRic_color()));
//            }
//
//            // Learn More button
//            Button learnmore_button = (Button) v.findViewById(R.id.learnmore_button);
//            if (learnmore_button != null) {
//                if (trackerConfig.getRic_learn_more() != null)
//                    learnmore_button.setText(trackerConfig.getRic_learn_more());
//                if (trackerConfig.getRic_color() != null)
//                    learnmore_button.setTextColor(Color.parseColor(trackerConfig.getRic_color()));
//            }
//
//            // Close button
//            Button close_button = (Button) v.findViewById(R.id.close_button);
//            if (close_button != null) {
//                if (trackerConfig.getClose_button() != null)
//                    close_button.setText(trackerConfig.getClose_button());
//                if (trackerConfig.getRic_color() != null)
//                    close_button.setTextColor(Color.parseColor(trackerConfig.getRic_color()));
//            }
//        }
//    }
//}
