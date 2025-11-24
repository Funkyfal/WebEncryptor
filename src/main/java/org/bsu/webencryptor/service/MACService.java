package org.bsu.webencryptor.service;

public interface MACService {

    String computeBeltMacBytes(byte[] key, byte[] data);
    boolean verifyBeltMacBytes(byte[] key, byte[] data, byte[] mac);
}
