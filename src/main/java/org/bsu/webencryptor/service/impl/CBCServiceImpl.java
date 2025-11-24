package org.bsu.webencryptor.service.impl;

import by.bcrypto.bee2j.constants.JceNameConstants;
import by.bcrypto.bee2j.provider.Bee2SecurityProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsu.webencryptor.service.CBCService;
import org.bsu.webencryptor.util.ServiceUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;

@Service
@Slf4j
@RequiredArgsConstructor
public class CBCServiceImpl implements CBCService {

    private final ServiceUtils serviceUtils;
    private static final String BEE2_PROVIDER_NAME = JceNameConstants.ProviderName;

    @PostConstruct
    public void init() {
        if (Security.getProvider(BEE2_PROVIDER_NAME) == null) {
            Security.addProvider(new Bee2SecurityProvider());
            log.info("Bee2 provider registered");
        } else {
            log.info("Bee2 provider already present");
        }
    }

    @Override
    public String encryptBeltCbcBytes(byte[] key, byte[] iv, byte[] plaintext) {
        validateKeyIvForBelt(key, iv);
        try {
            Cipher cipher = serviceUtils.createCipherWithPaddingCandidates("BeltCBC", Cipher.ENCRYPT_MODE, key, iv);
            String algUsed = cipher.getAlgorithm().toUpperCase();
            boolean usingNoPadding = algUsed.contains("NOPADDING");

            byte[] toEncrypt = plaintext;
            if (usingNoPadding) {
                toEncrypt = ServiceUtils.pkcs7Pad(plaintext, 16);
            }

            byte[] ct = cipher.doFinal(toEncrypt);
            return serviceUtils.encodeBase64(ct);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decryptBeltCbcBytes(byte[] key, byte[] iv, byte[] ciphertext) {
        validateKeyIvForBelt(key, iv);
        try {
            Cipher cipher = serviceUtils.createCipherWithPaddingCandidates("BeltCBC", Cipher.DECRYPT_MODE, key, iv);
            String algUsed = cipher.getAlgorithm().toUpperCase();
            boolean usingNoPadding = algUsed.contains("NOPADDING");

            byte[] plainPadded = cipher.doFinal(ciphertext);
            if (usingNoPadding) {
                return ServiceUtils.pkcs7Unpad(plainPadded);
            } else {
                return plainPadded;
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public File encryptFileStreamBeltCbc(byte[] key, byte[] iv, InputStream in) throws IOException {
        validateKeyIvForBelt(key, iv);
        try {
            Cipher cipher = serviceUtils.createCipherWithPaddingCandidates("BeltCBC", Cipher.ENCRYPT_MODE, key, iv);
            String algUsed = cipher.getAlgorithm().toUpperCase();
            boolean usingNoPadding = algUsed.contains("NOPADDING");

            File outFile = File.createTempFile("enc-cbc-", ".bin");
            outFile.deleteOnExit();

            if (!usingNoPadding) {
                try (CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(outFile), cipher)) {
                    byte[] buffer = new byte[8192];
                    int r;
                    while ((r = in.read(buffer)) != -1) {
                        cos.write(buffer, 0, r);
                    }
                }
            } else {
                log.warn("Provider returned NoPadding for BeltCBC — using in-memory padding (may use more memory for large files)");
                byte[] all = in.readAllBytes();
                byte[] padded = ServiceUtils.pkcs7Pad(all, 16);
                byte[] ct = cipher.doFinal(padded);
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(ct);
                }
            }
            return outFile;
        } catch (GeneralSecurityException e) {
            throw new IOException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public File decryptFileStreamBeltCbc(byte[] key, byte[] iv, InputStream in) throws IOException {
        validateKeyIvForBelt(key, iv);
        try {
            Cipher cipher = serviceUtils.createCipherWithPaddingCandidates("BeltCBC", Cipher.DECRYPT_MODE, key, iv);
            String algUsed = cipher.getAlgorithm().toUpperCase();
            boolean usingNoPadding = algUsed.contains("NOPADDING");

            File outFile = File.createTempFile("dec-cbc-", ".bin");
            outFile.deleteOnExit();

            if (!usingNoPadding) {
                try (CipherInputStream cis = new CipherInputStream(in, cipher);
                     FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[8192];
                    int r;
                    while ((r = cis.read(buffer)) != -1) {
                        fos.write(buffer, 0, r);
                    }
                }
            } else {
                log.warn("Provider returned NoPadding for BeltCBC — using in-memory unpadding (may use more memory for large files)");
                byte[] ct = in.readAllBytes();
                byte[] padded = cipher.doFinal(ct);
                byte[] plain = ServiceUtils.pkcs7Unpad(padded);
                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    fos.write(plain);
                }
            }
            return outFile;
        } catch (GeneralSecurityException e) {
            throw new IOException("Decryption failed: " + e.getMessage(), e);
        }
    }

    private void validateKeyIvForBelt(byte[] key, byte[] iv) {
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("Belt requires 32-byte key (256 bits)");
        }
        if (iv == null || iv.length != 16) {
            throw new IllegalArgumentException("Belt modes require 16-byte IV (128 bits)");
        }
    }
}
