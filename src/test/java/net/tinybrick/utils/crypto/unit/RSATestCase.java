package net.tinybrick.utils.crypto.unit;

import net.tinybrick.utils.crypto.Codec;
import net.tinybrick.utils.crypto.RSA;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ji.wang on 2017-05-10.
 */
public class RSATestCase {
    private static Logger logger = LogManager.getLogger(RSATestCase.class);
    int keyLength = 1024;

    @Test
    public void testGenerateKeyPair() {
        try {
            byte[][] keys = RSA.generateKeyPair(keyLength);
            System.out.println("Public Key: " + Codec.toBase64(keys[0]));
            System.out.println("Private Key: " + Codec.toBase64(keys[1]));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRSAKeys() {
        String message = "Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA!Hello RSA! Final Hello!";
        //String message = "Hello RSA!";
        try {
            byte[][] keys = RSA.generateKeyPair(keyLength);

            byte[] encoded = RSA.encrypt(message.getBytes("UTF-8"), keys[0]);
            System.out.println("Encoded message: " + Codec.toBase64(encoded));

            String decoded = new String(RSA.decrypt(encoded, keys[1]));
            System.out.println("Decoded message: " + decoded);

            Assert.assertEquals(decoded, message);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Test
    public void testVerify() {
        String message = "Hello RSA!";
        try {
            byte[][] keys = RSA.generateKeyPair(keyLength);

            byte[] encoded = RSA.sign(message.getBytes("UTF-8"), keys[1]);
            System.out.println("Signature for the message is: " + Codec.toBase64(encoded));

            Assert.assertTrue(RSA.verify(message.getBytes(), encoded, keys[0]));
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
