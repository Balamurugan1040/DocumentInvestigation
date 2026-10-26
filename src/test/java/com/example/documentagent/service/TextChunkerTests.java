package com.example.documentagent.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextChunkerTests {
    private final TextChunker chunker = new TextChunker();

    @Test
    void createsOverlappingChunksWithOriginalOffsets() {
        List<TextChunker.TextChunk> chunks = chunker.chunk("abcdefghij", 6, 2, 10);
        assertThat(chunks).hasSize(2);
        assertThat(chunks.get(0).text()).isEqualTo("abcdef");
        assertThat(chunks.get(1).text()).isEqualTo("efghij");
        assertThat(chunks.get(1).startOffset()).isEqualTo(4);
        assertThat(chunks.get(1).endOffset()).isEqualTo(10);
    }

    @Test
    void rejectsInvalidOverlap() {
        assertThatThrownBy(() -> chunker.chunk("abc", 4, 4, 10))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void enforcesMaximumChunkCount() {
        assertThatThrownBy(() -> chunker.chunk("abcdefghij", 3, 0, 2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("more chunks than the configured limit");
    }
}
