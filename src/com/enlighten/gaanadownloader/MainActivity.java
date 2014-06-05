package com.enlighten.gaanadownloader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.enlighten.gaanadownloader.tasks.Interceptor;
import com.enlighten.gaanadownloader.tasks.PrerequisiteChecker;
import com.enlighten.gaanadownloader.tasks.SongDownloader;

public class MainActivity extends Activity implements OnClickListener {
	private TextView infoTextView;
	private Button startInterceptingButton;
	private PrerequisiteChecker prerequisiteChecker;
	private Interceptor interceptor;
	private SongDownloader songDownloader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoTextView = (TextView) findViewById(R.id.info_text);
		startInterceptingButton = (Button) findViewById(R.id.start_intercepting);
		startInterceptingButton.setOnClickListener(this);
		prerequisiteChecker = new PrerequisiteChecker(this);
		prerequisiteChecker.execute((Void) null);

	}

	@Override
	protected void onStart() {
		super.onStart();
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
		Dialogs.showNotification(this, "Started Intercepting",
				"Intercepting...", "Started Intercepting",
				android.R.drawable.presence_online);

	}

	/**
	 * Called by {@link Interceptor} when socat command terminated
	 */
	public void stoppedIntercepting() {
		Dialogs.showNotification(this,
				"Stopped Intercepting because got url or socat timed out",
				"Stopped Intercepting...",
				"Stopped Intercepting because got url or socat timed out",
				android.R.drawable.presence_offline);
	}

	/**
	 * Called by {@link Interceptor} when stream url is received and processed
	 */
	public void streamUrl(String url) {
		songDownloader = new SongDownloader(this);
		songDownloader.execute(url);
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
		interceptor = new Interceptor(this);
		interceptor.execute((Void) null);

	}

}
