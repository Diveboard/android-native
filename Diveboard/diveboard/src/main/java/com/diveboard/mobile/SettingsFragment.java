package com.diveboard.mobile;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.diveboard.config.AppConfig;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.SpotsDbUpdater;
import com.diveboard.util.Callback;
import com.uservoice.uservoicesdk.Config;
import com.uservoice.uservoicesdk.UserVoice;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_screen, rootKey);
        ApplicationController ac = ((ApplicationController) getActivity().getApplicationContext());

        Preference cachePreference = this.findPreference("clear_cache");
        cachePreference.setOnPreferenceClickListener(new CacheOnPreferenceClickListener(ac));
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
        preference.setOnPreferenceClickListener(new LogoutPreferenceClickListener(ac));
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
            version = AppConfig.DEBUG_MODE == 1 ? pInfo.versionName + " Debug Build" : pInfo.versionName;
            findPreference("app_version").setSummary(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class CacheOnPreferenceClickListener implements Preference.OnPreferenceClickListener {
        private ApplicationController applicationController;

        CacheOnPreferenceClickListener(ApplicationController applicationController) {
            this.applicationController = applicationController;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            applicationController.getModel().getDataManager().trimCache(applicationController);
            Toast toast = Toast.makeText(applicationController, R.string.cache_cleared, Toast.LENGTH_SHORT);
            toast.show();
            return false;
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

        public LogoutPreferenceClickListener(ApplicationController ac) {
            this.ac = ac;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            logout();
            return true;
        }

        void logout() {
            ac.getAuthenticationService().logout();
            Intent loginActivity = new Intent(ac, DiveboardLoginActivity.class);
            loginActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginActivity);
        }
    }

    private class ReportBugPreferenceClickListener implements Preference.OnPreferenceClickListener {
        private ApplicationController ac;

        public ReportBugPreferenceClickListener(ApplicationController ac) {
            this.ac = ac;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            // Use of UserVoice report bug system
            DiveboardModel model = ac.getModel();
            WaitDialogFragment bugDialog = new WaitDialogFragment();
            bugDialog.show(getFragmentManager(), "WaitDialogFragment");
            Config config = new Config("diveboard.uservoice.com");
            if (model.getSessionEmail() != null)
                config.identifyUser(null, model.getUser().getNickname(), model.getSessionEmail());
            UserVoice.init(config, ac);
            config.setShowForum(false);
            config.setShowContactUs(true);
            config.setShowPostIdea(false);
            config.setShowKnowledgeBase(false);
            ApplicationController.UserVoiceReady = true;
            UserVoice.launchContactUs(ac);
            bugDialog.dismiss();
            return true;
        }
    }
}
