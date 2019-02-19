package com.diveboard.mobile;

import com.diveboard.mobile.R;
import com.diveboard.model.DiveboardModel;

import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class					SudoDialog extends DialogPreference
{
	public interface			SudoDialogListener
	{
        void					onSudoComplete(DialogFragment dialog);
    }
	
	private EditText			mSudo;
	private DiveboardModel		mModel;
	
	public SudoDialog(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_sudo);
    }

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder)
	{
		builder.setTitle("User ID");
//		builder.setPositiveButton(null, null);
//		builder.setNegativeButton(null, null);
		super.onPrepareDialogBuilder(builder);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult == true && mSudo.getText().toString().length() != 0)
		{
			System.out.println("Result : " + positiveResult + " : " + mSudo.getText().toString());
			mModel = ((ApplicationController) this.getContext().getApplicationContext()).getModel();
			//mModel.doLogout();
			callChangeListener(mSudo.getText().toString());
		}
		super.onDialogClosed(positiveResult);
	}
	
	@Override
	protected void onBindDialogView(View view) {
		mSudo = (EditText) view.findViewById(R.id.sudo);
		super.onBindDialogView(view);
	}
}
