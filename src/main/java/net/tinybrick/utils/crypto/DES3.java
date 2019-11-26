package net.tinybrick.utils.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.util.Base64;

import org.apache.log4j.Logger;

public class DES3 {
	private static Logger logger = Logger.getLogger(DES3.class);

	// 算法
	public static final String KEY_SPEC_PBE_DES = "PBEWithMD5AndDES";

	// Standard Data Encryption
	public static final String KEY_SPEC_DES = "DES";
	// DESede:
	public static final String KEY_SPEC_DES_EDE = "DESede";

	// DES with CBC
	public static final String KEY_SPEC_DES_CBC_PKCS5PADDING = "CBC/PKCS5Padding";

	private SecretKey secKey = null;
	private String passPhrase = null;
	private Long salt = null;
	private static String type = KEY_SPEC_DES;

	final int iterationCount = 19;

	@Deprecated
	public DES3(String passPhrase, Long salt, String type) throws Exception {
		this.passPhrase = passPhrase;
		this.salt = salt;
		if (null != type && 0 != type.trim().length()) {
			DES3.type = type;
		}

		if (null == passPhrase) {
			KeyGenerator keygen = KeyGenerator.getInstance(type);
			secKey = keygen.generateKey();
		}
	}

	/**
	 * @param type
	 * @param passPhrase
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 */
	protected static Key createKey(String type, String passPhrase) throws NoSuchAlgorithmException,
			InvalidKeyException, InvalidKeySpecException {
		KeySpec keySpec = null;
		logger.info("Key spec: " + type);

		if (type.equals(KEY_SPEC_DES_EDE)) {
			keySpec = new DESedeKeySpec(Padding(passPhrase).getBytes());
		}
		else if (type.equals(KEY_SPEC_DES)) {
			keySpec = new DESKeySpec(Padding(passPhrase, 8).getBytes());
		}
		else
			throw new NoSuchAlgorithmException(type);

		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(type);
		SecretKey secKey = keyFactory.generateSecret(keySpec);
		return secKey;
	}

	/**
	 * @param type
	 * @param key
	 * @param mode
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	protected static Cipher getCipher(String type, Key key, int mode) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		return getCipher(type, key, mode, null);
	}

	/**
	 * @param type
	 * @param key
	 * @param mode
	 * @param iv
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	protected static Cipher getCipher(String type, Key key, int mode, IvParameterSpec iv)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		Cipher cipher = null;

		if (null != iv) {
			// CBC 模式，CBC模式支持Salt
			cipher = Cipher.getInstance(type + "/" + KEY_SPEC_DES_CBC_PKCS5PADDING);
			cipher.init(mode, key, iv, new SecureRandom());
			logger.info("Salt is added.");
		}
		else {
			// 缺省的ECB模式，不支持加盐
			cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(mode, key);
		}

		logger.info("Cipher mode: " + cipher.getAlgorithm());
		return cipher;
	}

	protected static IvParameterSpec makeSalt(String salt) {
		IvParameterSpec iv = new IvParameterSpec(Padding(salt, 8).getBytes());
		return iv;
	}

	/**
	 * @param passPhrase
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String passPhrase, String str) throws Exception {
		return encrypt(passPhrase, str, null);
	}

	/**
	 * @param passPhrase
	 * @param str
	 * @param salt
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String passPhrase, String str, String salt) throws Exception {
		return encrypt(KEY_SPEC_DES_EDE, passPhrase, str, salt);
	}

	/**
	 * @param type
	 * @param passPhrase
	 * @param str
	 * @param salt
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("restriction")
	public static String encrypt(String type, String passPhrase, String str, String salt) throws Exception {
		// Encrypt
		byte[] enc = null;
		try {
			Key secKey = createKey(type, passPhrase);
			Cipher cipher = null;

			if (null != salt) {
				cipher = getCipher(type, secKey, Cipher.ENCRYPT_MODE, makeSalt(salt));
			}
			else {
				cipher = getCipher(type, secKey, Cipher.ENCRYPT_MODE);
			}

			// Encode the string into bytes using utf-8
			byte[] buffer = str.getBytes("UTF8");
			enc = cipher.doFinal(buffer);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		// Encode bytes to base64 to get a string
		return Base64.getEncoder().encodeToString(enc);
	}

	/**
	 * @param passPhrase
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String passPhrase, String str) throws Exception {
		return decrypt(passPhrase, str, null);
	}

	/**
	 * @param passPhrase
	 * @param str
	 * @param salt
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String passPhrase, String str, String salt) throws Exception {
		return decrypt(KEY_SPEC_DES_EDE, passPhrase, str, salt);
	}

	/**
	 * @param type
	 * @param passPhrase
	 * @param str
	 * @param salt
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String type, String passPhrase, String str, String salt) throws Exception {
		byte[] buffer = null;

		try {
			Key secKey = createKey(type, passPhrase);
			Cipher cipher = null;

			if (null != salt) {
				cipher = getCipher(type, secKey, Cipher.DECRYPT_MODE, makeSalt(salt));
			}
			else {
				cipher = getCipher(type, secKey, Cipher.DECRYPT_MODE);
			}
			// Decode base64 to get bytes
			@SuppressWarnings("restriction") byte[] dec = Base64.getDecoder().decode(str);

			// Decrypt
			buffer = cipher.doFinal(dec);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}

		// Decode using utf-8
		return new String(buffer, "UTF8");
	}

	@Deprecated
	@SuppressWarnings("restriction")
	public String encrypt(String str) throws Exception {
		Cipher ecipher = null;

		try {
			if (null != salt) {
				IvParameterSpec iv = new IvParameterSpec(Long.toHexString(salt.longValue()).getBytes());

				if (type.toUpperCase().equals(KEY_SPEC_PBE_DES.toUpperCase())) {
					KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), iv.getIV(), iterationCount);
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_PBE_DES);
					secKey = keyFactory.generateSecret(keySpec);

					ecipher = Cipher.getInstance(secKey.getAlgorithm());
					ecipher.init(Cipher.ENCRYPT_MODE, secKey, new PBEParameterSpec(iv.getIV(), iterationCount));
				}
				else if (type.toUpperCase().equals(KEY_SPEC_DES + "/" + KEY_SPEC_DES_CBC_PKCS5PADDING.toUpperCase())) {
					KeySpec keySpec = new DESKeySpec(Padding(passPhrase).getBytes());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_DES + "/"
							+ KEY_SPEC_DES_CBC_PKCS5PADDING);
					secKey = keyFactory.generateSecret(keySpec);

					ecipher = Cipher.getInstance(KEY_SPEC_DES + "/" + KEY_SPEC_DES_CBC_PKCS5PADDING);
					ecipher.init(Cipher.ENCRYPT_MODE, secKey, iv, new SecureRandom());
				}
			}
			else {
				if (type.toUpperCase().equals(KEY_SPEC_DES_EDE.toUpperCase())) {
					// 24 位密钥，如果位长不够则打补丁 0 补足
					KeySpec keySpec = new DESedeKeySpec(Padding(passPhrase).getBytes());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_DES_EDE);
					secKey = keyFactory.generateSecret(keySpec);

					ecipher = Cipher.getInstance(secKey.getAlgorithm());
					ecipher.init(Cipher.ENCRYPT_MODE, secKey);
				}
				else if (type.toUpperCase().equals(KEY_SPEC_DES.toUpperCase())) {
					// 28 位密钥，如果位长不够则打补丁 0 补足
					KeySpec keySpec = new DESKeySpec(Padding(passPhrase).getBytes());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_DES);
					secKey = keyFactory.generateSecret(keySpec);

					ecipher = Cipher.getInstance(secKey.getAlgorithm());
					ecipher.init(Cipher.ENCRYPT_MODE, secKey);
				}
			}

			// Encode the string into bytes using utf-8
			byte[] buffer = str.getBytes("UTF8");
			// Encrypt
			byte[] enc = ecipher.doFinal(buffer);
			// Encode bytes to base64 to get a string
			return Base64.getEncoder().encodeToString(enc);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Deprecated
	public String decrypt(String str) throws Exception {
		Cipher dcipher = null;

		try {
			if (null != salt) {
				IvParameterSpec iv = new IvParameterSpec(Long.toHexString(salt.longValue()).getBytes());

				if (type.toUpperCase().equals(KEY_SPEC_PBE_DES.toUpperCase())) {
					KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), iv.getIV(), iterationCount);
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_PBE_DES);
					secKey = keyFactory.generateSecret(keySpec);

					dcipher = Cipher.getInstance(secKey.getAlgorithm());
					dcipher.init(Cipher.DECRYPT_MODE, secKey, new PBEParameterSpec(iv.getIV(), iterationCount));
				}
				else if (type.toUpperCase().equals(KEY_SPEC_DES + "/" + KEY_SPEC_DES_CBC_PKCS5PADDING.toUpperCase())) {
					KeySpec keySpec = new DESKeySpec(Padding(passPhrase).getBytes());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_DES + "/"
							+ KEY_SPEC_DES_CBC_PKCS5PADDING);
					secKey = keyFactory.generateSecret(keySpec);

					dcipher = Cipher.getInstance(KEY_SPEC_DES + "/" + KEY_SPEC_DES_CBC_PKCS5PADDING);
					dcipher.init(Cipher.DECRYPT_MODE, secKey, iv, new SecureRandom());
				}
			}
			else {
				// 24 位密钥，如果位长不够则打补丁 0 补足
				if (type.toUpperCase().equals(KEY_SPEC_DES_EDE.toUpperCase())) {
					KeySpec keySpec = new DESedeKeySpec(Padding(passPhrase).getBytes());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_DES_EDE);
					secKey = keyFactory.generateSecret(keySpec);

					dcipher = Cipher.getInstance(secKey.getAlgorithm());
					dcipher.init(Cipher.DECRYPT_MODE, secKey);
				}
				// 8 位密钥，如果位长不够则打补丁 0 补足
				else if (type.toUpperCase().equals(KEY_SPEC_DES.toUpperCase())) {
					KeySpec keySpec = new DESKeySpec(Padding(passPhrase).getBytes());
					SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_SPEC_DES);
					secKey = keyFactory.generateSecret(keySpec);

					dcipher = Cipher.getInstance(secKey.getAlgorithm());
					dcipher.init(Cipher.DECRYPT_MODE, secKey);
				}
			}

			// Decode base64 to get bytes
			@SuppressWarnings("restriction") byte[] dec = Base64.getDecoder().decode(str);
			// Decrypt
			byte[] buffer = dcipher.doFinal(dec);
			// Decode using utf-8
			return new String(buffer, "UTF8");
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public static String Padding(String input) {
		return Padding(input, 24);
	}

	public static String Padding(String input, int length) {
		int len = input.length();
		if (len > length) {
			throw new RuntimeException("Input is too long.");
		}

		StringBuffer outputBuffer = new StringBuffer(input);
		outputBuffer.setLength(length);

		return outputBuffer.toString();
	}
}
