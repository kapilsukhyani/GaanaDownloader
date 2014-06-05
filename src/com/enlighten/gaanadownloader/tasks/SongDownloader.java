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
import com.enlighten.gaanadownloader.Dialogs;

public class SongDownloader extends AsyncTask<String, Void, String> {
	private Activity context;
	private static final String TAG = "SongDownloader";

	public SongDownloader(Activity context) {
		this.context = context;
	}

	protected void onPreExecute() {
		Dialogs.showNotification(context, "Got song url",
				"Downloading your song", "Downloading your song",
				android.R.drawable.arrow_down_float);
	};

	@Override
	protected String doInBackground(String... params) {
		String url = params[0];
		return downloadAndSaveSong(url);
	}

	private String downloadAndSaveSong(String url) {
		AppLog.logDebug(TAG, "Song url " + url);
		return saveSongToExternalStorage(getSongInputStream(url));

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

	private String saveSongToExternalStorage(InputStream downloadStream) {

		FileOutputStream fileWriter = null;
		File storage = Environment.getExternalStorageDirectory();

		File downloadedFile = new File(storage.getAbsolutePath() + "/"
				+ Constants.DOWNLOADED_SONG_FILE_NAME + "_"
				+ (new Date()).toString() + Constants.AUDIO_FILE_EXTENSION);
		if (downloadedFile.exists()) {
			downloadedFile.delete();
		}
		try {
			downloadedFile.createNewFile();
			fileWriter = new FileOutputStream(downloadedFile);
			byte[] buffer = new byte[4096];

			int c;
			while ((c = downloadStream.read(buffer)) != -1) {
				fileWriter.write(buffer);
			}

			return downloadedFile.getAbsolutePath();
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

		return null;

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		String tickerText = "Something went wrong while downloading your song";
		String contentTile = "Donwloding task completed";

		if (null != result) {
			tickerText = "Your song downloaded and available at " + result;
		}

		Dialogs.showNotification(context, tickerText, contentTile, tickerText,
				android.R.drawable.arrow_down_float);
	}

}
