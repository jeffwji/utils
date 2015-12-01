package com.wang.utils.crypto.unit;

import org.junit.Assert;
import org.junit.Test;

import com.wang.utils.crypto.DES3;

public class Dec3TestCase {
	final static String orginal = "Hello World";

	@Test
	public void testDES3ede() throws Exception {
		String encoded = DES3.encrypt("123456", orginal);
		Assert.assertTrue(DES3.decrypt("123456", encoded).equals(orginal));
	}

	@Test
	public void testDES3() throws Exception {
		String encoded = DES3.encrypt(DES3.KEY_SPEC_DES, "123456", orginal, null);
		Assert.assertTrue(DES3.decrypt(DES3.KEY_SPEC_DES, "123456", encoded, null).equals(orginal));
	}

	@Test
	public void testDES3WithSalt() throws Exception {
		String encoded = DES3.encrypt("123456", orginal, "abcd");
		Assert.assertTrue(DES3.decrypt("123456", encoded, "abcd").equals(orginal));
	}

	@Test
	public void testDESede() throws Exception {
		DES3 dec3 = new DES3("123456", null, DES3.KEY_SPEC_DES_EDE);
		String encoded = dec3.encrypt(orginal);
		Assert.assertTrue(dec3.decrypt(encoded).equals(orginal));
	}

	@Test
	public void testDES() throws Exception {
		DES3 dec3 = new DES3("123456", null, DES3.KEY_SPEC_DES);
		String encoded = dec3.encrypt(orginal);
		Assert.assertTrue(dec3.decrypt(encoded).equals(orginal));
	}
}
