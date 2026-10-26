package com.example.documentagent.api;

public record InvestigationEvidenceResponse(
    Long evidenceReferenceId,
    Long documentId,
    String filename,
    Long chunkId,
    Integer chunkIndex,
    Integer startOffset,
    Integer endOffset,
    String quote,
    double relevanceScore
) {}
