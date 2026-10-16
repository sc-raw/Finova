
package com.finova.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${finova.jwt.secret}")
    private String secret;

    @Value("${finova.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generarToken(String correo) {
        Date ahora = new Date();
        Date expiracionToken = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
                .subject(correo)
                .issuedAt(ahora)
                .expiration(expiracionToken)
                .signWith(getSigningKey())
                .compact();
    }

    public String obtenerCorreo(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean esTokenValido(String token, String correo) {
        try {
            Claims claims = extraerClaims(token);
            return claims.getSubject().equals(correo)
                    && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
