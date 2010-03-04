package net.microcozm.OggTag;

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
	private static final String OGGHEADERFLAG = "OggS";

	private String uri;

	/**
	 * Opens an Ogg Vorbis file, ensuring that it exists and is actuall an
	 * Ogg Vorbis stream. This method does not actually read any of the
	 * information or comment fields, and closes the file immediately.
	 * 
	 * @param		uri		A file URI to the Ogg Vorbis file
	 * @return		void
	 */
	public OggVorbisHeader(String uri) {
		this.uri = uri;
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

	public void _init() {
	}

	public void _skipID3Header() {
	}

	public void _checkHeader() {
	}

	public void _loadInfo() {
	}

	public void _loadComments() {
	}

	public void _processComments() {
	}

	public void Get8u() {
	}

	public void Get32u() {
	}

	public void _calculateTrackLength() {
	}

	public void _decodeInt() {
	}
}
