package com.example.documentagent.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TextChunker {

    public List<TextChunk> chunk(String text, int size, int overlap, int maxChunks) {
        if (text == null || text.isBlank()) return List.of();
        if (size <= 0) throw new IllegalArgumentException("Chunk size must be positive.");
        if (overlap < 0 || overlap >= size) throw new IllegalArgumentException("Chunk overlap must be between zero and chunk size - 1.");
        if (maxChunks <= 0) throw new IllegalArgumentException("Maximum chunks must be positive.");

        List<TextChunk> out = new ArrayList<>();
        int step = size - overlap;
        for (int start = 0; start < text.length(); start += step) {
            int end = Math.min(text.length(), start + size);
            String part = text.substring(start, end);
            if (!part.isBlank()) out.add(new TextChunk(out.size(), part, start, end));
            if (out.size() > maxChunks) {
                throw new IllegalArgumentException("Document produces more chunks than the configured limit.");
            }
            if (end == text.length()) break;
        }
        return List.copyOf(out);
    }

    public record TextChunk(int index, String text, int startOffset, int endOffset) { }
}
