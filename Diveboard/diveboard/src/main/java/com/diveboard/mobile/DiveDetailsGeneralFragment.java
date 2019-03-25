package com.diveboard.mobile;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.diveboard.mobile.databinding.DiveDetailsGeneralBinding;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank2;
import com.diveboard.model.Units;
import com.diveboard.util.BindingConvertions;
import com.diveboard.util.Callback;
import com.diveboard.util.DateConverter;
import com.diveboard.util.ResourceHolder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.ClickHandler;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.AddSafetyStopViewModel;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.diveboard.viewModel.TankViewModel;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import br.com.ilhasoft.support.validation.Validator;

public class DiveDetailsGeneralFragment extends Fragment {

    private DiveDetailsViewModel viewModel;
    private Validator validator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_general, container, false);

        //TODO: get model from singleton tempDive so it is restored after app switch
        ApplicationController ac = (ApplicationController) getActivity().getApplicationContext();
        if (ac.currentDive == null) {
            ResourceHolder resourceHolder = new ResourceHolder(ac);
            viewModel = DiveDetailsViewModel.createNewDive(55, "Mexico", Units.UnitsType.Metric, resourceHolder.getVisibilityValues(), resourceHolder.getCurrentValues());
            ac.currentDive = viewModel;
        }
        setupSafetyStops(view);
        setupTripName(view);
        return view;
    }

    private void setupSafetyStops(View view) {
        DiveDetailsGeneralBinding binding = DataBindingUtil.bind(view);
        binding.setModel(viewModel);
        binding.setView(this);
        binding.setConverter(new BindingConvertions());
        binding.setDateConverter(new DateConverter(getContext()));

        validator = new Validator(binding);
        validator.enableFormValidationMode();
    }

    private void setupTripName(View view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line,
                ApplicationController.getInstance().getModel().getUser().getTripNames());
        AutoCompleteTextView textView = view.findViewById(R.id.trip_name);
        textView.setAdapter(adapter);
    }

    public String getTimeFormat() {
        return ((SimpleDateFormat) DateFormat.getTimeFormat(getContext())).toPattern();
    }

    public void showSpotDialog() {
        Navigation.findNavController(this.getView()).navigate(R.id.selectSpot);
    }

    public void showSafetyStopDialog() {
        AddSafetyStopDialog dialog = new AddSafetyStopDialog();
        dialog.setPositiveCallback(new Callback<AddSafetyStopViewModel>() {
            @Override
            public void execute(AddSafetyStopViewModel data) {
                viewModel.safetyStops.add(data.toModel());
            }
        });
        dialog.show(getFragmentManager(), "add_safety_stop");
    }

    public ItemBinder<SafetyStop> safetyStopsItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.safety_stop_item);
    }

    public ClickHandler<SafetyStop> removeStopHandler() {
        return new ClickHandler<SafetyStop>() {
            @Override
            public void onClick(SafetyStop safetyStop) {
                viewModel.safetyStops.remove(safetyStop);
            }
        };
    }

    public ClickHandler<Tank2> editTanksDialogHandler() {

        return new ClickHandler<Tank2>() {
            @Override
            public void onClick(Tank2 tank) {
                showTanksDialog(tank);
            }
        };
    }

    public ItemBinder<String> diveTypeItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.dive_type_item);
    }

    public void showDiveTypesDialog() {
        SetDiveTypeDialog dialog = new SetDiveTypeDialog();
        dialog.show(getFragmentManager(), "set_dive_type");
    }

    public void newTankDialog() {
        showTanksDialog(null);
    }

    private void showTanksDialog(final Tank2 tank) {
        Callback<TankViewModel> callback = new Callback<TankViewModel>() {
            @Override
            public void execute(TankViewModel data) {
                if (tank == null) {
                    viewModel.tanks.add(data.toModel());
                } else {
                    int index = viewModel.tanks.indexOf(tank);
                    viewModel.tanks.set(index, data.toModel());
                }
            }
        };
        Runnable deleteCallback = new Runnable() {
            @Override
            public void run() {
                viewModel.tanks.remove(viewModel.tanks.indexOf(tank));
            }
        };
        TankDialog dialog = new TankDialog(tank, callback, deleteCallback);
        dialog.show(getFragmentManager(), "tank");
    }

    public ItemBinder<Tank2> tanksItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.tank_item);
    }
}
