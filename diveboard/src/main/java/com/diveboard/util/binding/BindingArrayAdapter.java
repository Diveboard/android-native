package com.diveboard.util.binding;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.diveboard.mobile.BR;

public class BindingArrayAdapter<T> extends AutoSuggestAdapter<T> {
    private Context context;
    private int resource;

    public BindingArrayAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, resource, null, false);
        binding.setVariable(BR.model, getItem(position));
        return binding.getRoot();
    }
}
