package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class DiveDetailsNotesFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_notes, container, false);
        ApplicationController ac = (ApplicationController) getActivity().getApplicationContext();
        return view;
    }
}
