package com.diveboard.util;

import com.diveboard.mobile.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ExitDialog extends Dialog implements android.view.View.OnClickListener{

	private String mTitle ="";
	private String mBody = "";
	private String okText, cancelText;
	private Context mContext;
	private View.OnClickListener positiveBtn, negativeBtn;	
	
	public ExitDialog(Activity a) {
		// TODO Auto-generated constructor stub
		super(a);
		mContext = a;
//		mTitle = mContext.getResources().getString(R.string.exit_title);
		mTitle = "WARNING";
		mBody = mContext.getResources().getString(R.string.edit_confirm_title);
		okText = mContext.getResources().getString(R.string.save);
		cancelText = mContext.getResources().getString(R.string.cancel);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	public void setNegativeListener(View.OnClickListener cancel){
		negativeBtn = cancel;
	}
	
	public void setPositiveListener(View.OnClickListener positive){
		positiveBtn = positive;
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getBody() {
		return mBody;
	}

	public void setBody(String mBody) {
		this.mBody = mBody;
	}
	
	public String getOkText() {
		return okText;
	}

	public void setOkText(String okText) {
		this.okText = okText;
	}

	public String getCancelText() {
		return cancelText;
	}

	public void setCancelText(String cancelText) {
		this.cancelText = cancelText;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_edit_confirm);
		
		Typeface faceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Regular.ttf");
		Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Light.ttf");
		TextView title = (TextView) findViewById(R.id.title);
		TextView exitTV = (TextView) findViewById(R.id.exitTV);
		Button cancel = (Button) findViewById(R.id.cancel);
		Button save = (Button) findViewById(R.id.save);
		title.setTypeface(faceB);
		title.setText(getTitle().toUpperCase());
		exitTV.setTypeface(faceR);
		exitTV.setText(getBody());
		cancel.setTypeface(faceR);
		cancel.setText(getCancelText());
		cancel.setOnClickListener(negativeBtn);
		save.setTypeface(faceR);
		save.setText(getOkText());
		save.setOnClickListener(positiveBtn);
	}
	
}
