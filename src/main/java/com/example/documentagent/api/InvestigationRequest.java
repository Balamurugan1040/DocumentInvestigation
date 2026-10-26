package com.example.documentagent.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record InvestigationRequest(
    @NotBlank(message = "question must not be blank")
    @Size(max = 2000, message = "question must not exceed 2000 characters")
    String question,
    @Min(value = 1, message = "limit must be at least 1")
    @Max(value = 20, message = "limit must not exceed 20")
    Integer limit,
    @Size(max = 100, message = "at most 100 document IDs may be selected")
    List<@NotNull @Positive Long> documentIds
) {
    public InvestigationRequest(String question, Integer limit) { this(question, limit, null); }
    public int effectiveLimit() { return limit == null ? 10 : limit; }
}
