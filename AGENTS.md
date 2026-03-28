# Notes for AI agents — API repo

## Before changing code

1. Read ADRs in the **docs** repo (`foster-together-mn`): [docs/adr/README.md](https://github.com/your-org/foster-together-mn/blob/main/docs/adr/README.md) (adjust URL to your remote).
2. **MVP 1** scope: directory CRUD only — see [docs/MVP1_CHECKLIST.md](https://github.com/your-org/foster-together-mn/blob/main/docs/MVP1_CHECKLIST.md).

## Hard rules

- **SQL-first:** Flyway/Liquibase + JdbcTemplate/MyBatis. Do not introduce Spring Data JPA/Hibernate as the default without a new ADR.
- **RBAC:** Strip or reject sensitive fields in the API based on JWT claims / `cognito:groups`.
- **CDK** lives here under `infra/` when created (TypeScript, CDK v2).

## Human implementer

The owner often implements themselves; prefer guidance and review unless they ask for full patches.
