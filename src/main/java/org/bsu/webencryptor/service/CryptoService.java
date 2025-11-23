package org.bsu.webencryptor.service;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;

public interface CryptoService {

    String encryptBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] plaintext);
    byte[] decryptBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] ciphertext);
    File encryptFileStream(String alg, byte[] key, byte[] iv, java.io.InputStream in) throws IOException;
    File decryptFileStream(String alg, byte[] key, byte[] iv, java.io.InputStream in) throws IOException;
    Cipher getCipher(String algName, int mode, byte[] key, byte[] iv);
    byte[] decodeBase64(String b64);
    String encodeBase64(byte[] data);
}
