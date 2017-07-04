package net.tinybrick.utils.crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import static net.tinybrick.utils.crypto.DATA_FORMAT.BASE64;
import static net.tinybrick.utils.crypto.DATA_FORMAT.HEX;


public class SHA1 {
	private static Logger logger = org.apache.log4j.LogManager.getLogger(SHA1.class);

	public static String hash(String password) throws UnsupportedEncodingException {
		return hash(password, false);
	}

	public static Object hash(byte[] password, DATA_FORMAT dataFormat) throws UnsupportedEncodingException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

		md.update(password);

		Object encrypedData = null;
		byte[] codec = md.digest();
		switch (dataFormat) {
			case RAW:
				encrypedData = codec;
				break;
			case BASE64:
				encrypedData = Codec.toBase64(codec);
				break;
			default:
			case HEX:
				encrypedData = bytesToHex(codec);
				break;
		}
		return encrypedData;
	}

	public static Object hash(String password, DATA_FORMAT dataFormat) throws UnsupportedEncodingException {
		byte[] pwdBytes = password.getBytes("UTF-8");
		return hash(pwdBytes, dataFormat);
	}

	@Deprecated
	public static String hash(String password, boolean base64) throws UnsupportedEncodingException {
		return (String) hash(password, base64?BASE64:HEX);
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
