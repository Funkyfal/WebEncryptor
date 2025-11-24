package org.bsu.webencryptor.service.impl;

import by.bcrypto.bee2j.constants.JceNameConstants;
import by.bcrypto.bee2j.provider.Bee2SecurityProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsu.webencryptor.service.CTRService;
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
public class CTRServiceImpl implements CTRService {

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
    public String encryptBeltCtrBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] plaintext) {
        try {
            Cipher cipher = serviceUtils.getCipher(providerAlgorithmName, Cipher.ENCRYPT_MODE, key, iv);
            byte[] ct = cipher.doFinal(plaintext);
            return serviceUtils.encodeBase64(ct);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decryptBeltCtrBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] ciphertext) {
        try {
            Cipher cipher = serviceUtils.getCipher(providerAlgorithmName, Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(ciphertext);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public File encryptBeltCtrFileStream(String algName, byte[] key, byte[] iv, InputStream in) throws IOException {
        Cipher cipher = serviceUtils.getCipher(algName, Cipher.ENCRYPT_MODE, key, iv);
        File outFile = File.createTempFile("enc-", ".bin");
        outFile.deleteOnExit();
        try (CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(outFile), cipher)) {
            byte[] buffer = new byte[8192];
            int r;
            while ((r = in.read(buffer)) != -1) {
                cos.write(buffer, 0, r);
            }
        }
        return outFile;
    }

    @Override
    public File decryptBeltCtrFileStream(String algName, byte[] key, byte[] iv, InputStream in) throws IOException {
        Cipher cipher = serviceUtils.getCipher(algName, Cipher.DECRYPT_MODE, key, iv);
        File outFile = File.createTempFile("dec-", ".bin");
        outFile.deleteOnExit();
        try (CipherInputStream cis = new CipherInputStream(in, cipher);
             FileOutputStream fos = new FileOutputStream(outFile)) {
            byte[] buffer = new byte[8192];
            int r;
            while ((r = cis.read(buffer)) != -1) {
                fos.write(buffer, 0, r);
            }
        }
        return outFile;
    }
}
