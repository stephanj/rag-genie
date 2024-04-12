const fs = require('fs');
const webpack = require('webpack');
const MergeJsonWebpackPlugin = require('merge-jsons-webpack-plugin');
const PACKAGE = require('../package.json');


module.exports = async (config, options, targetOptions) => {
  // Let's read the git sha1 from ./target/classes/git.properties (property git.commit.id.abbrev)
  // and store it in process.env.GIT_COMMIT_ID so that we can use it in our app.
  let gitProperties;
  let sha1;
  try {
    gitProperties = fs.readFileSync('./target/classes/git.properties', 'utf8');
    const match = gitProperties.match(/git\.commit\.id\.abbrev=(.*)/);
    if (match === null) {
      console.warn('git.commit.id.abbrev not found in git.properties');
      sha1 = 'N/A';
    } else {
      sha1 = match[1];
    }
  } catch (error) {
    if (error instanceof Error) {
      if (error.code === 'ENOENT') {
        console.warn('git.properties not found');
      } else {
        console.error('An unknown error occurred');
      }
    }
  }
  config.resolve = {
    extensions: ['.ts', '.js'],
    modules: ['node_modules'],
  };
  config.plugins.push(
    new webpack.DefinePlugin({
      'process.env': {
        BUILD_TIMESTAMP: `'${new Date().getTime()}'`,
        VERSION: `'${config.mode === 'production' ? PACKAGE.version : 'DEV'}'`,
        GIT_COMMIT_ID: `'${sha1}'`,
        DEBUG_INFO_ENABLED: config.mode !== 'production',
        // The root URL for API calls, ending with a '/' - for example: `"https://www.jhipster.tech:8081/myservice/"`.
        // If this URL is left empty (""), then it will be relative to the current context.
        // If you use an API server, in `prod` mode, you will need to enable CORS
        // (see the `jhipster.cors` common JHipster property in the `application-*.yml` configurations)
        SERVER_API_URL: `''`,
      },
    }),
  );
  return config;
};
