package com.example.documentagent.service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;

class KeywordEvidenceSearchServiceTests {
    @Test
    void tokenizesCaseAndPunctuationConsistently() {
        assertThat(KeywordEvidenceSearchService.tokenize("Payment, POLICY-2025! payment"))
            .containsExactly("payment", "policy", "2025", "payment");
    }

    @Test
    void ignoresNullOrBlankText() {
        assertThat(KeywordEvidenceSearchService.tokenize(null)).isEmpty();
        assertThat(KeywordEvidenceSearchService.tokenize("   ")).isEmpty();
    }

    @Test
    void keepsUnicodeWords() {
        assertThat(KeywordEvidenceSearchService.tokenize("résumé café"))
            .containsExactly("résumé", "café");
    }
}
