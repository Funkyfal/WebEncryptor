package org.bsu.webencryptor.service;

import java.io.File;
import java.io.IOException;

public interface CTRService {

    String encryptBeltCtrBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] plaintext);

    byte[] decryptBeltCtrBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] ciphertext);

    File encryptBeltCtrFileStream(String alg, byte[] key, byte[] iv, java.io.InputStream in) throws IOException;

    File decryptBeltCtrFileStream(String alg, byte[] key, byte[] iv, java.io.InputStream in) throws IOException;
}
