package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Crypto Controller", description = "Provides API via Bee2 library")
@RequestMapping("/crypto")
public interface CryptoController {

    @Operation(description = "Encrypt text message.")
    @PostMapping("/encrypt")
    ResponseEntity<?> encrypt(@RequestBody EncryptRequest req);

    @Operation(description = "Decrypt text message.")
    @PostMapping("/decrypt")
    ResponseEntity<?> decrypt(@RequestBody DecryptRequest req);

    @Operation(description = "Encrypt file.")
    @PostMapping(value = "/encrypt-file-stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> encryptFileStream(@RequestParam String algorithm,
                                        @RequestParam String keyBase64,
                                        @RequestParam(required = false) String ivBase64,
                                        @RequestPart("file") MultipartFile file);

    @Operation(description = "Decrypt file.")
    @PostMapping(value = "/decrypt-file-stream", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> decryptFileStream(@RequestParam String algorithm,
                                        @RequestParam String keyBase64,
                                        @RequestParam(required = false) String ivBase64,
                                        @RequestPart("file") MultipartFile file);

}
