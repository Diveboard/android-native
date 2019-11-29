package com.diveboard.mobile;

import android.app.Application;

import androidx.room.Room;

import com.diveboard.dataaccess.DiveboardSearchBuddyRepository;
import com.diveboard.dataaccess.DivesOfflineRepository;
import com.diveboard.dataaccess.DivesOnlineRepository;
import com.diveboard.dataaccess.SearchShopRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.dataaccess.SpotsDbUpdater;
import com.diveboard.dataaccess.SyncObjectDatabase;
import com.diveboard.dataaccess.UserOfflineRepository;
import com.diveboard.dataaccess.UserOnlineRepository;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.model.AuthenticationService;
import com.diveboard.model.DivesService;
import com.diveboard.model.SyncService;
import com.diveboard.model.UserPreferenceService;
import com.diveboard.model.UserService;
import com.diveboard.util.ResponseCallback;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.concurrent.CompletableFuture;

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
    private User currentUser;
    private SyncService syncService;
    private SyncObjectDatabase db;

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

        //TODO: handle properly if user is not logged in
        getUserService().getUserAsync(new ResponseCallback<User, Exception>() {
            @Override
            public void success(User data) {
                currentUser = data;
            }

            @Override
            public void error(Exception e) {
//TODO: log exception
            }
        });
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
                    new DivesOfflineRepository(this, getSyncService()),
                    getOnlineRepository(),
                    getUserPreferenceService(),
                    getSyncService());
        }
        return divesService;
    }

    public UserOnlineRepository getUserOnlineRepository() {
        return new UserOnlineRepository(this, getAuthenticationService());
    }

    private DivesOnlineRepository getOnlineRepository() {
        return new DivesOnlineRepository(this, getAuthenticationService(), getUserOnlineRepository(), getUserOfflineRepository());
    }

    private SyncService getSyncService() {
        db = Room.databaseBuilder(this, SyncObjectDatabase.class, "sync-objects").allowMainThreadQueries().build();
        if (syncService == null) {
            syncService = new SyncService(db.userDao(), getOnlineRepository());
        }
        CompletableFuture feature;
        return syncService;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = new UserService(this, getUserOfflineRepository(), getUserOnlineRepository());
        }
        return userService;
    }

    public DiveboardSearchBuddyRepository getDiveboardSearchBuddyRepository() {
        return new DiveboardSearchBuddyRepository(this);
    }

    public SearchShopRepository getSearchShopRepository() {
        return new SearchShopRepository(this);
    }
}