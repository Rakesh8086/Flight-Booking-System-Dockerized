package com.example.api.gateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${signup.app.jwtSecret}")
    private String jwtSecret;
    @Value("${signup.app.jwtCookieName}")
    private String jwtCookie;

    public String getJwtFromCookies(ServerHttpRequest request) {
        // gets cookie created by auth service
        List<HttpCookie> cookies = request.getCookies().get(jwtCookie);
        if(cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }
    
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } 
        catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } 
        catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } 
        catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } 
        catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}