# Notes for AI agents — API repo

## Canonical handoff

Start with the **docs** repo: [AGENTS.md](https://github.com/your-org/foster-together-mn/blob/main/AGENTS.md) (replace `your-org` with your Git host). If you use a **multi-root workspace**, open the sibling **`foster-together-mn`** folder and read `AGENTS.md` there.

## Before changing code

1. ADRs: **`foster-together-mn`** [docs/adr/README.md](https://github.com/your-org/foster-together-mn/blob/main/docs/adr/README.md).
2. **MVP 1 directory** backlog: [MVP1_DIRECTORY_BREAKDOWN.md](https://github.com/your-org/foster-together-mn/blob/main/docs/MVP1_DIRECTORY_BREAKDOWN.md) and [MVP1_LICENSING_AGENCY_CRUD.md](https://github.com/your-org/foster-together-mn/blob/main/docs/breakdown/MVP1_LICENSING_AGENCY_CRUD.md).

## Hard rules

- **SQL-first:** Flyway + JdbcTemplate/MyBatis. Do **not** introduce Spring Data JPA/Hibernate as the default without a new ADR.
- **PII and RBAC:** **Staff/admin** vs **volunteer** access models differ. Volunteers (e.g. event check-in) must **not** receive full directory PII—only **volunteer-scoped DTOs** (minimal person cue + non-identifying child counts as defined in stories). Strip or **403** sensitive fields and routes using JWT claims / `cognito:groups`; never rely on the UI to hide data.
- **CORS:** Allow the **web** origin in dev; production allowlist is a tracked story in the agency breakdown (`AGY-14`).
- **OpenAPI:** When **`openapi.yaml`** exists in this repo, treat it as the canonical HTTP contract and **per-operation security** (e.g. Bearer JWT on protected routes). Keep it aligned with Spring Security and controllers.

## Human implementer

The owner often implements application code themselves. Prefer **guidance and review** unless they explicitly ask you to apply patches (see docs repo **implementation** Cursor rule).
