package net.microcozm.OggTag;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Hashtable;

/**
 *
 * @author cosbyc
 *
 * An object-oriented interface to Ogg Vorbis information and comment fields,
 * implemented entirely in Java. This class is being implemented in hand based
 * from Ogg::Vorbis::Header::PurePerl version 1.0.
 *
 * This version will initially only support read operations.
 */
public class OggVorbisHeader {

	// First four bytes of stream are always OggS
	private static final String TAG = "OggVorbisHeader";
	private static final String OGGHEADERFLAG = "OggS";
	private static final int BUFSZ = 8192;
	private String uri;
	private BufferedInputStream file;
	private Hashtable data;

	/**
	 * Opens an Ogg Vorbis file, ensuring that it exists and is actuall an
	 * Ogg Vorbis stream. This method does not actually read any of the
	 * information or comment fields, and closes the file immediately.
	 * 
	 * @param		uri		A file URI to the Ogg Vorbis file
	 * @return		void
	 */
	public OggVorbisHeader(String uri) {
		try {
			Log.v(TAG,"__construct()");
			this.uri = uri;
			this.file = new BufferedInputStream(new FileInputStream(uri), BUFSZ);

			// Init the reader
			int startInfoHeader = this._init();
			if (startInfoHeader >= 0) {
				this._loadInfo(startInfoHeader);

			} else {
				Log.v(TAG, "startInfoHeader is " + String.valueOf(startInfoHeader));
				
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * Returns a hashref containing information about the Ogg Vorbis file from
	 * the file's information header.  Hash fields are: version, channels, rate,
	 * bitrate_upper, bitrate_nominal, bitrate_lower, bitrate_window, and length.
	 * The bitrate_window value is not currently used by the vorbis codec, and
	 * will always be -1.
	 *
	 * The optional parameter, key, allows you to retrieve a single value from
	 * the object's hash.  Returns nil if the key is not found.

	 *
	 * @param	key		Single key to retrieve
	 * @return	void	Hash of file info headers
	 */
	public void info(String key) {
	}

	/**
	 * Returns an array containing the key values for the comment fields.
	 * These values can then be passed to comment() to retrieve their values.
	 * 
	 * @return	void	Array of key values
	 */
	public String[] comment_tags() {
		String[] r = new String[0];
		return r;
	}

	/**
	 * Returns an array of comment values associated with the given key.
	 * 
	 * @param	key			Key to retrieve value list for
	 * @return	String[]	Array of comment values
	 */
	public String[] comment(String key) {
		String[] r = new String[0];
		return r;
	}

	/**
	 * Returns the URI of the file the object represents
	 *
	 * @return	String
	 */
	public String path() {
		return this.uri;
	}

	/**
	 * Initialize the reader and return where the Ogg Vorbis Header starts.
	 * 
	 * @param	file	BufferedInputStream
	 * @return	int		Byte position of Ogg Vorbis Header
	 */
	private int _init() {
		Log.v(TAG, "_init(" + uri + ")");

		int buffer = 0;
		int pageSegCount = 0;

		int byteCount = _skipID3Header(this.file);
		if ( byteCount > 0) {

		}

		return byteCount;
	}

	private int _skipID3Header(BufferedInputStream file) {
		/**
		 * Read the first 3 bytes of the file. If it's an ID3 header, read
		 * through it until we find the Ogg Header. Return the byte offset
		 * to make future reads skip to the right place.
		 */
		try {
			byte[] buffer = new byte[3];
			file.read(buffer, 0, buffer.length);
			if (String.valueOf(buffer).equals("ID3")) {
				Log.v(TAG, "Found ID3 Header");
				buffer = new byte[BUFSZ];
				while (file.available() != 0) {
					file.read(buffer, 0, buffer.length);
					Log.v(TAG, "Not Implemented - string search for " + OGGHEADERFLAG);
					/**
					my $found;
					if (($found = index($buffer, OGGHEADERFLAG)) >= 0) {
						$byteCount += $found;
						seek $fh, $byteCount, 0;
						last;
					} else {
						$byteCount += 4096;
					}
					 */
				}
				return -1;

			} else {
				Log.v(TAG, "No ID3 Header");
				file.mark(0);
				file.reset();
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	private void _loadInfo(int startInfoHeader) {
	}

	private void _loadComments(Hashtable data) {
	}

	private void _processComments() {
	}

	private void Get8u() {
	}

	private void Get32u() {
	}

	private void _calculateTrackLength(Hashtable data) {
	}

	private void _decodeInt() {
	}
}
