package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

public class DiveDetailsFragment2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dive_details2, container, false);
        setupToolbar(view);
        setupTabs(view);
        return view;
    }

    private void setupTabs(View view) {
        ViewPager viewPager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getActivity().getApplicationContext());
        adapter.addFragment(new DiveDetailsGeneralFragment(), R.string.tab_details_label);
        adapter.addFragment(new DiveDetailsPeopleFragment(), R.string.tab_people_label);
        adapter.addFragment(new DiveDetailsNotesFragment(), R.string.tab_notes_label);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_keyboard_backspace_24px));
        actionBar.setTitle(R.string.dive_details_title);
        actionBar.inflateMenu(R.menu.dive_details);
        actionBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });
        actionBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save:
                        save();
                        break;
                    case R.id.clone:
                        break;
                }
                return true;
            }
        });
    }

    public void save() {
//        if (validator.validate()) {
//
//        }
    }
}
