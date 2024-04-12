// This file is required by karma.conf.js and loads recursively all the .spec and framework files

import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

global.process.env = {
  ...global.process.env,
  BUILD_TIMESTAMP: '2021-01-01T00:00:00.000Z',
};


// First, initialize the Angular testing environment.
getTestBed().initTestEnvironment(
  [
    BrowserDynamicTestingModule,
    ReactiveFormsModule,
    RouterTestingModule,
    HttpClientTestingModule,
    FormsModule
  ],
  platformBrowserDynamicTesting(),
);

