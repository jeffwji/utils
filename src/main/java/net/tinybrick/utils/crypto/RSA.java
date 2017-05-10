package net.tinybrick.utils.crypto;

import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by ji.wang on 2017-05-10.
 */
public class RSA {
    private static Logger logger = org.apache.log4j.LogManager.getLogger(RSA.class);

    public static byte[][] generateKeyPair(int length) throws NoSuchAlgorithmException {
        byte[][] keyPair = new byte[2][];
        //String xform = "RSA/NONE/PKCS1PADDING";

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(length);
        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        keyPair[0] = publicKey.getEncoded();
        keyPair[1] = privateKey.getEncoded();
        return keyPair;
    }

    public static byte[] encrypt(byte[] inpBytes, File publicKeyFile) throws Exception {
        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);
        return encrypt(inpBytes, encodedKey);
    }

    public static byte[] encrypt(byte[] inpBytes, byte[] publicKey) throws Exception {
        //String xform = "RSA/NONE/PKCS1PADDING";
        String xform = "RSA";

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);

        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.ENCRYPT_MODE, pk);
        return cipher.doFinal(inpBytes);
    }


    public static byte[] decrypt(byte[] inpBytes, File privateKeyFile) throws Exception {
        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);
        return decrypt(inpBytes, encodedKey);
    }

    public static byte[] decrypt(byte[] inpBytes, byte[] privateKey) throws Exception{
        //String xform = "RSA/NONE/PKCS1PADDING";
        String xform = "RSA";

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.DECRYPT_MODE, pk);
        return cipher.doFinal(inpBytes);
    }
}
