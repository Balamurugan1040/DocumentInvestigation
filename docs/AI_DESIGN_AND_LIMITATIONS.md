# AI Design and Limitations
## Supported providers
The application supports two provider modes selected with `APP_AI_MODE`: `mock` (default) and `ollama`. Automated tests should run in Mock mode for repeatability and must not require a running model. For real local AI, install Ollama, pull `llama3.2:3b`, and configure `APP_AI_MODE=ollama`, `OLLAMA_MODEL`, and `OLLAMA_BASE_URL`. See `SPRING_AI_OLLAMA_SETUP.md`.
## Workflow
1. Ingest a PDF or TXT file and extract text.
2. Split extracted text into overlapping chunks with source offsets.
3. Retrieve owner-scoped evidence using keyword matching.
4. Send only the question and retrieved evidence to the configured provider.
5. Validate returned citation chunk IDs against the retrieved evidence.
6. Persist the investigation, evidence references, status, and audit events.
## Safety measures
- Retrieved document text is treated as untrusted content in the Ollama prompt.
- The model is instructed to use the supplied evidence and return structured output.
- Cited chunk IDs are checked against the current retrieved set before evidence references are saved.
- Provider failures use a controlled response path and should be verified in manual testing.
- Automated tests use Mock mode; live Ollama quality must be validated separately.
## Known limitations
- Retrieval is keyword-based; it is not semantic/vector search and can miss paraphrases or related terms.
- Citation ID validation does not prove that every sentence in a generated conclusion is entailed by the cited passage. Human review remains necessary.
- Conflict detection and sufficiency decisions can be imperfect, especially with multiple long documents.
- Prompt-injection guardrails reduce risk but do not guarantee complete resistance.
- `X-User-Id` is a demo owner-scoping header, not authentication.
- Scanned/image-only PDF OCR is not included.
- `recommendedNextAction` is derived from the status; it is a generic next step rather than a custom action plan inferred from every source document.
