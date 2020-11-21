package com.cnebrera.uc3.tech.lesson5.util.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.cnebrera.uc3.tech.lesson5.util.Lesson5Exception;

/**
 * Helper class for RSA encoding / decoding and to handle signatures
 * <p>
 * The instance of the class will contain the private key of the application and the public key of the other trusted
 * applications.
 * <p>
 * The options have been simplified to allow:
 * <p>
 * - Sign messages with the owned private key
 * - Verify signatures coming from trusted applications (trusted public keys)
 * - Encode messages with the public key of a trusted application
 * - Decode messages that have been encoded with the owned public key
 */
public class RSACrypto {
    /**
     * Size of the supported RSA Key
     */
    public static final int RSA_KEY_SIZE = 1024;
    /**
     * Encoding type for the signature
     */
    public static final String SIGNATURE_CODEC = "SHA1withRSA";
    /**
     * Encoding type for RSA
     */
    public static final String RSA_CODEC = "RSA";

    /**
     * Decoder to decode messages encoded with the own private key
     */
    private final Cipher ownPrivKeyDecoder;
    /**
     * Decoder to encode messages decoded with the own public key
     */
    private final Cipher ownPublKeyDecoder;

    /**
     * Create a new instance of the RSA cryptography helper class
     *
     * @throws Lesson5Exception exception thrown if there is any issue with the private key
     */
    public RSACrypto() throws Lesson5Exception {
        // Generate a key pair
        final KeyPair keyPair = this.generateKeyPair();

        // Create the decoded messages encoded with the own private key
        this.ownPrivKeyDecoder = this.initCipher(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        // Create the encode messages decoded with the own public key
        this.ownPublKeyDecoder = this.initCipher(Cipher.ENCRYPT_MODE, keyPair.getPublic());
    }

    /**
     * Generate a new public/private key pair
     */
    private KeyPair generateKeyPair() throws Lesson5Exception {
        try {
            // crea pares clave pub/priv a traves de API
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_CODEC);
            keyGen.initialize(RSA_KEY_SIZE);
            return keyGen.genKeyPair();
        } catch (final NoSuchAlgorithmException e) {
            throw new Lesson5Exception("Error generating key pair", e);
        }
    }

    /**
     * @param mode with the decrypt or encrypt mode
     * @param key  with the key
     * @return the cipher
     * @throws Lesson5Exception with an occurred exception
     */
    private Cipher initCipher(final int mode, final Key key) throws Lesson5Exception {
        try {
            // crea instancia cipher
            final Cipher cipher = Cipher.getInstance(RSA_CODEC);
            cipher.init(mode, key);
            return cipher;
        } catch (final NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new Lesson5Exception("Error initializing cipher classes", e);
        }
    }


    /**
     * Encode the given message with the public key of the given application. It has to be already registered
     *
     * @param msg the message to encode
     * @return the encoded message
     * @throws Lesson5Exception exception thrown if the application is not registered or the message cannot be encoded for any reason
     */
    public byte[] encodeWithPubKey(final byte[] msg) throws Lesson5Exception {
        try {
            // Cifra la cadena con la clave publica
            return this.ownPublKeyDecoder.doFinal(msg);
        } catch (final IllegalBlockSizeException | BadPaddingException e) {
            throw new Lesson5Exception("Unexpected error encoding RSA message", e);
        }
    }

    /**
     * Decode the given message using our own private key
     *
     * @param msg the message to decode
     * @return the decoded message
     * @throws Lesson5Exception exception thrown if there is any problem
     */
    public byte[] decodeWithOwnPrivKey(final byte[] msg) throws Lesson5Exception {
        try {
            // Descifra con la clave privada
            return this.ownPrivKeyDecoder.doFinal(msg);

        } catch (final IllegalBlockSizeException | BadPaddingException e) {
            throw new Lesson5Exception("Unexpected error encoding RSA message", e);
        }
    }
}
