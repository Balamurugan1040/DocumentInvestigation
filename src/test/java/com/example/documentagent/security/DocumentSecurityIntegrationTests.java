package com.example.documentagent.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.documentagent.repository.AuditEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** Integration tests for ownership isolation, upload validation, and audit traceability. */
@SpringBootTest
@AutoConfigureMockMvc
class DocumentSecurityIntegrationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private AuditEventRepository auditEvents;

    @Test
    void uploadedDocumentIsVisibleOnlyToItsOwnerAndSearchIsOwnerScoped() throws Exception {
        String owner = "m7-owner-" + UUID.randomUUID();
        String other = "m7-other-" + UUID.randomUUID();
        String uniquePhrase = "M7CONFIDENTIALTOKEN " + UUID.randomUUID().toString().replace("-", "");
        byte[] content = ("Security validation document. " + uniquePhrase +
            ". Only the document owner should retrieve this text.").getBytes(StandardCharsets.UTF_8);

        MvcResult upload = mockMvc.perform(multipart("/api/documents")
                .file(new MockMultipartFile("file", "m7-security-test.txt", "text/plain", content))
                .header("X-User-Id", owner))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.status").value("PROCESSED"))
            .andReturn();
        long documentId = objectMapper.readTree(upload.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/api/documents/{id}", documentId).header("X-User-Id", owner))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(documentId));

        mockMvc.perform(get("/api/documents/{id}", documentId).header("X-User-Id", other))
            .andExpect(status().isNotFound());

        MvcResult otherDocuments = mockMvc.perform(get("/api/documents").header("X-User-Id", other))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode otherDocumentList = objectMapper.readTree(otherDocuments.getResponse().getContentAsString());
        assertThat(otherDocumentList.isArray()).isTrue();
        for (JsonNode item : otherDocumentList) {
            assertThat(item.get("id").asLong()).isNotEqualTo(documentId);
        }

        String searchBody = objectMapper.writeValueAsString(java.util.Map.of("question", uniquePhrase, "limit", 5));
        mockMvc.perform(post("/api/evidence/search").header("X-User-Id", owner)
                .contentType(MediaType.APPLICATION_JSON).content(searchBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCount").value(1));

        mockMvc.perform(post("/api/evidence/search").header("X-User-Id", other)
                .contentType(MediaType.APPLICATION_JSON).content(searchBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCount").value(0));
    }

    @Test
    void duplicateContentIsRejectedForSameOwnerButAllowedForAnotherOwner() throws Exception {
        String owner = "m7-duplicate-owner-" + UUID.randomUUID();
        String other = "m7-duplicate-other-" + UUID.randomUUID();
        byte[] content = ("Unique duplicate test content " + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8);

        mockMvc.perform(multipart("/api/documents")
                .file(new MockMultipartFile("file", "duplicate.txt", "text/plain", content))
                .header("X-User-Id", owner))
            .andExpect(status().isCreated());

        mockMvc.perform(multipart("/api/documents")
                .file(new MockMultipartFile("file", "duplicate.txt", "text/plain", content))
                .header("X-User-Id", owner))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("This file has already been uploaded by this user."));

        mockMvc.perform(multipart("/api/documents")
                .file(new MockMultipartFile("file", "duplicate.txt", "text/plain", content))
                .header("X-User-Id", other))
            .andExpect(status().isCreated());
    }

    @Test
    void rejectsPdfExtensionWhenContentIsNotPdf() throws Exception {
        String owner = "m7-invalid-pdf-" + UUID.randomUUID();
        mockMvc.perform(multipart("/api/documents")
                .file(new MockMultipartFile("file", "not-really-a-pdf.pdf", "application/pdf",
                    "this is not a PDF".getBytes(StandardCharsets.UTF_8)))
                .header("X-User-Id", owner))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("The file extension is PDF, but the content is not a valid PDF header."));
    }

    @Test
    void uploadAuditEventContainsActorAndResourceButNotDocumentText() throws Exception {
        String owner = "m7-audit-owner-" + UUID.randomUUID();
        String secret = "PRIVATE_BODY_" + UUID.randomUUID().toString().replace("-", "");
        byte[] content = ("Audit test document " + secret).getBytes(StandardCharsets.UTF_8);
        MvcResult upload = mockMvc.perform(multipart("/api/documents")
                .file(new MockMultipartFile("file", "audit-test.txt", "text/plain", content))
                .header("X-User-Id", owner))
            .andExpect(status().isCreated())
            .andReturn();
        JsonNode response = objectMapper.readTree(upload.getResponse().getContentAsString());
        String id = response.get("id").asText();

        var events = auditEvents.findAllByResourceTypeAndResourceIdOrderByCreatedAtAsc("DOCUMENT", id);
        assertThat(events).anySatisfy(event -> {
            assertThat(event.getActorId()).isEqualTo(owner);
            assertThat(event.getEventType()).isEqualTo("DOCUMENT_UPLOADED");
            assertThat(event.getDetails()).doesNotContain(secret);
        });
    }
}
