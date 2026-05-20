const config = {
  mongodb: {
    url: process.env.MONGODB_URI || 'mongodb://localhost:27017/dokokasse',
    options: {},
  },
  migrationsDir: 'migrations',
  changelogCollectionName: 'doko-changelog',
  lockCollectionName: 'doko-changelog-lock',
  lockTtl: 0,
  migrationFileExtension: '.js',
  useFileHash: false,
  moduleSystem: 'commonjs',
};

module.exports = config;
