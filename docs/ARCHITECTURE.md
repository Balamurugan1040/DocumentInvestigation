# Architecture
## Style
The application is a modular monolith: one Spring Boot process and one relational persistence layer, with responsibilities separated by packages. The core workflow does not require distributed infrastructure.
## Logical components
- **API/controllers:** expose document, evidence-search, and investigation REST endpoints; bind request parameters and return response DTOs.
- **Configuration:** centralizes ingestion limits and application settings.
- **Services:** coordinate file ingestion, text extraction/chunking, evidence retrieval, and investigation evaluation.
- **Domain entities:** `Document`, `DocumentChunk`, `Investigation`, `EvidenceReference`, and `AuditEvent` represent persisted application state.
- **Repositories:** Spring Data JPA access to H2 and owner-scoped query methods.
- **Error handling:** translates expected request and resource errors into structured HTTP responses.
- **Tests:** unit and MockMvc integration tests use an isolated in-memory H2 database.
## Main data flow
```text
Client (Swagger / curl)
        |
        v
REST Controller + request validation
        |
        +---- Document upload --> extraction --> checksum --> chunks --> repositories
        |
        +---- Evidence search --> owner-scoped chunks --> keyword ranking --> source excerpts
        |
        +---- Investigation --> evidence retrieval --> Mock rules / Ollama LLM
                                         |                    |
                                         v                    v
                                Evidence references      Outcome/status
                                         \                    /
                                          --> persistence <--
                                                   |
                                                   v
                                             Audit events
```
## Persistence model
- **Document:** owner identity, file metadata, checksum, extracted text, status, timestamps.
- **DocumentChunk:** chunk text, index, original text offsets, and parent document.
- **Investigation:** question, requester identity, status, conclusion/clarification data, timestamps.
- **EvidenceReference:** links an investigation to the retrieved source chunk and quote/relevance metadata.
- **AuditEvent:** action type, actor, resource type/id, details, timestamp.
## Ownership boundary
Document and investigation reads, and evidence search, must be scoped by the supplied `X-User-Id`. This is application-level isolation for the assessment only. Because the caller can choose the header value, it is not an authentication mechanism.
## AI boundary
The investigation evaluator is selected behind an `InvestigationAi` service abstraction. Mock AI supports deterministic tests; the Spring AI/Ollama provider is optional for local LLM answers. The model receives only the question and owner-scoped retrieved evidence, and returned citation IDs are checked against the retrieved set before evidence references are persisted. Citation-ID checks do not establish that every generated statement is supported by the cited text.
## Browser UI
The optional browser interface is served from a Thymeleaf template by the same Spring Boot application. Its JavaScript calls the existing `/api/documents` and `/api/investigations` endpoints using the demo `X-User-Id` header. This keeps the deployment as a single application and avoids a separate Node.js frontend.
