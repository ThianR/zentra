package com.zentra.middleware.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * Servicio para generación y validación de tokens JWT.
 * La clave secreta se lee de la variable de entorno ZENTRA_JWT_SECRET.
 * Si no existe, se usa una clave por defecto (solo para desarrollo).
 */
@Service
public class JwtService {

    private static final long EXPIRATION_MS = 8 * 60 * 60 * 1000; // 8 horas
    private final SecretKey secretKey;

    public JwtService() {
        String secret = System.getenv("ZENTRA_JWT_SECRET");
        if (secret == null || secret.length() < 32) {
            // Clave por defecto solo para desarrollo — NO usar en producción
            secret = "ZentraSifenMiddleware2026SecretKeyForJWT!";
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT con los claims del usuario.
     * @param username nombre de usuario
     * @param extraClaims claims adicionales (clienteId, rol, empresaId, etc.)
     * @return token JWT firmado
     */
    public String generateToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .subject(username)
                .claims(extraClaims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrae los claims de un token válido.
     * @param token JWT
     * @return claims decodificados
     */
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida que el token no haya expirado.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
}
