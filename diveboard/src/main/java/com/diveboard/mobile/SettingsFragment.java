package com.diveboard.mobile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.diveboard.dataaccess.SpotsDbUpdater;
import com.diveboard.model.AuthenticationService;
import com.diveboard.util.Callback;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;
import com.uservoice.uservoicesdk.activity.ContactActivity;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey);
        ApplicationController ac = ((ApplicationController) getActivity().getApplicationContext());

        setAppVersion(ac);
        setSpotsInfo(ac);
        setLogout(ac);
        setReportBug(ac);
    }

    private void setReportBug(ApplicationController ac) {
        Preference preference = findPreference("report_bug");
        preference.setOnPreferenceClickListener(new ReportBugPreferenceClickListener(ac));
    }

    private void setLogout(ApplicationController ac) {
        Preference preference = findPreference("logout");
        preference.setOnPreferenceClickListener(new LogoutPreferenceClickListener(ac, this));
    }

    private void setSpotsInfo(ApplicationController ac) {
        SpotsDbUpdater dbUpdater = ac.getSpotsDbUpdater();
        File file = dbUpdater.getSpotsFile();
        String summary;
        if (file == null) {
            summary = getString(R.string.spots_db_never_updated);
        } else {
            DateFormat format = DateFormat.getDateInstance(DateFormat.DATE_FIELD, ac.getResources().getConfiguration().locale);
            summary = getString(R.string.spots_db_latest_update) + " " + format.format(new Date(file.lastModified()));
        }
        Preference preference = findPreference("db_version");
        preference.setSummary(summary);
        preference.setOnPreferenceClickListener(new SpotsPreferenceClickListener(ac));
    }

    private void setAppVersion(ApplicationController ac) {
        PackageInfo pInfo;
        String version;
        try {
            pInfo = ac.getApplicationContext().getPackageManager().getPackageInfo(ac.getPackageName(), 0);
            version = pInfo.versionName;
            findPreference("app_version").setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private class SpotsPreferenceClickListener implements Preference.OnPreferenceClickListener {
        private ApplicationController applicationController;

        SpotsPreferenceClickListener(ApplicationController applicationController) {
            this.applicationController = applicationController;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            applicationController.getSpotsDbUpdater().launchUpdate(new SpotsPreferenceClickCallback(applicationController));
            Toast toast = Toast.makeText(applicationController, R.string.spots_db_updating, Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
    }

    private class SpotsPreferenceClickCallback implements Callback<Boolean> {
        private ApplicationController applicationController;

        SpotsPreferenceClickCallback(ApplicationController applicationController) {
            this.applicationController = applicationController;
        }

        @Override
        public void execute(Boolean success) {
            if (success) {
                setSpotsInfo(applicationController);
            }
        }
    }

    private class LogoutPreferenceClickListener implements Preference.OnPreferenceClickListener {
        private ApplicationController ac;
        private SettingsFragment settingsFragment;

        LogoutPreferenceClickListener(ApplicationController ac, SettingsFragment settingsFragment) {
            this.ac = ac;
            this.settingsFragment = settingsFragment;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            logout();
            return true;
        }

        void logout() {
            if (ac.getSyncService().hasUnsynchedChanges()) {
                getConfirmationDialog().show();
            } else {
                doLogout();
            }
        }

        private void doLogout() {
            ac.getLogoutService().logout();
            ac.initCurrentUser();
            Navigation.findNavController(settingsFragment.getView()).navigate(SettingsPageDirections.actionSettingsToLogin());
        }

        private AlertDialog getConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.unsyncedChanges);
            builder.setPositiveButton(R.string.logout, (dialog, id) -> {
                doLogout();
            });
            builder.setNegativeButton(R.string.sync_changes, (dialog, id) -> {
                Navigation.findNavController(settingsFragment.getView()).navigate(R.id.logbook);
            });
            return builder.create();
        }
    }

    private class ReportBugPreferenceClickListener implements Preference.OnPreferenceClickListener {
        private ApplicationController ac;

        ReportBugPreferenceClickListener(ApplicationController ac) {
            this.ac = ac;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            WaitDialogFragment bugDialog = new WaitDialogFragment();
            bugDialog.show(getFragmentManager(), "WaitDialogFragment");
            Config config = new Config("diveboard.uservoice.com");
            AuthenticationService service;
            //TODO: fix email issue. where to get it from? it is not always presented in response. see API documentation
//            if (model.getSessionEmail() != null)
//                config.identifyUser(null, model.getUser().getNickname(), model.getSessionEmail());
            UserVoice.init(config, ac);
            config.setShowForum(false);
            config.setShowContactUs(true);
            config.setShowPostIdea(false);
            config.setShowKnowledgeBase(false);
            ApplicationController.UserVoiceReady = true;
            startActivity(new Intent(ac, ContactActivity.class));
//            UserVoice.launchContactUs(ac); // doesn't work on Android 9.0
            bugDialog.dismiss();
            return true;
        }
    }
}
