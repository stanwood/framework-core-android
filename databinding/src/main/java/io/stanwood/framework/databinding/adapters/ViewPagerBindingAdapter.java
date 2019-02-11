package io.stanwood.framework.databinding.adapters;

import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.ListenerUtil;
import androidx.viewpager.widget.ViewPager;
import io.stanwood.framework.databinding.R;

public class ViewPagerBindingAdapter {

    @BindingAdapter("onPageScrolled")
    public static void setListener(ViewPager view, OnPageScrolled pageScrolled) {
        setListener(view, pageScrolled, null, null);
    }

    @BindingAdapter("onPageSelected")
    public static void setListener(ViewPager view, OnPageSelected pageSelected) {
        setListener(view, null, pageSelected, null);
    }

    @BindingAdapter("onPageScrollStateChanged")
    public static void setListener(ViewPager view, OnPageScrollStateChanged scrollStateChanged) {
        setListener(view, null, null, scrollStateChanged);
    }

    @BindingAdapter({"onPageScrolled", "onPageSelected", "onPageScrollStateChanged"})
    public static void setListener(ViewPager view, final OnPageScrolled pageScrolled,
                                   final OnPageSelected pageSelected, final OnPageScrollStateChanged pageScrollStateChanged) {
        final ViewPager.OnPageChangeListener newListener;
        if (pageScrolled == null && pageSelected == null && pageScrollStateChanged == null) {
            newListener = null;
        } else {
            newListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (pageScrolled != null) {
                        pageScrolled.onPageScrolled(position, positionOffset, positionOffsetPixels);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    if (pageSelected != null) {
                        pageSelected.onPageSelected(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (pageScrollStateChanged != null) {
                        pageScrollStateChanged.onPageScrollStateChanged(state);
                    }

                }
            };
        }
        final ViewPager.OnPageChangeListener oldListener = ListenerUtil.trackListener(view,
                newListener, R.id.onPageChangeListener);
        if (oldListener != null) {
            view.removeOnPageChangeListener(oldListener);
        }
        if (newListener != null) {
            view.addOnPageChangeListener(newListener);
        }

    }

    public interface OnPageScrolled {
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
    }

    public interface OnPageSelected {
        void onPageSelected(int position);
    }

    public interface OnPageScrollStateChanged {
        void onPageScrollStateChanged(int state);
    }

}