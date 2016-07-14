package net.tinybrick.utils.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class SHA1 {
	private static Logger logger = org.apache.log4j.LogManager.getLogger(SHA1.class);

	public static String hash(String password) throws UnsupportedEncodingException {
		return hash(password, false);
	}

	public static String hash(String password, boolean base64) throws UnsupportedEncodingException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

		md.update(password.getBytes());

		String encrypedString = null;
		byte[] codec = md.digest();
		if (base64) {
			encrypedString = Codec.toBase64(codec);
		}
		else {
			encrypedString = bytesToHex(codec);
		}

		return encrypedString;
	}

	protected static String bytesToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString().toUpperCase();
	}
}
