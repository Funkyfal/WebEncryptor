package org.bsu.webencryptor.api.dto;

import lombok.Data;

@Data
public class DecryptRequest {

    private String algorithm;
    private String ciphertextBase64;
    private String keyBase64;
    private String ivBase64;
}
