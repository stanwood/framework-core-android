package io.stanwood.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.stanwood.framework.ui.R;

public class StatusBarScrimLayout extends ViewGroup {
    private WindowInsetsCompat lastWindowInsets;
    private Drawable statusBarScrim;
    private Rect insets = new Rect();
    private Rect tmpRect = new Rect();

    public StatusBarScrimLayout(Context context) {
        this(context, null);
    }

    public StatusBarScrimLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarScrimLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.StatusBarScrimLayout, defStyle, 0);
        statusBarScrim = a.getDrawable(R.styleable.StatusBarScrimLayout_scrimColor);
        a.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            ViewCompat.setOnApplyWindowInsetsListener(this, new android.support.v4.view.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    return onWindowInsetChanged(insets);
                }
            });
        }

    }

    public void setStatusBarScrim(@Nullable Drawable statusBarScrim) {
        this.statusBarScrim = statusBarScrim;
    }

    public void onMeasureChild(View child, int parentWidthMeasureSpec, int widthUsed,
                               int parentHeightMeasureSpec, int heightUsed) {
        measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed,
                parentHeightMeasureSpec, heightUsed);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final boolean applyInsets = lastWindowInsets != null && getFitsSystemWindows();
        final int widthPadding = getPaddingLeft() + getPaddingRight();
        final int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthUsed = getSuggestedMinimumWidth();
        int heightUsed = getSuggestedMinimumHeight();
        int childState = 0;
        for (int size = getChildCount(), i = 0; i < size; i++) {
            View child = getChildAt(i);
            int childWidthMeasureSpec = widthMeasureSpec;
            int childHeightMeasureSpec = heightMeasureSpec;
            if (applyInsets && !child.getFitsSystemWindows()) {
                int horizInsets = lastWindowInsets.getSystemWindowInsetLeft()
                        + lastWindowInsets.getSystemWindowInsetRight();
                int vertInsets = lastWindowInsets.getSystemWindowInsetBottom();
                if (statusBarScrim != null) {
                    vertInsets += lastWindowInsets.getSystemWindowInsetTop();
                }
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        widthSize - horizInsets, widthMode);
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        heightSize - vertInsets, heightMode);
            } else if (applyInsets && child.getFitsSystemWindows()) {
                ViewCompat.dispatchApplyWindowInsets(child, lastWindowInsets);
            }
            onMeasureChild(child, childWidthMeasureSpec, 0, childHeightMeasureSpec, 0);
            final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            widthUsed = Math.max(widthUsed, widthPadding + child.getMeasuredWidth() +
                    lp.leftMargin + lp.rightMargin);

            heightUsed = Math.max(heightUsed, heightPadding + child.getMeasuredHeight() +
                    lp.topMargin + lp.bottomMargin);
            childState = View.combineMeasuredStates(childState, child.getMeasuredState());
        }
        final int width = View.resolveSizeAndState(widthUsed, widthMeasureSpec,
                childState & View.MEASURED_STATE_MASK);
        final int height = View.resolveSizeAndState(heightUsed, heightMeasureSpec,
                childState << View.MEASURED_HEIGHT_STATE_SHIFT);
        setMeasuredDimension(width, height);
    }

    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected MarginLayoutParams generateDefaultLayoutParams() {
        return new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat insetsCompat) {
        WindowInsetsCompat newInsets = null;
        if (getFitsSystemWindows()) {
            newInsets = insetsCompat;
        }
        if (lastWindowInsets != newInsets) {
            lastWindowInsets = newInsets;
            if (newInsets != null) {
                insets.set(insetsCompat.getSystemWindowInsetLeft(),
                        insetsCompat.getSystemWindowInsetTop(),
                        insetsCompat.getSystemWindowInsetRight(),
                        insetsCompat.getSystemWindowInsetBottom());
            } else {
                insets.set(0, 0, 0, 0);
            }
            setWillNotDraw(newInsets == null || statusBarScrim == null);
            requestLayout();
        }
        return insetsCompat;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (statusBarScrim != null && insets.top > 0) {
            int width = getWidth();
            int height = getHeight();
            int sc = canvas.save();
            canvas.translate(getScrollX(), getScrollY());
            // Top
            tmpRect.set(0, 0, width, insets.top);
            statusBarScrim.setBounds(tmpRect);
            statusBarScrim.draw(canvas);
            // Bottom
            tmpRect.set(0, height - insets.bottom, width, height);
            statusBarScrim.setBounds(tmpRect);
            statusBarScrim.draw(canvas);
            // Left
            tmpRect.set(0, insets.top, insets.left, height - insets.bottom);
            statusBarScrim.setBounds(tmpRect);
            statusBarScrim.draw(canvas);
            // Right
            tmpRect.set(width - insets.right, insets.top, width, height - insets.bottom);
            statusBarScrim.setBounds(tmpRect);
            statusBarScrim.draw(canvas);
            canvas.restoreToCount(sc);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (statusBarScrim != null) {
            statusBarScrim.setCallback(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (statusBarScrim != null) {
            statusBarScrim.setCallback(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final boolean applyInsets = lastWindowInsets != null && getFitsSystemWindows();
        for (int size = getChildCount(), i = 0; i < size; i++) {
            View child = getChildAt(i);
            int top = t;
            int left = l;
            if (applyInsets && !getFitsSystemWindows()) {
                if (statusBarScrim != null) {
                    top += insets.top;
                }
                left += insets.left;
            }
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
        }
    }
}