package org.bsu.webencryptor.service.impl;

import by.bcrypto.bee2j.constants.JceNameConstants;
import by.bcrypto.bee2j.provider.Bee2SecurityProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsu.webencryptor.service.CFBService;
import org.bsu.webencryptor.service.CTRService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;

@Service
@Slf4j
@RequiredArgsConstructor
public class CFBServiceImpl implements CFBService {

    private final CTRService ctrService;
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
    public String encryptBeltCfbBytes(byte[] key, byte[] iv, byte[] plaintext) {
        validateKeyIvForBelt(key, iv);
        return ctrService.encryptBeltCtrBytes("BeltCFB", key, iv, plaintext);
    }

    @Override
    public byte[] decryptBeltCfbBytes(byte[] key, byte[] iv, byte[] ciphertext) {
        validateKeyIvForBelt(key, iv);
        return ctrService.decryptBeltCtrBytes("BeltCFB", key, iv, ciphertext);
    }

    @Override
    public File encryptFileStreamBeltCfb(byte[] key, byte[] iv, InputStream in) throws IOException {
        validateKeyIvForBelt(key, iv);
        return ctrService.encryptBeltCtrFileStream("BeltCFB", key, iv, in);
    }

    @Override
    public File decryptFileStreamBeltCfb(byte[] key, byte[] iv, InputStream in) throws IOException {
        validateKeyIvForBelt(key, iv);
        return ctrService.decryptBeltCtrFileStream("BeltCFB", key, iv, in);
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
