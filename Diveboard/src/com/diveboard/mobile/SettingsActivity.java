package com.diveboard.mobile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import com.diveboard.mobile.newdive.NewDiveTypeDialogFragment;
import com.diveboard.model.DiveboardModel;
import com.diveboard.model.Units;
import com.diveboard.model.UserPreference;
import com.google.analytics.tracking.android.EasyTracker;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private ListPreference mUnitSetting;
	
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	
	private DiveboardModel		mModel;

	@Override
	protected void onResume()
	{
		super.onResume();
		ApplicationController AC = (ApplicationController)getApplicationContext();
		AC.handleLowMemory();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApplicationController AC = (ApplicationController)getApplicationContext();
		if (AC.handleLowMemory() == true)
			return ;
		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressLint("NewApi")
	private void setupSimplePreferencesScreen() {
//		if (!isSimplePreferences(this)) {
//			return;
//		}
		ApplicationController AC = (ApplicationController)getApplicationContext();
		mModel = AC.getModel();
//		PreferenceCategory fakeHeader = new PreferenceCategory(this);
//		fakeHeader.setTitle("Pictures loading remaining: " + AC.getModel().getPictureCount());
//		getPreferenceScreen().addPreference(fakeHeader);
		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		
		PreferenceCategory userSettings = new PreferenceCategory(this);
		userSettings.setTitle(getResources().getString(R.string.user_settings_category));
		
		getPreferenceScreen().addPreference(userSettings);
		mUnitSetting = new ListPreference(this);
		//mUnitSetting.setEnabled(false);
		mUnitSetting.setTitle(getResources().getString(R.string.unit_setting_title));
		mUnitSetting.setEntries(getResources().getStringArray(R.array.unit_entries));
		mUnitSetting.setEntryValues(getResources().getStringArray(R.array.unit_entries));
		mUnitSetting.setKey("unit");
		
		try {
			if (UserPreference.getUnits().getString("distance").equals("Km"))
			{
				mUnitSetting.setValueIndex(1);
				mUnitSetting.setValue("Metric");
			}
			else
			{
				mUnitSetting.setValueIndex(0);
				mUnitSetting.setValue("Imperial");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		mUnitSetting.setSummary(mUnitSetting.getEntry());
		mUnitSetting.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(Preference pref, Object newVal)
			{
				ListPreference unit = (ListPreference) findPreference("unit");
				pref.setSummary(newVal.toString());
				String val = (String) newVal;
				if (val.equals("Metric"))
					mModel.getPreference().setUnits(1);
				else
					mModel.getPreference().setUnits(0);
				try {
					mModel.getUser().setUnitPreferences(new Units(UserPreference.getUnits()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				((ApplicationController)getApplicationContext()).setRefresh(1);
				return true;
			}
			
		});
		
		getPreferenceScreen().addPreference(mUnitSetting);
		
		PreferenceCategory systemCategory = new PreferenceCategory(this);
		systemCategory.setTitle(getResources().getString(R.string.system_settings_category));
		getPreferenceScreen().addPreference(systemCategory);		
		
//		if (Build.VERSION.SDK_INT >= 14)
//		{
//			SwitchPreference phoneNetworkDownload = new SwitchPreference(this);
//			phoneNetworkDownload.setChecked(true);
//			phoneNetworkDownload.setTitle(getResources().getString(R.string.phone_network_download_title));
//			phoneNetworkDownload.setSummary(getResources().getString(R.string.phone_network_download_summary));
//			
//			getPreferenceScreen().addPreference(phoneNetworkDownload);
//		}
//		else
//		{
			CheckBoxPreference phoneNetworkDownload = new CheckBoxPreference(this);
			if (mModel.getPreference().getNetwork() == 1)
				phoneNetworkDownload.setChecked(true);
			else
				phoneNetworkDownload.setChecked(false);
			phoneNetworkDownload.setTitle(getResources().getString(R.string.phone_network_download_title));
			phoneNetworkDownload.setSummary(getResources().getString(R.string.phone_network_download_summary));
			phoneNetworkDownload.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					Boolean val = (Boolean) newValue;
					if (val == true)
						mModel.getPreference().setNetwork(1);
					else
						mModel.getPreference().setNetwork(0);
					return true;
				}
			});
			//phoneNetworkDownload.setEnabled(false);
			
			getPreferenceScreen().addPreference(phoneNetworkDownload);
			
			DialogPreference connectionTimeout = new CoTimeoutDialog(this, null);
			connectionTimeout.setTitle("Connection Timeout");
			connectionTimeout.setSummary(DiveboardModel._coTimeout + "ms");
			connectionTimeout.setKey("cotime");
			connectionTimeout.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					preference.setSummary(DiveboardModel._coTimeout + "ms");
					return false;
				}
			});
			getPreferenceScreen().addPreference(connectionTimeout);
			DialogPreference socketTimeout = new SoTimeoutDialog(this, null);
			socketTimeout.setTitle("Socket Timeout");
			socketTimeout.setSummary(DiveboardModel._soTimeout + "ms");
			socketTimeout.setKey("sotime");
			socketTimeout.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					preference.setSummary(DiveboardModel._soTimeout + "ms");
					return false;
				}
			});
			getPreferenceScreen().addPreference(socketTimeout);
//		}
		
		// Add 'notifications' preferences, and a corresponding header.
//		PreferenceCategory fakeHeader = new PreferenceCategory(this);
//		
//		//fakeHeader.setTitle(R.string.pref_header_notifications);
//		fakeHeader.setTitle("Pictures loading remaining: " + AC.getModel().getPictureCount());
//		getPreferenceScreen().addPreference(fakeHeader);
		//addPreferencesFromResource(R.xml.pref_notification);

		// Add 'data and sync' preferences, and a corresponding header.
//		fakeHeader = new PreferenceCategory(this);
//		fakeHeader.setTitle(R.string.pref_header_data_sync);
//		getPreferenceScreen().addPreference(fakeHeader);
//		addPreferencesFromResource(R.xml.pref_data_sync);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
//		bindPreferenceSummaryToValue(findPreference("example_text"));
//		bindPreferenceSummaryToValue(findPreference("example_list"));
//		bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//		bindPreferenceSummaryToValue(findPreference("sync_frequency"));
			PreferenceCategory applicationInfo = new PreferenceCategory(this);
			applicationInfo.setTitle(getResources().getString(R.string.misc_category));
			
			getPreferenceScreen().addPreference(applicationInfo);
			
			Preference remainingPictures = new Preference(this);
			remainingPictures.setEnabled(true);
			remainingPictures.setKey("remaining");
			remainingPictures.setTitle(getResources().getString(R.string.remaining_pictures_title) + ": " + AC.getModel().getPictureCount());
			remainingPictures.setOnPreferenceClickListener(new OnPreferenceClickListener()
			{
				@Override
				public boolean onPreferenceClick(Preference arg0)
				{
					Preference remaining = (Preference) findPreference("remaining");
					remaining.setTitle(getResources().getString(R.string.remaining_pictures_title) + ": " + ((ApplicationController)getApplicationContext()).getModel().getPictureCount());
					return true;
				}
			});
			//remainingPictures.setSummary(getResources().getString(R.string.remaining_pictures_summary) + " " + AC.getModel().getPictureCount());
			
			getPreferenceScreen().addPreference(remainingPictures);
			
			Preference remainingRequest = new Preference(this);
			remainingRequest.setEnabled(true);
			remainingRequest.setKey("remaining_req");
			remainingRequest.setTitle(getResources().getString(R.string.remaining_requests_title) + ": " + ((ApplicationController)getApplicationContext()).getModel().getDataManager().getEditList().size());
			remainingRequest.setOnPreferenceClickListener(new OnPreferenceClickListener()
			{
				@Override
				public boolean onPreferenceClick(Preference arg0)
				{
					Preference remaining = (Preference) findPreference("remaining_req");
					remaining.setTitle(getResources().getString(R.string.remaining_requests_title) + ": " + ((ApplicationController)getApplicationContext()).getModel().getDataManager().getEditList().size());
					return true;
				}
			});
			
			getPreferenceScreen().addPreference(remainingRequest);
			
			Preference db_version = new Preference(this);
			db_version.setEnabled(true);
			db_version.setKey("db_version");
			db_version.setTitle(getResources().getString(R.string.db_version_title));
			File file_db = new File(this.getFilesDir() + "_db_update_date");
			if (file_db.exists())
			{
				try {
					FileInputStream fileInputStream = this.openFileInput(file_db.getName());
					StringBuffer fileContent = new StringBuffer("");
					byte[] buffer = new byte[1];
					while (fileInputStream.read(buffer) != -1)
						fileContent.append(new String(buffer));
					String date = fileContent.toString();
					fileInputStream.close();
					db_version.setSummary("Latest update: " + date);
					getPreferenceScreen().addPreference(db_version);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			else
			{
				db_version.setSummary("Never updated");
				getPreferenceScreen().addPreference(db_version);
			}
			
			Preference version = new Preference(this);
			version.setEnabled(true);
			version.setKey("app_version");
			PackageInfo pInfo;
			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				version.setTitle(getResources().getString(R.string.version_title));
				version.setSummary(pInfo.versionName);
				getPreferenceScreen().addPreference(version);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			if (mModel.getUser().getAdminRights() != null && mModel.getUser().getAdminRights() >= 4)
			{
				PreferenceCategory adminPanel = new PreferenceCategory(this);
				adminPanel.setTitle(getResources().getString(R.string.admin_category));
				
				getPreferenceScreen().addPreference(adminPanel);
				
				DialogPreference sudo = new SudoDialog(this, null);
				sudo.setKey("sudo");
				sudo.setTitle("Access Sudo");
				sudo.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						ApplicationController AC = ((ApplicationController)getApplicationContext());
						AC.setRefresh(4);
						ApplicationController.SudoId = Integer.parseInt(newValue.toString());
						finish();
						return false;
					}
				});

				getPreferenceScreen().addPreference(sudo);
			}
			
			if (ApplicationController.SudoId != 0)
			{
				PreferenceCategory adminPanel = new PreferenceCategory(this);
				adminPanel.setTitle(getResources().getString(R.string.admin_category));
				
				getPreferenceScreen().addPreference(adminPanel);
				
				Preference unSudo = new Preference(this);
				unSudo.setEnabled(true);
				unSudo.setKey("unsudo");
				unSudo.setTitle("Exit Sudo");
				unSudo.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						ApplicationController AC = ((ApplicationController)getApplicationContext());
						AC.setRefresh(4);
						ApplicationController.SudoId = 0;
						finish();
						return false;
					}
				});
				
				getPreferenceScreen().addPreference(unSudo);
			}
	}
	
//	/** {@inheritDoc} */
//	@Override
//	public boolean onIsMultiPane() {
//		return isXLargeTablet(this) && !isSimplePreferences(this);
//	}
//
//	/**
//	 * Helper method to determine if the device has an extra-large screen. For
//	 * example, 10" tablets are extra-large.
//	 */
//	private static boolean isXLargeTablet(Context context) {
//		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
//	}
//
//	/**
//	 * Determines whether the simplified settings UI should be shown. This is
//	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
//	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
//	 * doesn't have an extra-large screen. In these cases, a single-pane
//	 * "simplified" settings UI should be shown.
//	 */
//	private static boolean isSimplePreferences(Context context) {
//		return ALWAYS_SIMPLE_PREFS
//				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
//				|| !isXLargeTablet(context);
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	public void onBuildHeaders(List<Header> target) {
//		if (!isSimplePreferences(this)) {
//			loadHeadersFromResource(R.xml.pref_headers, target);
//		}
//	}
//
//	/**
//	 * A preference value change listener that updates the preference's summary
//	 * to reflect its new value.
//	 */
//	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
//		@Override
//		public boolean onPreferenceChange(Preference preference, Object value) {
//			String stringValue = value.toString();
//
//			if (preference instanceof ListPreference) {
//				// For list preferences, look up the correct display value in
//				// the preference's 'entries' list.
//				ListPreference listPreference = (ListPreference) preference;
//				int index = listPreference.findIndexOfValue(stringValue);
//
//				// Set the summary to reflect the new value.
//				preference
//						.setSummary(index >= 0 ? listPreference.getEntries()[index]
//								: null);
//
//			} else if (preference instanceof RingtonePreference) {
//				// For ringtone preferences, look up the correct display value
//				// using RingtoneManager.
//				if (TextUtils.isEmpty(stringValue)) {
//					// Empty values correspond to 'silent' (no ringtone).
//					preference.setSummary(R.string.pref_ringtone_silent);
//
//				} else {
//					Ringtone ringtone = RingtoneManager.getRingtone(
//							preference.getContext(), Uri.parse(stringValue));
//
//					if (ringtone == null) {
//						// Clear the summary if there was a lookup error.
//						preference.setSummary(null);
//					} else {
//						// Set the summary to reflect the new ringtone display
//						// name.
//						String name = ringtone
//								.getTitle(preference.getContext());
//						preference.setSummary(name);
//					}
//				}
//
//			} else {
//				// For all other preferences, set the summary to the value's
//				// simple string representation.
//				preference.setSummary(stringValue);
//			}
//			return true;
//		}
//	};
//
//	/**
//	 * Binds a preference's summary to its value. More specifically, when the
//	 * preference's value is changed, its summary (line of text below the
//	 * preference title) is updated to reflect the value. The summary is also
//	 * immediately updated upon calling this method. The exact display format is
//	 * dependent on the type of preference.
//	 * 
//	 * @see #sBindPreferenceSummaryToValueListener
//	 */
//	private static void bindPreferenceSummaryToValue(Preference preference) {
//		// Set the listener to watch for value changes.
//		preference
//				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
//
//		// Trigger the listener immediately with the preference's
//		// current value.
//		sBindPreferenceSummaryToValueListener.onPreferenceChange(
//				preference,
//				PreferenceManager.getDefaultSharedPreferences(
//						preference.getContext()).getString(preference.getKey(),
//						""));
//	}
//
//	/**
//	 * This fragment shows general preferences only. It is used when the
//	 * activity is showing a two-pane settings UI.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	public static class GeneralPreferenceFragment extends PreferenceFragment {
//		@Override
//		public void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//			addPreferencesFromResource(R.xml.pref_general);
//
//			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
//			// to their values. When their values change, their summaries are
//			// updated to reflect the new value, per the Android Design
//			// guidelines.
////			bindPreferenceSummaryToValue(findPreference("example_text"));
////			bindPreferenceSummaryToValue(findPreference("example_list"));
//		}
//	}
//
//	/**
//	 * This fragment shows notification preferences only. It is used when the
//	 * activity is showing a two-pane settings UI.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	public static class NotificationPreferenceFragment extends
//			PreferenceFragment {
//		@Override
//		public void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//			addPreferencesFromResource(R.xml.pref_notification);
//
//			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
//			// to their values. When their values change, their summaries are
//			// updated to reflect the new value, per the Android Design
//			// guidelines.
//			bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
//		}
//	}
//
//	/**
//	 * This fragment shows data and sync preferences only. It is used when the
//	 * activity is showing a two-pane settings UI.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	public static class DataSyncPreferenceFragment extends PreferenceFragment {
//		@Override
//		public void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//			addPreferencesFromResource(R.xml.pref_data_sync);
//
//			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
//			// to their values. When their values change, their summaries are
//			// updated to reflect the new value, per the Android Design
//			// guidelines.
//			bindPreferenceSummaryToValue(findPreference("sync_frequency"));
//		}
//	}
}
