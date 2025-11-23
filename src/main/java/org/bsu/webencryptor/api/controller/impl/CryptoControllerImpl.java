package org.bsu.webencryptor.api.controller.impl;

import lombok.RequiredArgsConstructor;
import org.bsu.webencryptor.api.controller.CryptoController;
import org.bsu.webencryptor.api.dto.DecryptRequest;
import org.bsu.webencryptor.api.dto.EncryptRequest;
import org.bsu.webencryptor.service.CryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CryptoControllerImpl implements CryptoController {

    private final CryptoService cryptoService;


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


}
