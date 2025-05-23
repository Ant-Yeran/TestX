package com.it.testx.controller;

import com.it.testx.utils.JwtUtils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.PublicKey;

@RestController
@RequestMapping("/auth")
public class JwksController {
    private final JwtUtils jwtUtils;

    public JwksController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/.well-known/jwks.json")
    public String getJwks() {
        PublicKey publicKey = jwtUtils.getPublicKey();
        RSAKey rsaKey = new RSAKey.Builder((java.security.interfaces.RSAPublicKey) publicKey)
                .keyID("my-key-id")  // 可自定义 Key ID
                .build();
        return new JWKSet(rsaKey).toString();
    }
}