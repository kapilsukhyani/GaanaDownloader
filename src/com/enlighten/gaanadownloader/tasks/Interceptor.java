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
	private boolean gotGetStreamingUriRequest = false;
	private boolean gotSuccessfulResponse = false;
	private boolean gotResponse = false;
	private boolean responseHeaderCompleted = false;
	private boolean gotStreamingUrl = false;

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
					AppLog.logDebug(TAG, "Iptables executed successfully "
							+ exitcode);
					CommandCapture interceptorCommand = new CommandCapture(
							Constants.GAANA_INTERCEPTOR,
							Constants.GAANA_INTERCEPTOR_COMMAND_TIMEOUT,
							Constants.GAANA_TCP_API_INTERCEPT_COMMAND) {
						@Override
						public void commandCompleted(int id, int exitcode) {
							super.commandCompleted(id, exitcode);
							stopIntercepting();
						}

						@Override
						public void commandOutput(int id, String line) {
							AppLog.logDebug(TAG, "new line " + line);
							// start processing lines only if get streaming url
							// request is hit
							setGotGetStreamingUriRequest(line);
							if (gotGetStreamingUriRequest) {
								// it will process line till response is not
								// completed
								if (!responseHeaderCompleted) {
									processGetStreamingUriResponseLine(line);
								} else if (gotSuccessfulResponse) {
									AppLog.logDebug(TAG,
											"got response of get streaming uri gaana service");
									AppLog.logDebug(TAG, "response " + line);
									// got successful response, process response
									// to get streaming url and stop
									// intercepting

								} else {
									AppLog.logDebug(TAG,
											"got unsuccessful response of get streaming uri gaana service");
									AppLog.logDebug(TAG, "response " + line);
									// did not get successful response show
									// alert and stop intercepting
									stopIntercepting();
								}
							}

						}

						@Override
						public void commandTerminated(int id, String reason) {
							super.commandTerminated(id, reason);
							stopIntercepting();

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

				@Override
				public void commandOutput(int id, String line) {
					AppLog.logDebug(TAG, "Iptables command output " + line);

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

	private void stopIntercepting() {
		stopNetworkAddressTranslation();

	}

	private void stopNetworkAddressTranslation() {
		try {
			CommandCapture cleanIptables = new CommandCapture(
					Constants.STOP_GAANA_ADDRESS_TRANSLATOR,
					Constants.CLEAN_IPTABLES_COMMAND) {
				@Override
				public void commandCompleted(int id, int exitcode) {
					AppLog.logDebug(TAG, "Iptables cleared " + exitcode);
					killSocat();
				}
			};
			RootTools.getShell(true).add(cleanIptables);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void killSocat() {
		try {
			CommandCapture killSocat = new CommandCapture(Constants.KILL_SOCAT,
					Constants.SOCAT_KILL_COMMAND) {
				@Override
				public void commandCompleted(int id, int exitcode) {

					AppLog.logDebug(TAG, "socat killed " + exitcode);
					notifyStoppedIntercepting();
				}
			};
			RootTools.getShell(true).add(killSocat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RootDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void notifyStoppedIntercepting() {
		context.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				((MainActivity) context).stoppedIntercepting();

			}
		});
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

	private void processGetStreamingUriResponseLine(String line) {
		if (line.contains(Constants.HTTP11)) {
			gotResponse = true;
			if (line.contains(Constants.HTTP_SUCCESSFUL_RESPONSE_CODE)) {
				gotSuccessfulResponse = true;
			}
		} else if (gotResponse && line.equals("\\r")) {
			responseHeaderCompleted = true;
		}
	}

	private boolean setGotGetStreamingUriRequest(String line) {

		if (line.contains(Constants.GAANA_GET_STREAMING_URI_FRAGMENT)) {
			AppLog.logDebug(TAG, "Get streaming uri service got hit");
			gotGetStreamingUriRequest = true;
			return gotGetStreamingUriRequest;
		}
		return false;
	}

}
