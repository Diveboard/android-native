package com.diveboard.mobile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.diveboard.viewModel.DiveTypeViewModel;
import com.diveboard.viewModel.SetDiveTypeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

public class SetDiveTypeDialog extends DialogFragment {

    public List<DiveTypeViewModel> allDiveTypes;
    private DiveDetailsViewModel rootViewModel;

    public ItemBinder<String> diveTypeItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.set_dive_type_item);
    }

    public void setCurrentDive(DiveDetailsViewModel viewModel) {
        this.rootViewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initDiveTypes();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_dive_type_dialog, null);
        ViewDataBinding binding = DataBindingUtil.bind(view);

        final SetDiveTypeViewModel viewModel = new SetDiveTypeViewModel();
        binding.setVariable(BR.model, viewModel);
        binding.setVariable(BR.view, this);

        builder.setView(view)
                .setTitle(R.string.dive_type)
                .setPositiveButton(R.string.save_button2, (dialog, id) -> {
                    rootViewModel.diveTypes.clear();
                    for (DiveTypeViewModel adt : allDiveTypes) {
                        if (adt.selected) {
                            rootViewModel.diveTypes.add(adt);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> getDialog().cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return alertDialog;
    }

    private void initDiveTypes() {
        allDiveTypes = new ArrayList<>();
        String[] values = getResources().getStringArray(R.array.dive_type_values);
        for (int i = 0; i < values.length; i++) {
            allDiveTypes.add(new DiveTypeViewModel(values[i]));
        }
        for (DiveTypeViewModel dt : rootViewModel.diveTypes) {
            boolean foundDiveType = false;
            for (DiveTypeViewModel adt : allDiveTypes) {
                boolean equals = adt.diveType.equals(dt.diveType);
                if (equals) {
                    adt.selected = true;
                    foundDiveType = true;
                    break;
                }
            }
            if (!foundDiveType) {
                DiveTypeViewModel nonDictionaryDiveType = new DiveTypeViewModel(dt.diveType);
                nonDictionaryDiveType.selected = true;
                allDiveTypes.add(nonDictionaryDiveType);
            }
        }

    }
}
