package io.stanwood.framework.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter

object ViewBindingAdapter {
    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setVisibileOrGone(view: View, value: Boolean?) {
        view.visibility = if (value == true) View.VISIBLE else View.GONE
    }

    @BindingAdapter("visibleOrInvisible")
    @JvmStatic
    fun setVisibleOrInvisible(view: View, value: Boolean?) {
        view.visibility = if (value == true) View.VISIBLE else View.INVISIBLE
    }
}