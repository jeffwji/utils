package net.tinybrick.utils.rest;

import net.tinybrick.utils.crypto.Codec;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.Map.Entry;

public class RestClient implements IRestClient {
    AUTHENTICATION_METHOD default_auth_method = null;

    public InputStream getCertificateInput() {
        return null;
    }

    public InputStream getPrivateKeyInput() {
        return null;
    }

    public String getKeyType() {
        return null;
    }

    public char[] getPrivateKeyPass() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getBearer() {
        return null;
    }

    @Override
    public AUTHENTICATION_METHOD getAuthenticationMethod() {
        return default_auth_method;
    }

    public void setAuthenticationMethod(AUTHENTICATION_METHOD method) {
        default_auth_method = method;
    }

    public RestClient() {
    }

    protected String encrypt(String str) throws Exception {
        return Codec.stringToBase64(str);
    }

    /**
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    protected HttpClient getSslHttpClient() throws NoSuchAlgorithmException, KeyManagementException, CertificateException, UnrecoverableKeyException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(getSSLSocketFactory()).build();

        return httpClient;
    }

    /**
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    protected SSLConnectionSocketFactory getSSLSocketFactory() throws NoSuchAlgorithmException, KeyManagementException, CertificateException, InvalidKeyException, IOException, UnrecoverableKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        SSLConnectionSocketFactory sslsf = null;
        try {
            if (null == getCertificateInput()) {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                sslsf = new SSLConnectionSocketFactory(builder.build(),
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            } else {
                SSLContext sslContext = getSSLContext(getCertificateInput(), getPrivateKeyInput(), getKeyType(), getPrivateKeyPass());
                sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }

        return sslsf;
    }

	/*private void setDefaultSocketContext(SSLContext context) {
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
	}*/

    public SSLContext getSSLContext(InputStream certificateInput, InputStream privateKeyInput, String keyType, char[] priKeyPass)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, UnrecoverableKeyException {
        java.security.cert.Certificate[] chainList = getCertificateChain(new BufferedInputStream(certificateInput));
        PrivateKey privateKey = getPrivateKey(privateKeyInput, keyType, priKeyPass);

        TrustManagerFactory tmf = getTrustManagerFactory(chainList);
        KeyManagerFactory kmf = getKeyManagerFactory(chainList, privateKey, priKeyPass);

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context;
    }

    private java.security.cert.Certificate[] getCertificateChain(BufferedInputStream input) throws CertificateException {
        Collection<? extends java.security.cert.Certificate> chain = CertificateFactory.getInstance("X.509").generateCertificates(input);
        List list = new ArrayList(chain);
        java.security.cert.Certificate[] certificates = new java.security.cert.Certificate[list.size()];
        list.toArray(certificates);
        return certificates;
    }

    private PrivateKey getPrivateKey(InputStream keyInput, String KEY_TYPE, char[] KeyPassphase)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] encoded = org.apache.commons.codec.binary.Base64.decodeBase64(getByteArrayFromInputStream(keyInput));

        EncryptedPrivateKeyInfo ekey = new EncryptedPrivateKeyInfo(encoded);
        Cipher cip = Cipher.getInstance(ekey.getAlgName());
        PBEKeySpec pspec = new PBEKeySpec(KeyPassphase);
        SecretKeyFactory skfac = SecretKeyFactory.getInstance(ekey.getAlgName());
        Key pbeKey = skfac.generateSecret(pspec);
        AlgorithmParameters algParams = ekey.getAlgParameters();
        cip.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
        PKCS8EncodedKeySpec pkcs8KeySpec = ekey.getKeySpec(cip);
        KeyFactory rsaKeyFac = KeyFactory.getInstance(KEY_TYPE);
        return (PrivateKey) rsaKeyFac.generatePrivate(pkcs8KeySpec);
    }

    private byte[] getByteArrayFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    private TrustManagerFactory getTrustManagerFactory(java.security.cert.Certificate[] chainList)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);

        ks.setCertificateEntry(Integer.toString(1), chainList[chainList.length - 1]);
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        return tmf;
    }

    private KeyManagerFactory getKeyManagerFactory(java.security.cert.Certificate[] chainList, PrivateKey privateKey, char[] priKeyPass) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, UnrecoverableKeyException {
        KeyStore clientKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        clientKeyStore.load(null, null);
        clientKeyStore.setCertificateEntry("certificate", chainList[0]);
        clientKeyStore.setKeyEntry("private-key",
                privateKey,
                priKeyPass,
                chainList);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(clientKeyStore, priKeyPass);

        return keyManagerFactory;
    }

    protected RestTemplate getRestTemplate() throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, KeyManagementException {
        return getRestTemplate(false);
    }

    protected RestTemplate getRestTemplate(boolean tlsEnabled) throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, KeyManagementException {
        class UnhandleErrorRestTemplate extends RestTemplate {
            public UnhandleErrorRestTemplate() throws IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, KeyManagementException {
                if (tlsEnabled) {
                    this.setRequestFactory(new HttpComponentsClientHttpRequestFactory(getSslHttpClient()));
                }
                setErrorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                    }
                });
            }
        }

        return new UnhandleErrorRestTemplate();
    }

    private HttpEntity<?> getHttpEntity(MultiValueMap<String, String> form) throws Exception {
        HttpHeaders headers = new HttpHeaders();

        if (getAuthenticationMethod() == AUTHENTICATION_METHOD.Basic) {
            headers.add("Authorization", "Basic " + encrypt(getUsername() + ":" + getPassword()));
        } else if (getAuthenticationMethod() == AUTHENTICATION_METHOD.Bearer) {
            headers.add("Authorization", "Bearer " + getBearer());
        }

        if (null != form) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(form,
                    headers);

            return httpEntity;
        } else
            return new HttpEntity<Void>(headers);
    }

    /**
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    protected RestTemplate getSSLRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, IOException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        HttpComponentsClientHttpRequestFactory httpClient = new HttpComponentsClientHttpRequestFactory(getSslHttpClient());
        return new RestTemplate(httpClient);
    }

    /**
     * @param testRestTemplate
     * @param url
     * @param method
     * @param requestEntity
     * @param returnType
     * @param redirect
     * @return
     */
    protected <T> ResponseEntity<T> request(RestTemplate testRestTemplate, String url, HttpMethod method,
                                            HttpEntity<?> requestEntity, List<MediaType> acceptableMediaTypes, Class<T> returnType, boolean redirect) {
        HttpHeaders requestHeaders = new HttpHeaders();

        if (null != requestEntity)
            requestHeaders.putAll(requestEntity.getHeaders());

        if (0 == requestHeaders.getAccept().size()) {
            requestHeaders.setAccept(acceptableMediaTypes);
        }

        requestEntity = new HttpEntity<Object>(null == requestEntity ? null : requestEntity.getBody(), requestHeaders);

        ResponseEntity<T> responseEntity = testRestTemplate.exchange(url, method, requestEntity, returnType);

        if (responseEntity.getStatusCode().equals(HttpStatus.FOUND)) {
            if (redirect) {
                String location = null;
                HttpHeaders responseHeaders = responseEntity.getHeaders();
                Iterator<Entry<String, List<String>>> items = responseHeaders.entrySet().iterator();
                while (items.hasNext()) {
                    Entry<String, List<String>> item = items.next();
                    if (item.getKey().equals("Set-Cookie")) {
                        requestHeaders.put("Cookie", item.getValue());
                    }
                    if (item.getKey().equals("Location")) {
                        location = item.getValue().get(0);
                    }
                }

                if (null != location) {
                    requestEntity = new HttpEntity<Object>(requestEntity.getBody(), requestHeaders);
                    responseEntity = request(testRestTemplate, location, HttpMethod.GET, requestEntity,
                            acceptableMediaTypes, returnType, redirect);
                }
            }
        }
        return responseEntity;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> get(String url) throws Exception {
        return (ResponseEntity<T>) get(url, String.class);
    }

    @Override
    public <T> ResponseEntity<T> get(String url, Class<T> returnType) throws Exception {
        return get(url, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> get(String url, Class<T> returnType, boolean redirect) throws Exception {
        return get(url, Arrays.asList(MediaType.ALL), returnType, redirect);
    }

    @Override
    public <T> ResponseEntity<T> get(String url, List<MediaType> acceptableMediaTypes, Class<T> returnType)
            throws Exception {
        return get(url, acceptableMediaTypes, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> get(String url, List<MediaType> acceptableMediaTypes, Class<T> returnType,
                                     boolean redirect) throws Exception {
        boolean tlsEnabled = url.toUpperCase().startsWith("HTTPS://");
        return request(getRestTemplate(tlsEnabled), url, HttpMethod.GET, getHttpEntity(null), acceptableMediaTypes, returnType,
                redirect);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> post(String url, MultiValueMap<String, String> form) throws Exception {
        return (ResponseEntity<T>) post(url, form, String.class, true);
    }

    @Override
    public <T> ResponseEntity<T> post(String url, MultiValueMap<String, String> form, Class<T> returnType)
            throws Exception {
        return post(url, form, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> post(String url, MultiValueMap<String, String> form, Class<T> returnType,
                                      boolean redirect) throws Exception {
        return post(url, form, Arrays.asList(MediaType.ALL), returnType, redirect);
    }

    @Override
    public <T> ResponseEntity<T> post(String url, MultiValueMap<String, String> form,
                                      List<MediaType> acceptableMediaTypes, Class<T> returnType) throws Exception {
        return post(url, form, acceptableMediaTypes, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> post(String url, MultiValueMap<String, String> form,
                                      List<MediaType> acceptableMediaTypes, Class<T> returnType, boolean redirect) throws Exception {
        boolean tlsEnabled = url.toUpperCase().startsWith("HTTPS://");
        return request(getRestTemplate(tlsEnabled), url, HttpMethod.POST, getHttpEntity(form), acceptableMediaTypes, returnType,
                redirect);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> delete(String url, MultiValueMap<String, String> form) throws Exception {
        return (ResponseEntity<T>) delete(url, form, String.class, true);
    }

    @Override
    public <T> ResponseEntity<T> delete(String url, MultiValueMap<String, String> form, Class<T> returnType)
            throws Exception {
        return delete(url, form, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> delete(String url, MultiValueMap<String, String> form, Class<T> returnType,
                                        boolean redirect) throws Exception {
        return delete(url, form, Arrays.asList(MediaType.ALL), returnType, redirect);
    }

    @Override
    public <T> ResponseEntity<T> delete(String url, MultiValueMap<String, String> form,
                                        List<MediaType> acceptableMediaTypes, Class<T> returnType) throws Exception {
        return delete(url, form, acceptableMediaTypes, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> delete(String url, MultiValueMap<String, String> form,
                                        List<MediaType> acceptableMediaTypes, Class<T> returnType, boolean redirect) throws Exception {
        boolean tlsEnabled = url.toUpperCase().startsWith("HTTPS://");
        return request(getRestTemplate(tlsEnabled), url, HttpMethod.DELETE, getHttpEntity(form), acceptableMediaTypes, returnType,
                redirect);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> put(String url, MultiValueMap<String, String> form) throws Exception {
        return (ResponseEntity<T>) put(url, form, String.class, true);
    }

    @Override
    public <T> ResponseEntity<T> put(String url, MultiValueMap<String, String> form, Class<T> returnType)
            throws Exception {
        return put(url, form, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> put(String url, MultiValueMap<String, String> form, Class<T> returnType,
                                     boolean redirect) throws Exception {
        return put(url, form, Arrays.asList(MediaType.ALL), returnType, redirect);
    }

    @Override
    public <T> ResponseEntity<T> put(String url, MultiValueMap<String, String> form,
                                     List<MediaType> acceptableMediaTypes, Class<T> returnType) throws Exception {
        return put(url, form, acceptableMediaTypes, returnType, true);
    }

    @Override
    public <T> ResponseEntity<T> put(String url, MultiValueMap<String, String> form,
                                     List<MediaType> acceptableMediaTypes, Class<T> returnType, boolean redirect) throws Exception {
        boolean tlsEnabled = url.toUpperCase().startsWith("HTTPS://");
        return request(getRestTemplate(tlsEnabled), url, HttpMethod.PUT, getHttpEntity(form), acceptableMediaTypes, returnType,
                redirect);
    }
}
