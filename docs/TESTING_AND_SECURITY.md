# Testing and security verification
## Automated tests
Run the full suite from the project root:
```bash
mvn clean test
```
The tests should use `src/test/resources/application.properties` to configure an isolated in-memory H2 database. The application runtime uses file-based H2. Do not mark tests as passed until the Maven output ends with `BUILD SUCCESS`.
## Recommended test order
1. Run all automated tests.
2. Start the application and open Swagger UI.
3. Upload a TXT file, list it, and retrieve its metadata.
4. Search a unique phrase and verify the result identifies the right source excerpt.
5. Create an investigation and retrieve it by ID.
6. Repeat document/investigation reads using a second identity and confirm they return 404.
7. Verify duplicate content returns 409 for the same identity and can be uploaded by a different identity.
8. Try blank questions, invalid limits, malformed JSON, missing headers, and unknown IDs.
9. Restart the application and verify persisted records remain available.
10. Inspect audit rows and confirm full document content is not unnecessarily logged.
## Security scope
The project verifies owner scoping using the `X-User-Id` header, but this header is supplied by the client and is not authentication. Production use requires a trusted identity provider, authentication, authorization rules, rate limiting, secure configuration, deployment hardening, and monitoring.
The deterministic mock is the default test provider; an optional Spring AI/Ollama provider is implemented for local use. Live-provider behavior is not covered by the default automated tests. When validating Ollama, include prompt injection in document text, unsupported model claims, missing and fabricated citations, provider timeout/unavailability, malformed structured output, and output validation failures. The model must not override access controls or receive documents that were not retrieved for the authorized user.
