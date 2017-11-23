package io.stanwood.framework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.stanwood.framework.ui.R;


public class AspectRatioFrameLayout extends FrameLayout {
    final public static int RELATIVE_TO_NONE = -1;
    final public static int RELATIVE_TO_WIDTH = 0;
    final public static int RELATIVE_TO_HEIGHT = 1;
    @RelativeTo
    private int widthRelativeTo = RELATIVE_TO_NONE;
    private float widthAspectRatio = 1;
    @RelativeTo
    private int heightRelativeTo = RELATIVE_TO_NONE;
    private float heightAspectRatio = 1.77f;

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
        if (widthRelativeTo == RELATIVE_TO_NONE) {
            heightRelativeTo = a.getInt(R.styleable.AspectRatioFrameLayout_height_relative_to, heightRelativeTo);
            heightAspectRatio = a.getFloat(R.styleable.AspectRatioFrameLayout_height_aspectRatio, heightAspectRatio);
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

    public int getIsRelativeTo() {
        return heightRelativeTo;
    }

    public float getHeightAspectRatio() {
        return heightAspectRatio;
    }

    public void setHeightAspectRatio(float ratio) {
        this.heightAspectRatio = ratio;
    }

    public float getWidthAspectRatio() {
        return widthAspectRatio;
    }

    public void setWidthAspectRatio(float ratio) {
        this.widthAspectRatio = ratio;
    }

    @RelativeTo
    public int getHeightRelativeTo() {
        return heightRelativeTo;
    }

    public void setHeightRelativeTo(@RelativeTo int relativeTo) {
        this.heightRelativeTo = relativeTo;
    }

    @RelativeTo
    public int getWidthRelativeTo() {
        return widthRelativeTo;
    }

    public void setWidthRelativeTo(@RelativeTo int relativeTo) {
        this.widthRelativeTo = relativeTo;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATIVE_TO_NONE, RELATIVE_TO_WIDTH, RELATIVE_TO_HEIGHT})
    public @interface RelativeTo {
    }
}