package org.wso2.tokenmigrator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.JSONObject;

import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

public class HashGenerator {
    public static final String ALGORITHM = "algorithm";
    public static final String HASH = "hash";


    public static String getHash(String token) {
        MessageDigest messageDigest = null;
        byte[] hash = null;
        String hashAlgorithm = "SHA-256";
        try {
            messageDigest = MessageDigest.getInstance(hashAlgorithm);
            messageDigest.update(token.getBytes());
            hash = messageDigest.digest();

        } catch (
            NoSuchAlgorithmException e) {
        }
        JSONObject object = new JSONObject();
        object.put(ALGORITHM, hashAlgorithm);
        object.put(HASH, bytesToHex(hash));
        return object.toString();
    }

    private static String bytesToHex(byte[] bytes) {

        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
