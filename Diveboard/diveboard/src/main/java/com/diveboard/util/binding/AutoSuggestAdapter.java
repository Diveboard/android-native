package com.diveboard.util.binding;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AutoSuggestAdapter<T> extends ArrayAdapter<T> implements Filterable {
    private List<T> listData;

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        listData = new ArrayList<>();
    }

    public void setData(List<T> list) {
        if (list == null) {
            notifyDataSetInvalidated();
            return;
        }

        listData.clear();
        listData.addAll(list);
        notify(list);
    }

    private void notify(List<T> list) {
        if (list.size() == 0) {
            notifyDataSetInvalidated();
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Nullable
    @Override
    public T getItem(int position) {
        return listData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = listData;
                    filterResults.count = listData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}