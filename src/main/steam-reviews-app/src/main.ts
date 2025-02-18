import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient } from '@angular/common/http';
import {routes} from './app/app.routes';
import {provideRouter} from '@angular/router';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {MAT_DATE_FORMATS, MAT_DATE_LOCALE} from '@angular/material/core';
import {CUSTOM_DATE_FORMATS} from './app/domain/custom-date-format';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(),
    provideRouter(routes),
    provideAnimationsAsync(),
  { provide: MAT_DATE_FORMATS, useValue: CUSTOM_DATE_FORMATS }, // Provide custom date format
  { provide: MAT_DATE_LOCALE, useValue: 'en-GB' }, // Use a locale that defaults to DD/MM/YYYY
  ]
}).catch(err => console.error(err));
