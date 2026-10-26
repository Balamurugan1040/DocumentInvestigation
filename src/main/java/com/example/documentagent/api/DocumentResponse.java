package com.example.documentagent.api;

import com.example.documentagent.domain.Document;
import com.example.documentagent.domain.DocumentStatus;
import java.time.Instant;

public record DocumentResponse(
    Long id,
    String fileName,
    String contentType,
    String sha256,
    DocumentStatus status,
    int extractedCharacters,
    long chunkCount,
    Instant createdAt
) {
    public static DocumentResponse from(Document d, long chunks) {
        String txt = d.getExtractedText();
        return new DocumentResponse(
            d.getId(), d.getFileName(), d.getContentType(), d.getSha256(), d.getStatus(),
            txt == null ? 0 : txt.length(), chunks, d.getCreatedAt()
        );
    }
}
