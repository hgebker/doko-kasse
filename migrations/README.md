# Database Migrations

Uses [migrate-mongo](https://github.com/seppevs/migrate-mongo) to manage MongoDB schema changes.

## Prerequisites

- Node 18+
- Local MongoDB running: `docker-compose up -d` from the project root

## Setup

```bash
cd migrations
npm install
```

## Usage

```bash
# Check which migrations have run
npm run migrate:status

# Create a new migration
npm run migrate:create -- <migration-name>
# e.g. npm run migrate:create -- add-player-stats-field

# Apply all pending migrations
npm run migrate:up

# Roll back the last applied migration
npm run migrate:down
```

## Production

Set `MONGODB_URI` before running:

```bash
MONGODB_URI=mongodb+srv://... npm run migrate:up
```

## Migration files

Migration scripts live in `migrations/` and are plain CommonJS modules:

```js
module.exports = {
  async up(db) {
    await db.collection('doko-abende').updateMany({}, { $set: { newField: null } });
  },

  async down(db) {
    await db.collection('doko-abende').updateMany({}, { $unset: { newField: '' } });
  },
};
```

Applied migrations are tracked in the `doko-changelog` collection.
