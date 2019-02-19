package com.diveboard.mobile;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class ObjectListAdapter<T> extends BaseAdapter {

    private List<T> items;
    private Activity activity;
    private int itemLayoutId;
    private int variableId;

    public ObjectListAdapter(Activity activity, List<T> items, @LayoutRes int itemLayoutId, int variableId) {
        this.items = items;
        this.activity = activity;
        this.itemLayoutId = itemLayoutId;
        this.variableId = variableId;
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
}