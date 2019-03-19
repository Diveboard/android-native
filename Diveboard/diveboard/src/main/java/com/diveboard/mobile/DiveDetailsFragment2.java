package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class DiveDetailsFragment2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dive_details2, container, false);
        setupTabs(view);
        return view;
    }

    private void setupTabs(View view) {
        ViewPager viewPager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getActivity().getApplicationContext());
        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_details_label);
//        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_people_label);
//        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_notes_label);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
