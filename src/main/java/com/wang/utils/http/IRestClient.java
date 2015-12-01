package com.wang.utils.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public interface IRestClient {
	public abstract int request(final StringBuffer stream, String uri, String content, String contentType,
			String method, String acceptType) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException;

	public abstract int request(final StringBuffer stream, String uri, Map<String, Object> form, String method,
			String acceptType) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException;
}