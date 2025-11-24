package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.HashRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/crypto/hash")
@Tag(name = "Bash256 Hashing", description = "Hashing and verification using Bash256 (Bee2)")
public interface HashController {

    @PostMapping("/bash256")
    @Operation(description = "Returns Bash256 hash in Base64 and HEX formats.")
    ResponseEntity<?> bash256(@RequestBody HashRequest req);

    @Operation(description = "Uploads file and returns its Bash256 hash in Base64 & HEX.")
    @PostMapping(value = "/bash256-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> bash256File(@RequestPart("file") MultipartFile file);

    @Operation(description = "Uploads file and compares its Bash256 hash with provided Base64 value.")
    @PostMapping(value = "/bash256/verify-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> verifyBash256File(@RequestPart("file") MultipartFile file,
                                        @RequestParam("hashBase64") String hashBase64);
}
