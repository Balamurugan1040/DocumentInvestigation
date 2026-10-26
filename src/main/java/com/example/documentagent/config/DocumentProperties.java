package com.example.documentagent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.documents")
public class DocumentProperties {
    private long maxUploadSizeBytes = 5 * 1024 * 1024;
    private int maxExtractedCharacters = 1_000_000;
    private int maxChunksPerDocument = 500;
    private int chunkSize = 1000;
    private int chunkOverlap = 100;

    public long getMaxUploadSizeBytes() { return maxUploadSizeBytes; }
    public void setMaxUploadSizeBytes(long v) { this.maxUploadSizeBytes = v; }
    public int getMaxExtractedCharacters() { return maxExtractedCharacters; }
    public void setMaxExtractedCharacters(int v) { this.maxExtractedCharacters = v; }
    public int getMaxChunksPerDocument() { return maxChunksPerDocument; }
    public void setMaxChunksPerDocument(int v) { this.maxChunksPerDocument = v; }
    public int getChunkSize() { return chunkSize; }
    public void setChunkSize(int v) { this.chunkSize = v; }
    public int getChunkOverlap() { return chunkOverlap; }
    public void setChunkOverlap(int v) { this.chunkOverlap = v; }
}
