package com.diveboard.viewModel;

import android.arch.lifecycle.ViewModel;

import com.diveboard.model.Dive;
import com.diveboard.model.Units;

import java.util.ArrayList;
import java.util.List;

public class DivesListViewModel extends ViewModel {
    public final List<DiveItem> items;

    public DivesListViewModel(List<DiveItem> items) {
        this.items = items;
    }

    public static DivesListViewModel create(List<Dive> dives, Units units) {
        List<DiveItem> result = new ArrayList<DiveItem>();
        result.add(new DiveItem(2, "some date"));
        return new DivesListViewModel(result);
    }
}
