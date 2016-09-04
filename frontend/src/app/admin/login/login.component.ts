import { Component } from '@angular/core';
import { Router    } from '@angular/router';
import {
  FormControl,
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { LoginService } from './login.service';
import { ApiKeyService } from '../utils';

@Component({
  selector: 'login-component',
  templateUrl: 'login.component.html'
})
export class LoginComponent {

  loginForm: FormGroup;

  ctrlEmail: FormControl;
  ctrlPassword: FormControl;

  /**
   * Set when an API request fails.
   *
   * @type {boolean}
   */
  apiRequestFailed: boolean = false;

  /**
   * Should we show the loading animation?
   *
   * @type {boolean}
   */
  showThrobber: boolean = false;

  /**
   * Any errors returned by the API.
   */
  errors: {
    email: undefined,
    password: undefined
  };

  /**
   * Build the login form, including setting up any validation.
   *
   * @param builder
   * @param router
   * @param loginService
   * @param apiKeyService
   */
  constructor(private builder: FormBuilder,
              private router: Router,
              private loginService: LoginService,
              private apiKeyService: ApiKeyService) {
    this.ctrlEmail = new FormControl('', Validators.required);
    this.ctrlPassword = new FormControl('', Validators.required);

    this.loginForm = builder.group({
      email: this.ctrlEmail,
      password: this.ctrlPassword
    });
  }

  /**
   * Attempt to authenticate a user.
   *
   * Will show an error message if the API call fails or if it returns an error.
   *
   * Navigates to the main admin page if successful.
   *
   * @param user (with params "email" and "password")
   */
  login(user) {
    this.showThrobber = true;

    this.loginService.login(user.email, user.password).subscribe(
      result => {
        // API request succeeded, check result
        this.apiRequestFailed = false;
        this.showThrobber = false;

        if (result.success) {
          this.apiKeyService.setKey(result.apiKey);

          this.router.navigate(['admin', 'courses', 'calendar']);
        } else {
          this.errors = result.errors;
        }
      },
      error => {
        // API request failed, show generic error
        console.log('Login failed: ' + error);

        this.apiRequestFailed = true;
        this.showThrobber = false;
      }
    );
  }

  /**
   * Translates an error message key received from the server.
   *
   * @param key
   * @returns {any}
   */
  translate(key: string) {
    return {
      'error.invalidEmailPass': 'The e-mail or password you entered is invalid. Please try again.',
      'error.accountLocked': 'Your account has been disabled. Please contact an administrator.'
    }[key];
  }

}
