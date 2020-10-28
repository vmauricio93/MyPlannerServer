package com.maurov.myplanner.security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class ApplicationSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private ApplicationUserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private JwtConfig jwtConfig;
    private SecretKey secretKey;

    @Autowired
    public ApplicationSecurityConfigurer(
        ApplicationUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder,
        JwtConfig jwtConfig,
        SecretKey secretKey
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(
                authenticationManager(),
                jwtConfig,
                secretKey
            ))
            .addFilterAfter(
                new JwtVerifier(jwtConfig, secretKey),
                JwtUsernameAndPasswordAuthenticationFilter.class
            )
            .authorizeRequests()
            .antMatchers("/api/v1/users/**").permitAll()
            .anyRequest()
            .authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
    throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

}
