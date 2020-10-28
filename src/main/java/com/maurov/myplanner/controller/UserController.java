package com.maurov.myplanner.controller;

import javax.validation.Valid;

import com.maurov.myplanner.entity.User;
import com.maurov.myplanner.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
    
    private static final String USERS_ENDPOINT = "/api/v1/users";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(USERS_ENDPOINT + "/register")
    public User registerUser(@Valid @RequestBody User user) {
        String username = user.getUsername();
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El nombre de usuario ya existe en la base de datos"
            );
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setRoles("ROLE_USER");

        return userRepository.saveAndFlush(user);
    }
}
