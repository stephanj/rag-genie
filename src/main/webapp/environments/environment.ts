// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --configuration production` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  buildTimestamp: process.env.BUILD_TIMESTAMP,
};

export const appConfig = {
  serverApiUrl: '',
};
