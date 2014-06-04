package com.enlighten.gaanadownloader.tasks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;

import com.enlighten.gaanadownloader.AppLog;
import com.enlighten.gaanadownloader.Constants;

public class SongDownloader extends AsyncTask<String, Void, Void> {
	private Activity context;
	private static final String TAG = "SongDownloader";

	SongDownloader(Activity context) {
		this.context = context;
	}

	protected void onPreExecute() {

	};

	@Override
	protected Void doInBackground(String... params) {
		String url = params[0];
		downloadAndSaveSong(url);

		return null;
	}

	private void downloadAndSaveSong(String url) {
		AppLog.logDebug(TAG, "Song url " + url);
		saveSongToExternalStorage(getSongInputStream(url));

	}

	private InputStream getSongInputStream(String url) {

		URL downloadURL;
		InputStream songStream = null;
		try {
			downloadURL = new URL(url);
			songStream = downloadURL.openStream();
		} catch (MalformedURLException e) {
			AppLog.logDebug(TAG, "Url provided is wrong " + url);
			e.printStackTrace();
		} catch (IOException e) {
			AppLog.logDebug(TAG, "not able to open streaming url " + url);
			e.printStackTrace();
		}

		return songStream;

	}

	private void saveSongToExternalStorage(InputStream downloadStream) {

		FileOutputStream fileWriter = null;
		File storage = Environment.getExternalStorageDirectory();

		File downloadedFile = new File(storage.getAbsolutePath() + "/"
				+ Constants.DOWNLOADED_SONG_FILE_NAME + "_"
				+ (new Date()).toString());
		if (downloadedFile.exists()) {
			downloadedFile.delete();
		}
		try {
			downloadedFile.createNewFile();
			fileWriter = new FileOutputStream(downloadedFile);

			int c;
			while ((c = downloadStream.read()) != -1) {
				fileWriter.write(c);
			}
		} catch (IOException e) {
			AppLog.logDebug(TAG,
					"Something went wrong while saving downloaded song");

			e.printStackTrace();
		} finally {
			if (null != fileWriter) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != downloadStream) {
				try {
					downloadStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	protected void onPostExecute(Void result) {

	};

}
