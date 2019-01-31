package io.stanwood.framework.databinding.adapters;

import android.databinding.BindingAdapter;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

public class ViewBindingAdapter {
    @BindingAdapter(value = {"android:visibility"})
    public static void setVisibility(View view, @Nullable Boolean visible) {
        view.setVisibility((visible != null && visible) ? View.VISIBLE : View.GONE);
    }

    /**
     * A binding adapter which sets the layout_width to wrap_content if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param width     the width if the condition is not met
     * @param condition if {@code true} the width will be set to wrap_content, if {@code false} the value of the width parameter is used
     */
    @BindingAdapter(value = {"android:layout_width", "layout_width_wrap_content_condition"})
    public static void setLayoutWidthWrapContentIfConditionMet(View view, float width, boolean condition) {
        if (condition) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (int) width;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * A binding adapter which sets the layout_height to wrap_content if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param height    the height if the condition is not met
     * @param condition if {@code true} the height will be set to wrap_content, if {@code false} the value of the height parameter is used
     */
    @BindingAdapter(value = {"android:layout_height", "layout_height_wrap_content_condition"})
    public static void setLayoutHeightWrapContentIfConditionMet(View view, float height, boolean condition) {
        if (condition) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) height;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * A binding adapter which sets the layout_width to match_parent if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param width     the width if the condition is not met
     * @param condition if {@code true} the width will be set to match_parent, if {@code false} the value of the width parameter is used
     */
    @BindingAdapter(value = {"android:layout_width", "layout_width_match_parent_condition"})
    public static void setLayoutWidthMatchParentIfConditionMet(View view, float width, boolean condition) {
        if (condition) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = (int) width;
            view.setLayoutParams(layoutParams);
        }
    }

    /**
     * A binding adapter which sets the layout_height to match_parent if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param height    the height if the condition is not met
     * @param condition if {@code true} the height will be set to match_parent, if {@code false} the value of the height parameter is used
     */
    @BindingAdapter(value = {"android:layout_height", "layout_height_match_parent_condition"})
    public static void setLayoutHeightMatchParentIfConditionMet(View view, float height, boolean condition) {
        if (condition) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            view.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = (int) height;
            view.setLayoutParams(layoutParams);
        }
    }
}
