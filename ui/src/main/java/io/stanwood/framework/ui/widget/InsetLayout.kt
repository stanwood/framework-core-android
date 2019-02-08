package io.stanwood.framework.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import io.stanwood.framework.ui.R

class InsetLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
    private var insets: WindowInsetsCompat? = null
    private val statusBarBounds = Rect()
    private val statusBarPaint: Paint?

    init {
        fitsSystemWindows = true
        val style = context.obtainStyledAttributes(attrs, R.styleable.InsetLayout)
        statusBarPaint = style.getColor(R.styleable.InsetLayout_statusBarColor, 0)
            .let {
                if (it > 0) Paint().apply { color = it } else null
            }
        style.recycle()
        ViewCompat.setOnApplyWindowInsetsListener(this)
        { _, insets ->
            this.insets = WindowInsetsCompat(insets)
            onInsetsChanged(insets)
            insets.consumeSystemWindowInsets()
        }
    }

    private fun onInsetsChanged(insets: WindowInsetsCompat) {
        statusBarBounds.bottom = insets.systemWindowInsetTop
        setWillNotDraw(statusBarBounds.height() <= 0 && background == null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        getChildAt(0)?.let {
            setMeasuredDimension(it.measuredWidth,
                if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                    MeasureSpec.getSize(heightMeasureSpec).apply {
                        it.measure(
                            widthMeasureSpec,
                            MeasureSpec.makeMeasureSpec(this - statusBarBounds.height(), MeasureSpec.EXACTLY)
                        )
                    }
                } else {
                    it.measure(widthMeasureSpec, heightMeasureSpec)
                    it.measuredHeight + statusBarBounds.height()
                })
        }
        statusBarBounds.right = measuredWidth
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (statusBarPaint != null && statusBarBounds.height() > 0) {
            canvas?.drawRect(statusBarBounds, statusBarPaint)
        }
    }
}