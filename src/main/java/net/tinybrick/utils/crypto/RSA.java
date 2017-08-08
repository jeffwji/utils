package net.tinybrick.utils.crypto;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ji.wang on 2017-05-10.
 */
public class RSA {
    private static Logger logger = LogManager.getLogger(RSA.class);
    protected static String xform = "RSA";
    protected static String algorithm = "RSA/ECB/PKCS1Padding";
    //protected static String algorithm = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    protected static String sign_algorithm = "SHA256withRSA";

    public static byte[][] generateKeyPair(int length) throws NoSuchAlgorithmException {
        byte[][] keyPair = new byte[2][];

        KeyPairGenerator kpg = KeyPairGenerator.getInstance(xform);
        kpg.initialize(length);
        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();
        keyPair[0] = publicKey.getEncoded();
        keyPair[1] = privateKey.getEncoded();
        return keyPair;
    }

    public static byte[] encrypt(byte[] toBeEncrypt, File publicKeyFile) throws Exception {
        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);

        return encrypt(toBeEncrypt, encodedKey);
    }

    public static byte[] encrypt(byte[] toBeEncrypt, byte[] publicKey) throws Exception {
        List encryptedBlocks = new ArrayList();

         // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance(xform);
        RSAPublicKey pk = (RSAPublicKey)kf.generatePublic(publicKeySpec);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, pk);

        int blockLength = pk.getModulus().bitLength()/8 - 11;
        int dataLength = crypt(cipher, toBeEncrypt, blockLength, encryptedBlocks);
        return convertToByteArray(encryptedBlocks, dataLength);
    }

    protected static int crypt(Cipher cipher, byte[] data, int blockLength, List resultList) throws BadPaddingException, IllegalBlockSizeException {
        int offset=0;
        int dataLength = 0;
        do {
            byte[] block = Arrays.copyOfRange(data,
                    offset,
                    (offset + blockLength)>data.length?data.length:(offset + blockLength));
            byte[] encryptedBlock = cipher.doFinal(block);
            resultList.add(encryptedBlock);
            offset+= block.length;
            dataLength += encryptedBlock.length;
        }while(offset < data.length);

        return dataLength;
    }

    public static byte[] decrypt(byte[] toBeDecrypt, File privateKeyFile) throws Exception {
        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);
        return decrypt(toBeDecrypt, encodedKey);
    }

    public static byte[] decrypt(byte[] toBeDecrypt, byte[] privateKey) throws Exception{
        List decryptedBlocks = new ArrayList();

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(xform);
        RSAPrivateKey pk = (RSAPrivateKey)kf.generatePrivate(privateKeySpec);

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, pk);

        int blockLength = pk.getModulus().bitLength()/8;
        int dataLength = crypt(cipher, toBeDecrypt, blockLength, decryptedBlocks);
        return convertToByteArray(decryptedBlocks, dataLength);
    }

    private static byte[] convertToByteArray(List blocks, int length) {
        byte[] data = new byte[length];
        int offset=0;
        for (int n=0; n<blocks.size(); n++){
            byte[] block = (byte[]) blocks.get(n);
            System.arraycopy(block, 0, data, offset, block.length);
            offset+= block.length;
        }

        return data;
    }

    public static byte[] sign(byte[] toBeSigned, byte[] privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(xform);
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        Signature privateSignature = Signature.getInstance(sign_algorithm);
        privateSignature.initSign(kf.generatePrivate(privateKeySpec));
        privateSignature.update(toBeSigned);
        return privateSignature.sign();
    }

    public static boolean verify(byte[] data, byte[] sec, byte[] publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance(xform);
        PublicKey pk = kf.generatePublic(publicKeySpec);

        Signature sign = Signature.getInstance(sign_algorithm);
        sign.initVerify(pk);
        sign.update(data);
        return sign.verify(sec);
    }
}
