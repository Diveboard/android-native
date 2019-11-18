package com.diveboard.mobile;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diveboard.mobile.databinding.DiveDetailsGeneralBinding;
import com.diveboard.model.SafetyStop;
import com.diveboard.model.Tank;
import com.diveboard.util.BindingConvertions;
import com.diveboard.util.Callback;
import com.diveboard.util.DateConverter;
import com.diveboard.util.binding.recyclerViewBinder.adapter.ClickHandler;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.diveboard.viewModel.DiveTypeViewModel;
import com.diveboard.viewModel.SafetyStopViewModel;
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
    private DiveDetailsGeneralBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dive_details_general, container, false);
        binding = DataBindingUtil.bind(view);
        binding.setView(this);
        if (viewModel != null) {
            binding.setModel(viewModel);
        }
        binding.setConverter(new BindingConvertions());
        binding.setDateConverter(new DateConverter(getContext()));
        validator = new Validator(binding);
        validator.enableFormValidationMode();
        return view;
    }

    public void setViewModel(DiveDetailsViewModel viewModel) {
        this.viewModel = viewModel;
        if (binding != null) {
            binding.setModel(viewModel);
        }
    }

    public Validator getValidator() {
        return validator;
    }

    public String getTimeFormat() {
        return ((SimpleDateFormat) DateFormat.getTimeFormat(getContext())).toPattern();
    }

    public void showSpotDialog() {
        Navigation.findNavController(this.getView()).navigate(R.id.selectSpot);
    }

    public void showSafetyStopDialog() {
        AddSafetyStopDialog dialog = new AddSafetyStopDialog();
        dialog.setPositiveCallback(data -> viewModel.safetyStops.add(data));
        dialog.show(getFragmentManager(), "add_safety_stop");
    }

    public ItemBinder<SafetyStop> safetyStopsItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.safety_stop_item);
    }

    public ClickHandler<SafetyStopViewModel> removeStopHandler() {
        return safetyStop -> viewModel.safetyStops.remove(safetyStop);
    }

    public ClickHandler<DiveTypeViewModel> showDiveTypesDialogHandler() {
        return dt -> showDiveTypesDialog();
    }

    public ClickHandler<Tank> editTanksDialogHandler() {
        return tank -> showTanksDialog(tank);
    }

    public ItemBinder<String> diveTypeItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.dive_type_item);
    }

    public void showDiveTypesDialog() {
        SetDiveTypeDialog dialog = new SetDiveTypeDialog();
        dialog.setCurrentDive(viewModel);
        dialog.show(getFragmentManager(), "set_dive_type");
    }

    public void newTankDialog() {
        showTanksDialog(null);
    }

    private void showTanksDialog(final Tank tank) {
        Callback<TankViewModel> callback = data -> {
            if (tank == null) {
                viewModel.tanks.add(data.toModel());
            } else {
                int index = viewModel.tanks.indexOf(tank);
                viewModel.tanks.set(index, data.toModel());
            }
        };
        Runnable deleteCallback = () -> viewModel.tanks.remove(viewModel.tanks.indexOf(tank));
        TankDialog dialog = new TankDialog(tank, callback, deleteCallback);
        dialog.show(getFragmentManager(), "tank");
    }

    public ItemBinder<Tank> tanksItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.tank_item);
    }
}
