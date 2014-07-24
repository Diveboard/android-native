package com.diveboard.mobile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import com.diveboard.mobile.WalletActivity.SaveChangesDialog;
import com.diveboard.mobile.newdive.NewDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.facebook.Session;

public class NavDrawer extends FragmentActivity {
	
	private DiveboardModel 					mModel;
	private ApplicationController			AC;
	
	//controls for navigation drawer
	private DrawerLayout 					mDrawerLayout;
	private ListView 						mDrawerList;
	private LinearLayout 					mDrawerContainer;
	private String[] 						mLinksTitles;
	

	protected void onCreate(Bundle savedInstanceState, int resLayoutID) {
		super.onCreate(savedInstanceState);
	    setContentView(resLayoutID);
		AC = (ApplicationController)getApplicationContext();
		mModel = AC.getModel();
		//Setting up controls for the navigation drawer 
        if(AC.getModel().hasRatedApp() != null && AC.getModel().hasRatedApp()){
			mLinksTitles = getResources().getStringArray(R.array.menu_links_has_rated);
        }
        else
        	mLinksTitles = getResources().getStringArray(R.array.menu_links_has_not_rated);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContainer = (LinearLayout) findViewById(R.id.left_drawer_cont);
        mDrawerList = (ListView) findViewById(R.id.menu_links);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(AC, R.layout.drawer_list_item, mLinksTitles){
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		// TODO Auto-generated method stub
        		
        		Typeface mFaceR = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Regular.ttf");
        		View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Light.ttf"));
				
				return v;
        		
        	}
        });
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        ImageView mDrawerTitle = (ImageView)findViewById(R.id.drawer_title);
        mDrawerTitle.setImageDrawable(getResources().getDrawable(R.drawable.logo_250));
        ImageView mDrawerMenu = (ImageView)findViewById(R.id.ic_drawer);
        mDrawerMenu.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDrawerLayout.openDrawer(mDrawerContainer);
			}
		});
	}
	
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
    	int currDive = AC.getModel().getDives().size() - AC.getPageIndex() - 1;
    	Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
    	//Check there are not unsaved changes
    	if ((currDive >= 0 && mModel.getDives().get(currDive).getEditList().size() > 0)
    			|| (mModel.getUser().getEditList().size() > 0)
    			|| (mDive != null && mDive.getEditList() != null && mDive.getEditList().size() > 0)) 
    	{			
    		SaveChangesDialog dialog = new SaveChangesDialog(this, position);
    		dialog.show();
    	}
    	else{
    		switch (position) {	
    		// Logbook
    		case 0:
    			Intent logbookActivity = new Intent(this, DivesActivity.class);
    			startActivity(logbookActivity);
    			if(!DivesActivity.active)
    				finish();
    			break;

    			// Refresh
    		case 1:
    			AC.setDataReady(false);
    			AC.getModel().stopPreloadPictures();
    			ApplicationController.mForceRefresh = true;
    			AC.setModel(null);
    			finish();
    			break;

    			// Wallet Activity
    		case 2:
    			Intent walletActivity = new Intent(this, WalletActivity.class);
    			startActivity(walletActivity);
    			if(!DivesActivity.active)
    				finish();
    			break;

    			// Closest Shop
    		case 3:
    			Intent closestShopActivity = new Intent(this, ClosestShopActivity.class);
    			startActivity(closestShopActivity);
    			if(!DivesActivity.active)
    				finish();
    			break;

    			// New Dive
    		case 4:
    			Intent newDiveActivity = new Intent(this, NewDiveActivity.class);
    			startActivity(newDiveActivity);
    			if(!DivesActivity.active)
    				finish();
    			break;

    			// Settings
    		case 5:
    			Intent settingsActivity = new Intent(this, SettingsActivity.class);
    			startActivity(settingsActivity);
    			if(!DivesActivity.active)
    				finish();
    			break;

    			// bug report
    		case 6:

    			// Use of UserVoice report bug system
    			WaitDialogFragment bugDialog = new WaitDialogFragment();
    			bugDialog.show(getSupportFragmentManager(), "WaitDialogFragment");
    			Config config = new Config("diveboard.uservoice.com");
    			if (mModel.getSessionEmail() != null)
    				config.identifyUser(null, mModel.getUser().getNickname(), mModel.getSessionEmail());
    			UserVoice.init(config, this);
    			config.setShowForum(false);
    			config.setShowContactUs(true);
    			config.setShowPostIdea(false);
    			config.setShowKnowledgeBase(false);
    			ApplicationController.UserVoiceReady = true;
    			UserVoice.launchContactUs(this);
    			bugDialog.dismiss();

    			break;

    			// Logout
    		case 7:
    			final Dialog dialog = new Dialog(this);
    			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    			dialog.setContentView(R.layout.dialog_edit_confirm);
    			Typeface faceB = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Quicksand-Regular.otf");
    			Typeface faceR = Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/Lato-Light.ttf");
    			TextView title = (TextView) dialog.findViewById(R.id.title);
    			TextView exitTV = (TextView) dialog.findViewById(R.id.exitTV);
    			title.setTypeface(faceB);
    			title.setText(getResources().getString(R.string.exit_title));
    			exitTV.setTypeface(faceR);
    			exitTV.setText(getResources().getString(R.string.confirm_exit));
    			Button cancel = (Button) dialog.findViewById(R.id.cancel);
    			cancel.setTypeface(faceR);
    			cancel.setText(getResources().getString(R.string.cancel));
    			cancel.setOnClickListener(new View.OnClickListener() {

    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					dialog.dismiss();
    				}
    			});
    			Button save = (Button) dialog.findViewById(R.id.save);
    			save.setTypeface(faceR);
    			save.setText(getResources().getString(R.string.menu_logout));
    			save.setOnClickListener(new View.OnClickListener() {

    				@Override
    				public void onClick(View v) {
    					// TODO Auto-generated method stub
    					logout();
    				}
    			});
    			dialog.show();
    			break;

    			// Rate app
    		case 8:
    			mModel.setHasRatedApp(true);
    			try {
    				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AppRater.APP_PNAME)));
    			} catch (android.content.ActivityNotFoundException anfe) {
    				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + AppRater.APP_PNAME)));
    			}
    			break;

    		default:
    			break;

    		}

    		// update selected item and title, then close the drawer
    		mDrawerList.setItemChecked(position, false);
    		mDrawerLayout.closeDrawer(mDrawerContainer);
    	}
    }
    
    public void logout()
	{
		if (Session.getActiveSession() != null)
			Session.getActiveSession().closeAndClearTokenInformation();
		Session.setActiveSession(null);
		ApplicationController AC = (ApplicationController)getApplicationContext();
    	AC.setDataReady(false);
    	AC.setPageIndex(0);
    	AC.getModel().doLogout();
    	finish();
	}
    
    public class SaveChangesDialog extends Dialog implements android.view.View.OnClickListener{
    	int pos;
		public SaveChangesDialog(Activity a, int nextActivity) {
			super(a);
			pos = nextActivity;
		}
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_edit_confirm);
			
			Typeface quickR = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Quicksand-Regular.otf");
			Typeface faceR = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
			Typeface faceB = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Regular.ttf");
    		TextView title = (TextView) findViewById(R.id.title);
    		TextView exitTV = (TextView) findViewById(R.id.exitTV);
    		title.setTypeface(quickR);
    		title.setText(getResources().getString(R.string.exit_title));
    		exitTV.setTypeface(faceR);
    		exitTV.setText(getResources().getString(R.string.edit_confirm_title));
			Button cancel = (Button) findViewById(R.id.cancel);
			cancel.setTypeface(faceR);
			cancel.setText(getResources().getString(R.string.cancel));
			cancel.setOnClickListener(this);
			Button save = (Button) findViewById(R.id.save);
			save.setTypeface(faceR);
			save.setText(getResources().getString(R.string.save));
			save.setOnClickListener(this);
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
		    case R.id.cancel:
		    	mDrawerLayout.closeDrawer(mDrawerContainer);
		    	mDrawerList.setItemChecked(pos, false);
		    	break;
		    case R.id.save:
		    	mModel.getUser().clearEditList();
		    	Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
		    	if (mDive != null)
		    		mDive.clearEditList();
		    	int currDive = AC.getModel().getDives().size() - AC.getPageIndex() - 1;
		    	if(currDive >= 0){
		    		mModel.getDives().get(currDive).clearEditList(); 
		    	}
		    	selectItem(pos);
//				Intent intent = new Intent(mContext, DivesActivity.class);
//				startActivity(intent);
//				finish();
		      break;
		    default:
		      break;
		    }
		    dismiss();
		}
	}
}
