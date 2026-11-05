package com.example.lab1.annotation.mvc;

import com.example.lab1.controller.AnnotationDemoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class CurrentUsernameMvcTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(
                new AnnotationDemoController()
        )
                .setCustomArgumentResolvers(
                        new CurrentUsernameArgumentResolver()
                )
                .build();
    }

    @Test
    @DisplayName("AC8: resolved username is used inside controller method")
    void currentUsername_isUsedByController()
            throws Exception {

        mockMvc.perform(
                        get("/annotation/current-user")
                                .principal(() -> "user")
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Current user: user"
                ));
    }
}