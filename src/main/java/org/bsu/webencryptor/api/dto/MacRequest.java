package org.bsu.webencryptor.api.dto;

import lombok.Data;

@Data
public class MacRequest {

    private String keyBase64;
    private String data;
}
