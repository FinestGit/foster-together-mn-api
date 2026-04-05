# Foster Together MN — API

Spring Boot **REST API** for Foster Together MN: directory (MVP 1), support, events, donations/inventory (later). **SQL-first** persistence per ADR-0006 in the docs repo (`docs/adr/0006-sql-first-persistence.md`). If you use a multi-root workspace, open the **foster-together-mn** clone; otherwise replace `your-org` in the links below with your Git host.

## Related repositories

| Repo | Role |
| ---- | ---- |
| **foster-together-mn** (docs) | ADRs, architecture diagram, [MVP1 directory breakdown](https://github.com/your-org/foster-together-mn/blob/main/docs/MVP1_DIRECTORY_BREAKDOWN.md) |
| **foster-together-mn-web** | React + Vite SPA |
| **this repo** | Java API + **AWS CDK** (`infra/` when added) |

## Stack (locked in ADRs)

- **Java 17**, **Spring Boot 3**
- **PostgreSQL** (Flyway or Liquibase migrations — SQL in repo)
- **JdbcTemplate** and/or **MyBatis** — not JPA-by-default
- **Amazon Cognito** JWT + Spring Security resource server
- **Deploy:** ECS Fargate + ALB + RDS (CDK in this repo)

## Local development

### JVM only (no containers)

```bash
./mvnw spring-boot:run
```

Health: `curl -s http://localhost:8080/actuator/health`

### Docker Compose — API + PostgreSQL (stories 2.1 + 4.0b)

Requires [Docker](https://docs.docker.com/get-docker/) with Compose v2.

```bash
docker compose up --build
```

- **Postgres:** `localhost:5432` from the **host**; inside the **API** container the JDBC URL uses hostname **`db`** (set via `SPRING_DATASOURCE_*` in [`docker-compose.yml`](docker-compose.yml)). Database `fostertogether`, user/password `fostertogether` (dev only; see [`.env.example`](.env.example)).
- **API:** `http://localhost:8080` — `depends_on` waits until Postgres passes `pg_isready` before starting the API.

Stop and remove containers: `docker compose down`. To wipe DB data: `docker compose down -v`.

### Later: JDBC from the host against Compose Postgres

1. Run only the DB: `docker compose up db` (or full compose without binding API if you prefer).
2. Set `SPRING_DATASOURCE_*` (see [`.env.example`](.env.example)); never commit secrets.
3. Run `./mvnw spring-boot:run`; point **web** at `VITE_API_BASE_URL` (e.g. `http://localhost:8080`).

## Conventions

- **Internal-only fields** (e.g. `internal_notes`): enforce in **controllers/services**, not only in the UI.
- **CORS:** allow the **`web`** origin (`app.` in prod, localhost dev port in dev).

See [AGENTS.md](AGENTS.md) for AI assistant notes.
