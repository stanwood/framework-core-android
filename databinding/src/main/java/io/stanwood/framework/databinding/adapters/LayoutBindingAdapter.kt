package io.stanwood.framework.databinding.adapters

import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter

object LayoutBindingAdapter {
    /**
     * A binding adapter which sets the layout_width to wrap_content if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param width     the width if the condition is not met
     * @param condition if `true` the width will be set to wrap_content, if `false` the value of the width parameter is used
     */
    @JvmStatic
    @BindingAdapter(value = ["android:layout_width", "layout_width_wrap_content_condition"])
    fun setLayoutWidthWrapContentIfConditionMet(view: View, width: Float, condition: Boolean) {
        view.layoutParams = if (condition) {
            view.layoutParams?.apply {
                this.width = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        } else {
            view.layoutParams.apply {
                this.width = width.toInt()
            }
        }
    }

    /**
     * A binding adapter which sets the layout_height to wrap_content if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param height    the height if the condition is not met
     * @param condition if `true` the height will be set to wrap_content, if `false` the value of the height parameter is used
     */
    @JvmStatic
    @BindingAdapter(value = ["android:layout_height", "layout_height_wrap_content_condition"])
    fun setLayoutHeightWrapContentIfConditionMet(view: View, height: Float, condition: Boolean) {
        view.layoutParams = if (condition) {
            view.layoutParams?.apply {
                this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        } else {
            view.layoutParams.apply {
                this.height = height.toInt()
            }
        }
    }

    /**
     * A binding adapter which sets the layout_width to match_parent if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param width     the width if the condition is not met
     * @param condition if `true` the width will be set to match_parent, if `false` the value of the width parameter is used
     */
    @JvmStatic
    @BindingAdapter(value = ["android:layout_width", "layout_width_match_parent_condition"])
    fun setLayoutWidthMatchParentIfConditionMet(view: View, width: Float, condition: Boolean) {
        view.layoutParams = if (condition) {
            view.layoutParams?.apply {
                this.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        } else {
            view.layoutParams.apply {
                this.width = width.toInt()
            }
        }
    }

    /**
     * A binding adapter which sets the layout_height to match_parent if the condition is met.
     *
     * @param view      the View this adapter is applied to
     * @param height    the height if the condition is not met
     * @param condition if `true` the height will be set to match_parent, if `false` the value of the height parameter is used
     */
    @JvmStatic
    @BindingAdapter(value = ["android:layout_height", "layout_height_match_parent_condition"])
    fun setLayoutHeightMatchParentIfConditionMet(view: View, height: Float, condition: Boolean) {
        view.layoutParams = if (condition) {
            view.layoutParams?.apply {
                this.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
        } else {
            view.layoutParams.apply {
                this.height = height.toInt()
            }
        }
    }
}