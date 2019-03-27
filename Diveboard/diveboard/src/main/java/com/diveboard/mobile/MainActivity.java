package com.diveboard.mobile;

import android.content.Intent;
import android.os.Bundle;

import com.diveboard.model.SpotsDbUpdater;
import com.diveboard.util.NetworkUtils;

import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApplicationController ac = (ApplicationController) getApplicationContext();
//        ac.getSessionService().loginAsync("pavel.golub@gmail.co", "akniz1", null, null);
        if (ac.getAuthenticationService().isLoggedIn()) {

        } else {
            Navigation.findNavController(this.findViewById(R.id.nav_host_fragment)).navigate(R.id.login);
        }

//        DiveboardModel mModel = new DiveboardModel(ac);
//        if (!mModel.isLogged()) {
//
//        }
//        ac.setModel(mModel);
//        mModel.loadData();
        updateSpots();
    }

    private void updateSpots() {
        ApplicationController ac = (ApplicationController) getApplication();
        SpotsDbUpdater dbUpdater = ac.getSpotsDbUpdater();
        double daysSinceUpdated = Math.floor((((new Date().getTime() / (24.0 * 60 * 60 * 1000)) - (dbUpdater.getSpotsFile().lastModified() / (24.0 * 60 * 60 * 1000)))));
        if (NetworkUtils.isWifiAlive(ac) && daysSinceUpdated > 7) {
            dbUpdater.launchUpdate(null);
        }
    }
}
