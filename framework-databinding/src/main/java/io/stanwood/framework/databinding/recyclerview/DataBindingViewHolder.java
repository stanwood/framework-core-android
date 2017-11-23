package io.stanwood.framework.databinding.recyclerview;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class DataBindingViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public V binding;

    public DataBindingViewHolder(V binding) {
        super(binding.getRoot());
        this.binding = binding;
    }


    public void onViewDetachedFromWindow() {
    }

    public void onViewAttachedToWindow() {

    }

    public void onViewRecycled() {
    }
}