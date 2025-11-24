package org.bsu.webencryptor.api.controller.impl;

import lombok.RequiredArgsConstructor;
import org.bsu.webencryptor.api.controller.HashController;
import org.bsu.webencryptor.api.dto.HashRequest;
import org.bsu.webencryptor.service.HashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class HashControllerImpl implements HashController {

    private final HashService hashService;

    @Override
    public ResponseEntity<?> bash256(HashRequest req) {
        try {
            byte[] data = req.getData() == null ? new byte[0] : req.getData().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String b64 = hashService.hashBash256Base64(data);
            String hex = hashService.hashBash256Hex(data);
            return ResponseEntity.ok(Map.of("bash256_base64", b64, "bash256_hex", hex));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> bash256File(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] hash = hashService.hashBash256FileStream(in);
            String b64 = Base64.getEncoder().encodeToString(hash);
            String hex = bytesToHex(hash);
            return ResponseEntity.ok(Map.of("bash256_base64", b64, "bash256_hex", hex, "len", hash.length));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> verifyBash256File(MultipartFile file, String hashBase64) {
        try (InputStream in = file.getInputStream()) {
            boolean valid = hashService.verifyBash256File(in, hashBase64);
            return ResponseEntity.ok(Map.of("valid", valid));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) sb.append(String.format("%02x", x & 0xFF));
        return sb.toString();
    }
}
