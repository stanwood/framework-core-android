package io.stanwood.framework.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.stanwood.framework.ui.R;


public class AspectRatioFrameLayout extends FrameLayout {

    final private static int RELATIVE_TO_WIDTH = 0;
    final private static int RELATIVE_TO_HEIGHT = 1;

    private int widthRelativeTo = -1;
    private float widthAspectRatio = 1;
    private int heightRelativeTo = -1;
    private float heightAspectRatio = -1;

    public AspectRatioFrameLayout(Context context) {
        this(context, null);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectRatioFrameLayout, 0, 0);
        widthRelativeTo = a.getInt(R.styleable.AspectRatioFrameLayout_width_relative_to, widthRelativeTo);
        widthAspectRatio = a.getFloat(R.styleable.AspectRatioFrameLayout_width_aspectRatio, widthAspectRatio);
        if (widthRelativeTo == -1) {
            heightRelativeTo = a.getInt(R.styleable.AspectRatioFrameLayout_height_relative_to, RELATIVE_TO_WIDTH);
            heightAspectRatio = a.getFloat(R.styleable.AspectRatioFrameLayout_height_aspectRatio, 1.77f);
        }
        a.recycle();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        if (widthRelativeTo == RELATIVE_TO_WIDTH) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width / widthAspectRatio), MeasureSpec.EXACTLY);
        } else if (widthRelativeTo == RELATIVE_TO_HEIGHT) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height / widthAspectRatio), MeasureSpec.EXACTLY);
        }
        if (heightRelativeTo == RELATIVE_TO_WIDTH) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width / heightAspectRatio), MeasureSpec.EXACTLY);
        } else if (heightRelativeTo == RELATIVE_TO_HEIGHT) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height / heightAspectRatio), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setWidthAspectRatio(float ratio) {
        widthAspectRatio = ratio;
    }

    public void setHeightAspectRatio(float ratio) {
        heightAspectRatio = ratio;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }
}