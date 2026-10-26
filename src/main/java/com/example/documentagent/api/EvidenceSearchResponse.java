package com.example.documentagent.api;

import java.util.List;

public record EvidenceSearchResponse(
    String question,
    int resultCount,
    List<EvidenceSearchResult> results,
    String retrievalMethod
) {}
