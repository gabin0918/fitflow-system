package com.fitflow.gym_operations.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;

@Component
public class JwtUtils {
    // Użyj TEGO SAMEGO klucza co w Auth-Service!
    private final String SECRET_KEY = "TwojBardzoDlugiTajnyKluczKtoryMaMinimumTrzydziesciDwaZnaki";

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}