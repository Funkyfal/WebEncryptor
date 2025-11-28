package org.bsu.webencryptor.service;

import java.io.InputStream;

public interface HashService {

    String hashBash256Base64(byte[] data);

    String hashBash256Hex(byte[] data);

    byte[] hashBash256FileStream(InputStream in);

    boolean verifyBash256File(InputStream in, String hashBase64);

    String hashBash384Base64(byte[] data);

    String hashBash384Hex(byte[] data);

    byte[] hashBash384FileStream(InputStream in);

    boolean verifyBash384File(InputStream in, String hashBase64);

    String hashBash512Base64(byte[] data);

    String hashBash512Hex(byte[] data);

    byte[] hashBash512FileStream(InputStream in);

    boolean verifyBash512File(InputStream in, String hashBase64);

    String hashBeltBase64(byte[] data);

    String hashBeltHex(byte[] data);

    byte[] hashBeltFileStream(InputStream in);

    boolean verifyBeltFile(InputStream in, String hashBase64);

}
