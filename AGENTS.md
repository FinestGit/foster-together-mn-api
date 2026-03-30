# Notes for AI agents — API repo

## Before changing code

1. Read ADRs in the **docs** repo (`foster-together-mn`): [docs/adr/README.md](https://github.com/your-org/foster-together-mn/blob/main/docs/adr/README.md) (adjust URL to your remote).
2. **MVP 1** scope: directory CRUD only — see [docs/MVP1_CHECKLIST.md](https://github.com/your-org/foster-together-mn/blob/main/docs/MVP1_CHECKLIST.md).
3. **Lost context?** Cursor plans on this machine: **`~/.cursor/plans/`** — read `ftmn_custom_platform_architecture_73303726.plan.md` (architecture), `mvp1_condensed_roadmap_78ea4e2f.plan.md` (current next steps), `mvp1_story_breakdown_a8b54b6d.plan.md` (historical; living detail is **docs** `MVP1_STORIES.md`), and `jotform_intake_integration_addendum_bea5f71c.plan.md` (deferred Micayla intake integration fields + mapping considerations). Full index: **docs repo** [AGENTS.md](https://github.com/your-org/foster-together-mn/blob/main/AGENTS.md) § *Cursor plans* (adjust URL to your remote).

## Hard rules

- **SQL-first:** Flyway/Liquibase + JdbcTemplate/MyBatis. Do not introduce Spring Data JPA/Hibernate as the default without a new ADR.
- **RBAC:** Strip or reject sensitive fields in the API based on JWT claims / `cognito:groups`.
- **CDK** lives here under `infra/` when created (TypeScript, CDK v2).

## Human implementer

The owner often implements themselves; prefer guidance and review unless they ask for full patches.
