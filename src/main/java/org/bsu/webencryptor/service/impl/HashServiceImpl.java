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

    @Override
    public String hashBash384Base64(byte[] data) {
        byte[] out = getDigest("Bash384").digest(data);
        return Base64.getEncoder().encodeToString(out);
    }

    @Override
    public String hashBash384Hex(byte[] data) {
        byte[] out = getDigest("Bash384").digest(data);
        return bytesToHex(out);
    }

    @Override
    public byte[] hashBash384FileStream(InputStream in) {
        try {
            MessageDigest md = getDigest("Bash384");
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
    public boolean verifyBash384File(InputStream in, String hashBase64) {
        byte[] expected;
        try {
            expected = Base64.getDecoder().decode(hashBase64);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Provided hashBase64 is not valid Base64: " + iae.getMessage(), iae);
        }
        byte[] actual = hashBash384FileStream(in);
        return MessageDigest.isEqual(actual, expected);
    }

    @Override
    public String hashBash512Base64(byte[] data) {
        byte[] out = getDigest("Bash512").digest(data);
        return Base64.getEncoder().encodeToString(out);
    }

    @Override
    public String hashBash512Hex(byte[] data) {
        byte[] out = getDigest("Bash512").digest(data);
        return bytesToHex(out);
    }

    @Override
    public byte[] hashBash512FileStream(InputStream in) {
        try {
            MessageDigest md = getDigest("Bash512");
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
    public boolean verifyBash512File(InputStream in, String hashBase64) {
        byte[] expected;
        try {
            expected = Base64.getDecoder().decode(hashBase64);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Provided hashBase64 is not valid Base64: " + iae.getMessage(), iae);
        }
        byte[] actual = hashBash512FileStream(in);
        return MessageDigest.isEqual(actual, expected);
    }

    // --- BELT HASH methods ---

    @Override
    public String hashBeltBase64(byte[] data) {
        byte[] out = getDigest("BeltHash").digest(data);
        return Base64.getEncoder().encodeToString(out);
    }

    @Override
    public String hashBeltHex(byte[] data) {
        byte[] out = getDigest("BeltHash").digest(data);
        return bytesToHex(out);
    }

    @Override
    public byte[] hashBeltFileStream(InputStream in) {
        try {
            MessageDigest md = getDigest("BeltHash");
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
    public boolean verifyBeltFile(InputStream in, String hashBase64) {
        byte[] expected;
        try {
            expected = Base64.getDecoder().decode(hashBase64);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("Provided hashBase64 is not valid Base64: " + iae.getMessage(), iae);
        }
        byte[] actual = hashBeltFileStream(in);
        return MessageDigest.isEqual(actual, expected);
    }


    private static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x & 0xFF));
        return sb.toString();
    }

    private MessageDigest getDigest(String alg) {
        try {
            Provider p = Security.getProvider(BEE2_PROVIDER_NAME);
            if (p != null) {
                try {
                    return MessageDigest.getInstance(alg, p);
                } catch (Exception ex) {
                    log.debug("{} via Bee2 provider failed: {}", alg, ex.getMessage());
                }
            }
        } catch (Throwable t) {
            log.debug("Provider lookup failed for {}: {}", alg, t.getMessage());
        }
        try {
            return MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(alg + " MessageDigest not available in this JVM", e);
        }
    }

}
