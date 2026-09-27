# Run Fixly with Docker

The frontend stays in `fixly-frontend/`. Compose runs PostgreSQL 16, the Java 17 backend, and the built React frontend served by Nginx. All traffic uses one local URL; `/api` is forwarded to the backend. Existing local development on ports 5173 and 8080 still works.

## First start

Start Docker Desktop, then run from the `fixly-backend` directory:

```sh
./scripts/setup-docker.sh
docker compose up --build -d --wait
```

Open **http://localhost:8081**. Check status with `docker compose ps`.

The setup script generates `.env` with random database and JWT secrets, and keeps an existing `.env` unchanged. Never commit it. `.env.example` documents the variables without credentials. The existing developer `application.properties` is excluded from Docker builds; images use `docker/application.properties` and environment variables instead.

Only the frontend is published, bound to your own computer. PostgreSQL and the backend are reachable inside the Compose network. This is a local setup; public hosting and HTTPS are a later step.

## Your data

Docker starts a **separate, empty database**. Your existing local PostgreSQL users, services and bookings are not copied or changed. Flyway creates the initial schema and applies the existing email uniqueness migration. No default administrator or shared password is created.

Register an account in the Docker app first. To make your own registered account an administrator, connect to this local database:

```sh
docker compose exec db psql -U fixly -d fixly
```

Then run the following with the email you registered:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
\q
```

Log out and back in for the token to carry your updated role. Add services from the dashboard. Use normal customer accounts for bookings.

The database is stored in the `postgres_data` named volume. `docker compose down` stops/removes containers while retaining that data. Do **not** add `--volumes`/`-v` unless you intend to erase the Docker database. Keep `.env` alongside the database: changing its password does not automatically change the password in an existing PostgreSQL volume.

## Commands

```sh
# Start/rebuild the app after changes
docker compose up --build -d --wait

# Read-only checks for the website, direct routes, API and access protection
./scripts/check-docker.sh

# Logs and status
docker compose logs --tail=100 backend
docker compose ps

# Stop the app while keeping its database
docker compose down

# Run the existing backend tests against a separate temporary test database
docker compose --profile test run --build --rm tests
# Stop the temporary test database afterwards
docker compose --profile test stop test-db
```

The image packaging step skips tests because it has no database. The separate test command above runs the full suite with PostgreSQL; it does not point at your development or app database.

Frontend checks remain:

```sh
cd fixly-frontend
npm run build
npm run lint
```

## Troubleshooting

- **Cannot connect to the Docker daemon:** open Docker Desktop and wait until its engine is running.
- **Port 8081 is busy:** change `FIXLY_PORT` in `.env`, then run `docker compose up -d` and use that port.
- **Backend is unhealthy:** check `docker compose logs --tail=100 backend` and `docker compose logs --tail=100 db`.
- **No services appear:** this is a new database; create an admin account as described above and add services.
- **Signing in with a development account fails:** the Docker database is separate; register in the Docker app.
- **Frontend changes are missing:** rebuild the frontend with `docker compose up --build -d frontend`.

## Design references

- [Compose health-based startup order](https://docs.docker.com/compose/how-tos/startup-order/)
- [Nginx proxy configuration](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)
- [Official PostgreSQL image](https://hub.docker.com/_/postgres)
