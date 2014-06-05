package com.enlighten.gaanadownloader.tasks;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.enlighten.gaanadownloader.AppLog;
import com.enlighten.gaanadownloader.Constants;
import com.enlighten.gaanadownloader.Dialogs;
import com.enlighten.gaanadownloader.MainActivity;
import com.enlighten.gaanadownloader.R;
import com.enlighten.gaanadownloader.R.raw;
import com.stericson.RootTools.RootTools;

public class PrerequisiteChecker extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = "PrerequisiteChecker";
	private Activity activity = null;
	private ProgressDialog dialog;
	private boolean socatInstalled = false;
	private boolean gaanaAvailable = false;
	private boolean busyboxAvailable = false;

	public PrerequisiteChecker(Activity activity) {
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
			AppLog.logDebug(TAG, "Root is available");
			if (RootTools.isAccessGiven()) {
				// root access is granted
				AppLog.logDebug(TAG, "Root is granted");
				if (installSocat()) {
					// socat is installed
					AppLog.logDebug(TAG, "socat installed");
					if (isBusyBoxAvailable()) {
						// busybox is available
						AppLog.logDebug(TAG, "busybox is available");
						return isGaanaAvailable();
					}
				}
			}

		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean successful) {
		dialog.dismiss();

		if (successful) {
			AppLog.logDebug(TAG,
					"Gaana is available, app prerequisite checker finishing");
			((MainActivity) activity).initView();
		} else {

			if (!socatInstalled) {
				AppLog.logDebug(TAG,
						"socat not installed successfully, finishing app");
				Dialogs.showErrorWhileInstallingSocatDialog(activity);

			} else if (!busyboxAvailable) {
				AppLog.logDebug(TAG,
						"busybox not installed, finishing app");
				Dialogs.showBusyboxNotAvailableDialog(activity);

			} else if (!gaanaAvailable) {
				AppLog.logDebug(TAG, "gaana is not available, finishing app");
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
			Constants.initSocatCommand();
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

	private boolean isBusyBoxAvailable() {
		busyboxAvailable = RootTools.findBinary(Constants.BUSYBOX);

		return busyboxAvailable;
	}

}
