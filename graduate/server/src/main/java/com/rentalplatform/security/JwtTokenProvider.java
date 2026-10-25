package com.rentalplatform.security;

import com.rentalplatform.config.RentalProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 签发/解析（jjwt 0.12.x，HS256）。
 * 载荷：sub=用户id、role、ver（token_version）、type（access|refresh）、jti、iat、exp。
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";

    private final RentalProperties properties;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(properties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public record TokenClaims(long userId, String role, int tokenVersion, String type, String jti, Date expiresAt) {
    }

    public String issueAccessToken(long userId, String role, int tokenVersion) {
        long ttlMs = properties.getJwt().getAccessTtlMinutes() * 60_000;
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("ver", tokenVersion)
                .claim("type", TYPE_ACCESS)
                .id(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMs))
                .signWith(key())
                .compact();
    }

    public String issueRefreshToken(long userId, String role, int tokenVersion, String deviceId) {
        long ttlMs = properties.getJwt().getRefreshTtlDays() * 24 * 3600_000;
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .claim("ver", tokenVersion)
                .claim("type", TYPE_REFRESH)
                .claim("deviceId", deviceId)
                .id(UUID.randomUUID().toString().replace("-", "").substring(0, 16))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMs))
                .signWith(key())
                .compact();
    }

    /** 解析并校验签名与有效期；过期抛 ExpiredJwtException，其余伪造情况抛 JwtException */
    public TokenClaims parse(String token) {
        Claims claims = Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload();
        return new TokenClaims(
                Long.parseLong(claims.getSubject()),
                claims.get("role", String.class),
                claims.get("ver", Integer.class),
                claims.get("type", String.class),
                claims.getId(),
                claims.getExpiration());
    }

    public long accessTtlSeconds() {
        return properties.getJwt().getAccessTtlMinutes() * 60;
    }

    public long refreshTtlSeconds() {
        return properties.getJwt().getRefreshTtlDays() * 24 * 3600;
    }
}
