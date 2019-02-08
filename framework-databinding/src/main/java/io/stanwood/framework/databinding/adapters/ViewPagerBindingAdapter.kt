package io.stanwood.framework.databinding.adapters

import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.viewpager.widget.ViewPager
import io.stanwood.framework.databinding.R

object ViewPagerBindingAdapter {

    @JvmStatic
    @BindingAdapter("onPageScrolled")
    fun setListener(view: ViewPager, pageScrolled: OnPageScrolled?) {
        setListener(view, pageScrolled, null, null)
    }

    @JvmStatic
    @BindingAdapter("onPageSelected")
    fun setListener(view: ViewPager, pageSelected: OnPageSelected?) {
        setListener(view, null, pageSelected, null)
    }

    @JvmStatic
    @BindingAdapter("onPageScrollStateChanged")
    fun setListener(view: ViewPager, scrollStateChanged: OnPageScrollStateChanged?) {
        setListener(view, null, null, scrollStateChanged)
    }

    @JvmStatic
    @BindingAdapter("onPageScrolled", "onPageSelected", "onPageScrollStateChanged")
    fun setListener(
        view: ViewPager,
        pageScrolled: OnPageScrolled?,
        pageSelected: OnPageSelected?,
        pageScrollStateChanged: OnPageScrollStateChanged?
    ) {
        val newListener: ViewPager.OnPageChangeListener?
        if (pageScrolled == null && pageSelected == null && pageScrollStateChanged == null) {
            newListener = null
        } else {
            newListener = object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    pageScrolled?.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    pageSelected?.onPageSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    pageScrollStateChanged?.onPageScrollStateChanged(state)

                }
            }
        }
        ListenerUtil.trackListener<ViewPager.OnPageChangeListener>(view, newListener, R.id.onPageChangeListener)
            ?.apply {
                view.removeOnPageChangeListener(this)
            }
        newListener?.apply {
            view.addOnPageChangeListener(this)
        }
    }

    interface OnPageScrolled {
        fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int)
    }

    interface OnPageSelected {
        fun onPageSelected(position: Int)
    }

    interface OnPageScrollStateChanged {
        fun onPageScrollStateChanged(state: Int)
    }
}