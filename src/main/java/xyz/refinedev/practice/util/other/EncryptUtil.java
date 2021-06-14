package xyz.refinedev.practice.util.other;

import it.unimi.dsi.fastutil.Hash;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;

/**
 * This Project is the property of Refine Development © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Drizzy
 * Created at 5/26/2021
 * Project: Array
 */

public class EncryptUtil {

    /**
     * Convert a String into Encrypted String
     * according to the hash type (MD5/SHA1)
     *
     * @param txt, text in plain format
     * @param hashType MD5 OR SHA1
     * @return hash in hashType
     */
    public static String getHash(String txt, String hashType) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashType);
            byte[] array = md.digest(txt.getBytes());
            StringBuilder sb = new StringBuilder();
            for ( byte b : array ) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            //error action
        }
        return null;
    }

    public static String md5(String txt) {
        return getHash(txt, "MD5");
    }

    public static String sha1(String txt) {
        return getHash(txt, "SHA1");
    }

    public String[] crypto(String crypt) {
        String[] cryp = new String[2];
        try {

            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);

            KeyPair pair = keyPairGen.generateKeyPair();
            PublicKey publicKey = pair.getPublic();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] input = crypt.getBytes();
            cipher.update(input);

            //Encrypted
            byte[] cipherText = cipher.doFinal();
            cryp[0] = new String(cipherText, StandardCharsets.UTF_8);

            cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());

            //Decrypted
            byte[] decipheredText=cipher.doFinal(cipherText);
            cryp[1] = new String(decipheredText);
        } catch (Exception ignored) {}
        return cryp;
    }
}
