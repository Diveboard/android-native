package com.diveboard.viewModel;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.mobile.R;
import com.diveboard.model.DivesService;
import com.diveboard.model.Units;
import com.diveboard.util.DateConverter;
import com.diveboard.util.ResponseCallback;

import java.util.ArrayList;
import java.util.Collections;

public class DivesListViewModel {

    public final ObservableList<DiveItemViewModel> dives = new ObservableArrayList<>();
    public final ObservableBoolean dataLoadInProgress = new ObservableBoolean(false);

    private Context context;
    private DivesService divesService;
    private Units.UnitsType units;

    public DivesListViewModel(Context context, DivesService divesService, Units.UnitsType units) {
        this.context = context;
        this.divesService = divesService;
        this.units = units;
    }

    public void init(boolean forceOnline) {
        dataLoadInProgress.set(true);
        DateConverter conversion = new DateConverter(context);
        divesService.getDivesAsync(new ResponseCallback<DivesResponse, String>() {
            @Override
            public void success(DivesResponse data) {
                dataLoadInProgress.set(false);
                SortedArrayList<DiveItemViewModel> result = new SortedArrayList<DiveItemViewModel>();
                for (int i = 0; i < data.result.size(); i++) {
                    Dive dive = data.result.get(i);
                    result.insertSorted(new DiveItemViewModel(i,
                            dive.id,
                            dive.shakenId,
                            dive.diveNumber,
                            conversion.convertDateToString(dive.getTimeIn()),
                            dive.spot,
                            dive.durationMin,
                            dive.maxDepth,
                            units));
                }
                dives.clear();
                dives.addAll(result);
            }

            @Override
            public void error(String s) {
                dataLoadInProgress.set(false);
                Toast.makeText(context, context.getString(R.string.error_get_dives_message) + " " + s, Toast.LENGTH_SHORT).show();
            }
        }, forceOnline);
    }

    public void refresh() {
        init(true);
    }

    static class SortedArrayList<T> extends ArrayList<T> {
        @SuppressWarnings("unchecked")
        public void insertSorted(T value) {
            add(value);
            Comparable<T> cmp = (Comparable<T>) value;
            for (int i = size() - 1; i > 0 && cmp.compareTo(get(i - 1)) < 0; i--)
                Collections.swap(this, i, i - 1);
        }
    }
}
