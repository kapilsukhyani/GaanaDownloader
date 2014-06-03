package com.enlighten.gaanadownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	TextView statusTextView;
	AsyncTask<String, Void, Void> songLoader = new AsyncTask<String, Void, Void>() {
		protected void onPreExecute() {
			statusTextView.setText("Downloading song");
		};

		@Override
		protected Void doInBackground(String... params) {
			String url = params[0];
			URL downloadURL;
			InputStream downloadStream = null;
			FileOutputStream fileWriter = null;

			try {
				System.out.println("download url " + url);
				downloadURL = new URL(url);
				downloadStream = downloadURL.openStream();
				File storage = Environment.getExternalStorageDirectory();

				File downloadedFile = new File(storage.getAbsolutePath() + "/"
						+ "testsong");
				if (downloadedFile.exists()) {
					downloadedFile.delete();
				}
				downloadedFile.createNewFile();
				fileWriter = new FileOutputStream(downloadedFile);

				int c;
				while ((c = downloadStream.read()) != -1) {
					fileWriter.write(c);
				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (null != downloadStream) {
						downloadStream.close();
					}
					if (null != fileWriter) {
						fileWriter.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return null;
		}

		protected void onPostExecute(Void result) {

			Toast.makeText(MainActivity.this, "song downloaded",
					Toast.LENGTH_LONG).show();
			statusTextView.setText("Song downloaded");
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		statusTextView = (TextView) findViewById(R.id.status_text);

		String url = "http://streams.gaana.com/mp3/64/34/209034/2470383.mp3?streamauth=1401732258_f78b68f30c80e2f49e53028946d6e7c3";

		songLoader.execute(url);
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void initView()
	{
		
	}

}
