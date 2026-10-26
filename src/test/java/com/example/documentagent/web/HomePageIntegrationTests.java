package com.example.documentagent.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class HomePageIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homePageShowsClearWorkflowAndMockModeInTests() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Upload source files")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Ask a specific question")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Review answer and sources")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Mock AI active")));
    }
}
