package com.diveboard.mobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinder;
import com.diveboard.util.binding.recyclerViewBinder.adapter.binder.ItemBinderBase;
import com.diveboard.viewModel.SetDiveTypeViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

public class SetDiveTypeDialog extends DialogFragment {

    public List<DiveType> allDiveTypes;
    private ApplicationController ac;

    public ItemBinder<String> diveTypeItemViewBinder() {
        return new ItemBinderBase<>(BR.model, R.layout.set_dive_type_item);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ac = (ApplicationController) getActivity().getApplicationContext();
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
                .setPositiveButton(R.string.save_button2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ac.currentDive.diveTypes.clear();
                        for (DiveType adt : allDiveTypes) {
                            if (adt.selected) {
                                ac.currentDive.diveTypes.add(adt);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return alertDialog;
    }

    private void initDiveTypes() {
        allDiveTypes = new ArrayList<>();
        String[] values = getResources().getStringArray(R.array.dive_type_values);
        String[] titles = getResources().getStringArray(R.array.dive_type_titles);
        for (int i = 0; i < values.length; i++) {
            allDiveTypes.add(new DiveType(values[i], titles[i]));
        }
        for (DiveType dt : ac.currentDive.diveTypes) {
            for (DiveType adt : allDiveTypes) {
                adt.selected = adt.value.equals(dt.value);
            }
        }
    }
}
