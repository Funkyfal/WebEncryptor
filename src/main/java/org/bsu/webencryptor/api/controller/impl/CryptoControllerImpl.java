package org.bsu.webencryptor.api.controller.impl;

import lombok.RequiredArgsConstructor;
import org.bsu.webencryptor.api.controller.CryptoController;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.bsu.webencryptor.service.CryptoService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CryptoControllerImpl implements CryptoController {

    private final CryptoService cryptoService;

    @Override
    public ResponseEntity<?> encrypt(@RequestBody EncryptRequest req) {
        try {
            byte[] key = cryptoService.decodeBase64(req.getKeyBase64());
            byte[] iv = req.getIvBase64() == null ? null : cryptoService.decodeBase64(req.getIvBase64());
            byte[] plain = req.getPlaintext().getBytes(StandardCharsets.UTF_8);
            String ciphertextB64 = cryptoService.encryptBytes(req.getAlgorithm(), key, iv, plain);
            return ResponseEntity.ok(Map.of("ciphertext", ciphertextB64));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<?> decrypt(DecryptRequest req) {
        try {
            byte[] key = cryptoService.decodeBase64(req.getKeyBase64());
            byte[] iv  = req.getIvBase64() == null
                    ? null
                    : cryptoService.decodeBase64(req.getIvBase64());

            byte[] ciphertext = cryptoService.decodeBase64(req.getCiphertextBase64());

            byte[] decryptedBytes = cryptoService.decryptBytes(
                    req.getAlgorithm(),
                    key,
                    iv,
                    ciphertext
            );

            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

            return ResponseEntity.ok(Map.of(
                    "plaintext", decryptedText
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @Override
    public ResponseEntity<?> encryptFileStream(String algorithm,
                                               String keyBase64,
                                               String ivBase64,
                                               MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] key = cryptoService.decodeBase64(keyBase64);
            byte[] iv = ivBase64 == null ? null : cryptoService.decodeBase64(ivBase64);
            File enc = cryptoService.encryptFileStream(algorithm, key, iv, in);
            String newFilename = addToFilename(file.getOriginalFilename(), "ENC");

            InputStreamResource resource = new InputStreamResource(new FileInputStream(enc));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + newFilename + "\"")
                    .contentLength(enc.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }


    @Override
    public ResponseEntity<?> decryptFileStream(String algorithm,
                                               String keyBase64,
                                               String ivBase64,
                                               MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            byte[] key = cryptoService.decodeBase64(keyBase64);
            byte[] iv = ivBase64 == null ? null : cryptoService.decodeBase64(ivBase64);
            File enc = cryptoService.decryptFileStream(algorithm, key, iv, in);
            String newFilename = addToFilename(file.getOriginalFilename(), "DEC");

            InputStreamResource resource = new InputStreamResource(new FileInputStream(enc));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + newFilename + "\"")
                    .contentLength(enc.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private String addToFilename(String original, String textToAdd) {
        if (original == null || original.isBlank()) {
            return "encryptedENC";
        }

        int lastDot = original.lastIndexOf('.');
        if (lastDot == -1) {
            return original + textToAdd;
        }

        String name = original.substring(0, lastDot);
        String ext = original.substring(lastDot);
        return name + textToAdd + ext;
    }

}
