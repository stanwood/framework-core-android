package io.stanwood.framework.ui.widget


import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import io.stanwood.framework.ui.R


class InsetLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, defStyleRes: Int = 0) :
    FrameLayout(context, attrs, defStyle, defStyleRes) {
    private var lastInsets: WindowInsetsCompat? = null
    private var statusBarBackground: Drawable? = null

    init {
        fitsSystemWindows = true
        context.obtainStyledAttributes(attrs, R.styleable.InsetLayout, defStyle, defStyleRes)
            ?.apply {
                statusBarBackground = getDrawable(R.styleable.InsetLayout_statusBarBackground)
                recycle()
            }
        ViewCompat.setOnApplyWindowInsetsListener(this)
        { _, insets ->
            onInsetsChanged(insets)
            insets
        }
        systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun onInsetsChanged(insets: WindowInsetsCompat) {
        if (lastInsets != insets) {
            lastInsets = insets
            setWillNotDraw(insets.systemWindowInsetTop <= 0 && background == null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val inset = lastInsets?.systemWindowInsetTop ?: 0
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            forEach { it.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height - inset, MeasureSpec.EXACTLY)) }
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
        } else {
            if (childCount > 1) {
                throw IllegalStateException("Only one child if height == wrap_content")
            }
            getChildAt(0)?.let {
                it.measure(widthMeasureSpec, heightMeasureSpec)
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), it.measuredHeight + inset)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        lastInsets?.apply {
            statusBarBackground?.apply {
                setBounds(0, 0, width, systemWindowInsetTop)
                draw(canvas)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (lastInsets == null) {
            ViewCompat.requestApplyInsets(this)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.BOTTOM)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    class LayoutParams : FrameLayout.LayoutParams {

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {
            if (gravity == UNSPECIFIED_GRAVITY) {
                gravity = Gravity.BOTTOM
            }
        }

        constructor(width: Int, height: Int, gravity: Int) : super(width, height, gravity)

        constructor(width: Int, height: Int) : super(width, height) {}

        constructor(source: ViewGroup.LayoutParams) : super(source) {}

    }
}