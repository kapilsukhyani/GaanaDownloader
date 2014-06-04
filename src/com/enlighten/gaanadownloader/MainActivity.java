package com.enlighten.gaanadownloader;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.enlighten.gaanadownloader.tasks.Interceptor;
import com.enlighten.gaanadownloader.tasks.PrerequisiteChecker;

public class MainActivity extends Activity implements OnClickListener {
	private TextView infoTextView;
	private Button startInterceptingButton;
	private PrerequisiteChecker prerequisiteChecker;
	private Interceptor interceptor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoTextView = (TextView) findViewById(R.id.info_text);
		startInterceptingButton = (Button) findViewById(R.id.start_intercepting);
		startInterceptingButton.setOnClickListener(this);
		prerequisiteChecker = new PrerequisiteChecker(this);
		interceptor = new Interceptor(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		prerequisiteChecker.execute((Void) null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Called by {@link PrerequisiteChecker} when it is done checking
	 * prerequisites and found everything needed
	 */
	public void initView() {
		startInterceptingButton.setEnabled(true);

	}

	/**
	 * Called by {@link Interceptor} when it is done setting up socat and
	 * iptables to intercept gaana app's traffic
	 */

	public void interceptSetupCompleted() {
		Notification notification;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext())
				.setSmallIcon(android.R.drawable.presence_online)
				.setTicker("Started Intercepting")
				.setContentTitle("Intercepting...")
				.setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true);

		notification = builder.build();
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(
				Constants.STARTED_INTERCEPTING_NOTIFICAITON, notification);

	}

	/**
	 * Called by {@link Interceptor} when socat command terminated
	 */
	public void stoppedIntercepting() {
		Notification notification;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext())
				.setSmallIcon(android.R.drawable.presence_online)
				.setTicker(
						"Stopped Intercepting because got url or socat timed out")
				.setContentTitle("Intercepting...")
				.setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true);

		notification = builder.build();
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(
				Constants.STARTED_INTERCEPTING_NOTIFICAITON, notification);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_intercepting:
			executeInterceptingTask();
			break;

		default:
			break;
		}
	}

	private void executeInterceptingTask() {
		interceptor.execute((Void) null);

	}

}
