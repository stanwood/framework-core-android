package io.stanwood.framework.databinding.recyclerview;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class ObservableListBindingAdapter<T> extends BaseBindingAdapter<T> {

    @NonNull
    private final WeakReferenceOnListChangedCallback<T> listChangedCallback;

    public ObservableListBindingAdapter(@NonNull LayoutInflater inflater) {
        super(inflater);
        this.listChangedCallback = new WeakReferenceOnListChangedCallback<>(this);
    }


    @Override
    public void setItems(@Nullable List<T> items) {
        if (this.items == items) {
            return;
        }
        if (this.items != null) {
            if (items instanceof ObservableList) {
                ((ObservableList<T>) items).removeOnListChangedCallback(listChangedCallback);
            }
            notifyItemRangeRemoved(0, this.getItemCount());
        }
        super.setItems(items);
        if (items != null) {
            notifyItemRangeInserted(0, items.size());
            if (items instanceof ObservableList) {
                ((ObservableList<T>) items).addOnListChangedCallback(listChangedCallback);
            }
        }
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (items != null && items instanceof ObservableList) {
            ((ObservableList<T>) items).removeOnListChangedCallback(listChangedCallback);
        }
    }

    private static class WeakReferenceOnListChangedCallback<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {

        private final WeakReference<ObservableListBindingAdapter> adapterReference;

        public WeakReferenceOnListChangedCallback(ObservableListBindingAdapter bindingRecyclerViewAdapter) {
            this.adapterReference = new WeakReference<>(bindingRecyclerViewAdapter);
        }

        @Override
        public void onChanged(ObservableList sender) {
            RecyclerView.Adapter adapter = adapterReference.get();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
            RecyclerView.Adapter adapter = adapterReference.get();
            if (adapter != null) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
            RecyclerView.Adapter adapter = adapterReference.get();
            if (adapter != null) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {
            RecyclerView.Adapter adapter = adapterReference.get();
            if (adapter != null) {
                adapter.notifyItemMoved(fromPosition, toPosition);
            }
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
            RecyclerView.Adapter adapter = adapterReference.get();
            if (adapter != null) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        }
    }
}