package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.diveboard.mobile.databinding.DiveDetailsNotesBinding;
import com.diveboard.viewModel.DiveDetailsViewModel;

public class DiveDetailsNotesFragment extends Fragment {
    private DiveDetailsViewModel viewModel;
    private DiveDetailsNotesBinding binding;
    private ApplicationController ac;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: should be better way to avoid view duplication while navigating back from spot selection
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.dive_details_notes, container, false);
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
