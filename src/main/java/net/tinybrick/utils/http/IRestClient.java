package net.tinybrick.utils.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public interface IRestClient {
	int request(final StringBuffer stream, String uri, String content, String contentType,
				String method, String acceptType) throws IOException, KeyManagementException,
			NoSuchAlgorithmException;

	int request(final StringBuffer stream, String uri, Map<String, Object> form, String method,
				String acceptType) throws IOException, KeyManagementException,
			NoSuchAlgorithmException;
}