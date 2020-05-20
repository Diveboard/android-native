package com.diveboard.mobile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

import com.diveboard.viewModel.StatisticViewModel;

public class MyCountriesDialog extends DialogFragment {

    private final StatisticViewModel viewModel;

    public MyCountriesDialog(StatisticViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_my_countries_dialog, null);
        ViewDataBinding binding = DataBindingUtil.bind(view);
        ApplicationController context = (ApplicationController) getActivity().getApplicationContext();

        binding.setVariable(BR.model, viewModel);

        builder.setView(view)
                .setTitle(R.string.countries)
                .setNegativeButton(R.string.close, (dialog, id) -> getDialog().cancel());
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}
