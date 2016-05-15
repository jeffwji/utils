package com.wang.utils.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.wang.utils.crypto.Codec;

public class RestClient implements IRestCrudClient {
	Logger logger = Logger.getLogger(this.getClass());

	public RestClient() {
	}

	public RestClient(String username, String password) {
		this.setUsername(username);
		this.setPassword(password);
	}

	String encode = "UTF-8";

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	String username;
	String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	boolean secure = false;

	HttpClient httpClient = null;

	public static String HTTP_OPERATION_GET = "get";
	public static String HTTP_OPERATION_PUT = "put";
	public static String HTTP_OPERATION_DELETE = "delete";
	public static String HTTP_OPERATION_POST = "post";

	public void initRestClient() throws NoSuchAlgorithmException, KeyManagementException {
		if (!secure) {
			httpClient = HttpClients.custom().build();
		}
		else {
			SSLConnectionSocketFactory sslsf = null;
			try {
				SSLContextBuilder builder = new SSLContextBuilder();
				builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
				sslsf = new SSLConnectionSocketFactory(builder.build(),
						SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			}
			catch (KeyStoreException e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}

			httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		}
	}

	/* (non-Javadoc)
	 * @see com.wang.utils.http.IRestCRUDClient#get(java.lang.String, boolean)
	 */
	@Override
	public int get(final StringBuffer stream, String uri) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException {
		return request(stream, uri, null, null, HTTP_OPERATION_GET, null);
	}

	/* (non-Javadoc)
	 * @see com.wang.utils.http.IRestCRUDClient#post(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public int post(final StringBuffer stream, String uri, String content, String contentType)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return request(stream, uri, content, contentType, HTTP_OPERATION_POST, null);
	}

	/* (non-Javadoc)
	 * @see com.wang.utils.http.IRestCRUDClient#delete(java.lang.String, boolean)
	 */
	@Override
	public int delete(final StringBuffer stream, String uri) throws ClientProtocolException, IOException,
			KeyManagementException, NoSuchAlgorithmException {
		return request(stream, uri, null, null, HTTP_OPERATION_DELETE, null);
	}

	/* (non-Javadoc)
	 * @see com.wang.utils.http.IRestCRUDClient#put(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public int put(final StringBuffer stream, String uri, String content, String contentType)
			throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
		return request(stream, uri, content, contentType, HTTP_OPERATION_PUT, null);
	}

	@Override
	public int post(StringBuffer stream, String uri, Map<String, Object> form) throws ClientProtocolException,
			IOException, KeyManagementException, NoSuchAlgorithmException {
		return request(stream, uri, form, HTTP_OPERATION_POST, null);
	}

	@Override
	public int put(StringBuffer stream, String uri, Map<String, Object> form) throws ClientProtocolException,
			IOException, KeyManagementException, NoSuchAlgorithmException {
		return request(stream, uri, form, HTTP_OPERATION_PUT, null);
	}

	public int request(final StringBuffer stream, HttpUriRequest httpUriRequest) throws ClientProtocolException,
			IOException, KeyManagementException, NoSuchAlgorithmException {
		if (null == httpClient) {
			initRestClient();
		}

		ResponseHandler<Integer> requestHandler = new ResponseHandler<Integer>() {
			//@Override
			public Integer handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				Integer statusCode = response.getStatusLine().getStatusCode();
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String resData = EntityUtils.toString(entity, encode);
					if (null != resData && resData.length() > 0) {
						processResult(resData, stream);
					}
				}

				return statusCode;
			}
		};

		Integer status = httpClient.execute(httpUriRequest, requestHandler);

		return status.intValue();
	}

	@Override
	public synchronized int request(final StringBuffer stream, String uri, String content, String contentType,
			String method, String acceptType) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException {
		setSecure(uri);

		HttpUriRequest httpUriRequest = generateHttpRequest(uri, method, acceptType);

		if (null != content) {
			StringEntity entity = new StringEntity(content, encode);
			entity.setContentEncoding(encode);
			if (null != contentType)
				entity.setContentType(contentType);
			((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(entity);
		}

		return request(stream, httpUriRequest);
	}

	@Override
	public synchronized int request(StringBuffer stream, String uri, Map<String, Object> form, String method,
			String acceptType) throws ClientProtocolException, IOException, KeyManagementException,
			NoSuchAlgorithmException {
		setSecure(uri);
		HttpUriRequest httpUriRequest = generateHttpRequest(uri, method, acceptType);

		if (null != form && form.size() > 0) {
			httpUriRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");

			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String key : form.keySet()) {
				list.add(new BasicNameValuePair(key, form.get(key).toString()));
			}
			if (httpUriRequest.getMethod().toLowerCase().equals(HTTP_OPERATION_POST)) {
				((HttpPost) httpUriRequest).setEntity(new UrlEncodedFormEntity(list, encode));
			}
			else {
				((HttpPut) httpUriRequest).setEntity(new UrlEncodedFormEntity(list, encode));
			}
		}

		return request(stream, httpUriRequest);
	}

	public void setSecure(String uri) {
		secure = uri.toLowerCase().startsWith("https://");
	}

	public void processResult(String object, StringBuffer stream) throws UnsupportedEncodingException, IOException {
		stream.delete(0, stream.length()).append(object);
	}

	protected HttpUriRequest generateHttpRequest(String uri, String method, String acceptType) {
		method = method.toLowerCase();
		HttpUriRequest httpUriRequest = method.equals(HTTP_OPERATION_PUT) ? new HttpPut(uri) : method
				.equals(HTTP_OPERATION_POST) ? new HttpPost(uri)
				: method.equals(HTTP_OPERATION_DELETE) ? new HttpDelete(uri)
						: method.equals(HTTP_OPERATION_GET) ? new HttpGet(uri) : null;
		if (null == httpUriRequest)
			throw new UnsupportedOperationException("Method " + method + " is not supported");

		if (null != acceptType)
			httpUriRequest.setHeader(HttpHeaders.ACCEPT, acceptType);

		httpUriRequest.setHeader("User-Agent", "RESTful Client");

		if (null != getUsername()) {
			try {
				httpUriRequest.setHeader(HttpHeaders.AUTHORIZATION,
						"Basic " + getAuthenticationString(getUsername(), getPassword()));
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new RuntimeException("Username or password is invalid.");
			}
		}

		return httpUriRequest;
	}

	protected static String getAuthenticationString(String username, String password)
			throws UnsupportedEncodingException {
		return Codec.toBase64((username + ":" + password).getBytes());
	}
}
