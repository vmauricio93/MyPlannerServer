package com.maurov.myplanner.security;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.security.Keys;

@Configuration
public class JwtSecretKey {
    
    @Bean
    public SecretKey secretKey(JwtConfig jwtConfig) {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }

}
