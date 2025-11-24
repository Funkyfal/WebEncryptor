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
}
