package com.diveboard.util;

import com.diveboard.mobile.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class EditDialog extends Dialog implements android.view.View.OnClickListener{

	protected String 					mTitle ="";
	protected String 					mBody = "";
	protected String 					okText, cancelText;
	protected Context 				mContext;
	protected View.OnClickListener 	positiveBtn, negativeBtn;	
	protected View 					mContent;
	
	public EditDialog(Activity a) {
		// TODO Auto-generated constructor stub
		super(a);
		mContext = a;
		mTitle = mContext.getResources().getString(R.string.exit_title);
		mBody = mContext.getResources().getString(R.string.edit_confirm_title);
		okText = mContext.getResources().getString(R.string.save);
		cancelText = mContext.getResources().getString(R.string.cancel);
		mContent = null;
		
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

	public View getmContent() {
		return mContent;
	}

	public void setmContent(View mContent) {
		this.mContent = mContent;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_edit_confirm);
		
		Typeface quickR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Quicksand-Regular.otf");
		Typeface faceR = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Light.ttf");
		TextView title = (TextView) findViewById(R.id.title);
		TextView exitTV = (TextView) findViewById(R.id.exitTV);
		LinearLayout dialogContent = (LinearLayout) findViewById(R.id.edit_dialog_content);
		Button cancel = (Button) findViewById(R.id.cancel);
		Button save = (Button) findViewById(R.id.save);
		title.setTypeface(faceR);
		title.setText(getTitle().toUpperCase());
		exitTV.setVisibility(View.GONE);
		if(mContent != null){
			LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			mContent.setLayoutParams(lp);
			dialogContent.setPadding(15, 15, 15, 15);
			dialogContent.setLayoutParams(lp);
			dialogContent.addView(mContent);
		}
			
		cancel.setTypeface(faceR);
		cancel.setText(getCancelText());
		cancel.setOnClickListener(negativeBtn);
		save.setTypeface(faceR);
		save.setText(getOkText());
		save.setOnClickListener(positiveBtn);
	}
	
}
