package io.stanwood.framework.databinding.adapters

import android.view.View
import androidx.databinding.BindingAdapter

@set:BindingAdapter("android:visibility")
var View.visibleOrGone
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

@set:BindingAdapter("visible")
var View.visibleOrInvisible
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }