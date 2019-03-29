package com.diveboard.mobile;

import android.app.Application;

import com.diveboard.dataaccess.DivesOfflineRepository;
import com.diveboard.dataaccess.DivesOnlineRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.dataaccess.SpotsDbUpdater;
import com.diveboard.dataaccess.UserOfflineRepository;
import com.diveboard.model.AuthenticationService;
import com.diveboard.model.DivesService;
import com.diveboard.model.UserPreferenceService;
import com.diveboard.model.UserService;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.google.android.gms.analytics.GoogleAnalytics;

public class ApplicationController extends Application {

    public static boolean UserVoiceReady = false;
    private static ApplicationController singleton;
    private static GoogleAnalytics sAnalytics;
    public DiveDetailsViewModel currentDive;
    private SpotsDbUpdater spotsDbUpdater;
    private UserPreferenceService userPreferenceService;
    private AuthenticationService authenticationService;
    private SessionRepository sessionRepository;
    private UserOfflineRepository userOfflineRepository;
    private DivesService divesService;
    private UserService userService;

    public static ApplicationController getInstance() {
        return singleton;
    }

    public AuthenticationService getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = new AuthenticationService(this);
        }
        return authenticationService;
    }

    public UserPreferenceService getUserPreferenceService() {
        if (userPreferenceService == null) {
            userPreferenceService = new UserPreferenceService(this);
        }
        return userPreferenceService;
    }

    public SpotsDbUpdater getSpotsDbUpdater() {
        if (spotsDbUpdater == null) {
            spotsDbUpdater = new SpotsDbUpdater(this);
        }
        return spotsDbUpdater;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAnalytics = GoogleAnalytics.getInstance(this);
        singleton = this;
    }

    public SessionRepository getSessionRepository() {
        if (sessionRepository == null) {
            sessionRepository = new SessionRepository(this);
        }
        return sessionRepository;
    }

    public UserOfflineRepository getUserOfflineRepository() {
        if (userOfflineRepository == null) {
            userOfflineRepository = new UserOfflineRepository(this);
        }
        return userOfflineRepository;
    }

    public DivesService getDivesService() {
        if (divesService == null) {
            divesService = new DivesService(
                    this,
                    new DivesOfflineRepository(this),
                    new DivesOnlineRepository(this, getAuthenticationService(), getUserOfflineRepository()));
        }
        return divesService;
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = new UserService(new UserOfflineRepository(this));
        }
        return userService;
    }
}