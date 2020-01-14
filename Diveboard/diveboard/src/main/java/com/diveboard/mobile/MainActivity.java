package com.diveboard.mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import com.android.volley.toolbox.NetworkImageView;
import com.diveboard.dataaccess.SpotsDbUpdater;
import com.diveboard.dataaccess.datamodel.User;
import com.diveboard.model.UserService;
import com.diveboard.util.NetworkUtils;
import com.diveboard.util.ResponseCallback;
import com.diveboard.util.Utils;
import com.diveboard.viewModel.DrawerHeaderViewModel;
import com.google.android.material.navigation.NavigationView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ApplicationController ac;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ac = (ApplicationController) getApplicationContext();
        setContentView(R.layout.activity_main);
        updateSpots();
        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        view = findViewById(R.id.nav_host_fragment);
        //TODO: user might be loggged in but doesn't have dives.json and user.json stored locally
        boolean loggedIn = ac.getAuthenticationService().isLoggedIn();
        if (loggedIn) {
            Navigation.findNavController(view).navigate(R.id.logbook);
        } else {
            Navigation.findNavController(view).navigate(R.id.login);
        }
        setupNavigationHeader(loggedIn);
        setupNavigationMenu();
    }

    private void setupNavigationHeader(boolean loggedIn) {
        View header = navigationView.getHeaderView(0);
        DrawerHeaderViewModel viewModel = new DrawerHeaderViewModel();
        ((NetworkImageView) header.findViewById(R.id.profile_image)).setDefaultImageResId(R.drawable.ic_diveboard_grey2);
        ViewDataBinding binding = DataBindingUtil.bind(header);
        binding.setVariable(BR.model, viewModel);

        if (!loggedIn) {
            return;
        }

        UserService userService = ac.getUserService();

        userService.getUserAsync(new ResponseCallback<User>() {
            @Override
            public void success(User data) {
                viewModel.setName(data.nickname);
                viewModel.setProfileImageUrl(data.getSanitizedPictureUrl());
            }

            @Override
            public void error(Exception e) {
                Toast.makeText(ac, R.string.error_profile_message + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Utils.logError(MainActivity.class, "Cannot get user profile", e);
            }
        }, false);
    }

    public void setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    menuItem.setChecked(true);
                    drawerLayout.closeDrawers();
                    //menu id should be equal to page id
                    Navigation.findNavController(view).navigate(menuItem.getItemId());
                    return true;
                });
    }

    private void updateSpots() {
        SpotsDbUpdater dbUpdater = ac.getSpotsDbUpdater();
        if (dbUpdater.getSpotsFile() == null && NetworkUtils.isConnected(ac)) {
            dbUpdater.launchUpdate(null);
            return;
        }
        double daysSinceUpdated = Math.floor((((new Date().getTime() / (24.0 * 60 * 60 * 1000)) - (dbUpdater.getSpotsFile().lastModified() / (24.0 * 60 * 60 * 1000)))));
        if (NetworkUtils.isWifiAlive(ac) && daysSinceUpdated > 7) {
            dbUpdater.launchUpdate(null);
        }
    }
}
