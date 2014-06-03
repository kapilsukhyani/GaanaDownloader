package com.enlighten.gaanadownloader;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.util.Log;

public class GaanaDownloaderApp extends Application {
	private static final String TAG = "GaanaDownloaderApp";

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				ex.printStackTrace();
				Log.d(TAG, "caught unhandeled exception, exiting gracefully");
				System.exit(1);

			}
		});

		// It has to be called before app init as all paths are defined there
		Constants.initPaths(getFilesDir().getAbsolutePath());
	}
}
