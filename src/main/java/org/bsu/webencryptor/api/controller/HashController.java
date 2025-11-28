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

    @PostMapping("/bash384")
    @Operation(description = "Returns Bash384 hash in Base64 and HEX formats.")
    ResponseEntity<?> bash384(@RequestBody HashRequest req);

    @Operation(description = "Uploads file and returns its Bash384 hash in Base64 & HEX.")
    @PostMapping(value = "/bash384-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> bash384File(@RequestPart("file") MultipartFile file);

    @Operation(description = "Uploads file and compares its Bash384 hash with provided Base64 value.")
    @PostMapping(value = "/bash384/verify-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> verifyBash384File(@RequestPart("file") MultipartFile file,
                                        @RequestParam("hashBase64") String hashBase64);

    @PostMapping("/bash512")
    @Operation(description = "Returns Bash512 hash in Base64 and HEX formats.")
    ResponseEntity<?> bash512(@RequestBody HashRequest req);

    @Operation(description = "Uploads file and returns its Bash512 hash in Base64 & HEX.")
    @PostMapping(value = "/bash512-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> bash512File(@RequestPart("file") MultipartFile file);

    @Operation(description = "Uploads file and compares its Bash512 hash with provided Base64 value.")
    @PostMapping(value = "/bash512/verify-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> verifyBash512File(@RequestPart("file") MultipartFile file,
                                        @RequestParam("hashBase64") String hashBase64);

    @PostMapping("/belt")
    @Operation(description = "Returns BeltHash in Base64 and HEX formats.")
    ResponseEntity<?> belt(@RequestBody HashRequest req);

    @Operation(description = "Uploads file and returns its BeltHash in Base64 & HEX.")
    @PostMapping(value = "/belt-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> beltFile(@RequestPart("file") MultipartFile file);

    @Operation(description = "Uploads file and compares its BeltHash with provided Base64 value.")
    @PostMapping(value = "/belt/verify-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> verifyBeltFile(@RequestPart("file") MultipartFile file,
                                     @RequestParam("hashBase64") String hashBase64);


}
