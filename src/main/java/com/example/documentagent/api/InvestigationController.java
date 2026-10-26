package com.example.documentagent.api;

import com.example.documentagent.service.InvestigationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/investigations")
public class InvestigationController {
    private final InvestigationService service;

    public InvestigationController(InvestigationService service) { this.service = service; }

    @PostMapping
    @Operation(summary = "Investigate a question using evidence from the requester's documents")
    public ResponseEntity<InvestigationResponse> create(
        @RequestHeader("X-User-Id") String requesterId,
        @Valid @RequestBody InvestigationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.investigate(requesterId, request));
    }

    @GetMapping
    @Operation(summary = "List investigations belonging to the supplied demo identity")
    public List<InvestigationResponse> list(@RequestHeader("X-User-Id") String requesterId) {
        return service.list(requesterId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one investigation if it belongs to the supplied demo identity")
    public InvestigationResponse get(@PathVariable @Positive(message = "id must be positive") Long id,
                                     @RequestHeader("X-User-Id") String requesterId) {
        return service.get(id, requesterId);
    }
}
