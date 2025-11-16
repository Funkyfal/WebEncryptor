package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Crypto Controller", description = "Provides API via Bee2 library")
@RequestMapping("/crypto")
public interface CryptoController {

    @Operation(description = "Encrypt text message.")
    @PostMapping("/encrypt")
    ResponseEntity<?> encrypt(@RequestBody EncryptRequest req);

    @Operation(description = "Decrypt text message.")
    @PostMapping("/decrypt")
    ResponseEntity<?> decrypt(@RequestBody DecryptRequest req);
}
