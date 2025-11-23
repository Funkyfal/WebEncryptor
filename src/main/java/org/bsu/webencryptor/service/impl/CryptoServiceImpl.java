package org.bsu.webencryptor.service.impl;

import by.bcrypto.bee2j.constants.JceNameConstants;
import by.bcrypto.bee2j.provider.Bee2SecurityProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bsu.webencryptor.service.CryptoService;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Base64;

@Service
@Slf4j
public class CryptoServiceImpl implements CryptoService {

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
    public String encryptBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] plaintext) {
        try {
            Cipher cipher = getCipher(providerAlgorithmName, Cipher.ENCRYPT_MODE, key, iv);
            byte[] ct = cipher.doFinal(plaintext);
            return encodeBase64(ct);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decryptBytes(String providerAlgorithmName, byte[] key, byte[] iv, byte[] ciphertext) {
        try {
            Cipher cipher = getCipher(providerAlgorithmName, Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(ciphertext);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    @Override
    public File encryptFileStream(String algName, byte[] key, byte[] iv, InputStream in) throws IOException {
        Cipher cipher = getCipher(algName, Cipher.ENCRYPT_MODE, key, iv);
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
    public File decryptFileStream(String algName, byte[] key, byte[] iv, InputStream in) throws IOException {
        Cipher cipher = getCipher(algName, Cipher.DECRYPT_MODE, key, iv);
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


    @Override
    public Cipher getCipher(String algName, int mode, byte[] key, byte[] iv) {
        try {
            Cipher cipher;
            Provider p = Security.getProvider(BEE2_PROVIDER_NAME);
            if (p != null) {
                try {
                    cipher = Cipher.getInstance(algName, p);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
                    cipher = Cipher.getInstance(algName);
                }
            } else {
                cipher = Cipher.getInstance(algName);
            }

            String keyAlg = keyAlgorithmFromCipherName(algName);
            SecretKeySpec keySpec = new SecretKeySpec(key, keyAlg);

            if (iv != null && iv.length > 0) {
                cipher.init(mode, keySpec, new IvParameterSpec(iv));
            } else {
                cipher.init(mode, keySpec);
            }
            return cipher;
        } catch (GeneralSecurityException e) {
            log.error("Failed getCipher for {}", algName, e);
            throw new RuntimeException("Failed to create/init cipher for " + algName + ": " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] decodeBase64(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    @Override
    public String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private String keyAlgorithmFromCipherName(String algName) {
        if (algName == null)
            return "Belt";
        String up = algName.toUpperCase();
        if (up.contains("BELT"))
            return "Belt";
        if (up.contains("BIGN"))
            return "Bign";
        return "Belt";
    }
}
