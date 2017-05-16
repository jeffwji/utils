package net.tinybrick.utils.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

public class Codec<T> {
	private static ObjectMapper mapper = new ObjectMapper();

	//private static Logger logger = LoggerFactory.getLogger(Codec.class);

	/**
	 * @param str
	 * @return
	 * @throws EncoderException
	 * @throws UnsupportedEncodingException
	 */
	public static String stringToBase64(String str) throws EncoderException, UnsupportedEncodingException {
		return toBase64(str.getBytes(Charset.forName("UTF-8")));
	}

	/**
	 * @param decoded
	 * @return
	 * @throws EncoderException
	 * @throws UnsupportedEncodingException
	 */
	public static String toBase64(byte[] decoded) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64(decoded), "UTF-8");
	}

	/**
	 * @param base64
	 * @return
	 * @throws DecoderException
	 * @throws UnsupportedEncodingException
	 */
	public static String stringFromBas64(String base64) throws DecoderException, UnsupportedEncodingException {
		return new String(fromBas64(base64), "UTF-8");
	}

	/**
	 * @param base64
	 * @return
	 * @throws DecoderException
	 */
	public static byte[] fromBas64(String base64) throws DecoderException {
		return Base64.decodeBase64(base64.getBytes(Charset.forName("UTF-8")));
	}

	public static final <T> String toJsonString(T object) {
		return toJsonString(object, false);
	}

	/**
	 * @see #toJsonString(T object)
	 * @param object
	 * @param wrapRoot
	 *            true将类名打包为root，否则不打包类名。缺省为true.
	 * @return
	 * @throws EncoderException
	 */
	public static final <T> String toJsonString(T object, boolean wrapRoot) {
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, wrapRoot);

		try {
			return mapper.writeValueAsString(object);
		}
		catch (Exception e) {
			//logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param jsonString
	 * @param clazz
	 * @return
	 * @throws DecoderException
	 */
	public static final <T> T toObject(String jsonString, Class<T> clazz) {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (null != jsonString && null != clazz) {
			try {
				return mapper.readValue(jsonString, clazz);
			}
			catch (IOException e) {
				//logger.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		else {
			//logger.error("Unable to convert string: \"" + jsonString + "\"");
			throw new RuntimeException("Unable to convert string: \"" + jsonString + "\"");
		}
	}

	/**
	 * @param jsonString
	 * @param modelPackagePath
	 * @return
	 * @throws DecoderException
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T toObject(String jsonString, String modelPackagePath) {
		String jsonValue = null;
		Class<T> clazz = null;
		try {
			jsonValue = jsonString;
			clazz = (Class<T>) Class.forName(modelPackagePath);
		}
		catch (Exception e) {
			//logger.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		return toObject(jsonValue, clazz);
	}

	public static Object toObject(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		String root = (String) jsonObject.keys().next();
		jsonObject = jsonObject.getJSONObject(root);

		return toObject(jsonObject.toString(), root);
	}

}
