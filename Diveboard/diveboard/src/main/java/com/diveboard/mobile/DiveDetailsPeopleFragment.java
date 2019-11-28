package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diveboard.mobile.databinding.DiveDetailsNotesBinding;
import com.diveboard.mobile.databinding.DiveDetailsPeopleBinding;
import com.diveboard.viewModel.DiveDetailsViewModel;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

public class DiveDetailsPeopleFragment extends Fragment {

    private DiveDetailsViewModel viewModel;
    private DiveDetailsPeopleBinding binding;
    private ApplicationController ac;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_people, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();
        binding = DataBindingUtil.bind(view);
        binding.setView(this);
        if (viewModel != null) {
            binding.setModel(viewModel);
        }
        return view;
    }

    public void setViewModel(DiveDetailsViewModel viewModel) {
        this.viewModel = viewModel;
        if (binding != null) {
            binding.setModel(viewModel);
        }
    }
}
