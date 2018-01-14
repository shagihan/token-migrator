/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.wso2.tokenmigrator;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Class related to Encrypt and Decrypt.
 */
public class EncryptDecryptUtils {

    /**
     * holds keystore.
     */
    private KeyStore keyStore = null;

    /**
     * Constructore initilize the keystore.
     * @param keystoreFile keysore file location
     * @param keystorePassword keystore password
     * @throws IOException input output file execption
     */
    public EncryptDecryptUtils(String keystoreFile, String keystorePassword)
            throws IOException {
        try {
            keyStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(keystoreFile);
            keyStore.load(in, keystorePassword.toCharArray());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * encrypt the keys.
     * @param plaintext plain text key/token
     * @param encryptAlgorhythm algorhythm use for encryption
     * @return encryptrd byte array
     * @throws Exception when encryption fails
     */
    public byte[] encrypt(String plaintext, String encryptAlgorhythm) throws Exception {
        Certificate[] certs = keyStore.getCertificateChain("wso2carbon");
        Cipher cipher;
        if (encryptAlgorhythm.isEmpty()) {
            cipher = Cipher.getInstance("RSA/ECB/OAEPwithSHA1andMGF1Padding", "BC");
        } else {
            cipher = Cipher.getInstance(encryptAlgorhythm, "BC");
        }
        cipher.init(Cipher.ENCRYPT_MODE, certs[0].getPublicKey());
        return cipher.doFinal(plaintext.getBytes());
    }

    /**
     * Decrypt the byte array.
     * @param ciphertext byte array contains encrypted text
     * @param decryptAlgorhythm algorhythm to decrypt
     * @return dycripted key
     * @throws Exception when unable to decrypt
     */
    public String decrypt(byte[] ciphertext, String decryptAlgorhythm) throws Exception {
        PrivateKey privateKey = (PrivateKey) keyStore
                .getKey("wso2carbon", "wso2carbon".toCharArray());
        Cipher cipher;
        if (decryptAlgorhythm.isEmpty()) {
            cipher = Cipher.getInstance("RSA", "BC");
        } else {
            cipher = Cipher.getInstance(decryptAlgorhythm, "BC");
        }
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherbyte = cipher.doFinal(ciphertext);
        return new String(cipherbyte);
    }

}
