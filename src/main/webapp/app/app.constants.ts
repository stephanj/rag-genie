// These constants are injected via webpack environment variables.
// You can add more variables in webpack.common.js or in profile specific webpack.<dev|prod>.js files.
// If you change the values in the webpack config files, you need to re run webpack to update the application

export const VERSION = process.env.VERSION;
export const GIT_COMMIT_ID = process.env.GIT_COMMIT_ID;
export const DEBUG_INFO_ENABLED = !!process.env.DEBUG_INFO_ENABLED;

// ------------------------------------------------------------------------------------
// The event names used by JhiEventManager
// ------------------------------------------------------------------------------------

export const ACCOUNT_DETAILS = 'accountDetails';

export const LOGOUT_EVENT = 'logout';
