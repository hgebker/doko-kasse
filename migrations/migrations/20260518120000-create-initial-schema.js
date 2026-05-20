const collections = ['doko-evenings', 'doko-earnings', 'doko-expenses', 'doko-semesters'];

module.exports = {
  async up(db) {
    const existing = (await db.listCollections({}, { nameOnly: true }).toArray()).map(c => c.name);
    for (const name of collections) {
      if (!existing.includes(name)) {
        await db.createCollection(name);
      }
    }
  },

  async down(db) {
    for (const name of collections) {
      const found = await db.listCollections({ name }).toArray();
      if (found.length > 0) {
        await db.collection(name).drop();
      }
    }
  },
};
