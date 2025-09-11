package com.dailycodework.universalpetcare.security.jwt;

import com.dailycodework.universalpetcare.security.user.UPCUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;
    @Value("${auth.token.expirationInMils}")
    private int jwtExpirationMs;

    public String generateTokenForUser(Authentication authentication){
        UPCUserDetails userPrincipal = (UPCUserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .subject(userPrincipal.getUsername())  // Updated API
                .claim("id", userPrincipal.getId())
                .claim("roles", roles)
                .issuedAt(new Date())  // Updated API
                .expiration(new Date((new Date()).getTime()+jwtExpirationMs))  // Updated API
                .signWith(key(), Jwts.SIG.HS256)  // Updated SignatureAlgorithm reference
                .compact();
    }

    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromToken(String token){
        return Jwts.parser()  // Updated from parserBuilder()
                .verifyWith(key())  // Updated from setSigningKey()
                .build()
                .parseSignedClaims(token)  // Updated from parseClaimsJws()
                .getPayload()  // Updated from getBody()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()  // Updated from parserBuilder()
                    .verifyWith(key())  // Updated from setSigningKey()
                    .build()
                    .parseSignedClaims(token);  // Updated from parseClaimsJws()
            return true;
        } catch (MalformedJwtException | IllegalArgumentException | UnsupportedJwtException | ExpiredJwtException e) {
            throw new JwtException(e.getMessage());
        }
    }
}