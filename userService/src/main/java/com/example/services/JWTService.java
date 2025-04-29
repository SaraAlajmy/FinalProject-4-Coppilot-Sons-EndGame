package com.example.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secretKey;
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60 * 30;  // 30 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 30L;  // 30 days

    public JWTService() {
        // Generate a secret key for JWT signing
//
//        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
//        secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
}

    public String generateToken(String username, Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        claims.put("username", username);
        System.out.println("feh claims"+claims);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
      //  return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Map<String, Object> validateAndExtractClaims(String token) {
        try {

            Claims claims = extractAllClaims(token);

            if (claims.getExpiration().before(new Date())) {
                throw new RuntimeException("Token expired");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", claims.get("userId", Long.class));
            response.put("username", claims.get("username", String.class));

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }



}
