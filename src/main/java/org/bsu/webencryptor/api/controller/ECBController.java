package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "ECB Controller", description = "Belt ECB operations")
@RequestMapping("/crypto")
public interface ECBController {

    @Operation(description = "Encrypt text with Belt ECB")
    @PostMapping("/encrypt/belt-ecb")
    ResponseEntity<?> encryptBeltEcb(@RequestBody EncryptRequest req);

    @Operation(description = "Decrypt text with Belt ECB")
    @PostMapping("/decrypt/belt-ecb")
    ResponseEntity<?> decryptBeltEcb(@RequestBody DecryptRequest req);

    @Operation(description = "Encrypt file with Belt ECB")
    @PostMapping(value = "/encrypt-file/belt-ecb", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> encryptFileStreamBeltEcb(@RequestParam String keyBase64,
                                               @RequestPart("file") MultipartFile file);

    @Operation(description = "Decrypt file with Belt ECB")
    @PostMapping(value = "/decrypt-file/belt-ecb", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> decryptFileStreamBeltEcb(@RequestParam String keyBase64,
                                               @RequestPart("file") MultipartFile file);
}
