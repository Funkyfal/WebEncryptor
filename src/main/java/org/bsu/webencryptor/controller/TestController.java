package org.bsu.webencryptor.controller;

import by.bcrypto.bee2j.Bee2Library;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@RestController
@RequestMapping("/test")
public class TestController {

    private final Bee2Library bee2Library = Bee2Library.INSTANCE;

    @GetMapping("/hash")
    public String hash(@RequestParam String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] out = new byte[32];
        int rc = bee2Library.beltHash(out, inputBytes, inputBytes.length);
        if (rc != 0) {
            throw new RuntimeException("beltHash failed code " + rc);
        }
        return HexFormat.of().formatHex(out);
    }

}
