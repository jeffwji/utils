package com.wang.utils.http;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.wang.utils.http.IRestCrudClient;
import com.wang.utils.http.RestClient;

public class TestRestClient {
	Logger logger = Logger.getLogger(this.getClass());

	IRestCrudClient restClient = new RestClient("scheduler", "Scheduler251X69E7");

	@Test
	public void restClientTest() throws KeyManagementException, NoSuchAlgorithmException,
			IOException {
		StringBuffer sb = new StringBuffer();
		int status = restClient.get(sb, "http://localhost:8080/htche-web-sms/rest/products");
		logger.debug(sb.toString());
	}

	@Test
	public void postFormTest() throws KeyManagementException, NoSuchAlgorithmException,
			IOException {
		final String uri = "http://store.test.htche.com/status.php";
		//final String uri = "http://localhost:8080/htche-web-sms/rest/changeRequests/0";

		Map<String, Object> form = new HashMap<String, Object>();
		form.put("username", "scheduler");
		form.put("password", "fNLGosx8GE");
		form.put("status", new Integer(3));
		form.put("id", "63");
		/*		form.put(
						"data",
						"{\"id\":63,\"checkSum\":null,\"version\":\"1.0\",\"basic\":{\"name\":\"2015款 宝马i3美国版 金/红\",\"invoicePrice\":260000.00,\"salePrice\":260000.00,\"marketPrice\":250000,\"mobile\":\"15011200000\",\"number\":40,\"batchId\":null,\"pattern\":{\"patternId\":26,\"brandId\":1,\"brandName\":\"宝马\",\"seriesId\":14,\"seriesName\":\"i3\",\"modelId\":null,\"modelName\":\"\",\"yearId\":1,\"year\":\"2015\",\"areaId\":8,\"area\":\"美国\"},\"color\":{\"outerColor\":\"金\",\"innerColor\":\"红\"}},\"packages\":[{\"id\":99,\"name\":\"适包\",\"itemList\":[{\"id\":107,\"name\":\"错\",\"valueList\":null}]}],\"images\":[{\"id\":698,\"mediaId\":\"20150413_2d09cc2c-c012-42fb-8b4e-7b97ce0dd167.png\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":536,\"position\":1001,\"isShared\":1},{\"id\":699,\"mediaId\":\"20150413_6d8bbad7-b0d4-4ba5-8453-d1f3c4e71d85.jpg\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":536,\"position\":1002,\"isShared\":1},{\"id\":700,\"mediaId\":\"20150413_116356f7-d4bf-49dc-90ec-fbdbe38d874e.jpg\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":536,\"position\":1003,\"isShared\":1},{\"id\":701,\"mediaId\":\"20150413_c7572393-9caf-4b96-9bfb-12439dcdf0c4.jpg\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":536,\"position\":1004,\"isShared\":1},{\"id\":702,\"mediaId\":\"20150413_40a83e20-cd86-4f97-969c-6e58f79e8606.jpg\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":536,\"position\":1005,\"isShared\":1},{\"id\":722,\"mediaId\":\"20150413_6647eea4-2638-47d4-96af-1ae12014bb4c.jpg\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":553,\"position\":1,\"isShared\":0},{\"id\":723,\"mediaId\":\"20150413_be526dac-4596-494c-92a2-79fef1e92374.jpg\",\"mediaSource\":\"http://sms.test.htche.com:81/file/\",\"repositoryId\":553,\"position\":2,\"isShared\":0}],\"config\":{\"id\":5,\"customizedConfig\":个性配置内容,\"cateList\":[{\"cateId\":1,\"cateName\":\"基本参数\",\"itemList\":[{\"schemeId\":1,\"schemeName\":\"厂商\",\"valueList\":[{\"valueId\":1,\"value\":\"广汽丰田\"}]},{\"schemeId\":2,\"schemeName\":\"级别\",\"valueList\":[{\"valueId\":2,\"value\":\"小型SUV\"}]},{\"schemeId\":3,\"schemeName\":\"发动机\",\"valueList\":[{\"valueId\":5,\"value\":\"2.0T\"},{\"valueId\":6,\"value\":\"220马力\"},{\"valueId\":7,\"value\":\"L4\"}]}]}]},\"member\":{\"memberId\":129,\"memberName\":\"test1\",\"memberMobile\":\"15011200000\",\"memberEmail\":\"test1@htche.com\"},\"status\":1}");*/

		StringBuffer sb = new StringBuffer();
		int status = restClient.post(sb, uri, form);
		Assert.assertEquals(200, status);
		logger.debug(sb.toString());
	}
}
