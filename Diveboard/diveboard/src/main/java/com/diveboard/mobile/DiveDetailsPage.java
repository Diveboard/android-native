package com.diveboard.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.util.ResourceHolder;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.TabAdapter;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;
import br.com.ilhasoft.support.validation.Validator;

public class DiveDetailsPage extends Fragment {
    private DiveDetailsViewModel viewModel;
    private DiveDetailsGeneralFragment general;
    private TabLayout tabLayout;
    private ApplicationController ac;
    private DiveDetailsPeopleFragment people;
    private DiveDetailsNotesFragment notes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dive_details2, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();
        int diveId = DiveDetailsPageArgs.fromBundle(getArguments()).getDiveId();
        setupToolbar(view);
        setupTabs(view);
        setupViewModel(diveId);
        return view;
    }

    private void setupViewModel(int diveId) {
        //TODO: get model from singleton tempDive so it is restored after app switch
        ResourceHolder resourceHolder = new ResourceHolder(ac);

        ac.getDivesService().getDivesAsync(new ResponseCallback<DivesResponse, String>() {
            @Override
            public void success(DivesResponse data) {
                if (diveId != -1) {
                    viewModel = DiveDetailsViewModel.createFromModel(data.getDive(diveId),
                            resourceHolder.getVisibilityValues(),
                            resourceHolder.getCurrentValues(),
                            ac.getUserPreferenceService().getUnits());
                } else {
                    if (ac.currentDive == null) {
                        viewModel = DiveDetailsViewModel.createNewDive(
                                data.getMaxDiveNumber() + 1,
                                data.getLastTripName(),
                                ac.getUserPreferenceService().getUnits(),
                                resourceHolder.getVisibilityValues(),
                                resourceHolder.getCurrentValues());
                    } else {
                        viewModel = ac.currentDive;
                    }
                }
                viewModel.setTripNames(data.getTripNames());
                setViewModel(viewModel);
            }

            @Override
            public void error(String s) {
                Toast.makeText(ac, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabs(View view) {
        ViewPager viewPager = view.findViewById(R.id.pager);
        tabLayout = view.findViewById(R.id.tabLayout);
        TabAdapter adapter = new TabAdapter(getChildFragmentManager(), getActivity().getApplicationContext());
        general = new DiveDetailsGeneralFragment();
        adapter.addFragment(general, R.string.tab_details_label);
        people = new DiveDetailsPeopleFragment();
        adapter.addFragment(people, R.string.tab_people_label);
        notes = new DiveDetailsNotesFragment();
        adapter.addFragment(notes, R.string.tab_notes_label);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setViewModel(DiveDetailsViewModel viewModel) {
        ac.currentDive = viewModel;
        general.setViewModel(viewModel);
        people.setViewModel(viewModel);
        notes.setViewModel(viewModel);
    }

    private void setupToolbar(View view) {
        Toolbar actionBar = view.findViewById(R.id.toolbar);
        actionBar.inflateMenu(R.menu.dive_details);
        actionBar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        actionBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.save:
                    save();
                    break;
                case R.id.clone:
                    break;
            }
            return true;
        });
    }

    public void save() {
        Validator generalValidator = general.getValidator();
        if (!generalValidator.validate()) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
        }
    }
}
