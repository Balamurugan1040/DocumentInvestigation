package com.example.documentagent.api;

import java.util.List;

public record EvidenceSearchResult(
    Long documentId,
    String filename,
    Long chunkId,
    Integer chunkIndex,
    Integer startOffset,
    Integer endOffset,
    String excerpt,
    double relevanceScore,
    List<String> matchedTerms
) {}
