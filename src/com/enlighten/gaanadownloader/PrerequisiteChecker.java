package com.enlighten.gaanadownloader;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.stericson.RootTools.RootTools;

public class PrerequisiteChecker extends AsyncTask<Void, Void, Boolean> {

	private Activity activity = null;
	private ProgressDialog dialog;
	private boolean socatInstalled = false;
	private boolean gaanaAvailable = false;

	PrerequisiteChecker(Activity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		dialog = Dialogs.showProgressDialog(activity, "Be patient people",
				"Checking prerequisites");
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (RootTools.isRootAvailable()) {
			// root is available
			if (RootTools.isAccessGiven()) {
				// root access is granted
				if (installSocat()) {
					// socat is installed
					return isGaanaAvailable();
				}
			}

		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean successful) {
		dialog.dismiss();

		if (successful) {
			((MainActivity) activity).initView();
		} else {

			if (!socatInstalled) {
				Dialogs.showErrorWhileInstallingSocatDialog(activity);

			} else if (!gaanaAvailable) {
				Dialogs.showGaanaNotAvailabeDialog(activity);
			} else {
				Dialogs.showRootNotAvailableOrNotGrrantedDialog(activity);
			}

		}

	}

	private boolean installSocat() {

		if (!PreferenceManager.getDefaultSharedPreferences(activity)
				.getBoolean(Constants.APPLICAITON_INITIALIZED, false)) {

			if (RootTools.installBinary(activity, R.raw.socat, "socat")) {
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(activity);

				preferences.edit()
						.putBoolean(Constants.APPLICAITON_INITIALIZED, true)
						.commit();
				socatInstalled = true;
				Constants.initSocatCommand();

			}

		} else {
			socatInstalled = true;
		}

		return socatInstalled;

	}

	private boolean isGaanaAvailable() {
		PackageManager pm = activity.getPackageManager();
		List<ApplicationInfo> packages;
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if (packageInfo.packageName.equals("com.gaana")) {
				gaanaAvailable = true;
				Constants.initIPTablesCommand(packageInfo.uid + "");
				break;
			}
		}
		return gaanaAvailable;
	}
}
