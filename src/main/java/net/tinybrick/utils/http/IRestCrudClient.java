package net.tinybrick.utils.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface IRestCrudClient extends IRestClient {

	int get(final StringBuffer stream, String uri) throws IOException,
			KeyManagementException, NoSuchAlgorithmException;

	int post(final StringBuffer stream, String uri, String content, String contentType)
			throws IOException, KeyManagementException, NoSuchAlgorithmException;

	int post(final StringBuffer stream, String uri, Map<String, Object> form)
			throws IOException, KeyManagementException, NoSuchAlgorithmException;

	int delete(final StringBuffer stream, String uri) throws IOException,
			KeyManagementException, NoSuchAlgorithmException;

	int put(final StringBuffer stream, String uri, String content, String contentType)
			throws IOException, KeyManagementException, NoSuchAlgorithmException;

	int put(final StringBuffer stream, String uri, Map<String, Object> form)
			throws IOException, KeyManagementException, NoSuchAlgorithmException;
}