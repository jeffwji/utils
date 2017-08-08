package net.tinybrick.utils.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;

public class MD5 {
	private static Logger logger = LogManager.getLogger(MD5.class);
	MessageDigest md;

	public MD5() {
		try {
			md = MessageDigest.getInstance("MD5");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
		md.reset();
	}

	public static String hash(String password) {
		byte[] unencodedPassword = password.getBytes();
		return hash(unencodedPassword);
	}

	public static String hash(byte[] data) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
		md.reset();

		md.update(data);
		byte[] encodedData = md.digest();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < encodedData.length; i++) {
			if ((encodedData[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedData[i] & 0xff, 16));
		}

		return buf.toString().toUpperCase();
	}
	
	public void update(byte[] data) {
		md.update(data);
	}

	public String digest() {
		byte[] encodedData = md.digest();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < encodedData.length; i++) {
			if ((encodedData[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedData[i] & 0xff, 16));
		}

		return buf.toString().toUpperCase();
	}
}
