package org.bsu.webencryptor.service;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface CryptoService {

    //CTR
    String encryptBeltCtrBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] plaintext);
    byte[] decryptBeltCtrBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] ciphertext);
    File encryptBeltCtrFileStream(String alg, byte[] key, byte[] iv, java.io.InputStream in) throws IOException;
    File decryptBeltCtrFileStream(String alg, byte[] key, byte[] iv, java.io.InputStream in) throws IOException;

    //CFB
    String encryptBeltCfbBytes(byte[] key, byte[] iv, byte[] plaintext);
    byte[] decryptBeltCfbBytes(byte[] key, byte[] iv, byte[] ciphertext);
    File encryptFileStreamBeltCfb(byte[] key, byte[] iv, InputStream in) throws IOException;
    File decryptFileStreamBeltCfb(byte[] key, byte[] iv, InputStream in) throws IOException;

    Cipher getCipher(String algName, int mode, byte[] key, byte[] iv);
    byte[] decodeBase64(String b64);
    String encodeBase64(byte[] data);
}
