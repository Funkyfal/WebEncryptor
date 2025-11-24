package org.bsu.webencryptor.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface ECBService {

    String encryptBeltEcbBytes(byte[] key, byte[] plaintext);

    byte[] decryptBeltEcbBytes(byte[] key, byte[] ciphertext);

    File encryptFileStreamBeltEcb(byte[] key, InputStream in) throws IOException;

    File decryptFileStreamBeltEcb(byte[] key, InputStream in) throws IOException;
}
