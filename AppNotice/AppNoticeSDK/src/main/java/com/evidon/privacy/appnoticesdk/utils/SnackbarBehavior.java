package com.evidon.privacy.appnoticesdk.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Steven.Overson on 7/8/2016.
 */
public class SnackbarBehavior extends CoordinatorLayout.Behavior<LinearLayout> {
    private static final String TAG = "SnackbarBehavior";
    int originalParentHeight = 0;

    public SnackbarBehavior(Context context, AttributeSet attrs) {
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {

        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {

        int snackbarHeight = dependency.getHeight();
        float translationYFloat = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        int translationYInt = Math.round(-translationYFloat);
        child.setPadding(0, 0, 0, translationYInt);
        Log.d(TAG, "translationYFloat = " + translationYFloat + "(" + translationYInt + ")");
        return true;
    }

}
