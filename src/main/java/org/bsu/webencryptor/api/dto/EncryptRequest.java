package org.bsu.webencryptor.api.dto;

import lombok.Data;

@Data
public class EncryptRequest {

    private String algorithm;
    private String plaintext;
    private String keyBase64;
    private String ivBase64;
}
