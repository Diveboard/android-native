package com.diveboard.viewModel;

import androidx.lifecycle.ViewModel;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;

import com.diveboard.mobile.ApplicationController;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.util.Callback;

import java.util.ArrayList;
import java.util.Collections;

public class DivesListViewModel extends ViewModel implements Callback<Boolean> {

    public final ObservableList<DiveItemViewModel> items = new ObservableArrayList<>();
    public final ObservableBoolean dataLoadInProgress = new ObservableBoolean(false);

    private DiveboardModel model;
    private ApplicationController ac;

    public void init(ApplicationController ac) {
        // it is not good to put such a generic dependency here but it is the easiest way for now to access ApplicationController.setDataReady()
        // setDataReady() should be part of DiveboardModel, not AC
        this.ac = ac;
        init(ac.getModel(), !ac.isDataReady());
    }

    private void init(DiveboardModel model, boolean shouldLoadData) {
        this.model = model;
        if (model == null) {
            throw new IllegalArgumentException("model cannot be null");
        }
        if (shouldLoadData) {
            LoadDataTask task = new LoadDataTask(model, this);
            dataLoadInProgress.set(true);
            task.execute();
        } else {
            execute(true);
        }
    }

    @Override
    public void execute(Boolean success) {
        ac.setDataReady(true);
        dataLoadInProgress.set(false);
        ArrayList<Dive> dives = model.getDives();
        SortedArrayList<DiveItemViewModel> result = new SortedArrayList<DiveItemViewModel>();
        for (int i = 0; i < dives.size(); i++) {
            Dive dive = dives.get(i);
            result.insertSorted(new DiveItemViewModel(i,
                    dive.getNumber(),
                    dive.getDate(),
                    dive.getSpot(),
                    dive.getDuration(),
                    dive.getMaxdepth(),
                    dive.getMaxdepthUnit()));
        }
        items.addAll(result);
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
