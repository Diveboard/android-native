package com.diveboard.mobile;

import android.app.Dialog;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.diveboard.model.Units;
import com.diveboard.util.Callback;
import com.diveboard.viewModel.AddSafetyStopViewModel;

public class AddSafetyStopDialog extends DialogFragment {

    private Callback<AddSafetyStopViewModel> callback;

    public void setPositiveCallback(Callback<AddSafetyStopViewModel> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_add_safety_stop_dialog, null);
        ViewDataBinding binding = DataBindingUtil.bind(view);
        //TODO: implement
        final AddSafetyStopViewModel viewModel = new AddSafetyStopViewModel(Units.Depth.Meters);
        binding.setVariable(BR.model, viewModel);

        builder.setView(view)
                .setTitle(R.string.add_safetystop)
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
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
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return alertDialog;
    }
}
