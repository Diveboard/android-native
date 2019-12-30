package com.diveboard.mobile;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.diveboard.dataaccess.DiveboardSearchBuddyRepository;
import com.diveboard.dataaccess.DivesOfflineRepository;
import com.diveboard.dataaccess.DivesOnlineRepository;
import com.diveboard.dataaccess.SearchShopRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.dataaccess.SpotsDbUpdater;
import com.diveboard.dataaccess.SyncObjectDatabase;
import com.diveboard.dataaccess.SyncObjectRepository;
import com.diveboard.dataaccess.UserOfflineRepository;
import com.diveboard.dataaccess.UserOnlineRepository;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.model.AuthenticationService;
import com.diveboard.model.DivesService;
import com.diveboard.model.LogoutService;
import com.diveboard.model.SyncService;
import com.diveboard.model.UserPreferenceService;
import com.diveboard.model.UserService;
import com.diveboard.util.Exclude;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.viewModel.DiveDetailsViewModel;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApplicationController extends MultiDexApplication {

    public static boolean UserVoiceReady = false;
    private static ApplicationController singleton;
    private static GoogleAnalytics sAnalytics;
    private static Gson gson;
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
    private DivesOfflineRepository divesOfflineRepository;
    private UserOnlineRepository userOnlineRepository;
    private DivesOnlineRepository divesOnlineRepository;
    private DiveboardSearchBuddyRepository diveboardSearchBuddyRepository;
    private SearchShopRepository searchShopRepository;
    private SyncObjectRepository syncObjectRepository;
    private LogoutService logoutService;

    public static ApplicationController getInstance() {
        return singleton;
    }

    public static Gson getGson() {
        if (gson == null) {
            ExclusionStrategy strategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }

                @Override
                public boolean shouldSkipField(FieldAttributes field) {
                    return field.getAnnotation(Exclude.class) != null;
                }
            };
            gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .addSerializationExclusionStrategy(strategy)
                    .create();
        }
        return gson;
    }

    public AuthenticationService getAuthenticationService() {
        if (authenticationService == null) {
            authenticationService = new AuthenticationService(this, getSessionRepository(), getUserOfflineRepository());
        }

        return authenticationService;
    }

    public LogoutService getLogoutService() {
        if (logoutService == null) {
            logoutService = new LogoutService(getSessionRepository(), getUserOfflineRepository(), getDivesOfflineRepository(), getSyncObjectRepository());
        }
        return logoutService;
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

        if (!getAuthenticationService().isLoggedIn()) {
            return;
        }
        getUserService().getUserAsync(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                currentUser = data;
            }

            @Override
            public void error(Exception e) {
                Utils.logError(ApplicationController.class, "Cannot login", e);
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
                    getDivesOfflineRepository(),
                    getDivesOnlineRepository(),
                    getUserPreferenceService(),
                    getSyncService());
        }
        return divesService;
    }

    private DivesOfflineRepository getDivesOfflineRepository() {
        if (divesOfflineRepository == null) {
            divesOfflineRepository = new DivesOfflineRepository(this, getSyncService());
        }
        return divesOfflineRepository;
    }

    public UserOnlineRepository getUserOnlineRepository() {
        if (userOnlineRepository == null) {
            userOnlineRepository = new UserOnlineRepository(this, getAuthenticationService());
        }
        return userOnlineRepository;
    }

    private DivesOnlineRepository getDivesOnlineRepository() {
        if (divesOnlineRepository == null) {
            divesOnlineRepository = new DivesOnlineRepository(this, getAuthenticationService(), getUserOnlineRepository(), getUserOfflineRepository());
        }
        return divesOnlineRepository;
    }

    public SyncService getSyncService() {
        if (syncService == null) {
            syncService = new SyncService(getSyncObjectRepository(), getDivesOnlineRepository());
        }
        return syncService;
    }

    private SyncObjectRepository getSyncObjectRepository() {
        if (syncObjectRepository == null) {
            SyncObjectDatabase db = Room.databaseBuilder(this, SyncObjectDatabase.class, "sync-objects").allowMainThreadQueries().build();
            syncObjectRepository = db.syncObjectRepository();
        }
        return syncObjectRepository;
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
        if (diveboardSearchBuddyRepository == null) {
            diveboardSearchBuddyRepository = new DiveboardSearchBuddyRepository(this);
        }
        return diveboardSearchBuddyRepository;
    }

    public SearchShopRepository getSearchShopRepository() {
        if (searchShopRepository == null) {
            searchShopRepository = new SearchShopRepository(this, getAuthenticationService());
        }
        return searchShopRepository;
    }
}