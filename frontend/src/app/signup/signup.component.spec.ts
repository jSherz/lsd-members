/* tslint:disable:no-unused-variable */

import { inject, addProviders   } from '@angular/core/testing';
import { FormBuilder            } from '@angular/forms';
import { APP_BASE_HREF          } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { APP_ROUTER_PROVIDERS   } from '../app.routes';
import { SignupComponent        } from './signup.component';

beforeEach(() => addProviders([
  APP_ROUTER_PROVIDERS,
  {provide: APP_BASE_HREF, useValue: '/'},
  {provide: ActivatedRoute, useValue: {}},
  {provide: Router, useValue: {}},
  FormBuilder,
  SignupComponent
]));

describe('LSD.SignupComponent', () => {
  it('should create the app', inject([SignupComponent], (app: SignupComponent) => {
    expect(app).toBeTruthy();
  }));
});
