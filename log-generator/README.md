# LAMS log generator

Synthetic Kafka log generator for the LAMSv2 ingestion pipeline.

It sends JSON messages to the `lams.logs` topic. Logstash reads them and calls
`public.ingest_kafka_log(...)` in Postgres.

## Message format

Each generated request is sent as two Kafka messages with the same
`correlation_id`:

- `start` log with HTTP status `102` and `duration: 0`
- `finish` log with final HTTP status and measured duration in milliseconds

The payload includes fields used by `logstash.conf`:

- `correlation_id`
- `microservice_name`
- `action_eng`
- `action_rus`
- `request_status_code`
- `log_date`
- `log_type_name`
- `username`
- `duration`

It also includes extra JSON fields for smart search/filtering experiments:
`ip`, `browser`, `userAgent`, `url`, `method`, `device`, `message`.

## Local run

Start Kafka from the backend Docker Compose first:

```powershell
cd ..\LAMSv2
docker compose up -d kafka logstash postgres
```

Then run the generator:

```powershell
cd ..\lams-log-generator
npm install
npm start
```

By default it sends logs to `localhost:9092`, topic `lams.logs`.

## Docker run

When running the generator as a separate Docker container, put it into the same
Docker network as Kafka and use the internal broker address:

```powershell
docker build -t lams-log-generator .
docker run --rm --network lamsv2_default -e KAFKA_BROKERS=kafka:29092 lams-log-generator
```

## One-shot mode

Send one burst and exit:

```powershell
npm run once
```

or:

```powershell
docker run --rm --network lamsv2_default -e KAFKA_BROKERS=kafka:29092 -e GENERATOR_MODE=once lams-log-generator
```

## Environment variables

- `KAFKA_BROKERS`: comma-separated Kafka brokers, default `localhost:9092`
- `KAFKA_TOPIC`: Kafka topic, default `lams.logs`
- `KAFKA_CLIENT_ID`: Kafka client id, default `lams-log-generator`
- `GENERATOR_MODE`: `continuous` or `once`, default `continuous`
- `GENERATOR_INTERVAL_MS`: delay between bursts, default `1000`
- `GENERATOR_BURST_SIZE`: requests per burst, default `1`
