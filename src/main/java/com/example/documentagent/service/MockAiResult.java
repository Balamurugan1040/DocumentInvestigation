package com.example.documentagent.service;

import com.example.documentagent.domain.InvestigationStatus;
import java.util.List;

/** Result from an investigation AI provider. A null citedChunkIds means use the Mock AI's retrieved evidence set. */
public record MockAiResult(
    InvestigationStatus status,
    String conclusion,
    String clarificationQuestion,
    List<Long> citedChunkIds
) {
    public MockAiResult(InvestigationStatus status, String conclusion, String clarificationQuestion) {
        this(status, conclusion, clarificationQuestion, null);
    }
}
