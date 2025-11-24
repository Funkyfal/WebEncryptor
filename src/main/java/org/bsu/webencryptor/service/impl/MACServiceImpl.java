package org.bsu.webencryptor.service.impl;

import by.bcrypto.bee2j.constants.JceNameConstants;
import by.bcrypto.bee2j.provider.Bee2SecurityProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsu.webencryptor.service.MACService;
import org.bsu.webencryptor.util.ServiceUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Provider;
import java.security.Security;

@Service
@Slf4j
@RequiredArgsConstructor
public class MACServiceImpl implements MACService {

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
    public String computeBeltMacBytes(byte[] key, byte[] data) {
        validateKeyForBelt(key);
        try {
            Mac mac = getMac("BeltMAC", key);
            mac.update(data);
            byte[] out = mac.doFinal();
            return serviceUtils.encodeBase64(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute MAC: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean verifyBeltMacBytes(byte[] key, byte[] data, byte[] macBytes) {
        validateKeyForBelt(key);
        try {
            Mac mac = getMac("BeltMAC", key);
            mac.update(data);
            byte[] out = mac.doFinal();
            return constantTimeEquals(out, macBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify MAC: " + e.getMessage(), e);
        }
    }

    private Mac getMac(String macAlg, byte[] key) throws Exception {
        Provider p = Security.getProvider(BEE2_PROVIDER_NAME);
        Mac mac;
        if (p != null) {
            try {
                mac = Mac.getInstance(macAlg, p);
            } catch (Exception ex) {
                mac = Mac.getInstance(macAlg);
            }
        } else {
            mac = Mac.getInstance(macAlg);
        }
        SecretKeySpec keySpec = new SecretKeySpec(key, "Belt");
        mac.init(keySpec);
        return mac;
    }

    private void validateKeyForBelt(byte[] key) {
        if (key == null || key.length != 32) {
            throw new IllegalArgumentException("BeltMAC requires 32-byte key (256 bits)");
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
