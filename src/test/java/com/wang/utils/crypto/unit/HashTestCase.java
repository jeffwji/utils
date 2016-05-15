package com.wang.utils.crypto.unit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wang.utils.crypto.MD5;

public class HashTestCase {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void md5Test() {
		String hash = MD5.hash("ABC");
		logger.debug(hash);
	}
}
