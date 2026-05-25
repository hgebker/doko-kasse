# Migrating data from DynamoDB to local MongoDB

This runbook describes how to move existing Doppelkopf data from AWS DynamoDB into the local MongoDB instance used by the migrated application. The application code is already switched to MongoDB — this guide covers the data-only migration.

## Prerequisites

- AWS CLI configured with read access to the four DynamoDB tables
- Local MongoDB running: `docker-compose up -d`
- `mongoimport` installed (included in the [MongoDB Database Tools](https://www.mongodb.com/docs/database-tools/installation/))
- `jq` installed (`brew install jq` on macOS)

## DynamoDB → MongoDB table/collection mapping

| DynamoDB table     | MongoDB collection | Hash key (→ `_id`) |
|--------------------|--------------------|--------------------|
| `doko-abende`      | `doko-evenings`    | `Datum`            |
| `doko-semester`    | `doko-semesters`   | `key`              |
| `doko-einnahmen`   | `doko-earnings`    | auto (ObjectId)    |
| `doko-ausgaben`    | `doko-expenses`    | auto (ObjectId)    |

Field names are translated from German DynamoDB attribute names to English MongoDB field names (see transform steps below).

## Step 1 — Export from DynamoDB

Run a full scan for each table and save the raw output:

```bash
aws dynamodb scan --table-name doko-abende    --output json > doko-abende.json
aws dynamodb scan --table-name doko-semester  --output json > doko-semester.json
aws dynamodb scan --table-name doko-einnahmen --output json > doko-einnahmen.json
aws dynamodb scan --table-name doko-ausgaben  --output json > doko-ausgaben.json
```

Confirm record counts before proceeding:

```bash
aws dynamodb scan --table-name doko-abende    --select COUNT | jq .Count
aws dynamodb scan --table-name doko-semester  --select COUNT | jq .Count
aws dynamodb scan --table-name doko-einnahmen --select COUNT | jq .Count
aws dynamodb scan --table-name doko-ausgaben  --select COUNT | jq .Count
```

## Step 2 — Transform DynamoDB JSON to MongoDB NDJSON

DynamoDB scan output uses typed wrappers (`{"S": "..."}`, `{"N": "..."}`). The `jq` commands below strip those wrappers, rename German fields to their English equivalents, set `_id` from the hash key, and emit one JSON document per line (NDJSON format required by `mongoimport`).

> **Gotcha — numeric fields:** DynamoDB stores numbers as strings inside `{"N": "..."}`. The transforms below convert them to numbers explicitly. If you skip this, MongoDB will store them as strings and sorting/aggregation will break.

> **Gotcha — `getResults()` is derived:** The `results` list on an evening is computed at runtime from the five player fields (`resultJan`, `resultTim`, `resultOle`, `resultLouisa`, `resultHannes`). It is annotated `@Transient` and stored in MongoDB as `jan`, `tim`, `ole`, `louisa`, `hannes` via `@Field`. Do not attempt to persist `results` — it will be ignored on read.

### doko-abende → doko-evenings

DynamoDB field `Datum` (the date string hash key) becomes `_id`. Player score fields keep their lowercase names.

```bash
jq -c '.Items[] | {
  _id:      .Datum.S,
  semester: .semester.S,
  jan:      (.jan.N    | tonumber),
  tim:      (.tim.N    | tonumber),
  ole:      (.ole.N    | tonumber),
  louisa:   (.louisa.N | tonumber),
  hannes:   (.hannes.N | tonumber)
}' doko-abende.json > doko-abende.ndjson
```

### doko-semester → doko-semesters

```bash
jq -c '.Items[] | {
  _id:   .key.S,
  label: .label.S
}' doko-semester.json > doko-semester.ndjson
```

### doko-einnahmen → doko-earnings

DynamoDB field `art` (type/description) maps to `description`; `betrag` (amount) maps to `value`. `_id` is omitted so MongoDB auto-generates an ObjectId.

```bash
jq -c '.Items[] | {
  description: .art.S,
  value:       (.betrag.N | tonumber),
  semester:    .semester.S
}' doko-einnahmen.json > doko-einnahmen.ndjson
```

### doko-ausgaben → doko-expenses

Same field mapping as earnings:

```bash
jq -c '.Items[] | {
  description: .art.S,
  value:       (.betrag.N | tonumber),
  semester:    .semester.S
}' doko-ausgaben.json > doko-ausgaben.ndjson
```

## Step 3 — Load into MongoDB

```bash
mongoimport --uri mongodb://localhost:27017/dokokasse \
  --collection doko-evenings  --file doko-abende.ndjson
mongoimport --uri mongodb://localhost:27017/dokokasse \
  --collection doko-semesters --file doko-semester.ndjson
mongoimport --uri mongodb://localhost:27017/dokokasse \
  --collection doko-earnings  --file doko-einnahmen.ndjson
mongoimport --uri mongodb://localhost:27017/dokokasse \
  --collection doko-expenses  --file doko-ausgaben.ndjson
```

## Step 4 — Verify

**Count check** — counts must match Step 1 export counts:

```bash
mongosh mongodb://localhost:27017/dokokasse --eval '
  ["doko-evenings","doko-semesters","doko-earnings","doko-expenses"].forEach(c =>
    print(c + ": " + db.getCollection(c).countDocuments())
  )
'
```

**Spot check** — inspect one record per collection:

```bash
mongosh mongodb://localhost:27017/dokokasse --eval '
  db["doko-evenings"].findOne()
  db["doko-semesters"].findOne()
  db["doko-earnings"].findOne()
  db["doko-expenses"].findOne()
'
```

Confirm that:
- Evening records have all five numeric player fields (`jan`, `tim`, `ole`, `louisa`, `hannes`) as numbers (not strings)
- Earning/Expense records have `description` (string) and `value` (number); `_id` equals `description`
- Semester records have `_id` (the key string) and `label` as a string

**Application smoke test:**

```bash
./mvnw spring-boot:run -pl backend/domain -am
# in another terminal:
curl http://localhost:8080/evenings | jq '.["_embedded"].eveningDTOList | length'
curl http://localhost:8080/semester | jq '.'
curl http://localhost:8080/reports/cash | jq '.'
```

Confirm the record counts in the API responses match the DynamoDB source counts.

## Step 5 — Rollback

The DynamoDB export was read-only — all four tables are untouched. To revert:

1. Drop the application code changes (revert the Git commit)
2. Drop the MongoDB database: `mongosh --eval 'db.getSiblingDB("dokokasse").dropDatabase()'`
3. Start the previous version against DynamoDB

No DynamoDB data is at risk at any point during this migration.
