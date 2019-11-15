package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.mobile.databinding.ActivityStatisticBinding;
import com.diveboard.model.DivesService;
import com.diveboard.model.Statistic;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.StatisticViewModel;


public class StatisticPage extends Fragment {

    private ApplicationController ac;

    public static ItemBinderBase<Object> getItemBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.statistic_row);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityStatisticBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_statistic, container, false);
        View view = binding.getRoot();
        setupToolbar(view);
        ac = (ApplicationController) getActivity().getApplicationContext();
        DivesService divesService = ac.getDivesService();
        divesService.getDivesAsync(new ResponseCallback<DivesResponse, String>() {
            @Override
            public void success(DivesResponse data) {
                binding.setModel(StatisticViewModel.create(Statistic.create(data.result), ac.getUserPreferenceService().getUnits()));
            }

            @Override
            public void error(String s) {
                Toast.makeText(ac, s, Toast.LENGTH_SHORT).show();
            }
        }, false);
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.setNavigationOnClickListener(v -> ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START));
    }
}
