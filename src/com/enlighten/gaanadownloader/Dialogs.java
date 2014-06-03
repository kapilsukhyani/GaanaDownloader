package com.enlighten.gaanadownloader;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Dialogs {

	public static ProgressDialog showProgressDialog(Context context,
			String title, String message) {
		return ProgressDialog.show(context, title, message);
	}

	public static void showErrorWhileInstallingSocatDialog(
			final Activity context) {
		showApplcationFinishAlertDialog(context,
				"Something went wrong while installing socat!");
	}

	public static void showGaanaNotAvailabeDialog(final Activity context) {
		showApplcationFinishAlertDialog(context,
				"Gaana android app is not available, install it first to download songs");
	}

	public static void showRootNotAvailableOrNotGrrantedDialog(
			final Activity context) {
		showApplcationFinishAlertDialog(context,
				"Either root is not available or not granted");
	}

	public static void showApplcationFinishAlertDialog(final Activity context,
			String message) {
		Builder builder = new Builder(context);
		builder.setTitle("Cannot run app dude");
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				context.finish();
			}
		});

		builder.create().show();
	}

}
