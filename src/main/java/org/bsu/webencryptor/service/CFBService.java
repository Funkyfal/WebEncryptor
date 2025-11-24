package org.bsu.webencryptor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface CFBService {

    String encryptBeltCfbBytes(byte[] key, byte[] iv, byte[] plaintext);
    byte[] decryptBeltCfbBytes(byte[] key, byte[] iv, byte[] ciphertext);
    File encryptFileStreamBeltCfb(byte[] key, byte[] iv, InputStream in) throws IOException;
    File decryptFileStreamBeltCfb(byte[] key, byte[] iv, InputStream in) throws IOException;
}
