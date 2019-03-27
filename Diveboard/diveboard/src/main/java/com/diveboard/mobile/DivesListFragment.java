package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diveboard.mobile.databinding.ActivityDivesListBinding;
import com.diveboard.util.binding.recyclerViewBinder.adapter.ClickHandler;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.DiveItemViewModel;
import com.diveboard.viewModel.DivesListViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class DivesListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityDivesListBinding binding = DataBindingUtil.inflate(inflater, R.layout.activity_dives_list, container, false);
        View view = binding.getRoot();

        DivesListViewModel viewModel = ViewModelProviders.of(this).get(DivesListViewModel.class);
        viewModel.init((ApplicationController) getActivity().getApplicationContext());

        binding.setModel(viewModel);
        binding.setView(this);
        return view;
    }

    public ClickHandler<DiveItemViewModel> showDiveHandler() {
        return new ClickHandler<DiveItemViewModel>() {
            @Override
            public void onClick(DiveItemViewModel viewModel) {

            }
        };
    }

    public ItemBinder<String> diveItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.dives_row);
    }
}
