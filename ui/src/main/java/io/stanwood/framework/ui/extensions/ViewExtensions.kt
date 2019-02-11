package io.stanwood.framework.ui.extensions

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.forEach


fun View.setApplyWindowInsetsToChild() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        (v as? ViewGroup)?.forEach { ViewCompat.dispatchApplyWindowInsets(it, insets) }
        insets
    }
}

fun View.showKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }
}

fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
        hideSoftInputFromWindow(windowToken, 0)
    }
}