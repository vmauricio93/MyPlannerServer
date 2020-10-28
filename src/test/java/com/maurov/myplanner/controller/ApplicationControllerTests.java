package com.maurov.myplanner.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import com.maurov.myplanner.security.ApplicationSecurityConfigurer;

@ExtendWith(SpringExtension.class)
@WebMvcTest(
    controllers = ApplicationController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = ApplicationSecurityConfigurer.class
        )},
    excludeAutoConfiguration = { SecurityAutoConfiguration.class }
)
public class ApplicationControllerTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldForwardInvalidPathsToHome() throws Exception {
        this.mockMvc.perform(get("/invalidPath"))
            .andExpect(forwardedUrl("/"));
    }
}
