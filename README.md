# AI Document Investigation Agent

A Java 21 / Spring Boot application that searches a user's PDF and TXT documents, gathers traceable excerpts, and returns a persisted investigation. It supports deterministic mock analysis by default and an optional local LLM through Spring AI and Ollama.

## What it does

- Accepts PDF and TXT uploads, extracts text, calculates a SHA-256 checksum, and stores overlapping chunks with offsets into the extracted text.
- Searches only processed documents associated with the supplied demo identity. Results include document and chunk identifiers, excerpt, offsets, matched terms, and a ranking score.
- Lets you select one or more previously uploaded documents in the browser UI to limit an investigation to those sources. With no selection, it searches all documents belonging to the current demo identity.
- Runs an investigation over the retrieved evidence and reports a conclusion/status, cited evidence, missing information or conflict status, and a recommended next action.
- Persists documents, chunks, investigations, evidence references, and audit events in H2.
- Exposes REST endpoints, Swagger/OpenAPI documentation, and an optional Thymeleaf browser interface.
- Returns structured errors for expected invalid requests and hides internal exception details from API responses.

## Technology and prerequisites

- Java 21 (Java 17 or later is the assessment requirement; this project is configured for Java 21)
- Maven 3.9 or newer
- Spring Boot 3.5.x, Spring Web, Jakarta Validation, Spring Data JPA
- H2, Apache PDFBox, Springdoc OpenAPI, Spring AI Ollama
- JUnit 5, AssertJ, Spring Boot Test, and MockMvc

Check the local tools:

```bash
java -version
mvn -version
```

## Build and run

From the directory containing `pom.xml`:

```bash
mvn clean test
mvn spring-boot:run
```

Alternatively, in Eclipse, open `DocumentAgentApplication.java` and choose **Run As → Java Application**. No Ollama installation or model download is needed for the normal run: `APP_AI_MODE` defaults to `mock`, which is built into the application. Ollama is optional and is needed only if you explicitly choose the `ollama` mode described below.

The application uses file-based H2 at `./data/document-agent`, so records survive normal restarts when run from the same project directory. Tests configure an isolated in-memory H2 database.

- Browser UI: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Local H2 console: http://localhost:8080/h2-console (development convenience only)

Stop with Ctrl+C. The H2 console and demo identity setup are for local assessment use, not production deployment.

## Optional local LLM

The default `APP_AI_MODE=mock` uses deterministic rules and requires no model download. To use the implemented Spring AI/Ollama provider, install and start Ollama, then pull the configured model:

```bash
ollama pull llama3.2:3b
```

Set the following environment variables before starting the app:

```text
APP_AI_MODE=ollama
OLLAMA_MODEL=llama3.2:3b
OLLAMA_BASE_URL=http://localhost:11434
```

The application does not automatically pull models. See [Ollama setup](docs/SPRING_AI_OLLAMA_SETUP.md) for platform-specific instructions and failure behavior. Automated tests use mock mode and do not establish the quality of live model responses.

## REST API

All API examples use `X-User-Id` as a demo identity. Use the same value for a user's uploads and questions. This caller-supplied header demonstrates application-level owner scoping; it is **not authentication**.

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/documents` | Upload a PDF or TXT as multipart field `file` |
| `GET` | `/api/documents` | List the caller's documents |
| `GET` | `/api/documents/{id}` | Get owned document metadata |
| `POST` | `/api/evidence/search` | Search evidence in the caller's processed documents |
| `POST` | `/api/investigations` | Create and persist an investigation |
| `GET` | `/api/investigations` | List the caller's investigations |
| `GET` | `/api/investigations/{id}` | Retrieve an owned investigation and its evidence |

Upload a sample file:

```bash
curl -X POST http://localhost:8080/api/documents \
  -H "X-User-Id: demo-user" \
  -F "file=@test-data/payment-policy.txt"
```

Ask a question using the same identity:

```bash
curl -X POST http://localhost:8080/api/investigations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: demo-user" \
  -d '{"question":"What is the invoice payment deadline?","limit":5}'
```

The response includes a status, conclusion, evidence references, active AI mode, limitation note, and recommended next action. The optional `documentIds` array can restrict an investigation to selected owned documents; omit it or send an empty array to search all of the caller's documents. Use returned IDs for subsequent retrieval; IDs shown in examples or local databases are not portable.

Expected request errors are returned as structured JSON. Typical cases include `400` for invalid input or missing identity header, `404` for unknown or non-owned resources, `409` for duplicate same-owner uploads, `413` for oversized uploads, and `415` for unsupported file types. Exact handling can depend on whether the request is rejected by the web server or application layer.

## Design and workflow

The application is a modular monolith: controllers validate and route requests, services handle ingestion/retrieval/investigation, repositories persist domain entities, and a provider-neutral `InvestigationAi` interface selects mock or Ollama analysis.

1. Validate the PDF/TXT upload, extract text, check limits, compute a checksum, and reject duplicate content for the same identity.
2. Split extracted text into overlapping chunks while preserving source offsets.
3. Retrieve and rank matching chunks from the caller's processed documents using normalized keyword matching.
4. Pass the question and retrieved evidence to the selected analysis provider. In Ollama mode, retrieved excerpts are treated as untrusted data and the model returns structured output.
5. Validate model-cited chunk IDs against the retrieved set, persist the investigation and supported evidence references, and record audit events.

The local application limits are configured in `src/main/resources/application.yml`: 5 MiB upload, 1,000,000 extracted characters, 1,000-character chunks with 100-character overlap, up to 500 chunks per document, and up to 20 retrieved results per investigation (default 10). Questions are limited to 2,000 characters.

## Assumptions and safety boundaries

- Documents are private to the demo identity that uploaded them; evidence retrieval is scoped to that identity.
- Uploaded text is evidence, not trusted instructions. Prompt-injection guardrails are applied to the Ollama prompt, but they do not guarantee resistance to every attack.
- A generated answer may only cite chunk IDs from retrieved evidence. This validates citation references, but does not prove that every statement is entailed by its cited excerpt.
- `X-User-Id` can be chosen by the caller and must not be used as a production authorization boundary.
- Audit events record action, actor, resource, and limited details; they are intended to make workflow activity reviewable.
- H2 file storage and the enabled H2 console are local assessment conveniences.

## Known limitations

- Retrieval is keyword-based, not semantic/vector search; paraphrases and synonym-heavy questions may miss relevant passages.
- Conflict and sufficiency decisions are heuristic in mock mode and probabilistic in LLM mode. A later-dated document is not automatically authoritative.
- Scanned/image-only PDFs are not OCR-processed.
- LLM answers can be incorrect or incomplete even when their citation IDs are valid. Review cited excerpts against original documents.
- `recommendedNextAction` is a generic action based on the investigation status.
- Production authentication, authorization integration, rate limiting, secret management, monitoring, and deployment hardening are outside this assessment implementation.

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [API examples and validation matrix](docs/API_AND_VALIDATION.md)
- [AI design and limitations](docs/AI_DESIGN_AND_LIMITATIONS.md)
- [Ollama setup](docs/SPRING_AI_OLLAMA_SETUP.md)
- [Testing and security](docs/TESTING_AND_SECURITY.md)
- [Manual validation plan](docs/MANUAL_VALIDATION_PLAN.md)
- [Requirement traceability](docs/REQUIREMENT_TRACEABILITY_AND_VALIDATION.md)
- [Submission checklist](docs/SUBMISSION_CHECKLIST.md)

## AI tools used during development

OpenAI Codex was used during submission preparation to review the project against the assessment and revise project documentation, including this README. Update this disclosure to include any other AI tools used during implementation and describe their contributions accurately. Follow the interviewer's AI-use policy; do not claim work or tools that do not reflect your process.
