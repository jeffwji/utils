package net.tinybrick.utils.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestNumberRestClient {
	Logger logger = Logger.getLogger(this.getClass());

	IRestCrudClient restClient = new RestClient("", "");


	@Test
	public void postFormTest() throws KeyManagementException, NoSuchAlgorithmException,
			IOException {
		final String uri = "http://store.test.htche.com/number.php";
		//final String uri = "http://localhost:8080/htche-web-sms/rest/changeRequests/0";
		Map<String, Object> form = new HashMap<String, Object>();
		form.put("username", "scheduler");
		form.put("password", "fNLGosx8GE");
		form.put("number", "222");
		form.put("id", "1");
		StringBuffer sb = new StringBuffer();
		int status = restClient.put(sb, uri, form);
		logger.debug(sb.toString());
	}
}
