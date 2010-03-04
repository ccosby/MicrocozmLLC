package net.microcozm.OggTag;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * @author cosbyc
 * 
 *         An object-oriented interface to Ogg Vorbis information and comment
 *         fields, implemented entirely in Java. This class is being implemented
 *         in hand based from Ogg::Vorbis::Header::PurePerl version 1.0.
 * 
 *         This version will initially only support read operations.
 */
public class OggVorbisHeader {

	// First four bytes of stream are always OggS
	private static final String TAG = "OggVorbisHeader";
	private static final String ID3 = "ID3";
	private static final int BUFSZ = 4096;

	private String uri;
	private BufferedReader file;
	private Context context;

	/**
	 * Opens an Ogg Vorbis file, ensuring that it exists and is actually an Ogg
	 * Vorbis stream. This method does not actually read any of the information
	 * or comment fields, and closes the file immediately.
	 * 
	 * @param uri
	 *            A file URI to the Ogg Vorbis file
	 * @return void
	 */
	public OggVorbisHeader(Context context, String uri) {
		try {
			Log.v(TAG, "__construct()");

			this.context = context;
			this.uri = uri;
			this.file = new BufferedReader(new FileReader(uri), BUFSZ);

			// Initialize the reader
			int startInfoHeader = _checkHeader(this.file);
			barf("startInfoHeader :: " + String.valueOf(startInfoHeader) + " bytes");
			if ( startInfoHeader >= 0 ) {
				this._loadInfo(startInfoHeader);

			} else {
				Log.v(TAG, "startInfoHeader is " + String.valueOf(startInfoHeader));

			}

		} catch ( Exception e ) {
			barf(e.toString());

		}
	}

	/**
	 * Returns a hashref containing information about the Ogg Vorbis file from
	 * the file's information header. Hash fields are: version, channels, rate,
	 * bitrate_upper, bitrate_nominal, bitrate_lower, bitrate_window, and
	 * length. The bitrate_window value is not currently used by the vorbis
	 * codec, and will always be -1.
	 * 
	 * The optional parameter, key, allows you to retrieve a single value from
	 * the object's hash. Returns nil if the key is not found.
	 * 
	 * 
	 * @param key
	 *            Single key to retrieve
	 * @return void Hash of file info headers
	 */
	public void info(String key) {
	}

	/**
	 * Returns an array containing the key values for the comment fields. These
	 * values can then be passed to comment() to retrieve their values.
	 * 
	 * @return void Array of key values
	 */
	public String[] comment_tags() {
		String[] r = new String[0];
		return r;
	}

	/**
	 * Returns an array of comment values associated with the given key.
	 * 
	 * @param key
	 *            Key to retrieve value list for
	 * @return String[] Array of comment values
	 */
	public String[] comment(String key) {
		String[] r = new String[0];
		return r;
	}

	/**
	 * Returns the URI of the file the object represents
	 * 
	 * @return String
	 */
	public String path() {
		return this.uri;
	}

	private int _skipID3Header(BufferedReader file) {
		/**
		 * Read the first 3 bytes of the file. If it's an ID3 header, read
		 * through it until we find the Ogg Header. Return the byte offset to
		 * make future reads skip to the right place.
		 */
		try {
			/**
			 * Mark the file for reset - invalidate this mark if we read past
			 * the ID3 header flag.
			 */
			file.mark(ID3.length() + 1);

			char[] b = new char[ID3.length()];
			file.read(b, 0, b.length);
			if ( String.valueOf(b).equals(ID3) ) {
				Log.v(TAG, "Found ID3 Header");
				b = new char[BUFSZ];
				while ( file.read(b, 0, b.length) != -1 ) {
					Log.v(TAG, "Not Implemented - string search for OggS");
					/**
					 * my $found; if (($found = index($buffer, OGGHEADERFLAG))
					 * >= 0) { $byteCount += $found; seek $fh, $byteCount, 0;
					 * last; } else { $byteCount += 4096; }
					 */
				}
				return -1;

			} else {
				Log.v(TAG, "No ID3 Header");
				file.reset();
				return 0;
			}

		} catch ( Exception e ) {
			barf(e.toString());

		}

		return -1;
	}

	private int _checkHeader(BufferedReader file) {
		int byteCount = _skipID3Header(this.file);

		try {

			// check that the first 4 bytes are 'OggS'
			char[] b = new char[27];
			byteCount += file.read(b, 0, b.length);
			if ( !String.valueOf(b).substring(0, 0 + 4).equals("OggS") ) {
				barf("This is not an Ogg bitstream (no OggS header).");
				return -1;
			}

			// check the stream structure version (1 byte, should be 0x00)
			if ( b[4] != 0x00 ) {
				barf("This is not an Ogg bitstream (no OggS header).");
				return -1;
			}

			/**
			 * check the header type flag This is a bitfield, so technically we
			 * should check all of the bits that could potentially be set.
			 * However, the only value this should possibly have at the
			 * beginning of a proper Ogg-Vorbis file is 0x02, so we just check
			 * for that. If it's not that, we go on anyway, but give a warning
			 * (this behavior may (should?) be modified in the future.
			 */
			if ( b[5] != 0x02 ) {
				barf("Invalid header type flag (trying to proceed anyway).");
			}

			// read the number of page segments
			int pageSegCount = (int) b[26];
			barf("pageSegCount :: " + pageSegCount);

			// read pageSegCount bytes, then throw 'em out
			b = new char[pageSegCount];
			byteCount += file.read(b, 0, b.length);

			// check packet type. Should be 0x01 (for identification header)
			b = new char[7];
			byteCount += file.read(b, 0, b.length);
			if ( b[0] != 0x01 ) {
				barf("Wrong vorbis header type, giving up.");
				return -1;
			}

			// check that the packet identifies itself as 'vorbis'
			Log.v(TAG, String.valueOf(b).substring(1, 1 + 6));
			if ( !String.valueOf(b).substring(1, 1 + 6).equals("vorbis") ) {
				barf("This does not appear to be a vorbis stream, giving up.");
				return -1;
			}

		} catch ( IOException e ) {
			barf(e.toString());

		}

		// at this point, we assume the bitstream is valid
		barf("Got a valid bitstream");
		return byteCount;

	}

	private void _loadInfo(int startInfoHeader) {
	}

	@SuppressWarnings("unused")
	private void _loadComments() {
	}

	@SuppressWarnings("unused")
	private void _processComments() {
	}

	@SuppressWarnings("unused")
	private void Get8u() {
	}

	@SuppressWarnings("unused")
	private void Get32u() {
	}

	@SuppressWarnings("unused")
	private void _calculateTrackLength() {
	}

	@SuppressWarnings("unused")
	private void _decodeInt() {
	}

	private void barf(String e) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this.context, e, duration);
		toast.show();

		Log.v(TAG, e);
	}
}
