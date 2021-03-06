package com.diveboard.viewModel;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableList;

import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.dataaccess.datamodel.SyncObject;
import com.diveboard.mobile.R;
import com.diveboard.model.DivesService;
import com.diveboard.model.SyncService;
import com.diveboard.model.Units;
import com.diveboard.util.DateConverter;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DivesListViewModel {

    public final ObservableList<DiveItemViewModel> dives = new ObservableArrayList<>();
    public final ObservableBoolean dataLoadInProgress = new ObservableBoolean(false);

    private Context context;
    private DivesService divesService;
    private Units.UnitsType units;
    private SyncService syncService;

    public DivesListViewModel(Context context, DivesService divesService, Units.UnitsType units, SyncService syncService) {
        this.context = context;
        this.divesService = divesService;
        this.units = units;
        this.syncService = syncService;
    }

    public void init(boolean forceOnline) {
        dataLoadInProgress.set(true);
        DateConverter conversion = new DateConverter(context);
        divesService.getDivesAsync(new ResponseCallback<DivesResponse>() {
            @Override
            public void success(DivesResponse data) {
                List<SyncObject> changes = syncService.getChanges();
                dataLoadInProgress.set(false);
                SortedArrayList<DiveItemViewModel> result = new SortedArrayList<DiveItemViewModel>();
                for (int i = 0; i < data.result.size(); i++) {
                    Dive dive = data.result.get(i);
                    result.insertSorted(new DiveItemViewModel(conversion,
                            i,
                            dive.id,
                            dive.shakenId,
                            dive.diveNumber,
                            dive.getTimeIn(),
                            dive.spot,
                            dive.tripName,
                            dive.durationMin,
                            dive.maxDepth,
                            dive.thumbnailImageUrl,
                            units, isUnsynced(changes, dive)));
                }
                //add trip name grouping
                String lastTripName = "abrakadabra:)";
                DiveItemViewModel groupDive = null;
                int divesInTrip = 0;
                for (int i = 0; i < result.size(); i++) {
                    DiveItemViewModel dive = result.get(i);
                    String currentTripName = Utils.isNullOrEmpty(dive.tripName) ? context.getString(R.string.unnamedTrip) : dive.tripName;
                    if (!lastTripName.equals(currentTripName)) {
                        //add group title
                        dive.isGroupStart = true;
                        //reset for Unnamed Trip case
                        dive.tripName = currentTripName;
                        if (groupDive != null) {
                            groupDive.divesInTrip = divesInTrip;
                            groupDive.divesInTripTitle = divesInTrip == 1 ? context.getString(R.string.dive) : context.getString(R.string.dives);
                        }
                        groupDive = dive;
                        divesInTrip = 1;
                    } else {
                        divesInTrip++;
                    }
                    lastTripName = currentTripName;
                }
                if (groupDive != null) {
                    groupDive.divesInTrip = divesInTrip;
                    groupDive.divesInTripTitle = divesInTrip == 1 ? context.getString(R.string.dive) : context.getString(R.string.dives);
                }
                dives.clear();
                dives.addAll(result);
            }

            @Override
            public void error(Exception e) {
                dataLoadInProgress.set(false);
                Toast.makeText(context, context.getString(R.string.error_get_dives_message) + " " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.logError(DivesListViewModel.class, "Cannot get dives", e);
            }
        }, forceOnline);
    }

    private boolean isUnsynced(List<SyncObject> changes, Dive dive) {
        for (SyncObject change : changes) {
            if (change.id.equals(dive.shakenId)) {
                return true;
            }
        }
        return false;
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
