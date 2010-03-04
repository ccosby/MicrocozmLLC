/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microcozm.OggTag;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 *
 * @author cosbyc
 */
public class OggTagMain extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		TextView tv = new TextView(this);
		OggVorbisHeader ovh = new OggVorbisHeader("/sdcard/test.ogg");
		tv.setText(ovh.path());
		setContentView(tv);
	}
}
