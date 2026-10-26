# API examples and validation matrix
Base URL: `http://localhost:8080`. Unless testing missing-header behavior, include `X-User-Id: demo-user`.
## Endpoint quick reference
| Method | Path | Input | Expected success |
|---|---|---|---|
| POST | `/api/documents` | multipart `file` | 201 |
| GET | `/api/documents` | header | 200 |
| GET | `/api/documents/{id}` | positive ID + header | 200 |
| POST | `/api/evidence/search` | JSON `question`, optional `limit` | 200 |
| POST | `/api/investigations` | JSON `question`, optional `limit` | 201 |
| GET | `/api/investigations` | header | 200 |
| GET | `/api/investigations/{id}` | positive ID + header | 200 |
## Example requests
### Upload
```bash
curl -X POST http://localhost:8080/api/documents \
  -H "X-User-Id: demo-user" \
  -F "file=@sample.txt"
```
### Evidence search
```bash
curl -X POST http://localhost:8080/api/evidence/search \
  -H "Content-Type: application/json" \
  -H "X-User-Id: demo-user" \
  -d '{"question":"What is the maximum upload size?","limit":5}'
```
### Investigation
```bash
curl -X POST http://localhost:8080/api/investigations \
  -H "Content-Type: application/json" \
  -H "X-User-Id: demo-user" \
  -d '{"question":"What is the document processing time limit?","limit":5}'
```
## Manual validation matrix
| # | Scenario | Request / setup | Expected behavior |
|---:|---|---|---|
| 1 | Valid upload | Upload a small TXT file | 201; metadata and ID returned |
| 2 | List owned documents | `GET /api/documents` | 200; only caller's documents |
| 3 | Get owned document | `GET /api/documents/{id}` | 200; metadata only |
| 4 | Search matching text | POST evidence search | 200; source/chunk/excerpt and offsets returned |
| 5 | Valid investigation | POST investigations | 201; saved result and evidence references where found |
| 6 | List investigations | `GET /api/investigations` | 200; only caller's investigations |
| 7 | Get investigation | `GET /api/investigations/{id}` | 200 for owner |
| 8 | Blank question | `question` contains spaces | 400 validation error |
| 9 | Missing question | omit `question` | 400 validation error |
| 10 | Long question | more than 2,000 characters | 400 validation error |
| 11 | Invalid limit | 0, -1, or 21 | 400; 1 and 20 are valid boundary values |
| 12 | Malformed JSON | incomplete JSON body | 400 structured error |
| 13 | Missing identity header | omit `X-User-Id` | controlled 400 error |
| 14 | Unknown ID | use a non-existent positive ID | 404 |
| 15 | Cross-user document read | create as user A, get as user B | 404 |
| 16 | Cross-user investigation read | create as user A, get as user B | 404 |
| 17 | Evidence isolation | upload unique phrase as A, search as B | B must not receive A's document excerpt |
| 18 | Duplicate same-owner upload | upload identical bytes twice as A | second upload returns 409 |
| 19 | Same content, different owner | upload same bytes as B | independent upload allowed |
| 20 | Unsupported file/content | upload unsupported file or invalid PDF bytes | controlled client error; no stack trace |
| 21 | Oversized upload | file larger than 5 MiB | controlled 413/client error |
| 22 | Persistence | create investigation, restart app, GET by ID | saved data remains in file-based H2 |
| 23 | Audit | upload/search/investigate, inspect audit rows | actor/resource recorded; no full document body |
| 24 | Regression suite | `mvn clean test` | BUILD SUCCESS |
Exact status codes can depend on which HTTP parsing layer rejects the request first. Record the observed status and verify the response remains controlled and does not leak internals.
