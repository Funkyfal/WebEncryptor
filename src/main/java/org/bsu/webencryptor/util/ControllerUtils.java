package org.bsu.webencryptor.util;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class ControllerUtils {

    public String addToFilename(String original, String textToAdd) {
        if (original == null || original.isBlank()) {
            return "encryptedENC";
        }

        int lastDot = original.lastIndexOf('.');
        if (lastDot == -1) {
            return original + textToAdd;
        }

        String name = original.substring(0, lastDot);
        String ext = original.substring(lastDot);
        return name + textToAdd + ext;
    }

    public byte[] decodeBase64(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    public String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
