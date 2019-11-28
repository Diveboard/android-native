package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.diveboard.mobile.databinding.ActivityDivesListBinding;
import com.diveboard.util.binding.recyclerViewBinder.adapter.ClickHandler;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.DiveItemViewModel;
import com.diveboard.viewModel.DivesListViewModel;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LogbookPage extends Fragment {

    private DrawerLayout drawerLayout;
    private View listView;
    private ApplicationController ac;
    private DivesListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityDivesListBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_dives_list, container, false);
        View view = binding.getRoot();
        setupToolbar(view);
        ac = (ApplicationController) getActivity().getApplicationContext();
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        //TODO: make a singlton?
        viewModel = new DivesListViewModel(ac, ac.getDivesService(), ac.getUserPreferenceService().getUnits());
        setupPullToRefresh(view);
        viewModel.init(getForceOnline());
        listView = binding.listView;
        binding.setModel(viewModel);
        binding.setView(this);
        return view;
    }

    private boolean getForceOnline() {
        Date lastSyncTime = ac.getUserPreferenceService().getLastSyncTime();
        if (lastSyncTime == null) {
            return true;
        }
        long days = TimeUnit.DAYS.convert(new Date().getTime() - lastSyncTime.getTime(), TimeUnit.MILLISECONDS);
        return days >= 3;
    }

    private void setupPullToRefresh(View view) {
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        viewModel.dataLoadInProgress.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                pullToRefresh.setRefreshing(viewModel.dataLoadInProgress.get());
            }
        });
        pullToRefresh.setOnRefreshListener(() -> viewModel.refresh());
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        actionBar.setTitle(getString(R.string.logbook));
        actionBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    public ClickHandler<DiveItemViewModel> showDiveHandler() {
        return viewModel -> {
            LogbookPageDirections.ActionLogbookToDiveDetails action = LogbookPageDirections.actionLogbookToDiveDetails(viewModel.shakenId);
            Navigation.findNavController(listView).navigate(action);
        };
    }

    public void addDive() {
        LogbookPageDirections.ActionLogbookToDiveDetails action = LogbookPageDirections.actionLogbookToDiveDetails(null);
        Navigation.findNavController(listView).navigate(action);
    }

    public ItemBinder<String> diveItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.dives_row);
    }
}
