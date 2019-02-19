package com.diveboard.mobile;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Observable;

public class ObjectListAdapter<T> extends BaseAdapter {

    private List<T> items;
    private Activity activity;
    private int itemLayoutId;
    private int variableId;
    private final WeakReferenceOnListChangedCallback onListChangedCallback;

    public ObjectListAdapter(Activity activity, List<T> items, @LayoutRes int itemLayoutId, int variableId) {
        this.items = items;
        this.activity = activity;
        this.itemLayoutId = itemLayoutId;
        this.variableId = variableId;
        this.onListChangedCallback = new WeakReferenceOnListChangedCallback<>(this);
        subscribeToChanges();
    }

    private void subscribeToChanges() {
        if (items instanceof ObservableList){
            ((ObservableList)items).addOnListChangedCallback(onListChangedCallback);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding binding;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(itemLayoutId, null);
            binding = DataBindingUtil.bind(convertView);
            convertView.setTag(binding);
        } else {
            binding = (ViewDataBinding) convertView.getTag();
        }
        binding.setVariable(variableId, items.get(position));
        return binding.getRoot();
    }

    private static class WeakReferenceOnListChangedCallback<T> extends ObservableList.OnListChangedCallback
    {
        private final WeakReference<BaseAdapter> adapterReference;

        public WeakReferenceOnListChangedCallback(BaseAdapter adapter)
        {
            this.adapterReference = new WeakReference<>(adapter);
        }

        @Override
        public void onChanged(ObservableList sender)
        {
            BaseAdapter adapter = adapterReference.get();
            if (adapter != null)
            {
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount)
        {
            onChanged(sender);
        }

        @Override
        public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount)
        {
            onChanged(sender);
        }

        @Override
        public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount)
        {
            onChanged(sender);
        }

        @Override
        public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount)
        {
            onChanged(sender);
        }
    }
}