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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.diveboard.mobile.databinding.ActivityDivesListBinding;
import com.diveboard.util.binding.recyclerViewBinder.adapter.ClickHandler;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.DiveItemViewModel;
import com.diveboard.viewModel.DivesListViewModel;

public class LogbookPage extends Fragment {

    private DrawerLayout drawerLayout;
    private View listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityDivesListBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_dives_list, container, false);
        View view = binding.getRoot();
        setupToolbar(view);
        ApplicationController applicationContext = (ApplicationController) getActivity().getApplicationContext();
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        //TODO: make a singlton?
        DivesListViewModel viewModel = new DivesListViewModel(applicationContext, applicationContext.getDivesService());
        viewModel.init();
        listView = binding.listView;
        binding.setModel(viewModel);
        binding.setView(this);
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_menu));
        actionBar.setTitle(getString(R.string.logbook));
        actionBar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    public ClickHandler<DiveItemViewModel> showDiveHandler() {
        return viewModel -> {
            LogbookPageDirections.ActionLogbookToDiveDetails action = LogbookPageDirections.actionLogbookToDiveDetails();
            action.setDiveId(viewModel.id);
            Navigation.findNavController(listView).navigate(action);
        };
    }

    public void addDive() {
        LogbookPageDirections.ActionLogbookToDiveDetails action = LogbookPageDirections.actionLogbookToDiveDetails();
        Navigation.findNavController(listView).navigate(action);
    }

    public ItemBinder<String> diveItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.dives_row);
    }
}
