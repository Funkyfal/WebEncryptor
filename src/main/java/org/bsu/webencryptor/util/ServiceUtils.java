package org.bsu.webencryptor.util;

import by.bcrypto.bee2j.constants.JceNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;

@Component
@Slf4j
public class ServiceUtils {

    private static final String BEE2_PROVIDER_NAME = JceNameConstants.ProviderName;

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

    public byte[] decodeBase64(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    public String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] pkcs7Pad(byte[] data, int blockSize) {
        int pad = blockSize - (data.length % blockSize);
        if (pad == 0) pad = blockSize;
        byte[] out = new byte[data.length + pad];
        System.arraycopy(data, 0, out, 0, data.length);
        for (int i = data.length; i < out.length; i++) {
            out[i] = (byte) pad;
        }
        return out;
    }

    public static byte[] pkcs7Unpad(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Invalid padded data");
        }
        int pad = data[data.length - 1] & 0xFF;
        if (pad < 1 || pad > 16 || pad > data.length) {
            throw new IllegalArgumentException("Invalid padding value: " + pad);
        }
        for (int i = data.length - pad; i < data.length; i++) {
            if ((data[i] & 0xFF) != pad) {
                throw new IllegalArgumentException("Invalid padding bytes");
            }
        }
        byte[] out = new byte[data.length - pad];
        System.arraycopy(data, 0, out, 0, out.length);
        return out;
    }

    public Cipher createCipherWithPaddingCandidates(String baseAlg, int mode, byte[] key, byte[] iv) throws GeneralSecurityException {
        Provider p = Security.getProvider(BEE2_PROVIDER_NAME);
        String[] candidates = new String[] {
                baseAlg + "/PKCS5Padding",
                baseAlg + "/PKCS7Padding",
                baseAlg + "/NoPadding",
                baseAlg
        };

        GeneralSecurityException lastEx = null;
        for (String cand : candidates) {
            try {
                Cipher cipher;
                if (p != null) {
                    try {
                        cipher = Cipher.getInstance(cand, p);
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
                        cipher = Cipher.getInstance(cand);
                    }
                } else {
                    cipher = Cipher.getInstance(cand);
                }

                SecretKeySpec keySpec = new SecretKeySpec(key, keyAlgorithmFromCipherName(baseAlg));
                if (iv != null && iv.length > 0) {
                    cipher.init(mode, keySpec, new IvParameterSpec(iv));
                } else {
                    cipher.init(mode, keySpec);
                }
                return cipher;
            } catch (GeneralSecurityException ex) {
                lastEx = ex;
            }
        }

        throw lastEx;
    }
}
