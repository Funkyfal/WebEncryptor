package org.bsu.webencryptor.api.controller.impl;

import lombok.RequiredArgsConstructor;
import org.bsu.webencryptor.api.controller.CFBController;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.bsu.webencryptor.service.CFBService;
import org.bsu.webencryptor.util.ControllerUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CFBControllerImpl implements CFBController {

    private final ControllerUtils controllerUtils;
    private final CFBService CFBService;

    @Override
    public ResponseEntity<?> encryptBeltCfb(EncryptRequest req) {
        try {
            byte[] key = controllerUtils.decodeBase64(req.getKeyBase64());
            byte[] iv = req.getIvBase64() == null ? null : controllerUtils.decodeBase64(req.getIvBase64());
            byte[] plain = req.getPlaintext().getBytes(StandardCharsets.UTF_8);
            String ciphertextB64 = CFBService.encryptBeltCfbBytes(key, iv, plain);
            return ResponseEntity.ok(Map.of("ciphertext", ciphertextB64));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> decryptBeltCfb(DecryptRequest req) {
        try {
            byte[] key = controllerUtils.decodeBase64(req.getKeyBase64());
            byte[] iv = req.getIvBase64() == null ? null : controllerUtils.decodeBase64(req.getIvBase64());
            byte[] ciphertext = controllerUtils.decodeBase64(req.getCiphertextBase64());
            byte[] decryptedBytes = CFBService.decryptBeltCfbBytes(key, iv, ciphertext);
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

            return ResponseEntity.ok(Map.of("plaintext", decryptedText));

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @Override
    public ResponseEntity<?> encryptFileStreamBeltCfb(String keyBase64, String ivBase64, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] key = controllerUtils.decodeBase64(keyBase64);
            byte[] iv = controllerUtils.decodeBase64(ivBase64);
            File enc = CFBService.encryptFileStreamBeltCfb(key, iv, in);
            String newFilename = controllerUtils.addToFilename(file.getOriginalFilename(), "ENC");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(enc));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFilename + "\"")
                    .contentLength(enc.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> decryptFileStreamBeltCfb(String keyBase64, String ivBase64, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] key = controllerUtils.decodeBase64(keyBase64);
            byte[] iv = controllerUtils.decodeBase64(ivBase64);
            File enc = CFBService.decryptFileStreamBeltCfb(key, iv, in);
            String newFilename = controllerUtils.addToFilename(file.getOriginalFilename(), "DEC");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(enc));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + newFilename + "\"")
                    .contentLength(enc.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}
