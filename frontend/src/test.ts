// This file is required by karma.conf.js and loads recursively all the .spec and framework files
import 'zone.js/testing';

import { HttpClientModule } from '@angular/common/http';
import { getTestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { NgBusyModule } from 'ng-busy';

import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

declare const require: {
  context(path: string, deep?: boolean, filter?: RegExp): {
    keys(): string[];
    <T>(id: string): T;
  };
};

// First, initialize the Angular testing environment.
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting(),
);

// Then we find all the tests.
const context = require.context('./', true, /\.spec\.ts$/);
// And load the modules.
context.keys().map(context);

beforeEach(() => {
  getTestBed().configureTestingModule({
    imports: [FormsModule, HttpClientModule, MatDialogModule, MatIconModule, NgBusyModule, ReactiveFormsModule, RouterTestingModule],
  });
});
