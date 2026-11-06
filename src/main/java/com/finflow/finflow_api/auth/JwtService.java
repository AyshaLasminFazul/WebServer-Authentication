package com.finflow.finflow_api.auth;

import com.finflow.finflow_api.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {


    private final SecretKey signingKey;



    public JwtService(@Value("${jwt.secret}")String secret) {
        byte[] bytes=Decoders.BASE64.decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(User user){
       return  Jwts.builder()
              .claim("userId",user.getId())
              .claim("role",user.getRole())
               .issuedAt(new Date())
              .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
               .signWith(signingKey).compact();

    }

    public boolean validateToken(String token){
      try{
          Jwts.parser()
                   .verifyWith(signingKey)
                   .build()
                   .parseSignedClaims(token);
          return true;
       }

       catch(JwtException jwtException){
           return false;
       }

    }

    public Claims claimExtraction(String token){
       Claims claim=  Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
       return claim;
    }
}
