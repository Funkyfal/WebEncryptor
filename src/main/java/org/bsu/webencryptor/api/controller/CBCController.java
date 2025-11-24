package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "CBC Controller", description = "BeltCBC endpoints")
@RequestMapping("/crypto")
public interface CBCController {

    @Operation(description = "Encrypt text (BeltCBC).")
    @PostMapping("/encrypt/belt-cbc")
    ResponseEntity<?> encryptBeltCbc(@RequestBody EncryptRequest req);

    @Operation(description = "Decrypt text (BeltCBC).")
    @PostMapping("/decrypt/belt-cbc")
    ResponseEntity<?> decryptBeltCbc(@RequestBody DecryptRequest req);

    @Operation(description = "Encrypt file (BeltCBC).")
    @PostMapping(value = "/encrypt-file/belt-cbc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> encryptFileStreamBeltCbc(@RequestParam String keyBase64,
                                               @RequestParam String ivBase64,
                                               @RequestPart("file") MultipartFile file);

    @Operation(description = "Decrypt file (BeltCBC).")
    @PostMapping(value = "/decrypt-file/belt-cbc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> decryptFileStreamBeltCbc(@RequestParam String keyBase64,
                                               @RequestParam String ivBase64,
                                               @RequestPart("file") MultipartFile file);
}
