package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "CTR Controller", description = "Provides API for CTR")
@RequestMapping("/crypto")
public interface CTRController {

    @Operation(description = "Encrypt text message with CTR")
    @PostMapping("/encrypt/belt-ctr")
    ResponseEntity<?> encrypt(@RequestBody EncryptRequest req);

    @Operation(description = "Decrypt text message with CTR.")
    @PostMapping("/decrypt/belt-ctr")
    ResponseEntity<?> decrypt(@RequestBody DecryptRequest req);

    @Operation(description = "Encrypt file with CTR.")
    @PostMapping(value = "/encrypt-file-stream/belt-ctr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> encryptFileStream(@RequestParam String algorithm,
                                        @RequestParam String keyBase64,
                                        @RequestParam(required = false) String ivBase64,
                                        @RequestPart("file") MultipartFile file);

    @Operation(description = "Decrypt file with CTR.")
    @PostMapping(value = "/decrypt-file-stream/belt-ctr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> decryptFileStream(@RequestParam String algorithm,
                                        @RequestParam String keyBase64,
                                        @RequestParam(required = false) String ivBase64,
                                        @RequestPart("file") MultipartFile file);

}
