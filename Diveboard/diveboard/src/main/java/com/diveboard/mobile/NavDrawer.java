package com.diveboard.mobile;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.diveboard.mobile.editdive.EditDiveActivity;
import com.diveboard.mobile.newdive.NewDiveActivity;
import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.util.ExitDialog;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

public abstract class NavDrawer extends FragmentActivity {
	
	private DiveboardModel 					mModel;
	private ApplicationController			AC;
	
	//controls for navigation drawer
	private DrawerLayout 					mDrawerLayout;
	protected ListView 						mDrawerList;
	private LinearLayout 					mDrawerContainer;
	protected ArrayList<String> 			mLinksTitles;
	private Activity 						mActivity;
	protected Object mBinding;
	

	protected void onCreate(Bundle savedInstanceState, int resLayoutID) {
		super.onCreate(savedInstanceState);
		mBinding=DataBindingUtil.setContentView(this, resLayoutID);
//	    setContentView(resLayoutID);
		AC = (ApplicationController)getApplicationContext();
		mModel = AC.getModel();
		mLinksTitles = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.menu_links_has_rated)));
		mActivity = this;
		if (AC.getModel() != null && (AC.getModel().hasRatedApp() != null && !AC.getModel().hasRatedApp()))
			mLinksTitles.add(getString(R.string.menu_links_has_not_rated));
		//Setting up controls for the navigation drawer 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerContainer = (LinearLayout) findViewById(R.id.left_drawer_cont);
        mDrawerList = (ListView) findViewById(R.id.menu_links);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        final Typeface faceR = mModel.getLatoR();
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(AC, R.layout.drawer_list_item, mLinksTitles){
        	
        LayoutInflater inflater = LayoutInflater.from(AC.getApplicationContext());
        	@Override
        	public View getView(int position, View convertView, ViewGroup parent) {
        		// TODO Auto-generated method stub
        		
//        		View ov = super.getView(position, convertView, parent);
        		ViewHolder mViewHolder;
        		if (convertView == null) {  
        			convertView = inflater.inflate(R.layout.drawer_list_item, null);  
        		} 
        		LinearLayout ll = (LinearLayout) convertView;
        		View line = (View)ll.getChildAt(0);
        		TextView t = (TextView)ll.getChildAt(1);
				t.setTypeface(faceR);
				t.setText(mLinksTitles.get(position));
				line.setVisibility(View.GONE);
				
				if((mActivity instanceof DivesActivity || mActivity instanceof EditDiveActivity ) && position == 0)
					line.setVisibility(View.VISIBLE);
				else if(mActivity instanceof NewDiveActivity && position == 1)
		        	line.setVisibility(View.VISIBLE);
				else if(mActivity instanceof StatisticActivity && position == 2)
					line.setVisibility(View.VISIBLE);
		        else if(mActivity instanceof WalletActivity && position == 3)
		        	line.setVisibility(View.VISIBLE);
		        else if(mActivity instanceof ClosestShopActivity && position == 4)
		        	line.setVisibility(View.VISIBLE);
		        
				if(position <= 4)
		        {
		        	t.setTextSize(20);
		        	t.setAllCaps(false);
		        }else{
		        	t.setTextSize(12);
		        	t.setAllCaps(true);
		        }
		        
				
				
				return convertView;
        		
        	}
        });
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        ImageView mDrawerTitle = (ImageView)findViewById(R.id.drawer_title);
        mDrawerTitle.setImageDrawable(getResources().getDrawable(R.drawable.logo_250));
        ImageView mDrawerMenu = (ImageView)findViewById(R.id.ic_drawer);
        if(mDrawerMenu != null)
        	mDrawerMenu.setOnClickListener(new View.OnClickListener() {

        		@Override
        		public void onClick(View v) {
        			// TODO Auto-generated method stub
        			mDrawerLayout.openDrawer(mDrawerContainer);
        		}
        	});
        
	}
	
	private class ViewHolder {  
		TextView tvTitle;  
		View vLine;  
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
    			
    		//New Dive
    		case 1:
    			if(!(this instanceof NewDiveActivity)){
    				Intent newDiveActivity = new Intent(this, DiveDetailsFragment2.class);
    				startActivity(newDiveActivity);
    				if(!(this instanceof DivesActivity))
    					finish();
    			}
    			break;
    			
    		// Statistics
    		case 2:
                if(!(this instanceof StatisticActivity)){
                    Intent intent = new Intent(this, StatisticActivity.class);
                    startActivity(intent);
                    if(!(this instanceof DivesActivity))
                        finish();
                }
                break;

    		// Wallet Activity
    		case 3:
    			if(!(this instanceof WalletActivity)){
    				Intent walletActivity = new Intent(this, SelectSpotFragment.class);
    				startActivity(walletActivity);
    				if(!(this instanceof DivesActivity))
    					finish();
    			}
    			break;

    		// Closest Shop
    		case 4:
    			if(!(this instanceof ClosestShopActivity)){
    				Intent closestShopActivity = new Intent(this, ClosestShopActivity.class);
    				startActivity(closestShopActivity);
    				if(!(this instanceof DivesActivity))
    					finish();
    			}
    			break;
    			
    		// Refresh
    		case 5:
    			AC.setDataReady(false);
    			AC.getModel().stopPreloadPictures();
    			ApplicationController.mForceRefresh = true;
    			AC.setModel(null);
    			finish();
    			break;
    			
    		// Settings
    		case 6:
    			Intent settingsActivity = new Intent(this, SettingsActivity2.class);
    			startActivity(settingsActivity);
    			if(!(this instanceof DivesActivity))
    				finish();
    			break;

    			// bug report
    		case 7:

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
    		case 8:
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
    		case 9:
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
	}
}
