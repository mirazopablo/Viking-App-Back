package com.ElVikingoStore.Viking_App.JWT;

import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import io.jsonwebtoken.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ElVikingoStore.Viking_App.Exception.ApiException;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@Component
public class JwtTokenProvider {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpirationInMs;

    // generate token
    public String generateToken(Authentication authentication, UUID roleId, UUID userId) {
        String username = authentication.getName();
        return Jwts.builder()
                .subject(username)
                .claim("roleId", roleId)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(signWithKey(), Jwts.SIG.HS512)
                .compact();
    }

    // get username from the token
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signWithKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signWithKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return !claims.getExpiration().before(new Date());
        } catch (MalformedJwtException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "JWT claims string is empty.");
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "falla api exception");
        }
    }

    public String parseBearerToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.toLowerCase().startsWith("bearer")) {
            String[] parts = bearerToken.split("\\s+");
            if (parts.length == 2) {
                String token = parts[1].trim();
                return token;
            }
        }
        return null;
    }

    private SecretKey signWithKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}