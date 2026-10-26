# Final submission checklist
## Build and run
- [ ] JDK and Maven versions are documented and available.
- [ ] `mvn clean test` completes with `BUILD SUCCESS`.
- [ ] The application starts from a clean project folder.
- [ ] Swagger UI and OpenAPI JSON are accessible.
- [ ] A reviewer can upload a TXT file, search evidence, create an investigation, and retrieve it.
## Reliability and security
- [ ] Blank/missing questions and invalid limits return controlled 400 responses.
- [ ] Malformed JSON and missing identity headers return controlled errors.
- [ ] Unknown resources return 404.
- [ ] Cross-user document/investigation access is blocked.
- [ ] Evidence search is owner-scoped.
- [ ] Duplicate upload behavior is verified.
- [ ] Data persists after application restart.
- [ ] Audit events identify actor/resource without unnecessary document text.
- [ ] Error responses do not expose stack traces, SQL, or internal exception details.
## Documentation and packaging
- [ ] README includes setup, run, test, endpoint examples, architecture, limitations, and AI approach.
- [ ] Assessment-specific assumptions are clearly stated.
- [ ] AI tools disclosure is completed honestly and follows the assessment rules.
- [ ] No secrets, personal documents, local H2 data, or build output are included.
- [ ] The project ZIP contains source code and documentation, not IDE workspace state.
- [ ] The README describes the implemented Ollama provider as optional and distinguishes it from default Mock mode.
## Before handoff
Record the actual Java/Maven versions and actual test result. Do not state that tests passed unless you ran them. If demonstrating the real LLM, run Ollama locally and record the model and observed behavior separately from the automated Mock-mode tests.
