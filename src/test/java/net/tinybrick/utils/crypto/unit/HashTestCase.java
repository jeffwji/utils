package net.tinybrick.utils.crypto.unit;

import net.tinybrick.utils.crypto.MD5;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class HashTestCase {
	Logger logger = LogManager.getLogger(this.getClass());

	@Test
	public void md5Test() {
		String hash = MD5.hash("ABC");
		logger.debug(hash);
	}
}
