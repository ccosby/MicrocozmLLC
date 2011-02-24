package net.microcozm.OggTag;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
	private BufferedInputStream reader;
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
	public OggVorbisHeader(final Context context, final String uri) {
		try {
			Log.v(TAG, "__construct()");

			this.context = context;
			this.uri = uri;
			this.reader = new BufferedInputStream(new FileInputStream(uri), BUFSZ);

			// Initialize the reader
			int startInfoHeader = _checkHeader();
			if ( startInfoHeader >= 0 ) {
				this._loadInfo(startInfoHeader);

			} else {
				Log.v(TAG, "startInfoHeader = " + String.valueOf(startInfoHeader));

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
	public void info(final String key) {
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
	public String[] comment(final String key) {
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

	private int _skipID3Header() {
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
			this.reader.mark(ID3.length() + 1);

			byte[] b = new byte[ID3.length()];
			this.reader.read(b, 0, b.length);
			if ( String.valueOf(b).equals(ID3) ) {
				Log.v(TAG, "Found ID3 Header");
				b = new byte[BUFSZ];
				while ( this.reader.read(b, 0, b.length) != -1 ) {
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
				this.reader.reset();
				return 0;
			}

		} catch ( Exception e ) {
			barf(e.toString());

		}

		return -1;
	}

	private int _checkHeader() {
		int byteCount = _skipID3Header();

		try {
			// check that the first 4 bytes are 'OggS'
			byte[] b = new byte[27];
			byteCount += this.reader.read(b, 0, b.length);
			if ( !new String(b, 0, 4).equals("OggS") ) {
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

			// read pageSegCount bytes, then throw 'em out
			b = new byte[pageSegCount];
			byteCount += this.reader.read(b, 0, b.length);

			// read the next 7 bytes for packet type and identifier
			b = new byte[7];
			byteCount += this.reader.read(b, 0, b.length);

			// check packet type. Should be 0x01 (for identification header)
			if ( b[0] != 0x01 ) {
				barf("Wrong vorbis header type, giving up.");
				return -1;
			}

			// check that the packet identifies itself as 'vorbis'
			if ( !new String(b, 1, 6).equals("vorbis") ) {
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

	/**
	 * Read audio info
	 * 
	 * @param startInfoHeader
	 */
	private int _loadInfo(final int startInfoHeader) {
		int byteCount = startInfoHeader;

		try {
			// read 23 bytes, all of the audio data
			byte[] b = new byte[23];
			byteCount += this.reader.read(b, 0, b.length);

			// convert the audio data into a Little Endian ByteBuffer
			ByteBuffer bb = ByteBuffer.wrap(b);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			// read the vorbis version
			int version = bb.getInt();
			barf("version: " + version);

			// read the number of audio channels
			int channels = (int) bb.get();
			barf("channels: " + channels);

			// read the sample rate
			int rate = bb.getInt();
			barf("rate: " + rate);

			// read the bitrate maximum
			int bitrate_upper = bb.getInt();
			barf("bitrate_upper: " + bitrate_upper);

			// read the bitrate nominal
			int bitrate_nominal = bb.getInt();
			barf("bitrate_nominal: " + bitrate_nominal);

			// read the bitrate minimal
			int bitrate_lower = bb.getInt();
			barf("bitrate_lower: " + bitrate_lower);

			/**
			 * read the blocksize_0 and blocksize_1 these are each 4 bit fields,
			 * whose actual value is 2 to the power of the value of the field
			 */
			int blocksize = (int) bb.get();
			int blocksize_0 = 2 << ((blocksize & 0xF0) >> 4);
			int blocksize_1 = 2 << (blocksize & 0x0F);
			barf("blocksize_0: " + blocksize_0);
			barf("blocksize_1: " + blocksize_1);

			// read the framing_flag
			int framing_flag = (int) bb.get();
			barf("framing_flag: " + framing_flag);

			// bitrate_window is -1 in the current version of vorbisfile
			int bitrate_window = -1;
			barf("bitrate_window:" + bitrate_window);

		} catch ( IOException e ) {
			barf(e.toString());

		}

		return byteCount;
	}

	private void _loadComments() {
	}

	private void _processComments() {
	}

	private void Get8u() {
	}

	private void Get32u() {
	}

	private void _calculateTrackLength() {
	}

	private void barf(final String e) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this.context, e, duration);
		// toast.show();

		Log.v(TAG, e);
	}
}
