package com.enlighten.gaanadownloader;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

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

	public static void showBusyboxNotAvailableDialog(final Activity context) {
		new Builder(context)
				.setTitle("Busybox is required to run this app.")
				.setMessage("Do you want to install it?")
				.setCancelable(false)
				.setPositiveButton("Install busybox",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(
										context,
										"Remember to run the busybox installer app after downloading it",
										Toast.LENGTH_LONG).show();
								dialog.dismiss();
								// install the binary as it is not available
								RootTools.offerBusyBox(context);
								context.finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Toast.makeText(
								context,
								"Closing app as prerequisites are not available",
								Toast.LENGTH_LONG).show();
						context.finish();
					}
				}).create().show();
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

	public static void showNotification(Activity context, String tickerText,
			String contentTile, String contentDescription, int drawaableId) {

		Notification notification;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(drawaableId)
				.setTicker(tickerText).setContentTitle(contentTile)
				.setContentInfo(contentDescription)
				.setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true);

		notification = builder.build();
		((NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
				Constants.STARTED_INTERCEPTING_NOTIFICAITON, notification);

	}
}
