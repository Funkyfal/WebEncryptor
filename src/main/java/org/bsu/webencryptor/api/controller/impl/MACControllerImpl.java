package org.bsu.webencryptor.api.controller.impl;

import lombok.RequiredArgsConstructor;
import org.bsu.webencryptor.api.controller.MACController;
import org.bsu.webencryptor.api.dto.MacRequest;
import org.bsu.webencryptor.api.dto.VerifyMacRequest;
import org.bsu.webencryptor.service.MACService;
import org.bsu.webencryptor.util.ControllerUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MACControllerImpl implements MACController {

    private final MACService macService;
    private final ControllerUtils controllerUtils;

    @Override
    public ResponseEntity<?> computeBeltMac(MacRequest req) {
        try {
            byte[] key = controllerUtils.decodeBase64(req.getKeyBase64());
            byte[] data = req.getData().getBytes(StandardCharsets.UTF_8);
            String macB64 = macService.computeBeltMacBytes(key, data);
            return ResponseEntity.ok(Map.of("macBase64", macB64));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> verifyBeltMac(VerifyMacRequest req) {
        try {
            byte[] key = controllerUtils.decodeBase64(req.getKeyBase64());
            byte[] data = req.getData().getBytes(StandardCharsets.UTF_8);
            byte[] expected = controllerUtils.decodeBase64(req.getMacBase64());
            boolean ok = macService.verifyBeltMacBytes(key, data, expected);
            return ResponseEntity.ok(Map.of("valid", ok));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
