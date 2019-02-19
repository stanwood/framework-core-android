package io.stanwood.framework.ui.widget


import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        getChildAt(0)?.let {
            val inset = lastInsets?.systemWindowInsetTop ?: 0
            setMeasuredDimension(it.measuredWidth,
                if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
                    MeasureSpec.getSize(heightMeasureSpec).apply {
                        it.measure(
                            widthMeasureSpec,
                            MeasureSpec.makeMeasureSpec(this - inset, MeasureSpec.EXACTLY)
                        )
                    }
                } else {
                    it.measure(widthMeasureSpec, heightMeasureSpec)
                    it.measuredHeight + inset
                })
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
}