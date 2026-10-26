package com.example.documentagent.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.documentagent.api.EvidenceSearchResult;
import com.example.documentagent.domain.InvestigationStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class MockInvestigationAiTests {
    private final MockInvestigationAi ai = new MockInvestigationAi();

    @Test
    void asksForClarificationWhenQuestionIsTooBroad() {
        MockAiResult result = ai.analyze("What is the policy?", List.of(match("policy.txt", 0.9, "The policy is reviewed yearly.")));
        assertThat(result.status()).isEqualTo(InvestigationStatus.CLARIFICATION_REQUIRED);
        assertThat(result.clarificationQuestion()).isNotBlank();
    }

    @Test
    void reportsInsufficientEvidenceForWeakMatch() {
        MockAiResult result = ai.analyze("What is the payment delay process?",
            List.of(match("policy.txt", 0.30, "The document processing policy applies.")));
        assertThat(result.status()).isEqualTo(InvestigationStatus.INSUFFICIENT_EVIDENCE);
    }

    @Test
    void returnsCompletedWithSourceCaveatForStrongMatch() {
        MockAiResult result = ai.analyze("What is the payment delay process?",
            List.of(match("payment-policy.txt", 0.8, "Payment delays must be reported within five days.")));
        assertThat(result.status()).isEqualTo(InvestigationStatus.COMPLETED);
        assertThat(result.conclusion()).contains("payment-policy.txt", "five days", "not an independent verification");
    }

    @Test
    void detectsExplicitOppositeStatementsAcrossEvidence() {
        MockAiResult result = ai.analyze("Is this process allowed under the payment policy?", List.of(
            match("policy-a.txt", 0.9, "Payment requests are allowed under the process."),
            match("policy-b.txt", 0.8, "Payment requests are not allowed under the process.")));
        assertThat(result.status()).isEqualTo(InvestigationStatus.CONFLICTING_EVIDENCE);
    }

    private EvidenceSearchResult match(String file, double score, String text) {
        return new EvidenceSearchResult(1L, file, 1L, 0, 0, text.length(), text, score, List.of("payment"));
    }
}
