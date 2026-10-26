package com.example.documentagent.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.documentagent.domain.Investigation;
import com.example.documentagent.domain.InvestigationStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvestigationResponseTests {

    @Test
    void completedResultIncludesReviewNextAction() {
        Investigation investigation = investigationWithStatus(InvestigationStatus.COMPLETED);
        InvestigationResponse response = InvestigationResponse.from(investigation, List.of(), "mock");
        assertThat(response.recommendedNextAction()).contains("Review the cited excerpts");
    }

    @Test
    void insufficientEvidenceIncludesUsefulNextAction() {
        Investigation investigation = investigationWithStatus(InvestigationStatus.INSUFFICIENT_EVIDENCE);
        InvestigationResponse response = InvestigationResponse.from(investigation, List.of(), "ollama");
        assertThat(response.recommendedNextAction()).contains("Upload additional relevant documents");
    }

    @Test
    void conflictingEvidenceAsksUserToVerifyAuthority() {
        Investigation investigation = investigationWithStatus(InvestigationStatus.CONFLICTING_EVIDENCE);
        InvestigationResponse response = InvestigationResponse.from(investigation, List.of(), "mock");
        assertThat(response.recommendedNextAction()).contains("authoritative and current");
    }

    private Investigation investigationWithStatus(InvestigationStatus status) {
        Investigation investigation = new Investigation("What is the deadline?", "demo-user");
        investigation.setStatus(status);
        return investigation;
    }
}
