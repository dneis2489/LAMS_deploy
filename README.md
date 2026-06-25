# LAMS deploy

This is a standalone deployment project assembled from the backend, frontend, and log generator.

## Structure

```text
backend/        Spring Boot backend and DB/Logstash Docker files
frontend/       React frontend
log-generator/  Synthetic Kafka log generator
docker/         Frontend nginx image/config
scripts/        Deploy/update helper scripts
docker-compose.yml
.env.example
```

The original source folders outside this directory are not used by Docker Compose.

## First local or server run

```bash
cp .env.example .env
nano .env
chmod +x scripts/*.sh
./scripts/deploy.sh
```

Open:

```text
http://SERVER_IP/
```

For local Windows testing, create `.env` manually or copy `.env.example`, then run:

```powershell
docker compose up -d --build
```

## Updating on the server

After pushing changes to Git:

```bash
cd /opt/lams
git pull --ff-only
./scripts/update.sh
```

Or let the script pull:

```bash
./scripts/update.sh --pull
```

## Git setup

From this directory:

```bash
git init -b main
git add .
git commit -m "Add LAMS deploy project"
git remote add origin <REPOSITORY_URL>
git push -u origin main
```

Do not commit `.env`; it is ignored. Commit only `.env.example`.

## Notes

- Ollama is not included. `LAMS_AI_SMART_SEARCH_ENABLED=false` keeps the backend on its fallback parser.
- Only the frontend/Nginx container publishes a port. Postgres, Kafka, Logstash, and backend stay inside the Docker network.
- PostgreSQL initialization scripts run only on first creation of the `postgres_data` volume. Recreating the volume deletes database data.
