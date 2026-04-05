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

## API contract

**OpenAPI** (`openapi.yaml`, when added to this repo) is the **authoritative** description of HTTP routes, schemas, and **which operations require authentication** (e.g. Cognito JWT as Bearer). Until that file exists, refer to `SecurityConfig`, controllers, and the [JWT resource server](#jwt-resource-server-agy-4) section below.

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

## JWT resource server (AGY-4)

The API validates **access tokens** from your Cognito user pool (Spring OAuth2 resource server). Configure the issuer (no trailing slash):

```bash
# Example — use your pool id from Cognito → User pool → General settings
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=https://cognito-idp.us-east-2.amazonaws.com/us-east-2_twZRjZ9VP
```

Or set `spring.security.oauth2.resourceserver.jwt.issuer-uri` in `application.properties` / profile.

### Authorities (exact strings Spring checks)

| Spring authority   | Routes |
| ------------------ | ------ |
| `agency:write`     | `POST /api/v1/agencies`, `PUT /api/v1/agencies/{id}` |
| `agency:delete`    | `DELETE /api/v1/agencies/{id}` |

All other `/api/**` routes (including `GET /api/v1/agencies` and `GET /api/v1/agencies/{id}`) require a **valid** JWT but **no** named authority beyond authentication.

### Cognito group → authority mapping

Implemented in `CognitoGroupConverter`:

| Cognito group (`cognito:groups`) | Granted authorities      |
| -------------------------------- | ------------------------ |
| `ftmn-directory-admin`           | `agency:write`, `agency:delete` |

Add more groups in that converter when you introduce finer roles.

### Smoke test with `curl`

You need a pool **access token** (JWT) for a user in **`ftmn-directory-admin`**. The in-app login story is not built yet, so use one of these:

1. **Cognito Hosted UI (no custom UI)** — In AWS Console → User pool → **App integration** → your domain → use the **Hosted UI** sign-in link (or build the `/oauth2/authorize` URL). After sign-in, complete the OAuth code flow (token from `/oauth2/token`) or use a tool that captures the token. You do **not** need the Foster Together SPA for this.
2. **AWS CLI** — If the app client allows it, `admin-initiate-auth` with `ADMIN_NO_SRP_AUTH` returns `AuthenticationResult.AccessToken` (needs IAM permission `cognito-idp:AdminInitiateAuth` and a user with a password). Enable **ALLOW_ADMIN_USER_PASSWORD_AUTH** on the app client if you use this path.
3. **Until you have a token** — Run `./mvnw test` (e.g. `AgencyControllerTest`) to verify JWT rules without manual `curl`.

```bash
API=http://localhost:8080
TOKEN='eyJraWQ...'   # access token from Hosted UI flow, CLI, or future app login (not id token)

# Valid JWT, user has agency:delete → 204 when id exists and is deletable
curl -s -o /dev/null -w "HTTP %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "$API/api/v1/agencies/1"

# Same user can read the list (any authenticated JWT)
curl -s -o /dev/null -w "HTTP %{http_code}\n" \
  -H "Authorization: Bearer $TOKEN" \
  "$API/api/v1/agencies"
```

**No token** on `DELETE` → **401** (anonymous). **Valid JWT** without `agency:delete` (e.g. user not in `ftmn-directory-admin`) → **403**. Wrong/expired signature → **401** from the resource server.

```bash
# No Authorization header → 401
curl -s -o /dev/null -w "HTTP %{http_code}\n" -X DELETE "$API/api/v1/agencies/999999"
```
