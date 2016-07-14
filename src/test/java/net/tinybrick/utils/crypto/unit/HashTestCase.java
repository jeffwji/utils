package net.tinybrick.utils.crypto.unit;

import net.tinybrick.utils.crypto.MD5;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashTestCase {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void md5Test() {
		String hash = MD5.hash("ABC");
		logger.debug(hash);
	}
}
