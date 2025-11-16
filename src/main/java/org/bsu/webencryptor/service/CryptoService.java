package org.bsu.webencryptor.service;

import javax.crypto.Cipher;

public interface CryptoService {

    String encryptBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] plaintext);
    byte[] decryptBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] ciphertext);
    Cipher getCipher(String algName, int mode, byte[] key, byte[] iv);
    byte[] decodeBase64(String b64);
    String encodeBase64(byte[] data);
}
