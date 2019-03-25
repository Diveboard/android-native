package com.diveboard.mobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.diveboard.model.Tank2;
import com.diveboard.util.BindingConvertions;
import com.diveboard.util.Callback;
import com.diveboard.util.ResourceHolder;
import com.diveboard.viewModel.TankViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

public class TankDialog extends DialogFragment {

    private Tank2 tank;
    private Callback<TankViewModel> callback;
    private Runnable deleteCallback;

    public TankDialog(Tank2 tank, Callback<TankViewModel> callback, Runnable deleteCallback) {
        this.tank = tank;
        this.callback = callback;
        this.deleteCallback = deleteCallback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ApplicationController context = (ApplicationController) getActivity().getApplicationContext();

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_tank_dialog, null);
        ViewDataBinding binding = DataBindingUtil.bind(view);

        ResourceHolder holder = new ResourceHolder(context);
        final TankViewModel viewModel = tank == null ?
                TankViewModel.createDefault(context.getUserPreference().getUnitsTyped(), holder) :
                TankViewModel.fromModel(tank, context.getUserPreference().getUnitsTyped(), holder);

        binding.setVariable(BR.model, viewModel);
        binding.setVariable(BR.converter, new BindingConvertions());

        builder.setView(view)
                .setTitle(R.string.tank_dialog_title)
                .setPositiveButton(R.string.save_button2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (callback != null) {
                            callback.execute(viewModel);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        if (tank != null) {
            builder.setNeutralButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (deleteCallback != null) {
                        deleteCallback.run();
                    }
                }
            });
        }
        return builder.create();
    }
}
