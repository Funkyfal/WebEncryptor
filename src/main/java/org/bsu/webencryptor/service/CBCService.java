package org.bsu.webencryptor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface CBCService {

    String encryptBeltCbcBytes(byte[] key, byte[] iv, byte[] plaintext);

    byte[] decryptBeltCbcBytes(byte[] key, byte[] iv, byte[] ciphertext);

    File encryptFileStreamBeltCbc(byte[] key, byte[] iv, InputStream in) throws IOException;

    File decryptFileStreamBeltCbc(byte[] key, byte[] iv, InputStream in) throws IOException;
}
