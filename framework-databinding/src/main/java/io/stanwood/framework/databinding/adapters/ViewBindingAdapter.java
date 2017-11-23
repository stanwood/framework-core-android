package io.stanwood.framework.databinding.adapters;

import android.databinding.BindingAdapter;
import android.view.View;

public class ViewBindingAdapter {
    @BindingAdapter(value = {"android:visibility"})
    public static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
