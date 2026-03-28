# Foster Together MN — API

Spring Boot **REST API** for Foster Together MN: directory (MVP 1), support, events, donations/inventory (later). **SQL-first** persistence per ADR-0006 in the docs repo (`docs/adr/0006-sql-first-persistence.md`). If you use a multi-root workspace, open the **foster-together-mn** clone; otherwise replace `your-org` in the links below with your Git host.

## Related repositories

| Repo | Role |
| ---- | ---- |
| **foster-together-mn** (docs) | ADRs, architecture diagram, [MVP1 checklist](https://github.com/your-org/foster-together-mn/blob/main/docs/MVP1_CHECKLIST.md) |
| **foster-together-mn-web** | React + Vite SPA |
| **this repo** | Java API + **AWS CDK** (`infra/` when added) |

## Stack (locked in ADRs)

- **Java 17**, **Spring Boot 3**
- **PostgreSQL** (Flyway or Liquibase migrations — SQL in repo)
- **JdbcTemplate** and/or **MyBatis** — not JPA-by-default
- **Amazon Cognito** JWT + Spring Security resource server
- **Deploy:** ECS Fargate + ALB + RDS (CDK in this repo)

## Local development (when scaffold exists)

1. Run PostgreSQL (Docker recommended).
2. Set `SPRING_DATASOURCE_*` or profile-specific config (see [`.env.example`](.env.example)); never commit secrets.
3. Run the app; point **web** at `VITE_API_BASE_URL` (e.g. `http://localhost:8080`).

## Conventions

- **Internal-only fields** (e.g. `internal_notes`): enforce in **controllers/services**, not only in the UI.
- **CORS:** allow the **`web`** origin (`app.` in prod, localhost dev port in dev).

See [AGENTS.md](AGENTS.md) for AI assistant notes.
