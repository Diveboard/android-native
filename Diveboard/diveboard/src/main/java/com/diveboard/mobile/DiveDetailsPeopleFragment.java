package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diveboard.viewModel.DiveDetailsViewModel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DiveDetailsPeopleFragment extends Fragment {

    private DiveDetailsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_people, container, false);
        ApplicationController ac = (ApplicationController) getActivity().getApplicationContext();
        return view;
    }

    public void setViewModel(DiveDetailsViewModel viewModel) {
        this.viewModel = viewModel;
    }
}
