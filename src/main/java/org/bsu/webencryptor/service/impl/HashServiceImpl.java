package org.bsu.webencryptor.service.impl;

import by.bcrypto.bee2j.constants.JceNameConstants;
import by.bcrypto.bee2j.provider.Bee2SecurityProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bsu.webencryptor.service.HashService;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

@Service
@Slf4j
public class HashServiceImpl implements HashService {

    private static final String BEE2_PROVIDER_NAME = JceNameConstants.ProviderName;

    @PostConstruct
    public void init() {
        if (Security.getProvider(BEE2_PROVIDER_NAME) == null) {
            try {
                Security.addProvider(new Bee2SecurityProvider());
                log.info("Bee2 provider registered for hashing");
            } catch (Throwable t) {
                log.warn("Failed to register Bee2 provider: {}", t.getMessage());
            }
        }
    }

    private MessageDigest getBash256Digest() {
        try {
            Provider p = Security.getProvider(BEE2_PROVIDER_NAME);
            if (p != null) {
                try {
                    return MessageDigest.getInstance("Bash256", p);
                } catch (Exception ex) {
                    log.debug("Bash256 via Bee2 provider failed: {}", ex.getMessage());
                }
            }
        } catch (Throwable t) {
            log.debug("Provider lookup failed: {}", t.getMessage());
        }
        try {
            return MessageDigest.getInstance("Bash256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Bash256 MessageDigest not available in this JVM", e);
        }
    }

    @Override
    public String hashBash256Base64(byte[] data) {
        MessageDigest md = getBash256Digest();
        byte[] out = md.digest(data);
        return Base64.getEncoder().encodeToString(out);
    }

    @Override
    public String hashBash256Hex(byte[] data) {
        byte[] out = getBash256Digest().digest(data);
        return bytesToHex(out);
    }

    @Override
    public byte[] hashBash256FileStream(InputStream in) {
        try {
            MessageDigest md = getBash256Digest();
            try (BufferedInputStream bin = new BufferedInputStream(in)) {
                byte[] buf = new byte[8192];
                int r;
                while ((r = bin.read(buf)) != -1) {
                    md.update(buf, 0, r);
                }
            }
            return md.digest();
        } catch (IOException e) {
            throw new RuntimeException("I/O error while hashing file: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyBash256File(InputStream in, String hashBase64) {
        byte[] expected;
        try {
            expected = Base64.getDecoder().decode(hashBase64);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Provided hashBase64 is not valid Base64: " + iae.getMessage(), iae);
        }
        byte[] actual = hashBash256FileStream(in);
        return MessageDigest.isEqual(actual, expected);
    }

    private static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x & 0xFF));
        return sb.toString();
    }
}
