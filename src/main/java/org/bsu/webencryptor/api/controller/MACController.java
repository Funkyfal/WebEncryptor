package org.bsu.webencryptor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bsu.webencryptor.api.dto.MacRequest;
import org.bsu.webencryptor.api.dto.VerifyMacRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MAC Controller", description = "Belt MAC operations")
@RequestMapping("/crypto")
public interface MACController {

    @Operation(description = "Compute Belt MAC over text/plain payload")
    @PostMapping("/mac/belt")
    ResponseEntity<?> computeBeltMac(@RequestBody MacRequest req);

    @Operation(description = "Verify Belt MAC for given text")
    @PostMapping("/mac/belt/verify")
    ResponseEntity<?> verifyBeltMac(@RequestBody VerifyMacRequest req);
}
