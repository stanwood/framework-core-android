package io.stanwood.framework.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Input {

    public static void hideSoftInput(@NonNull Activity activity) {
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            focusedView.clearFocus();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }
        }
    }
}