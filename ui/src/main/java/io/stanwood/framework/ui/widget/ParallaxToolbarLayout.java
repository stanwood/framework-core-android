package io.stanwood.framework.ui.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import io.stanwood.framework.ui.R;


public class ParallaxToolbarLayout extends FrameLayout {
    public static final int COLLAPSE_MODE_DEFAULT = 0;
    public static final int COLLAPSE_MODE_BELOW_TOOLBAR = 1;
    public static final int COLLAPSE_MODE_PIN = 2;
    public static final int TITLE_TRANSITON_MODE_FADE = 0;
    public static final int TITLE_TRANSITON_MODE_MOVE = 1;
    private final static long sScrimAnimationDuration = 400;
    private final int[] out = new int[]{0, 0};
    int currentOffset;
    WindowInsetsCompat lastInsets;
    Drawable statusBarScrim;
    Drawable contentScrim;
    private int maxTitleTranslationX;
    private float titleTranslationX = 0;
    private boolean refreshToolbar = true;
    private View toolbarLayout;
    private int scrimAlpha;
    private boolean scrimsAreShown;
    private ValueAnimator scrimAnimator;
    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
    private View floatingToolbar;
    private TextView floatingToolbarTitle;
    private View title;
    private int titleViewId;
    private int floatingToolbarId;
    private int floatingToolbarTitleId;

    public ParallaxToolbarLayout(Context context) {
        this(context, null);
    }

    public ParallaxToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParallaxToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ParallaxToolbarLayout, defStyleAttr, 0);
        setContentScrim(a.getDrawable(R.styleable.ParallaxToolbarLayout_scrim));
        setStatusBarScrim(a.getDrawable(R.styleable.ParallaxToolbarLayout_statusScrim));
        titleViewId = a.getResourceId(R.styleable.ParallaxToolbarLayout_titleTextView, 0);
        floatingToolbarId = a.getResourceId(R.styleable.ParallaxToolbarLayout_floatingToolbar, 0);
        floatingToolbarTitleId = a.getResourceId(R.styleable.ParallaxToolbarLayout_floatingToolbarTitleTextView, 0);
        a.recycle();
        setWillNotDraw(false);
        ViewCompat.setOnApplyWindowInsetsListener(this,
                new androidx.core.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                        return onWindowInsetChanged(insets);
                    }
                });
    }

    private static int getHeightWithMargins(@NonNull final View view) {
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof MarginLayoutParams) {
            final MarginLayoutParams mlp = (MarginLayoutParams) lp;
            return view.getHeight() + mlp.topMargin + mlp.bottomMargin;
        }
        return view.getHeight();
    }

    static ViewOffsetHelper getViewOffsetHelper(View view) {
        ViewOffsetHelper offsetHelper = (ViewOffsetHelper) view.getTag(R.id.offset_helper);
        if (offsetHelper == null) {
            offsetHelper = new ViewOffsetHelper(view);
            view.setTag(R.id.offset_helper, offsetHelper);
        }
        return offsetHelper;
    }

    public View getFloatingToolbar() {
        return floatingToolbar;
    }

    public TextView getFloatingToolbarTitle() {
        return floatingToolbarTitle;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewParent parent = getParent();
        if (parent instanceof AppBarLayout) {
            setFitsSystemWindows(ViewCompat.getFitsSystemWindows((View) parent));
            if (mOnOffsetChangedListener == null) {
                mOnOffsetChangedListener = new OffsetUpdateListener();
            }
            ((AppBarLayout) parent).addOnOffsetChangedListener(mOnOffsetChangedListener);
            ViewCompat.requestApplyInsets(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        final ViewParent parent = getParent();
        if (mOnOffsetChangedListener != null && parent instanceof AppBarLayout) {
            ((AppBarLayout) parent).removeOnOffsetChangedListener(mOnOffsetChangedListener);
        }
        super.onDetachedFromWindow();
    }

    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;
        if (ViewCompat.getFitsSystemWindows(this)) {
            newInsets = insets;
        }
        if (!(lastInsets == newInsets) || (lastInsets != null && lastInsets.equals(newInsets))) {
            lastInsets = newInsets;
            requestLayout();
        }
        return insets.consumeSystemWindowInsets();
    }

    private boolean isToolbarChild(View child) {
        return child == toolbarLayout;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        ensureToolbar();
        if (toolbarLayout == null && contentScrim != null && scrimAlpha > 0) {
            contentScrim.mutate().setAlpha(scrimAlpha);
            contentScrim.draw(canvas);
        }

        if (statusBarScrim != null && scrimAlpha > 0) {
            final int topInset = lastInsets != null ? lastInsets.getSystemWindowInsetTop() : 0;
            if (topInset > 0) {
                statusBarScrim.setBounds(0, -currentOffset, getWidth(), topInset - currentOffset);
                statusBarScrim.mutate().setAlpha(scrimAlpha);
                statusBarScrim.draw(canvas);
            }
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean invalidated = false;
        if (contentScrim != null && isToolbarChild(child)) {
            contentScrim.mutate().setAlpha(scrimAlpha);
            contentScrim.draw(canvas);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.getTitleTranstionMode() == TITLE_TRANSITON_MODE_FADE) {
                if (title != null) {
                    title.setAlpha(scrimAlpha / 255f);
                }
            } else if (lp.getTitleTranstionMode() == TITLE_TRANSITON_MODE_MOVE && maxTitleTranslationX > 0) {
                if (floatingToolbarTitle != null && title != null) {
                    float alpha = scrimAlpha / 255f;
                    title.setAlpha(alpha);
                    floatingToolbarTitle.setAlpha(1 - alpha);
                    floatingToolbarTitle.setTranslationX((1 - titleTranslationX) * maxTitleTranslationX);
                }
            }
            invalidated = true;
        }
        return super.drawChild(canvas, child, drawingTime) || invalidated;
    }

    public View getToolbarTitle() {
        ensureToolbar();
        return title;
    }

    private void ensureToolbar() {
        if (!refreshToolbar) {
            return;
        }
        toolbarLayout = getChildAt(getChildCount() - 1);
        refreshToolbar = false;
        if (titleViewId > 0) {
            title = findViewById(titleViewId);
        }
        if (floatingToolbarId > 0) {
            floatingToolbar = findViewById(floatingToolbarId);
        }
        if (floatingToolbarTitleId > 0) {
            floatingToolbarTitle = findViewById(floatingToolbarTitleId);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ensureToolbar();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LayoutParams lp = (LayoutParams) getChildAt(0).getLayoutParams();
        if (lp.getParallaxMode() == COLLAPSE_MODE_BELOW_TOOLBAR) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + toolbarLayout.getMeasuredHeight());
        }

        final int mode = MeasureSpec.getMode(heightMeasureSpec);
        final int topInset = lastInsets != null ? lastInsets.getSystemWindowInsetTop() : 0;
        if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
            // If we have a top inset and we're set to wrap_content height make sure top inset is added
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() + topInset, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (lastInsets != null) {
            // Shift down any views which are not set to fit system windows
            final int insetTop = lastInsets.getSystemWindowInsetTop();
            for (int i = 0, z = getChildCount(); i < z; i++) {
                final View child = getChildAt(i);
                if (!ViewCompat.getFitsSystemWindows(child)) {
                    if (child.getTop() < insetTop) {
                        ViewCompat.offsetTopAndBottom(child, insetTop);
                    }
                }
            }
        }
        if (floatingToolbarTitle != null) {
            title.getLocationOnScreen(out);
            int maxTranslationX = out[0];
            floatingToolbarTitle.getLocationOnScreen(out);
            maxTranslationX -= (out[0] - floatingToolbarTitle.getTranslationX());
            maxTitleTranslationX = maxTranslationX;
        }
        for (int size = getChildCount(), i = 0; i < size; i++) {
            getViewOffsetHelper(getChildAt(i)).onViewLayout();
        }
        if (toolbarLayout != null) {
            setMinimumHeight(getHeightWithMargins(toolbarLayout));
        }
        updateScrimVisibility();
    }

    public void setScrimsShown(boolean shown) {
        setScrimsShown(shown, ViewCompat.isLaidOut(this) && !isInEditMode());
    }

    public void setScrimsShown(boolean shown, boolean animate) {
        if (scrimsAreShown != shown) {
            if (animate) {
                animateScrim(shown ? 255 : 0);
            } else {
                setScrimAlpha(shown ? 255 : 0);
            }
            scrimsAreShown = shown;
        }
    }

    private void animateScrim(int targetAlpha) {
        ensureToolbar();
        if (scrimAnimator == null) {
            scrimAnimator = new ValueAnimator();
            scrimAnimator.setDuration(sScrimAnimationDuration);
            scrimAnimator.setInterpolator(
                    targetAlpha > scrimAlpha
                            ? new FastOutSlowInInterpolator()
                            : new LinearOutSlowInInterpolator());
            scrimAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    setScrimAlpha((int) animator.getAnimatedValue());
                }
            });
        } else if (scrimAnimator.isRunning()) {
            scrimAnimator.cancel();
        }

        scrimAnimator.setIntValues(scrimAlpha, targetAlpha);
        scrimAnimator.start();
    }

    void setScrimAlpha(int alpha) {
        ensureToolbar();
        if (alpha != scrimAlpha) {
            scrimAlpha = alpha;
            ViewCompat.postInvalidateOnAnimation(ParallaxToolbarLayout.this);
        }
    }

    public void setContentScrim(@Nullable Drawable drawable) {
        if (contentScrim != drawable) {
            if (contentScrim != null) {
                contentScrim.setCallback(null);
            }
            contentScrim = drawable != null ? drawable.mutate() : null;
            if (contentScrim != null) {
                contentScrim.setBounds(0, 0, getWidth(), getHeight());
                contentScrim.setCallback(this);
                contentScrim.setAlpha(scrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (contentScrim != null) {
            contentScrim.setBounds(0, 0, w, h);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        final int[] state = getDrawableState();
        boolean changed = false;

        Drawable d = statusBarScrim;
        if (d != null && d.isStateful()) {
            changed |= d.setState(state);
        }
        d = contentScrim;
        if (d != null && d.isStateful()) {
            changed |= d.setState(state);
        }
        if (changed) {
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == contentScrim;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        final boolean visible = visibility == VISIBLE;
        if (statusBarScrim != null && statusBarScrim.isVisible() != visible) {
            statusBarScrim.setVisible(visible, false);
        }
        if (contentScrim != null && contentScrim.isVisible() != visible) {
            contentScrim.setVisible(visible, false);
        }
    }

    public int getScrimVisibleHeightTrigger() {
        final int insetTop = lastInsets != null ? lastInsets.getSystemWindowInsetTop() : 0;
        final int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight > 0) {
            return Math.min((minHeight * 2) + insetTop, getHeight());
        }
        return getHeight() / 3;
    }

    final void updateScrimVisibility() {
        if (contentScrim != null || statusBarScrim != null) {
            setScrimsShown(getHeight() + currentOffset < getScrimVisibleHeightTrigger());
        }
    }

    final int getMaxOffsetForPinChild(View child) {
        final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return getHeight()
                - offsetHelper.getLayoutTop()
                - child.getHeight()
                - lp.bottomMargin;
    }

    @Nullable
    public Drawable getStatusBarScrim() {
        return statusBarScrim;
    }

    public void setStatusBarScrim(@Nullable Drawable drawable) {
        if (statusBarScrim != drawable) {
            if (statusBarScrim != null) {
                statusBarScrim.setCallback(null);
            }
            statusBarScrim = drawable != null ? drawable.mutate() : null;
            if (statusBarScrim != null) {
                if (statusBarScrim.isStateful()) {
                    statusBarScrim.setState(getDrawableState());
                }
                DrawableCompat.setLayoutDirection(statusBarScrim, ViewCompat.getLayoutDirection(this));
                statusBarScrim.setVisible(getVisibility() == VISIBLE, false);
                statusBarScrim.setCallback(this);
                statusBarScrim.setAlpha(scrimAlpha);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5f;
        int parallaxMode = COLLAPSE_MODE_DEFAULT;
        float parallaxMult = DEFAULT_PARALLAX_MULTIPLIER;
        int titleTranstionMode = TITLE_TRANSITON_MODE_FADE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.ParallaxToolbarLayout);
            parallaxMode = a.getInt(
                    R.styleable.ParallaxToolbarLayout_parallaxMode,
                    -1);
            titleTranstionMode = a.getInt(
                    R.styleable.ParallaxToolbarLayout_titleTransitionMode,
                    TITLE_TRANSITON_MODE_FADE);
            setParallaxMultiplier(a.getFloat(
                    R.styleable.ParallaxToolbarLayout_parallaxMultiplier,
                    DEFAULT_PARALLAX_MULTIPLIER));
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public int getTitleTranstionMode() {
            return titleTranstionMode;
        }

        public int getParallaxMode() {
            return parallaxMode;
        }

        public void setParallaxMode(int parallaxMode) {
            this.parallaxMode = parallaxMode;
        }

        public float getParallaxMultiplier() {
            return parallaxMult;
        }

        public void setParallaxMultiplier(float multiplier) {
            parallaxMult = multiplier;
        }


    }

    private class OffsetUpdateListener implements AppBarLayout.OnOffsetChangedListener {
        OffsetUpdateListener() {
        }

        @Override
        public void onOffsetChanged(AppBarLayout layout, int verticalOffset) {
            currentOffset = verticalOffset;

            for (int size = getChildCount(), i = 0; i < size; i++) {
                final View child = getChildAt(i);
                final ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int mode = lp.getParallaxMode();
                switch (mode) {
                    case COLLAPSE_MODE_DEFAULT: {
                        offsetHelper.setTopAndBottomOffset(
                                Math.round(-verticalOffset * lp.getParallaxMultiplier()));
                        break;
                    }
                    case COLLAPSE_MODE_BELOW_TOOLBAR: {
                        offsetHelper.setTopAndBottomOffset(
                                Math.round(-verticalOffset * lp.getParallaxMultiplier()) + toolbarLayout.getHeight());
                        break;
                    }
                    case COLLAPSE_MODE_PIN: {
                        offsetHelper.setTopAndBottomOffset(
                                constrain(-verticalOffset, 0, getMaxOffsetForPinChild(child)));
                        break;
                    }
                }
                updateScrimVisibility();
                final int insetTop = lastInsets != null ? lastInsets.getSystemWindowInsetTop() : 0;
                if (statusBarScrim != null && insetTop > 0) {
                    ViewCompat.postInvalidateOnAnimation(ParallaxToolbarLayout.this);
                }
                if (floatingToolbarTitle != null) {
                    int height = getHeight();
                    int minHeight = getMinimumHeight();
                    titleTranslationX = (float) (height - minHeight - insetTop + currentOffset) / (height - minHeight - insetTop);
                }
            }
        }
    }
}

