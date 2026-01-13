package com.fitflow.gym_operations.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders; // Nowy import
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtils {

    // TEN KLUCZ MUSI BYĆ IDENTYCZNY JAK W AUTH-SERVICE
    private final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        // Zmieniamy na dekodowanie BASE64, żeby pasowało do Auth-Service
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public java.util.List<String> extractRoles(String token) {
        return (java.util.List<String>) getClaims(token).get("roles");
    }
}