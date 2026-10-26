package com.example.documentagent.api;

import com.example.documentagent.domain.Document;
import com.example.documentagent.service.DocumentIngestionService;
import com.example.documentagent.service.DocumentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@Validated
public class DocumentController {
    private final DocumentIngestionService ingestion;
    private final DocumentQueryService query;

    public DocumentController(DocumentIngestionService ingestion, DocumentQueryService query) {
        this.ingestion = ingestion;
        this.query = query;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload and process a PDF or TXT document")
    public ResponseEntity<DocumentResponse> upload(
        @Parameter(description = "Demo requester identifier; not a replacement for production authentication")
        @RequestHeader("X-User-Id") String ownerId,
        @RequestParam("file") MultipartFile file) {
        Document saved = ingestion.ingest(file, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(DocumentResponse.from(saved, queryChunkCount(saved.getId())));
    }

    @GetMapping
    @Operation(summary = "List documents owned by the supplied demo requester")
    public List<DocumentResponse> list(@RequestHeader("X-User-Id") String ownerId) {
        return query.list(ownerId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document metadata if owned by the supplied demo requester")
    public DocumentResponse get(@PathVariable @Positive Long id,
                                @RequestHeader("X-User-Id") String ownerId) {
        return query.get(id, ownerId);
    }

    private long queryChunkCount(Long id) {
        // A small query-service call avoids returning document text or loading chunks into the response.
        return query.chunkCount(id);
    }
}
