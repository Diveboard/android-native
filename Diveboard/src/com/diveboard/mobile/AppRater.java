package com.diveboard.mobile;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AppRater {
    private final static String APP_PNAME = "com.diveboard.mobile";
    
    private static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 10;
    
    public static void app_launched(ApplicationController AC, Context mContext) {
    	//first time user logs initialization
        if(AC.getModel().hasRatedApp() == null || AC.getModel().getFirstLaunch() == null || AC.getModel().getLaunchCount() == null){
        	AC.getModel().setFirstLaunch(0L);
        	AC.getModel().setLaunchCount(0L);
        	AC.getModel().setHasRatedApp(false);
        }
        	
        if (AC.getModel().hasRatedApp()) { 
        	System.out.println("User has already rated the app");
        	return ; 
        }
        
        // Increment launch counter
        Long launch_count = AC.getModel().getLaunchCount() + 1L;
        AC.getModel().setLaunchCount(launch_count);
        
        // Get date of first launch
        Long date_firstLaunch = AC.getModel().getFirstLaunch();
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            AC.getModel().setFirstLaunch(date_firstLaunch);
        }
        System.out.println("LAUNCH COUNTER: " + launch_count );
        System.out.println("DATE FIRST LAUNCH: " + new SimpleDateFormat("MMM dd,yyyy HH:mm").format(new Date(date_firstLaunch)));
        
        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(AC, mContext);
            }
        }

    }   
    
    public static void showRateDialog(final ApplicationController AC, final Context mContext) {
//    	final Context mContext = AC.getApplicationContext();
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_rate);
        dialog.findViewById(R.id.title);
        Typeface faceB = Typeface.createFromAsset(AC.getAssets(), "fonts/Quicksand-Bold.otf");
        Typeface faceR = Typeface.createFromAsset(AC.getAssets(), "fonts/Quicksand-Regular.otf");
        TextView title = (TextView) dialog.findViewById(R.id.title);
		TextView body = (TextView) dialog.findViewById(R.id.bodyTV);
		Button b1 = (Button) dialog.findViewById(R.id.cancel);
		Button b2 = (Button) dialog.findViewById(R.id.later);
		Button b3 = (Button) dialog.findViewById(R.id.rate);
		title.setTypeface(faceB);
		title.setText(AC.getResources().getString(R.string.rate_title));
        body.setTypeface(faceR);
        body.setText(AC.getResources().getString(R.string.rate_text));
        b1.setTypeface(faceR);
        b2.setTypeface(faceR);
        b3.setTypeface(faceR);        
        b1.setText(AC.getResources().getString(R.string.rate_refuse));
        b2.setText(AC.getResources().getString(R.string.rate_later));
        b3.setText(AC.getResources().getString(R.string.rate_accept));
        
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	AC.getModel().setHasRatedApp(true);
                dialog.dismiss();
            }
        });
        
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                AC.getModel().setHasRatedApp(false);
                DAYS_UNTIL_PROMPT++;
            }
        });
        
        b3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				AC.getModel().setHasRatedApp(true);
                dialog.dismiss();
			}
		});
        
        dialog.show();        
    }
}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater