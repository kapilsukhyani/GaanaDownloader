package com.enlighten.gaanadownloader.tasks;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.enlighten.gaanadownloader.AppLog;
import com.enlighten.gaanadownloader.Constants;
import com.enlighten.gaanadownloader.Dialogs;
import com.enlighten.gaanadownloader.MainActivity;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.CommandCapture;

public class Interceptor extends AsyncTask<Void, Void, Void> {

	private Activity context;
	private static final String TAG = "Interceptor";
	private boolean setupSuccessful = false;
	private ProgressDialog dialog;

	public Interceptor(Activity context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = Dialogs.showProgressDialog(context, "Be patient people",
				"Setting up");
	}

	@Override
	protected Void doInBackground(Void... params) {

		startIntercepting();

		return null;
	}

	private void startIntercepting() {
		try {
			CommandCapture addressTranslationCommand = new CommandCapture(
					Constants.GAANA_ADDRESS_TRANSLATOR,
					Constants.GAANA_API_ADDRESS_TRANSLATION_COMMAND) {
				@Override
				public void commandCompleted(int id, int exitcode) {
					AppLog.logDebug(TAG, "Iptables executed successfully");

					CommandCapture interceptorCommand = new CommandCapture(
							Constants.GAANA_INTERCEPTOR,
							Constants.GAANA_INTERCEPTOR_COMMAND_TIMEOUT,
							Constants.GAANA_TCP_API_INTERCEPT_COMMAND) {
						@Override
						public void commandCompleted(int id, int exitcode) {
							super.commandCompleted(id, exitcode);
						}

						@Override
						public void commandOutput(int id, String line) {
							super.commandOutput(id, line);
							AppLog.logDebug(TAG, "new line " + line);
						}

						@Override
						public void commandTerminated(int id, String reason) {
							super.commandTerminated(id, reason);
							context.runOnUiThread(new Runnable() {

								@Override
								public void run() {

									((MainActivity) context)
											.stoppedIntercepting();

								}
							});

						}
					};

					try {
						RootTools.getShell(true).add(interceptorCommand);
						AppLog.logDebug(TAG,
								"Socat configured successfully, setup completed");
						setupSuccessful = true;

					} catch (IOException e) {
						e.printStackTrace();
					} catch (TimeoutException e) {
						e.printStackTrace();
					} catch (RootDeniedException e) {
						e.printStackTrace();
					}
				}

			};

			RootTools.getShell(true).add(addressTranslationCommand);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (RootDeniedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		dialog.dismiss();
		if (setupSuccessful) {
			((MainActivity) context).interceptSetupCompleted();
		}
	}

	public boolean isSetupSuccessful() {
		return setupSuccessful;
	}

}
