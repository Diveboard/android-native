package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class SettingsPage extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);
        setupToolbar(view);
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.setNavigationOnClickListener(v -> ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START));
    }
}