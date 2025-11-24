package org.bsu.webencryptor.service;

import java.io.InputStream;

public interface HashService {

    String hashBash256Base64(byte[] data);

    String hashBash256Hex(byte[] data);

    byte[] hashBash256FileStream(InputStream in);

    boolean verifyBash256File(InputStream in, String hashBase64);
}
