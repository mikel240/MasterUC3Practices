package com.cnebrera.uc3.tech.lesson5.util.crypto;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.cnebrera.uc3.tech.lesson5.util.Lesson5Exception;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Helper class to perform AES cryptography.
 * <p>
 * This class is thread safe
 */
public class AESCrypto {
    /**
     * Random value for the key generation
     */
    private static final Random RND = new Random(System.currentTimeMillis());

    /**
     * Size of the AES key in bytes
     */
    public static final int KEY_SIZE = 16;

    /**
     * The AES key that will be used to encode / decode
     */
    private final byte[] aesKey;
    /**
     * The Cipher that will encode the messages
     */
    private final Cipher cipher;
    /**
     * The Cipher that will decode the messages
     */
    private final Cipher decipher;

    /**
     * Create a new instance for AES encoding / decoding, the instance will use the provided key
     *
     * @return the created instance
     * @throws Lesson5Exception exception thrown if the instance cannot be created
     */
    public static AESCrypto createNewInstance() throws Lesson5Exception {
        // Definimos clave compartida aleatoria
        byte[] rndKey = new byte[KEY_SIZE];
        RND.nextBytes(rndKey);
        return new AESCrypto(rndKey);
    }

    /**
     * Create a new instance given the AES key that will be used to encode / decode
     *
     * @param key the key to encode and decode
     * @throws Lesson5Exception exception thrown if there is a problem creating the internal encoding classes
     */
    public AESCrypto(final byte[] key) throws Lesson5Exception {
        // Clonamos parametro de entrada
        // Evitamos futuros cambios de estado y valor del atr
        this.aesKey = key.clone();

        // Store the binary key
        final SecretKeySpec secretAESKey = new SecretKeySpec(this.aesKey, "AES");

        try {
            // Instancias
            this.cipher = Cipher.getInstance("AES");
            this.decipher = Cipher.getInstance("AES");
            this.cipher.init(Cipher.ENCRYPT_MODE, secretAESKey);
            this.decipher.init(Cipher.DECRYPT_MODE, secretAESKey);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new Lesson5Exception("Error creating AES encoding classes", e);
        }
    }

    /**
     * Return the AES key used by this encoder / decoder
     */
    public byte[] getAESKey() {
        return this.aesKey;
    }

    /**
     * Encode the given message and return the AES encoding with the key used during creation of the class
     *
     * @param msg the message to be encoded
     * @return the encoded message
     * @throws Lesson5Exception exception thrown if there is a problem encoding the message
     */
    public byte[] encode(final byte[] msg) throws Lesson5Exception {
        synchronized (this.cipher) {
            try {
                // Cifrar entrada
                return this.cipher.doFinal(msg);
            } catch (final IllegalBlockSizeException | BadPaddingException e) {
                throw new Lesson5Exception("Unexpected error performing AES encode", e);
            }
        }
    }

    /**
     * Decode the given message and return the AES decoding with the key used during creation of the class
     *
     * @param msg the message to be decoded
     * @return the decoded message
     * @throws Lesson5Exception exception thrown if there is a problem decoding the message
     */
    public byte[] decode(final byte[] msg) throws Lesson5Exception {
        synchronized (this.cipher) {
            try {
                // Descifrar entrada
                return this.decipher.doFinal(msg);
            } catch (final IllegalBlockSizeException | BadPaddingException e) {
                throw new Lesson5Exception("Unexpected error performing AES decode", e);
            }
        }
    }
}
