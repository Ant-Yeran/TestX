package com.it.testx.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Date;

@Component
public class JwtUtils {
    private final KeyPair keyPair;  // RSA 密钥对
    private final String issuer;
    private final String audience;
    private final long expiration;  // JWT 过期时间（毫秒）


    public JwtUtils(
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.audience}") String audience,
            @Value("${jwt.expiration:86400000}") long expiration  // 默认 24 小时
    ) {
        this.issuer = issuer;
        this.audience = audience;
        this.keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);  // 自动生成 RSA 密钥对
        this.expiration = expiration;
    }

    // 生成 JWT
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(issuer)      // 必须与 API Gateway 配置的 x-google-issuer 一致
                .setAudience(audience)  // 必须与 x-google-audiences 一致
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    // 解析 JWT（验证签名并提取 Claims）
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 校验 JWT
    public boolean validateToken(String token) {
        try {
            parseToken(token); // 复用现有逻辑
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // 获取当前 RSA 公钥（用于 JWKS 端点）
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
}