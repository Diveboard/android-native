package com.diveboard.mobile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;

import com.diveboard.util.BindingConvertions;
import com.diveboard.util.Callback;
import com.diveboard.util.ResourceHolder;
import com.diveboard.viewModel.TankViewModel;

public class TankDialog extends DialogFragment {

    private TankViewModel tank;
    private Callback<TankViewModel> callback;
    private Runnable deleteCallback;

    public TankDialog(TankViewModel tank, Callback<TankViewModel> callback, Runnable deleteCallback) {
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
                TankViewModel.createDefault(context.getUserPreferenceService().getUnits(), holder.getMaterialsValues(), holder.getGasMixValues(), holder.getCylindersCountValues()) :
                tank;

        binding.setVariable(BR.model, viewModel);
        binding.setVariable(BR.converter, new BindingConvertions());

        builder.setView(view)
                .setTitle(R.string.tank_dialog_title)
                .setPositiveButton(R.string.save_button2, (dialog, id) -> {
                    if (callback != null) {
                        callback.execute(viewModel);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> getDialog().cancel());
        if (tank != null) {
            builder.setNeutralButton(getString(R.string.dialog_delete), (dialog, which) -> {
                if (deleteCallback != null) {
                    deleteCallback.run();
                }
            });
        }
        return builder.create();
    }
}
