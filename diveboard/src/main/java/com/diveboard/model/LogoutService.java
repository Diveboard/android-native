package com.diveboard.model;

import com.diveboard.dataaccess.DivesOfflineRepository;
import com.diveboard.dataaccess.SessionRepository;
import com.diveboard.dataaccess.SyncObjectRepository;
import com.diveboard.dataaccess.UserOfflineRepository;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

public class LogoutService {
    private final SessionRepository sessionRepository;
    private final UserOfflineRepository userOfflineRepository;
    private DivesOfflineRepository divesOfflineRepository;
    private SyncObjectRepository syncObjectRepository;

    public LogoutService(
            SessionRepository sessionRepository,
            UserOfflineRepository userOfflineRepository,
            DivesOfflineRepository divesOfflineRepository,
            SyncObjectRepository syncObjectRepository) {
        this.sessionRepository = sessionRepository;
        this.userOfflineRepository = userOfflineRepository;
        this.divesOfflineRepository = divesOfflineRepository;
        this.syncObjectRepository = syncObjectRepository;
    }

    public void logout() {
        sessionRepository.resetSession();
        userOfflineRepository.cleanUp();
        divesOfflineRepository.cleanUp();
        syncObjectRepository.purge();
        //facebook
        facebookLogout();
    }

    private void facebookLogout() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, null).executeAsync();
        LoginManager.getInstance().logOut();
    }
}
