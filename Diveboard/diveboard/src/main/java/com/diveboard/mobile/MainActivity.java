package com.diveboard.mobile;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.diveboard.model.SpotsDbUpdater;
import com.diveboard.util.NetworkUtils;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {
    private ApplicationController ac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ac = (ApplicationController) getApplicationContext();
        setContentView(R.layout.activity_main);
        updateSpots();
        View view = this.findViewById(R.id.nav_host_fragment);
        if (ac.getAuthenticationService().isLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.logbook);
        } else {
            Navigation.findNavController(view).navigate(R.id.login);
        }
    }

    private void updateSpots() {
        SpotsDbUpdater dbUpdater = ac.getSpotsDbUpdater();
        double daysSinceUpdated = Math.floor((((new Date().getTime() / (24.0 * 60 * 60 * 1000)) - (dbUpdater.getSpotsFile().lastModified() / (24.0 * 60 * 60 * 1000)))));
        if (NetworkUtils.isWifiAlive(ac) && daysSinceUpdated > 7) {
            dbUpdater.launchUpdate(null);
        }
    }
}
