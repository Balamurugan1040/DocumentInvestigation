# Manual Validation Plan
## Purpose
This plan provides a practical way to validate the application against the supplied AI Document Investigation Agent assessment. Record actual outcomes in `Requirement_Validation_Tracker.xlsx`. A case must remain **Not Run** until it has been executed.
## Environment
- JDK 21
- Maven 3.9+
- Application started from the project root (the directory containing `pom.xml`)
- Default AI mode: Mock
- Local H2 file database; no external database required
## Start-up checks
1. Run `mvn clean test` and review the build result and Surefire reports.
2. Start with `mvn spring-boot:run` or run `DocumentAgentApplication` in Eclipse.
3. Open `http://localhost:8080/`, `http://localhost:8080/swagger-ui.html`, and (for local development only) `http://localhost:8080/h2-console`.
4. Upload the supplied sample text files from `test-data/` for positive, conflicting-evidence, and prompt-injection scenarios.
5. Use the same `X-User-Id` value for a user's upload and investigation. Use a second value when testing owner isolation.
## Core assessment scenarios
1. **Clear answer:** upload a document containing a direct answer; ask a focused question; verify conclusion and source evidence.
2. **Multiple documents:** upload complementary facts in separate documents; ask a question that requires both; review all citations.
3. **Conflicting facts:** upload two documents with different values for the same topic; confirm the result communicates the conflict and cites sources.
4. **Insufficient evidence:** ask about a fact absent from the uploaded documents; verify the response does not invent a supported answer.
5. **Ambiguous question:** ask an underspecified question; verify clarification is requested or uncertainty is communicated.
6. **Invalid input:** test blank questions, invalid limits, missing upload parts, unsupported file types, malformed JSON, and oversize uploads.
7. **Provider failure:** in Ollama mode, stop the local provider and verify safe failure handling without exposing stack traces or secrets.
8. **Unauthorized access:** create resources under one demo identity and attempt to read them under another; confirm no other user's content is revealed.
9. **Prompt injection:** upload the provided adversarial sample and inspect whether the system treats document text as untrusted.
10. **Persistence and audit:** create an investigation, restart the app normally, retrieve the saved result, and inspect audit records.
## Recording results
For each case, record:
- Actual status: Not Run, Passed, Failed, or Blocked
- Observed result and relevant response/status
- Defect or follow-up note, if any
- Evidence collected (test output, response, screenshot, or log excerpt)
Do not mark a test as Passed based only on source-code inspection. Run the test and record its observed outcome.
## Known limitations to account for
- Retrieval is keyword-based, not semantic/vector search.
- OCR for scanned/image-only PDFs is not implemented.
- Citation ID validation does not guarantee semantic correctness of every answer sentence.
- Prompt-injection controls reduce risk but are not a complete security guarantee.
- `X-User-Id` is a demo scoping mechanism, not real authentication.
- Mock AI tests do not establish the quality of real Ollama model output.
