package com.example.lab1.annotation.web;

import com.example.lab1.controller.AnnotationDemoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class TaskGetEndpointTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(
                new AnnotationDemoController()
        ).build();
    }

    @Test
    @DisplayName("AC5: composed annotation provides GET routing and HTTP 200")
    void taskGetEndpoint_providesExpectedBehavior() throws Exception {
        mockMvc.perform(get("/annotation/task-info"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Task annotation endpoint works"
                ));
    }
}