package com.diveboard.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.diveboard.dataaccess.datamodel.DeleteResponse;
import com.diveboard.dataaccess.datamodel.Dive;
import com.diveboard.dataaccess.datamodel.DivesResponse;
import com.diveboard.model.DivesService;
import com.diveboard.util.ResourceHolder;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.TabAdapter;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.google.android.material.tabs.TabLayout;

import br.com.ilhasoft.support.validation.Validator;

public class DiveDetailsPage extends Fragment {
    private DiveDetailsViewModel viewModel;
    private DiveDetailsGeneralFragment general;
    private TabLayout tabLayout;
    private ApplicationController ac;
    private DiveDetailsPeopleFragment people;
    private DiveDetailsNotesFragment notes;
    private AlertDialog deleteConfirmationDialog;
    private DivesService divesService;
    private boolean isNewDive;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dive_details2, container, false);
        ac = (ApplicationController) getActivity().getApplicationContext();
        divesService = ac.getDivesService();
        //cannot use dive.id as it is not always exist e.g. for offline dives
        String shakenId = DiveDetailsPageArgs.fromBundle(getArguments()).getShakenId();
        isNewDive = shakenId == null;
        setupToolbar(view);
        setupTabs(view);
        setupViewModel(shakenId);
        return view;
    }

    private void setupViewModel(String shakenId) {
        //TODO: get model from singleton tempDive so it is restored after app switch
        ResourceHolder resourceHolder = new ResourceHolder(ac);

        ac.getDivesService().getDivesAsync(new ResponseCallback<DivesResponse, String>() {
            @Override
            public void success(DivesResponse data) {
                if (isNewDive) {
//                    if (ac.currentDive == null) {
                        viewModel = DiveDetailsViewModel.createNewDive(
                                data.getMaxDiveNumber() + 1,
                                data.getLastTripName(),
                                ac.getUserPreferenceService().getUnits(),
                                resourceHolder.getVisibilityValues(),
                                resourceHolder.getCurrentValues(),
                                ac.getCurrentUser().id);
//                    } else {
//                        viewModel = ac.currentDive;
//                    }
                } else {
                    viewModel = DiveDetailsViewModel.createFromModel(data.getDive(shakenId),
                            resourceHolder.getVisibilityValues(),
                            resourceHolder.getCurrentValues(),
                            ac.getUserPreferenceService().getUnits());
                }
                viewModel.setTripNames(data.getTripNames());
                setViewModel(viewModel);
            }

            @Override
            public void error(String s) {
                Toast.makeText(ac, s, Toast.LENGTH_SHORT).show();
            }
        }, false);
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
        actionBar.setNavigationOnClickListener(v -> {
            //TODO: ask to save changes
            hideKeyBoard(getView());
            Navigation.findNavController(v).popBackStack();
        });
        actionBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.save:
                    save();
                    break;
                case R.id.clone:
                    break;
                case R.id.delete:
                    getDeleteConfirmationDialog().show();
                    break;
            }
            return true;
        });
    }

    private void delete() {
        divesService.deleteDiveAsync(viewModel.getModel(), new ResponseCallback<DeleteResponse, Exception>() {
            @Override
            public void success(DeleteResponse data) {
                Navigation.findNavController(tabLayout).popBackStack();
            }

            @Override
            public void error(Exception e) {
                Toast.makeText(ac, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AlertDialog getDeleteConfirmationDialog() {
        if (deleteConfirmationDialog != null) {
            return deleteConfirmationDialog;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.deleteDive);
        builder.setPositiveButton(R.string.delete, (dialog, id) -> {
            delete();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {

        });
        deleteConfirmationDialog = builder.create();
        return deleteConfirmationDialog;
    }

    public void save() {
        //TODO: there should be a better solution for this
        this.getView().requestFocus();
        hideKeyBoard(getView());

        Validator generalValidator = general.getValidator();
        if (!generalValidator.validate()) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            return;
        }
        ac.getDivesService().saveDiveAsync(viewModel.getModel(), new ResponseCallback<Dive, Exception>() {
            @Override
            public void success(Dive data) {
                Navigation.findNavController(tabLayout).popBackStack();
            }

            @Override
            public void error(Exception e) {
                Toast toast = Toast.makeText(ac, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }, isNewDive);
    }

    public void hideKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }
}