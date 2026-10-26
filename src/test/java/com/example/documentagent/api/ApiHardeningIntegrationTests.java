package com.example.documentagent.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ApiHardeningIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void blankInvestigationQuestionReturnsStructuredBadRequest() throws Exception {
        mockMvc.perform(post("/api/investigations")
                .header("X-User-Id", "m6-test-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"   \",\"limit\":5}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.path").value("/api/investigations"));
    }

    @Test
    void invalidLimitReturnsStructuredBadRequest() throws Exception {
        mockMvc.perform(post("/api/evidence/search")
                .header("X-User-Id", "m6-test-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"payment deadline\",\"limit\":0}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void malformedJsonReturnsSafeBadRequest() throws Exception {
        mockMvc.perform(post("/api/investigations")
                .header("X-User-Id", "m6-test-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{not-json"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Request body is missing or contains invalid JSON or field values."));
    }

    @Test
    void missingIdentityHeaderReturnsStructuredBadRequest() throws Exception {
        mockMvc.perform(get("/api/investigations"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Required header X-User-Id is missing."));
    }

    @Test
    void unknownInvestigationReturnsNotFoundWithoutLeakingDetails() throws Exception {
        mockMvc.perform(get("/api/investigations/99999999")
                .header("X-User-Id", "m6-test-user"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("Investigation not found."))
            .andExpect(jsonPath("$.trace").doesNotExist());
    }

    @Test
    void anotherIdentityCannotReadInvestigation() throws Exception {
        String response = mockMvc.perform(post("/api/investigations")
                .header("X-User-Id", "m6-owner")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"What is the payment deadline?\",\"limit\":5}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/investigations/" + id)
                .header("X-User-Id", "m6-other-user"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }
}
