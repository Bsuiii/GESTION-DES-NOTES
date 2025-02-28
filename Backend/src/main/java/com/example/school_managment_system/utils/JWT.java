package com.example.school_managment_system.utils;

import com.example.school_managment_system.models.User;
import com.example.school_managment_system.models.Role;
import com.example.school_managment_system.models.UserDetails;
import io.jsonwebtoken.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.login.LoginException;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWT {
    private String signatureAlgorithm = SignatureAlgorithm.HS256.getJcaName();
    private String encodedSecretKey = "dGhpcytpcythbit+YXBwK3RvK2xlYXirc3ByaW5nK2Jvb3QrTWFzdGVy";
    private Key decodedSecretKey = new SecretKeySpec(Base64.getDecoder().decode(encodedSecretKey), this.signatureAlgorithm);

    // Generate token from user
    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("firstName", user.getFirstname());
        claims.put("lastName", user.getLastname());
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getUser_Role());

        return createToken(claims, user.getEmail());
    }

    // Generate token from an existing token
    public String generateToken(String token) {
        Map<String, Object> claims = new HashMap<>();
        Claims ourClaims = extractAllClaims(token);
        claims.put("role", ourClaims.get("role"));
        return createToken(claims, ourClaims.getSubject());
    }

    // Create token from claims
    private String createToken(Map<String, Object> claims, String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(30, ChronoUnit.MINUTES)))
                .signWith(this.decodedSecretKey)
                .compact();
    }

    // Extract all claims from the token
    public Claims extractAllClaims(String token) throws ExpiredJwtException, SignatureException, MalformedJwtException {
        if (token == null || token.isEmpty()) {
            throw new MalformedJwtException("Token is missing or empty");
        }
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(decodedSecretKey)
                .build();
        return jwtParser.parseClaimsJws(token).getBody();
    }

    // Extract email from the token
    public String extractEmail(String token) throws SignatureException, MalformedJwtException {
        return extractAllClaims(token).getSubject();
    }

    // Extract expiration date from the token
    public java.util.Date extractExpirationDate(String token) throws SignatureException, MalformedJwtException {
        return extractAllClaims(token).getExpiration();
    }

    // Check if the token is expired
    public boolean isTokenExpired(String token) throws SignatureException, MalformedJwtException {
        try {
            extractAllClaims(token);
            return false;
        } catch (ExpiredJwtException err) {
            return true;
        }
    }

    // Get role from the token
    public String getRole(String token) throws SignatureException, MalformedJwtException {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    // Validate the token against a user's email and expiration
    public boolean validateToken(String token, User user) throws MalformedJwtException, SignatureException {
        final String userEmail = extractEmail(token);
        return (userEmail.equals(user.getEmail()) && !isTokenExpired(token));
    }

    // Validate the token (general version)
    public boolean validateToken(String token) throws MalformedJwtException, SignatureException {
        if (token == null || token.isEmpty()) {
            throw new MalformedJwtException("Token is missing or empty");
        }
        final Claims claims = extractAllClaims(token);
        return true;
    }

    // Check if the user type is valid based on the token
    public boolean checkUser(String token, Role role) throws LoginException, SignatureException, MalformedJwtException {
        String newToken = token.replace("Bearer ", "");
        if (validateToken(newToken)) {
            if (!getRole(newToken).equals(role.getName())) {
                throw new LoginException("User not allowed");
            }
        }
        return true;
    }

    // Check JWT token and return headers
    public HttpHeaders checkTheJWT(String jwt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        if (jwt == null || jwt.isEmpty()) {
            throw new MalformedJwtException("Token is missing or empty");
        }
        String myJWT = jwt.split(" ")[1];
        if (validateToken(myJWT)) {
            headers.set("Authorization", "Bearer " + generateToken(myJWT));
        }
        return headers;
    }

    public ResponseEntity checkTheJWT() throws Exception {
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }
}