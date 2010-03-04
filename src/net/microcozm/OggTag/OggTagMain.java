/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microcozm.OggTag;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * @author cosbyc
 */
public class OggTagMain extends Activity {

	private Context context;
	public TextView tv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		context = getApplicationContext();

		// new OggVorbisHeader(context, "/sdcard/notfound.ogg");
		new OggVorbisHeader(context, "/sdcard/test.ogg");

		setContentView(R.layout.main);
	}

	protected void onStart() {
		super.onStart();
	};

	protected void onRestart() {
		super.onRestart();
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
		this.finish();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
	}
}
