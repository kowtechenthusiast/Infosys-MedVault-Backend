//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.medibook.medibook_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String SECRET_KEY = "bW9ueWFfcmFuZG9tX3NlY3JldF9rZXlfbWVkaWJvb2tfYXBwXzIwMjU=";

    private Key getSigningKey() {
        byte[] keyBytes = (byte[])Decoders.BASE64.decode("bW9ueWFfcmFuZG9tX3NlY3JldF9rZXlfbWVkaWJvb2tfYXBwXzIwMjU=");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = Map.of("role", role);
        long now = System.currentTimeMillis();
        long expiryMillis = now + 86400000L;
        return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date(now)).setExpiration(new Date(expiryMillis)).signWith(this.getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    public String extractUsername(String token) {
        return (String)this.extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return (String)this.extractAllClaims(token).get("role", String.class);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = this.extractAllClaims(token);
        return (T)claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return (Claims)Jwts.parserBuilder().setSigningKey(this.getSigningKey()).build().parseClaimsJws(token).getBody();
    }
}
