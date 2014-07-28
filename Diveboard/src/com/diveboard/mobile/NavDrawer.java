package com.diveboard.mobile;

import java.util.ArrayList;
import java.util.Arrays;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.diveboard.mobile.newdive.NewDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.util.ExitDialog;
import com.facebook.Session;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

public class NavDrawer extends FragmentActivity {
	
	private DiveboardModel 					mModel;
	private ApplicationController			AC;
	
	//controls for navigation drawer
	private DrawerLayout 					mDrawerLayout;
	protected ListView 						mDrawerList;
	private LinearLayout 					mDrawerContainer;
	protected ArrayList<String> 						mLinksTitles;
	

	protected void onCreate(Bundle savedInstanceState, int resLayoutID) {
		super.onCreate(savedInstanceState);
	    setContentView(resLayoutID);
		AC = (ApplicationController)getApplicationContext();
		mModel = AC.getModel();
		mLinksTitles = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.menu_links_has_rated)));
		//Setting up controls for the navigation drawer 
//        if(AC.getModel().hasRatedApp() != null && AC.getModel().hasRatedApp()){
//			
//        }
//        else
//        	mLinksTitles = getResources().getStringArray(R.array.menu_links_has_not_rated);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContainer = (LinearLayout) findViewById(R.id.left_drawer_cont);
        mDrawerList = (ListView) findViewById(R.id.menu_links);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        final Typeface faceR = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(AC, R.layout.drawer_list_item, mLinksTitles){
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		// TODO Auto-generated method stub
        		View v = super.getView(position, convertView, parent);
				((TextView) v).setTypeface(faceR);
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
            navigateTo(position);
        }
    }
    
    public void navigateTo(final int position) {
    	int currDive = AC.getModel().getDives().size() - AC.getPageIndex() - 1;
    	Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
    	//Check there are not unsaved changes
    	if (mModel.getDives().size() > 0 &&
    			(currDive >= 0 && mModel.getDives().get(currDive).getEditList().size() > 0)
    			|| (mModel.getUser().getEditList().size() > 0)
    			|| (mDive != null && mDive.getEditList() != null && mDive.getEditList().size() > 0)) 
    	{			
    		final ExitDialog saveDialog = new ExitDialog(this);
    		saveDialog.setTitle(getResources().getString(R.string.exit_title));
    		saveDialog.setBody(getResources().getString(R.string.edit_confirm_title));
    		saveDialog.setPositiveListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mModel.getUser().clearEditList();
			    	Dive mDive = ((ApplicationController)getApplicationContext()).getTempDive();
			    	if (mDive != null)
			    		mDive.clearEditList();
			    	int currDive = AC.getModel().getDives().size() - AC.getPageIndex() - 1;
			    	if(currDive >= 0){
			    		mModel.getDives().get(currDive).clearEditList(); 
			    	}
			    	navigateTo(position);
			    	saveDialog.dismiss();
				}
			});
    		
    		saveDialog.setNegativeListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mDrawerLayout.closeDrawer(mDrawerContainer);
			    	mDrawerList.setItemChecked(position, false);
			    	saveDialog.dismiss();
				}
			});
    		
    		saveDialog.show();
    	}
    	else{
    		switch (position) {	
    		// Logbook
    		case 0:
    			if(!(this instanceof DivesActivity)){
					((ApplicationController)getApplicationContext()).setRefresh(1);
    				finish();
    			}

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
    			if(!(this instanceof WalletActivity)){
    				Intent walletActivity = new Intent(this, WalletActivity.class);
    				startActivity(walletActivity);
    				if(!(this instanceof DivesActivity))
    					finish();
    			}
    			break;

    			// Closest Shop
    		case 3:
    			if(!(this instanceof ClosestShopActivity)){
    				Intent closestShopActivity = new Intent(this, ClosestShopActivity.class);
    				startActivity(closestShopActivity);
    				if(!(this instanceof DivesActivity))
    					finish();
    			}
    			break;

    			// New Dive
    		case 4:
    			if(!(this instanceof NewDiveActivity)){
    				Intent newDiveActivity = new Intent(this, NewDiveActivity.class);
    				startActivity(newDiveActivity);
    				if(!(this instanceof DivesActivity))
    					finish();
    			}
    			break;

    			// Settings
    		case 5:
    			Intent settingsActivity = new Intent(this, SettingsActivity.class);
    			startActivity(settingsActivity);
    			if(!(this instanceof DivesActivity))
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
    			final ExitDialog exitDialog = new ExitDialog(this);
    			exitDialog.setTitle(getResources().getString(R.string.exit_title));
    			exitDialog.setBody(getResources().getString(R.string.confirm_logout));
    			exitDialog.setPositiveListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						logout();
					}
				});

    			exitDialog.setNegativeListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						exitDialog.dismiss();
					}
				});
    			
    			exitDialog.show();
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
    	Intent loginActivity = new Intent(this, DiveboardLoginActivity.class);
    	loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(loginActivity);
    	
	}

}
