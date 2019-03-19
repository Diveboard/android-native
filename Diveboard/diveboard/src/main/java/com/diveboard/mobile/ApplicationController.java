package com.diveboard.mobile;

import com.diveboard.model.Dive;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SpotsDbUpdater;
import com.diveboard.model.UserPreference;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;

public class ApplicationController extends Application {

    private static ApplicationController singleton;
    public static final String prefName = "appsettings";
    public static final String logBookModeKey = "logBookModeKey";
    public static final String logBookListMode = "list";
    private DiveboardModel mModel = null;
    private boolean mDataReady = false;
    public static boolean mDataRefreshed = false;
    public static boolean UserVoiceReady = false;
    private int mPageIndex;
    private int mCarouselIndex = 0;
    private Dive mtempDive = null;
    private int mRefresh = 0;
    private int mCurrentTab = 0;
    public static boolean mForceRefresh = false;
    public static int SudoId = 0;
    public static boolean tokenExpired = false;
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    private SpotsDbUpdater spotsDbUpdater;
    private UserPreference userPreference;

    public Boolean handleLowMemory() {
        if (getModel() == null) {
            Intent editDiveActivity = new Intent(getApplicationContext(), DiveboardLoginActivity.class);
            editDiveActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(editDiveActivity);
            return true;
        }
        return false;
    }

    public boolean isDataRefreshed() {
        return mDataRefreshed;
    }

    public void setDataRefreshed(boolean mDataRefreshed) {
        this.mDataRefreshed = mDataRefreshed;
    }

    public static ApplicationController getInstance() {
        return singleton;
    }

    public UserPreference getUserPreference() {
        if (userPreference == null) {
            userPreference = new UserPreference(this);
        }
        return userPreference;
    }

    public DiveboardModel getModel() {
        return mModel;
    }

    public SpotsDbUpdater getSpotsDbUpdater() {
        if (spotsDbUpdater == null) {
            spotsDbUpdater = new SpotsDbUpdater(this);
        }
        return spotsDbUpdater;
    }

    public boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PackageManager.PERMISSION_GRANTED == checkSelfPermission(permission);
        } else {
            return true;
        }
    }

    public void setModel(DiveboardModel mModel) {
        this.mModel = mModel;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAnalytics = GoogleAnalytics.getInstance(this);
        singleton = this;
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

    public DiveDetailsViewModel currentDive;

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public int getPageIndex() {
        return mPageIndex;
    }

    public void setPageIndex(int mPageIndex) {
        this.mPageIndex = mPageIndex;
    }

    public int getCarouselIndex() {
        return mCarouselIndex;
    }

    public void setCarouselIndex(int mCarouselIndex) {
        this.mCarouselIndex = mCarouselIndex;
    }

    public boolean isDataReady() {
        return mDataReady;
    }

    public void setDataReady(boolean mDataReady) {
        this.mDataReady = mDataReady;
    }

    public Dive getTempDive() {
        return mtempDive;
    }

    public void setTempDive(Dive mtempDive) {
        this.mtempDive = mtempDive;
    }

    public int getRefresh() {
        return mRefresh;
    }

    public void setRefresh(int mRefresh) {
        this.mRefresh = mRefresh;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public void setCurrentTab(int mCurrentTab) {
        this.mCurrentTab = mCurrentTab;
    }

    public void activityStart(Activity activity) {
        Tracker tracker = getDefaultTracker();
        tracker.setScreenName("Screen~" + activity.getLocalClassName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void activityStop(Activity activity) {
        //do nothing for now
    }

    public static Intent getDivesActivity(Activity activity) {
        //save setting
        SharedPreferences sharedPref = activity.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        if (logBookListMode.equals(sharedPref.getString(logBookModeKey, null))) {
            return new Intent(activity, DivesListActivity.class);
        } else {
            return new Intent(activity, DivesActivity.class);
        }
    }
}
