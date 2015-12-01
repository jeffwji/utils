package com.wang.utils.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public interface IRestCrudClient extends IRestClient {

	public abstract int get(final StringBuffer stream, String uri) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException;

	public abstract int post(final StringBuffer stream, String uri, String content, String contentType)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException;

	public abstract int post(final StringBuffer stream, String uri, Map<String, Object> form)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException;

	public abstract int delete(final StringBuffer stream, String uri) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException;

	public abstract int put(final StringBuffer stream, String uri, String content, String contentType)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException;

	public abstract int put(final StringBuffer stream, String uri, Map<String, Object> form)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException;
}