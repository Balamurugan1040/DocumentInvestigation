package com.example.documentagent.api;

import com.example.documentagent.service.KeywordEvidenceSearchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceSearchController {
    private final KeywordEvidenceSearchService searchService;

    public EvidenceSearchController(KeywordEvidenceSearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    public EvidenceSearchResponse search(
        @RequestHeader("X-User-Id") String ownerId,
        @Valid @RequestBody EvidenceSearchRequest request) {
        return searchService.search(ownerId, request);
    }
}
