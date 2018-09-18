package com.moselo.HomingPigeon.View.Adapter;

import android.support.v7.widget.RecyclerView;

import com.moselo.HomingPigeon.Helper.BaseViewHolder;

import java.util.List;

/**
 * Created by Fadhlan on 8/2/16.
 *
 * @see {https://gist.github.com/aurae/ebf8ec212e4296aebb24}
 */
public abstract class BaseAdapter<T, VH extends BaseViewHolder<T>> extends
        RecyclerView.Adapter<VH> {
    private List<T> items;

    @Override
    public void onBindViewHolder(VH vh, int position) {
        T item = items.get(position);
        vh.performBind(item, position);
    }

    public List<T> getItems() {
        return items;
    }

    public T getItemAt(int position) {
        return items.get(position);
    }

    final public void setItemAt(int index, T t) {
        items.set(index, t);
    }

    final public void setItems(List<T> items, boolean notifyRangeChange) {
        this.items = items;
        if (notifyRangeChange) notifyItemRangeChanged(0, getItemCount());
        else notifyDataSetChanged();
    }

    final public void updateItem(T item, int index) {
        getItems().set(index, item);
    }

    final public void updateNotify(T item, int index) {
        updateItem(item, index);
        notifyItemChanged(index);
    }

    final public void addItem(T item, boolean isNotify) {
        this.addItem(item);
        if (isNotify) notifyInserted(item);
    }

    final public void addItem(T item) {
        this.items.add(item);
        notifyItemInserted(this.items.size() - 1);
    }

    final public void addItem(int index, T item) {
        this.items.add(index, item);
        notifyItemInserted(0);
    }

    final public void addItem(List<T> items, boolean isNotify) {
        this.addItem(items);
        if (isNotify) notifyInserted(items);
    }

    final public void addItem(List<T> items) {
        this.items.addAll(items);
    }

    private <T> void notifyInserted(T t) {
        if (t == null) return;
        else if (t instanceof List) {
            notifyItemRangeInserted(this.items.size() - ((List) t).size(), ((List) t).size());
        } else
            notifyItemInserted(this.items.size() - 1);
    }

    final public void clearItems() {
        int size = this.items.size();
        this.items.clear();
        notifyItemRangeRemoved(0, size);
    }

    final public void removeItem(T item) {
        int position = items.indexOf(item);
        this.items.remove(item);
        notifyItemRemoved(position);
    }

    final public void removeItemAt(int index) {
        this.items.remove(index);
        notifyItemRemoved(index);
    }

    final public void removeItem(List<T> items) {
        this.items.removeAll(items);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
}
