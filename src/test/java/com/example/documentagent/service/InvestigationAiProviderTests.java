package com.example.documentagent.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InvestigationAiProviderTests {
    @Test
    void mockProviderAdvertisesItsMode() {
        InvestigationAi provider = new MockInvestigationAi();
        assertThat(provider.mode()).isEqualTo("mock");
    }

    @Test
    void legacyMockResultConstructorKeepsNullCitationListForDeterministicMockBehavior() {
        MockAiResult result = new MockAiResult(
            com.example.documentagent.domain.InvestigationStatus.INSUFFICIENT_EVIDENCE,
            "No evidence", null);
        assertThat(result.citedChunkIds()).isNull();
    }
}
