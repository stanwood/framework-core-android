
package io.stanwood.framework.databinding.recyclerview;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseBindingAdapter<T> extends RecyclerView.Adapter<DataBindingViewHolder> {

    @Nullable
    protected List<T> items;
    @NonNull
    private LayoutInflater inflater;

    public BaseBindingAdapter(@NonNull LayoutInflater inflater) {
        this.inflater = inflater;
        this.setItems(items);
    }

    public void setItems(@Nullable List<T> items) {
        this.items = items;
    }

    @Override
    public final void onBindViewHolder(DataBindingViewHolder holder, int position) {
        throw new IllegalArgumentException("just overridden to make final.");
    }

    @Override
    public DataBindingViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        return onCreateViewHolder(inflater, viewGroup, type);
    }

    public abstract DataBindingViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup viewGroup, int type);

    @Override
    public final void onBindViewHolder(DataBindingViewHolder holder, int position,
                                       List<Object> payloads) {
        bindItem(holder, position, items.get(position), payloads);
        holder.binding.executePendingBindings();
    }

    /**
     * Override this method to handle binding your items into views
     *
     * @param holder   The ViewHolder that has the binding instance
     * @param position The position of the item in the adapter
     * @param payloads The payloads that were passed into the onBind method
     */
    protected abstract void bindItem(DataBindingViewHolder holder, int position, T item,
                                     List<Object> payloads);

    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, items.get(position));
    }

    public int getItemViewType(int position, T item) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public void onViewRecycled(DataBindingViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onViewRecycled();

    }

    @Override
    public void onViewDetachedFromWindow(DataBindingViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onViewDetachedFromWindow();
    }

    @Override
    public void onViewAttachedToWindow(DataBindingViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttachedToWindow();
    }
}