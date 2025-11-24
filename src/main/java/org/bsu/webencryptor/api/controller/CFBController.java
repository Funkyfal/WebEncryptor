package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "CFB Controller", description = "Provides API for CFB")
@RequestMapping("/crypto")
public interface CFBController {

    @Operation(description = "Encrypt text message with CFB")
    @PostMapping("/encrypt/belt-cfb")
    ResponseEntity<?> encryptBeltCfb(@RequestBody EncryptRequest req);

    @Operation(description = "Decrypt text message with CFB.")
    @PostMapping("/decrypt/belt-cfb")
    ResponseEntity<?> decryptBeltCfb(@RequestBody DecryptRequest req);

    @Operation(description = "Encrypt file with CFB.")
    @PostMapping(value = "/encrypt-file/belt-cfb", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> encryptFileStreamBeltCfb(@RequestParam String keyBase64,
                                               @RequestParam String ivBase64,
                                               @RequestPart("file") MultipartFile file);

    @Operation(description = "Decrypt file with CFB.")
    @PostMapping(value = "/decrypt-file/belt-cfb", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> decryptFileStreamBeltCfb(@RequestParam String keyBase64,
                                               @RequestParam String ivBase64,
                                               @RequestPart("file") MultipartFile file);
}
