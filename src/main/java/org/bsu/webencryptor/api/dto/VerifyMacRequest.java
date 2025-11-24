package org.bsu.webencryptor.api.dto;

import lombok.Data;

@Data
public class VerifyMacRequest {
    private String keyBase64;
    private String data;
    private String macBase64;
}
