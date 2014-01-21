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

public class					SoTimeoutDialog extends DialogPreference
{
	public interface			SoTimeoutDialogListener
	{
        void					onSoTimeoutComplete(DialogFragment dialog);
    }
	
	private EditText			mTimeout;
	
	public SoTimeoutDialog(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setDialogLayoutResource(R.layout.dialog_timeout);
    }

	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder)
	{
		builder.setTitle("Socket Timeout");
//		builder.setPositiveButton(null, null);
//		builder.setNegativeButton(null, null);
		super.onPrepareDialogBuilder(builder);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult == true && mTimeout.getText().toString().length() != 0)
		{
			DiveboardModel._soTimeout = Integer.parseInt(mTimeout.getText().toString());
			callChangeListener(mTimeout.getText().toString());
		}
		super.onDialogClosed(positiveResult);
	}
	
	@Override
	protected void onBindDialogView(View view) {
		mTimeout = (EditText) view.findViewById(R.id.timeout);
		mTimeout.setText(DiveboardModel._soTimeout.toString());
		super.onBindDialogView(view);
	}
}
